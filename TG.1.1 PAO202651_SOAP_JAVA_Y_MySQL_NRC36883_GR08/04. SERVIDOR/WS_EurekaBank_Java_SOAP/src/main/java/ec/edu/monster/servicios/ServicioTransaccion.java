package ec.edu.monster.servicios;

import ec.edu.monster.datos.CuentaDAO;
import ec.edu.monster.datos.FabricaConexion;
import ec.edu.monster.datos.MovimientoDAO;
import ec.edu.monster.datos.UnidadTrabajo;
import ec.edu.monster.excepciones.ExcepcionReglaNegocio;
import ec.edu.monster.excepciones.ExcepcionValidacion;
import ec.edu.monster.modelos.Cuenta;
import ec.edu.monster.modelos.Movimiento;
import ec.edu.monster.modelos.SesionActiva;
import ec.edu.monster.modelos.TipoMovimiento;
import ec.edu.monster.modelos.dto.RespuestaTransaccion;
import ec.edu.monster.util.FormatoUtil;
import ec.edu.monster.util.RelojUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * CU-05, CU-06 y CU-07: depósitos, retiros y transferencias. Cada operación es una transacción:
 * se bloquean las cuentas (SELECT ... FOR UPDATE), se actualiza el saldo y el contador, se registra el
 * movimiento con el empleado de la sesión y se confirma. Ante cualquier error se revierte (rollback).
 */
public class ServicioTransaccion {

    public static final String MENSAJE_CUENTAS_IGUALES = "La cuenta de origen y la de destino deben ser distintas";
    public static final String MENSAJE_MONEDAS_DISTINTAS = "Las cuentas tienen monedas distintas";

    private final FabricaConexion fabrica;
    private final CuentaDAO cuentas;
    private final MovimientoDAO movimientos;
    private final Clock reloj;

    public ServicioTransaccion(FabricaConexion fabrica, CuentaDAO cuentas, MovimientoDAO movimientos, Clock reloj) {
        this.fabrica = fabrica;
        this.cuentas = cuentas;
        this.movimientos = movimientos;
        this.reloj = reloj;
    }

    public RespuestaTransaccion registrarDeposito(String numeroCuenta, double importe, SesionActiva sesion)
            throws SQLException {
        return registrarEnCuenta(numeroCuenta, importe, sesion, TipoMovimiento.DEPOSITO);
    }

    public RespuestaTransaccion registrarRetiro(String numeroCuenta, double importe, SesionActiva sesion)
            throws SQLException {
        return registrarEnCuenta(numeroCuenta, importe, sesion, TipoMovimiento.RETIRO);
    }

    public RespuestaTransaccion registrarTransferencia(String cuentaOrigen, String cuentaDestino, double importe,
            SesionActiva sesion) throws SQLException {
        String numeroOrigen = ValidadorDatos.validarCuenta(cuentaOrigen);
        String numeroDestino = ValidadorDatos.validarCuenta(cuentaDestino);
        BigDecimal valor = ValidadorDatos.validarImporte(importe);
        if (numeroOrigen.equals(numeroDestino)) {
            throw new ExcepcionValidacion(MENSAJE_CUENTAS_IGUALES);
        }

        try (UnidadTrabajo unidad = fabrica.abrir(true)) {
            // Se bloquean siempre en orden de número: dos transferencias cruzadas (A→B y B→A) no se
            // esperan mutuamente para siempre (interbloqueo)
            boolean origenPrimero = numeroOrigen.compareTo(numeroDestino) < 0;
            Cuenta primera = cuentas.bloquear(unidad, origenPrimero ? numeroOrigen : numeroDestino);
            Cuenta segunda = cuentas.bloquear(unidad, origenPrimero ? numeroDestino : numeroOrigen);
            Cuenta origen = origenPrimero ? primera : segunda;
            Cuenta destino = origenPrimero ? segunda : primera;
            if (origen == null) {
                throw ServicioCuenta.noExiste(numeroOrigen);
            }
            if (destino == null) {
                throw ServicioCuenta.noExiste(numeroDestino);
            }

            exigirActiva(origen);
            exigirActiva(destino);
            if (!origen.getCodigoMoneda().equals(destino.getCodigoMoneda())) {
                throw new ExcepcionReglaNegocio(MENSAJE_MONEDAS_DISTINTAS);
            }
            exigirSaldo(origen, valor);
            exigirCapacidad(destino, valor);

            LocalDateTime ahora = RelojUtil.ahoraLocal(reloj);
            origen.debitar(valor);
            destino.acreditar(valor);
            cuentas.actualizar(unidad, origen);
            cuentas.actualizar(unidad, destino);
            movimientos.registrar(unidad, crearMovimiento(origen, TipoMovimiento.TRANSFERENCIA_ENVIADA, valor,
                    destino.getNumero(), sesion, ahora));
            movimientos.registrar(unidad, crearMovimiento(destino, TipoMovimiento.TRANSFERENCIA_RECIBIDA, valor,
                    origen.getNumero(), sesion, ahora));
            unidad.confirmar();

            return new RespuestaTransaccion(HttpServletResponse.SC_CREATED,
                    "Transferencia de " + FormatoUtil.monto(origen.getSimboloMoneda(), valor) + " de la cuenta "
                    + origen.getNumero() + " a la cuenta " + destino.getNumero() + " registrada correctamente",
                    FormatoUtil.dosDecimales(origen.getSaldo()), origen.getContadorMovimientos());
        }
    }

    private RespuestaTransaccion registrarEnCuenta(String numeroCuenta, double importe, SesionActiva sesion,
            String tipo) throws SQLException {
        String numero = ValidadorDatos.validarCuenta(numeroCuenta);
        BigDecimal valor = ValidadorDatos.validarImporte(importe);

        try (UnidadTrabajo unidad = fabrica.abrir(true)) {
            Cuenta cuenta = cuentas.bloquear(unidad, numero);
            if (cuenta == null) {
                throw ServicioCuenta.noExiste(numero);
            }
            exigirActiva(cuenta);

            boolean esRetiro = TipoMovimiento.RETIRO.equals(tipo);
            if (esRetiro) {
                exigirSaldo(cuenta, valor);
                cuenta.debitar(valor);
            } else {
                exigirCapacidad(cuenta, valor);
                cuenta.acreditar(valor);
            }
            cuentas.actualizar(unidad, cuenta);
            movimientos.registrar(unidad, crearMovimiento(cuenta, tipo, valor, null, sesion,
                    RelojUtil.ahoraLocal(reloj)));
            unidad.confirmar();

            String operacion = esRetiro ? "Retiro" : "Depósito";
            return new RespuestaTransaccion(HttpServletResponse.SC_CREATED,
                    operacion + " de " + FormatoUtil.monto(cuenta.getSimboloMoneda(), valor) + " en la cuenta "
                    + cuenta.getNumero() + " registrado correctamente",
                    FormatoUtil.dosDecimales(cuenta.getSaldo()), cuenta.getContadorMovimientos());
        }
    }

    private static void exigirActiva(Cuenta cuenta) {
        if (!cuenta.estaActiva()) {
            throw new ExcepcionReglaNegocio("La cuenta " + cuenta.getNumero() + " no está activa");
        }
    }

    private static void exigirSaldo(Cuenta cuenta, BigDecimal importe) {
        if (!cuenta.tieneSaldo(importe)) {
            throw new ExcepcionReglaNegocio("Saldo insuficiente. Saldo disponible: "
                    + FormatoUtil.monto(cuenta.getSimboloMoneda(), cuenta.getSaldo()));
        }
    }

    // El saldo resultante debe caber en la columna decimal(12,2)
    private static void exigirCapacidad(Cuenta cuenta, BigDecimal importe) {
        if (cuenta.getSaldo().add(importe).compareTo(ValidadorDatos.IMPORTE_MAXIMO) > 0) {
            throw new ExcepcionReglaNegocio("El saldo de la cuenta " + cuenta.getNumero()
                    + " superaría el máximo permitido");
        }
    }

    // El número del movimiento es el contador de la cuenta ya incrementado por acreditar/debitar
    private static Movimiento crearMovimiento(Cuenta cuenta, String tipo, BigDecimal importe, String referencia,
            SesionActiva sesion, LocalDateTime fecha) {
        Movimiento movimiento = new Movimiento();
        movimiento.setNumeroCuenta(cuenta.getNumero());
        movimiento.setNumero(cuenta.getContadorMovimientos());
        movimiento.setCodigoTipo(tipo);
        movimiento.setCodigoEmpleado(sesion.getCodigoEmpleado());
        movimiento.setCuentaReferencia(referencia);
        movimiento.setFechaHora(fecha);
        movimiento.setImporte(importe);
        return movimiento;
    }
}
