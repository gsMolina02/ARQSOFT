using ec.edu.monster.Cliente;
using ec.edu.monster.Cliente.Dto;
using ec.edu.monster.Controlador;
using ec.edu.monster.Modelo;
using ec.edu.monster.Util;

namespace ec.edu.monster.Vista;

/// <summary>Cómo terminó el menú principal.</summary>
public enum ResultadoMenu
{
    /// <summary>Cerró sesión o la sesión venció: vuelve al inicio de sesión.</summary>
    CerrarSesion,
    /// <summary>Se perdió la conexión y el usuario quiere cambiar la dirección del servidor.</summary>
    ConfigurarServidor,
    /// <summary>Salir del programa.</summary>
    Salir
}

public class MenuVista
{
    private readonly MensajesModelo mensajes = new();
    private readonly LoginControlador loginControlador;
    private readonly CuentaControlador cuentaControlador;
    private ResultadoMenu? resultado;

    public MenuVista(LoginControlador loginControlador)
    {
        this.loginControlador = loginControlador;
        cuentaControlador = loginControlador.CrearControladorCuenta();
    }

    public async Task<ResultadoMenu> EjecutarAsync()
    {
        while (resultado is null)
        {
            mensajes.MostrarTitulo("MENÚ PRINCIPAL");
            mensajes.MostrarDetalle("  Empleado: " + loginControlador.Empleado);
            mensajes.MostrarMensaje("  [1] Consultar movimientos de una cuenta");
            mensajes.MostrarMensaje("  [2] Registrar depósito");
            mensajes.MostrarMensaje("  [3] Registrar retiro");
            mensajes.MostrarMensaje("  [4] Registrar transferencia");
            mensajes.MostrarMensaje("  [5] Cambiar contraseña");
            mensajes.MostrarMensaje("  [6] Cerrar sesión");
            mensajes.MostrarMensaje("  [0] Salir");

            int opcion = EntradaUtil.LeerEntero("  > Seleccione una opción: ");

            switch (opcion)
            {
                case 1:
                    await EjecutarAsync(ConsultarMovimientosAsync);
                    break;
                case 2:
                    await EjecutarAsync(() => RegistrarEnCuentaAsync(esDeposito: true));
                    break;
                case 3:
                    await EjecutarAsync(() => RegistrarEnCuentaAsync(esDeposito: false));
                    break;
                case 4:
                    await EjecutarAsync(RegistrarTransferenciaAsync);
                    break;
                case 5:
                    await EjecutarAsync(CambiarClaveAsync);
                    break;
                case 6:
                    await CerrarSesionAsync();
                    resultado = ResultadoMenu.CerrarSesion;
                    break;
                case 0:
                    await CerrarSesionAsync();
                    resultado = ResultadoMenu.Salir;
                    break;
                default:
                    mensajes.MostrarError("Opción no válida. Intente nuevamente.");
                    break;
            }
        }
        return resultado.Value;
    }

    private async Task ConsultarMovimientosAsync()
    {
        mensajes.MostrarTitulo("CONSULTAR MOVIMIENTOS");
        string cuenta = await LeerCuentaAsync("Número de cuenta");

        RespuestaMovimientos respuesta = await cuentaControlador.ConsultarAsync(cuenta);
        CuentaDto datos = respuesta.Cuenta!;

        mensajes.MostrarMensaje("");
        mensajes.MostrarDato("Cuenta", datos.Numero);
        mensajes.MostrarDato("Cliente", $"{datos.Cliente} (cédula {datos.Cedula})");
        mensajes.MostrarDato("Sucursal", datos.Sucursal);
        mensajes.MostrarDato("Moneda", datos.Moneda);
        mensajes.MostrarDato("Fecha de apertura", FormatoUtil.FormatearFecha(datos.FechaApertura));
        mensajes.MostrarDato("Estado", datos.Estado);
        mensajes.MostrarDato("Saldo", FormatoUtil.FormatearMonto(datos.Saldo, datos.SimboloMoneda));
        mensajes.MostrarMensaje("");

        if (respuesta.Movimientos.Count == 0)
        {
            mensajes.MostrarAviso("  La cuenta no registra movimientos.");
            return;
        }
        List<string[]> filas = respuesta.Movimientos.Select(movimiento => new[]
        {
            movimiento.Numero.ToString(),
            FormatoUtil.FormatearFechaHora(movimiento.FechaHora),
            movimiento.Tipo,
            movimiento.Accion,
            FormatoUtil.FormatearMovimiento(movimiento.Importe, movimiento.EsIngreso, datos.SimboloMoneda),
            movimiento.CuentaReferencia ?? "-"
        }).ToList();
        mensajes.MostrarTabla(["Nro", "Fecha y hora", "Tipo", "Acción", "Importe", "Referencia"], filas, 0, 4);
        mensajes.MostrarDetalle("  " + respuesta.Mensaje);
    }

    private async Task RegistrarEnCuentaAsync(bool esDeposito)
    {
        mensajes.MostrarTitulo(esDeposito ? "REGISTRAR DEPÓSITO" : "REGISTRAR RETIRO");
        string cuenta = await LeerCuentaAsync("Número de cuenta");
        decimal importe = EntradaUtil.LeerImporte("  > Importe en USD: ");

        string operacion = esDeposito ? "depositar" : "retirar";
        if (!EntradaUtil.LeerSiNo($"  ¿Confirma {operacion} {FormatoUtil.FormatearMonto(importe)} en la cuenta {cuenta}?"))
        {
            mensajes.MostrarAviso("  Operación cancelada.");
            return;
        }

        RespuestaTransaccion respuesta = esDeposito
                ? await cuentaControlador.DepositarAsync(cuenta, importe)
                : await cuentaControlador.RetirarAsync(cuenta, importe);
        MostrarComprobante(respuesta, cuenta);
    }

    private async Task RegistrarTransferenciaAsync()
    {
        mensajes.MostrarTitulo("REGISTRAR TRANSFERENCIA");
        string origen = await LeerCuentaAsync("Cuenta de origen ");
        string destino;
        while (true)
        {
            destino = await LeerCuentaAsync("Cuenta de destino");
            if (destino != origen)
            {
                break;
            }
            mensajes.MostrarError(ValidacionUtil.MensajeCuentasIguales + ".");
        }
        decimal importe = EntradaUtil.LeerImporte("  > Importe en USD: ");

        if (!EntradaUtil.LeerSiNo($"  ¿Confirma transferir {FormatoUtil.FormatearMonto(importe)} de la cuenta {origen} a la cuenta {destino}?"))
        {
            mensajes.MostrarAviso("  Operación cancelada.");
            return;
        }

        RespuestaTransaccion respuesta = await cuentaControlador.TransferirAsync(origen, destino, importe);
        MostrarComprobante(respuesta, origen);
    }

    /// <summary>
    /// Pide el número de cuenta: se escribe (8 dígitos) o se escribe L para elegirlo de la lista del servidor,
    /// así el empleado no tiene que memorizar los números.
    /// </summary>
    private async Task<string> LeerCuentaAsync(string etiqueta)
    {
        while (true)
        {
            string texto = EntradaUtil.LeerTextoNoVacio($"  > {etiqueta} (8 dígitos, o L para ver la lista): ");
            if (texto.Equals("L", StringComparison.OrdinalIgnoreCase))
            {
                string? elegida = await ElegirCuentaDeListaAsync();
                if (elegida is not null)
                {
                    return elegida;
                }
                continue;
            }
            try
            {
                return ValidacionUtil.ValidarCuenta(texto);
            }
            catch (ArgumentException ex)
            {
                mensajes.MostrarError(ex.Message + ".");
            }
        }
    }

    // Muestra todas las cuentas numeradas; 0 vuelve a pedir el número escrito
    private async Task<string?> ElegirCuentaDeListaAsync()
    {
        List<CuentaDto> cuentas = (await cuentaControlador.ListarCuentasAsync()).Cuentas;
        if (cuentas.Count == 0)
        {
            mensajes.MostrarAviso("  No hay cuentas registradas en el servidor.");
            return null;
        }

        List<string[]> filas = cuentas.Select((cuenta, indice) => new[]
        {
            (indice + 1).ToString(), cuenta.Numero, cuenta.Cliente, cuenta.Estado
        }).ToList();
        mensajes.MostrarTabla(["Opción", "Cuenta", "Cliente", "Estado"], filas, 0);

        while (true)
        {
            int opcion = EntradaUtil.LeerEntero($"  > Elija una cuenta (1 a {cuentas.Count}, 0 para escribir el número): ");
            if (opcion == 0)
            {
                return null;
            }
            if (opcion <= cuentas.Count)
            {
                CuentaDto elegida = cuentas[opcion - 1];
                mensajes.MostrarDetalle("  Cuenta elegida: " + FormatoUtil.FormatearOpcionCuenta(elegida));
                return elegida.Numero;
            }
            mensajes.MostrarError($"Opción no válida. Elija un número del 1 al {cuentas.Count}.");
        }
    }

    private async Task CambiarClaveAsync()
    {
        mensajes.MostrarTitulo("CAMBIAR CONTRASEÑA");
        mensajes.MostrarDetalle($"  La nueva contraseña debe tener al menos {ValidacionUtil.LongitudMinimaClave} caracteres.");
        string actual = EntradaUtil.LeerClave("  > Contraseña actual       : ");
        string nueva = EntradaUtil.LeerClave("  > Nueva contraseña        : ");
        string confirmacion = EntradaUtil.LeerClave("  > Confirme la contraseña  : ");

        mensajes.MostrarExito(await loginControlador.CambiarClaveAsync(actual, nueva, confirmacion));
    }

    private async Task CerrarSesionAsync()
    {
        try
        {
            mensajes.MostrarExito(await loginControlador.CerrarSesionAsync());
        }
        catch (ExcepcionClienteWS ex)
        {
            // El token local ya se borró: solo se informa que el servidor no se enteró
            mensajes.MostrarAviso(ex.EsErrorConexion
                    ? "  Sesión cerrada en este equipo, pero no se pudo notificar al servidor."
                    : "  Sesión cerrada.");
        }
    }

    private void MostrarComprobante(RespuestaTransaccion respuesta, string cuenta)
    {
        mensajes.MostrarMensaje("");
        mensajes.MostrarExito(respuesta.Mensaje ?? "Operación registrada correctamente");
        mensajes.MostrarDato("Movimiento Nro", respuesta.NumeroMovimiento.ToString());
        mensajes.MostrarDato($"Nuevo saldo {cuenta}", FormatoUtil.FormatearMonto(respuesta.Saldo));
    }

    // Los errores del servicio se muestran sin salir del menú, salvo la sesión vencida (vuelve al login)
    // y la falta de conexión (ofrece configurar el servidor)
    private async Task EjecutarAsync(Func<Task> accion)
    {
        try
        {
            await accion();
        }
        catch (ArgumentException ex)
        {
            mensajes.MostrarError(ex.Message + ".");
        }
        catch (ExcepcionClienteWS ex)
        {
            mensajes.MostrarError(ex.Message);
            if (ex.EsErrorAutenticacion)
            {
                mensajes.MostrarAviso("  Debe iniciar sesión nuevamente.");
                resultado = ResultadoMenu.CerrarSesion;
            }
            else if (ex.EsErrorConexion && EntradaUtil.LeerSiNo("  ¿Desea configurar la dirección del servidor?"))
            {
                resultado = ResultadoMenu.ConfigurarServidor;
            }
        }
    }
}
