package ec.edu.monster.vista;

import ec.edu.monster.cliente.ExcepcionClienteWS;
import ec.edu.monster.cliente.dto.CuentaDTO;
import ec.edu.monster.cliente.dto.MovimientoDTO;
import ec.edu.monster.cliente.dto.RespuestaMovimientos;
import ec.edu.monster.cliente.dto.RespuestaTransaccion;
import ec.edu.monster.controlador.CuentaControlador;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.modelo.MensajesModelo;
import ec.edu.monster.util.EntradaUtil;
import ec.edu.monster.util.FormatoUtil;
import ec.edu.monster.util.ValidacionUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MenuVista {

    private final MensajesModelo mensajes = new MensajesModelo();
    private final LoginControlador loginControlador;
    private final CuentaControlador cuentaControlador;
    private ResultadoMenu resultado;

    public MenuVista(LoginControlador loginControlador) {
        this.loginControlador = loginControlador;
        this.cuentaControlador = loginControlador.crearControladorCuenta();
    }

    public ResultadoMenu ejecutar() {
        while (resultado == null) {
            mensajes.mostrarTitulo("MENÚ PRINCIPAL");
            mensajes.mostrarDetalle("  Empleado: " + loginControlador.getEmpleado());
            mensajes.mostrarMensaje("  [1] Consultar movimientos de una cuenta");
            mensajes.mostrarMensaje("  [2] Registrar depósito");
            mensajes.mostrarMensaje("  [3] Registrar retiro");
            mensajes.mostrarMensaje("  [4] Registrar transferencia");
            mensajes.mostrarMensaje("  [5] Cambiar contraseña");
            mensajes.mostrarMensaje("  [6] Cerrar sesión");
            mensajes.mostrarMensaje("  [0] Salir");

            int opcion = EntradaUtil.leerEntero("  > Seleccione una opción: ");

            switch (opcion) {
                case 1 -> ejecutar(this::consultarMovimientos);
                case 2 -> ejecutar(() -> registrarEnCuenta(true));
                case 3 -> ejecutar(() -> registrarEnCuenta(false));
                case 4 -> ejecutar(this::registrarTransferencia);
                case 5 -> ejecutar(this::cambiarClave);
                case 6 -> {
                    cerrarSesion();
                    resultado = ResultadoMenu.CERRAR_SESION;
                }
                case 0 -> {
                    cerrarSesion();
                    resultado = ResultadoMenu.SALIR;
                }
                default -> mensajes.mostrarError("Opción no válida. Intente nuevamente.");
            }
        }
        return resultado;
    }

    private void consultarMovimientos() {
        mensajes.mostrarTitulo("CONSULTAR MOVIMIENTOS");
        String cuenta = leerCuenta("Número de cuenta");

        RespuestaMovimientos respuesta = cuentaControlador.consultar(cuenta);
        CuentaDTO datos = respuesta.cuenta();

        mensajes.mostrarMensaje("");
        mensajes.mostrarDato("Cuenta", datos.numero());
        mensajes.mostrarDato("Cliente", datos.cliente() + " (cédula " + datos.cedula() + ")");
        mensajes.mostrarDato("Sucursal", datos.sucursal());
        mensajes.mostrarDato("Moneda", datos.moneda());
        mensajes.mostrarDato("Fecha de apertura", FormatoUtil.formatearFecha(datos.fechaApertura()));
        mensajes.mostrarDato("Estado", datos.estado());
        mensajes.mostrarDato("Saldo", FormatoUtil.formatearMonto(datos.saldo(), datos.simboloMoneda()));
        mensajes.mostrarMensaje("");

        if (respuesta.movimientos().isEmpty()) {
            mensajes.mostrarAviso("  La cuenta no registra movimientos.");
            return;
        }
        List<String[]> filas = new ArrayList<>();
        for (MovimientoDTO movimiento : respuesta.movimientos()) {
            filas.add(new String[] {
                String.valueOf(movimiento.numero()),
                FormatoUtil.formatearFechaHora(movimiento.fechaHora()),
                movimiento.tipo(),
                movimiento.accion(),
                FormatoUtil.formatearMovimiento(movimiento.importe(), movimiento.esIngreso(), datos.simboloMoneda()),
                movimiento.cuentaReferencia() == null ? "-" : movimiento.cuentaReferencia()
            });
        }
        mensajes.mostrarTabla(new String[] {"Nro", "Fecha y hora", "Tipo", "Acción", "Importe", "Referencia"},
                filas, 0, 4);
        mensajes.mostrarDetalle("  " + respuesta.mensaje());
    }

    private void registrarEnCuenta(boolean esDeposito) {
        mensajes.mostrarTitulo(esDeposito ? "REGISTRAR DEPÓSITO" : "REGISTRAR RETIRO");
        String cuenta = leerCuenta("Número de cuenta");
        BigDecimal importe = EntradaUtil.leerImporte("  > Importe en USD: ");

        String operacion = esDeposito ? "depositar" : "retirar";
        if (!EntradaUtil.leerSiNo("  ¿Confirma " + operacion + " " + FormatoUtil.formatearMonto(importe)
                + " en la cuenta " + cuenta + "?")) {
            mensajes.mostrarAviso("  Operación cancelada.");
            return;
        }

        RespuestaTransaccion respuesta = esDeposito
                ? cuentaControlador.depositar(cuenta, importe)
                : cuentaControlador.retirar(cuenta, importe);
        mostrarComprobante(respuesta, cuenta);
    }

    private void registrarTransferencia() {
        mensajes.mostrarTitulo("REGISTRAR TRANSFERENCIA");
        String origen = leerCuenta("Cuenta de origen ");
        String destino;
        while (true) {
            destino = leerCuenta("Cuenta de destino");
            if (!destino.equals(origen)) {
                break;
            }
            mensajes.mostrarError(ValidacionUtil.MENSAJE_CUENTAS_IGUALES + ".");
        }
        BigDecimal importe = EntradaUtil.leerImporte("  > Importe en USD: ");

        if (!EntradaUtil.leerSiNo("  ¿Confirma transferir " + FormatoUtil.formatearMonto(importe) + " de la cuenta "
                + origen + " a la cuenta " + destino + "?")) {
            mensajes.mostrarAviso("  Operación cancelada.");
            return;
        }

        RespuestaTransaccion respuesta = cuentaControlador.transferir(origen, destino, importe);
        mostrarComprobante(respuesta, origen);
    }

    /**
     * Pide el número de cuenta: se escribe (8 dígitos) o se escribe L para elegirlo de la lista del servidor,
     * así el empleado no tiene que memorizar los números.
     */
    private String leerCuenta(String etiqueta) {
        while (true) {
            String texto = EntradaUtil.leerTextoNoVacio("  > " + etiqueta + " (8 dígitos, o L para ver la lista): ");
            if (texto.equalsIgnoreCase("L")) {
                String elegida = elegirCuentaDeLista();
                if (elegida != null) {
                    return elegida;
                }
                continue;
            }
            try {
                return ValidacionUtil.validarCuenta(texto);
            } catch (IllegalArgumentException ex) {
                mensajes.mostrarError(ex.getMessage() + ".");
            }
        }
    }

    // Muestra todas las cuentas numeradas; 0 vuelve a pedir el número escrito
    private String elegirCuentaDeLista() {
        List<CuentaDTO> cuentas = cuentaControlador.listarCuentas().cuentas();
        if (cuentas.isEmpty()) {
            mensajes.mostrarAviso("  No hay cuentas registradas en el servidor.");
            return null;
        }

        List<String[]> filas = new ArrayList<>();
        for (int indice = 0; indice < cuentas.size(); indice++) {
            CuentaDTO cuenta = cuentas.get(indice);
            filas.add(new String[] {String.valueOf(indice + 1), cuenta.numero(), cuenta.cliente(), cuenta.estado()});
        }
        mensajes.mostrarTabla(new String[] {"Opción", "Cuenta", "Cliente", "Estado"}, filas, 0);

        while (true) {
            int opcion = EntradaUtil.leerEntero("  > Elija una cuenta (1 a " + cuentas.size() + ", 0 para escribir el número): ");
            if (opcion == 0) {
                return null;
            }
            if (opcion <= cuentas.size()) {
                CuentaDTO elegida = cuentas.get(opcion - 1);
                mensajes.mostrarDetalle("  Cuenta elegida: " + FormatoUtil.formatearOpcionCuenta(elegida));
                return elegida.numero();
            }
            mensajes.mostrarError("Opción no válida. Elija un número del 1 al " + cuentas.size() + ".");
        }
    }

    private void cambiarClave() {
        mensajes.mostrarTitulo("CAMBIAR CONTRASEÑA");
        mensajes.mostrarDetalle("  La nueva contraseña debe tener al menos " + ValidacionUtil.LONGITUD_MINIMA_CLAVE
                + " caracteres.");
        String actual = EntradaUtil.leerClave("  > Contraseña actual       : ");
        String nueva = EntradaUtil.leerClave("  > Nueva contraseña        : ");
        String confirmacion = EntradaUtil.leerClave("  > Confirme la contraseña  : ");

        mensajes.mostrarExito(loginControlador.cambiarClave(actual, nueva, confirmacion));
    }

    private void cerrarSesion() {
        try {
            mensajes.mostrarExito(loginControlador.cerrarSesion());
        } catch (ExcepcionClienteWS ex) {
            // El token local ya se borró: solo se informa que el servidor no se enteró
            mensajes.mostrarAviso(ex.esErrorConexion()
                    ? "  Sesión cerrada en este equipo, pero no se pudo notificar al servidor."
                    : "  Sesión cerrada.");
        }
    }

    private void mostrarComprobante(RespuestaTransaccion respuesta, String cuenta) {
        mensajes.mostrarMensaje("");
        mensajes.mostrarExito(respuesta.mensaje());
        mensajes.mostrarDato("Movimiento Nro", String.valueOf(respuesta.numeroMovimiento()));
        mensajes.mostrarDato("Nuevo saldo " + cuenta, FormatoUtil.formatearMonto(respuesta.saldo()));
    }

    // Los errores del servicio se muestran sin salir del menú, salvo la sesión vencida (vuelve al login)
    // y la falta de conexión (ofrece configurar el servidor)
    private void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException ex) {
            mensajes.mostrarError(ex.getMessage() + ".");
        } catch (ExcepcionClienteWS ex) {
            mensajes.mostrarError(ex.getMessage());
            if (ex.esErrorAutenticacion()) {
                mensajes.mostrarAviso("  Debe iniciar sesión nuevamente.");
                resultado = ResultadoMenu.CERRAR_SESION;
            } else if (ex.esErrorConexion() && EntradaUtil.leerSiNo("  ¿Desea configurar la dirección del servidor?")) {
                resultado = ResultadoMenu.CONFIGURAR_SERVIDOR;
            }
        }
    }
}
