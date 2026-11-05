package ec.edu.monster.servicios;

import ec.edu.monster.datos.CuentaDAO;
import ec.edu.monster.datos.FabricaConexion;
import ec.edu.monster.datos.MovimientoDAO;
import ec.edu.monster.datos.UnidadTrabajo;
import ec.edu.monster.excepciones.ExcepcionNoEncontrado;
import ec.edu.monster.modelos.Cuenta;
import ec.edu.monster.modelos.Movimiento;
import ec.edu.monster.modelos.dto.CuentaDTO;
import ec.edu.monster.modelos.dto.MovimientoDTO;
import ec.edu.monster.modelos.dto.RespuestaCuentas;
import ec.edu.monster.modelos.dto.RespuestaMovimientos;
import ec.edu.monster.util.FormatoUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * CU-04: datos de la cuenta (cliente, moneda, sucursal, estado y saldo) y sus movimientos.
 * La consulta se permite en cualquier estado de la cuenta.
 * También entrega la lista de cuentas para que los clientes no tengan que memorizar los números.
 */
public class ServicioCuenta {

    private final FabricaConexion fabrica;
    private final CuentaDAO cuentas;
    private final MovimientoDAO movimientos;

    public ServicioCuenta(FabricaConexion fabrica, CuentaDAO cuentas, MovimientoDAO movimientos) {
        this.fabrica = fabrica;
        this.cuentas = cuentas;
        this.movimientos = movimientos;
    }

    public RespuestaMovimientos consultarMovimientos(String numeroCuenta) throws SQLException {
        String numero = ValidadorDatos.validarCuenta(numeroCuenta);

        try (UnidadTrabajo unidad = fabrica.abrir()) {
            Cuenta cuenta = cuentas.buscar(unidad, numero);
            if (cuenta == null) {
                throw noExiste(numero);
            }
            List<Movimiento> lista = movimientos.listar(unidad, numero);

            CuentaDTO datosCuenta = convertir(cuenta);

            List<MovimientoDTO> detalle = new ArrayList<>();
            for (Movimiento movimiento : lista) {
                detalle.add(new MovimientoDTO(movimiento.getNumero(), FormatoUtil.fechaHora(movimiento.getFechaHora()),
                        movimiento.getCodigoTipo(), movimiento.getDescripcionTipo(), movimiento.getAccion(),
                        FormatoUtil.dosDecimales(movimiento.getImporte()), movimiento.getCuentaReferencia(),
                        movimiento.getCodigoEmpleado()));
            }

            String mensaje = lista.isEmpty()
                    ? "La cuenta no registra movimientos"
                    : "La cuenta registra " + lista.size() + " movimiento(s)";
            return new RespuestaMovimientos(HttpServletResponse.SC_OK, mensaje, datosCuenta, detalle);
        }
    }

    /**
     * Todas las cuentas (activas o no) para la lista desplegable de los clientes.
     */
    public RespuestaCuentas listarCuentas() throws SQLException {
        try (UnidadTrabajo unidad = fabrica.abrir()) {
            List<CuentaDTO> lista = new ArrayList<>();
            for (Cuenta cuenta : cuentas.listar(unidad)) {
                lista.add(convertir(cuenta));
            }
            return new RespuestaCuentas(HttpServletResponse.SC_OK,
                    "Se encontraron " + lista.size() + " cuenta(s)", lista);
        }
    }

    private static CuentaDTO convertir(Cuenta cuenta) {
        return new CuentaDTO(cuenta.getNumero(), cuenta.getNombreCliente(),
                cuenta.getCedulaCliente(), cuenta.getCodigoMoneda(), cuenta.getSimboloMoneda(),
                cuenta.getNombreSucursal(), cuenta.getEstado(), FormatoUtil.dosDecimales(cuenta.getSaldo()),
                FormatoUtil.fecha(cuenta.getFechaApertura()), cuenta.getContadorMovimientos());
    }

    public static ExcepcionNoEncontrado noExiste(String numero) {
        return new ExcepcionNoEncontrado("La cuenta " + numero + " no existe");
    }
}
