using ec.edu.monster.Cliente;
using ec.edu.monster.Cliente.Dto;
using ec.edu.monster.Filtros;
using ec.edu.monster.Models;
using ec.edu.monster.Servicios;
using ec.edu.monster.Sesion;
using ec.edu.monster.Util;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.Controllers;

/// <summary>
/// Pantalla principal: consulta de movimientos, depósito, retiro y transferencia.
/// Después de registrar una operación se redirige a la consulta de la cuenta (patrón PRG):
/// recargar la página no repite el depósito.
/// </summary>
[RequiereSesion]
public class OperacionesController : Controller
{
    private const string ClaveComprobanteTitulo = "ComprobanteTitulo";
    private const string ClaveComprobanteValor = "ComprobanteValor";

    private readonly ServicioOperaciones servicioOperaciones;
    private readonly AlmacenSesion sesion;

    public OperacionesController(ServicioOperaciones servicioOperaciones, AlmacenSesion sesion)
    {
        this.servicioOperaciones = servicioOperaciones;
        this.sesion = sesion;
    }

    /// <param name="cuenta">Cuenta cuyos datos y movimientos se muestran a la derecha.</param>
    [HttpGet]
    public async Task<IActionResult> Index(TipoOperacion operacion = TipoOperacion.Movimientos, string? cuenta = null)
    {
        var modelo = new OperacionesModelo { Operacion = operacion, Cuenta = cuenta };

        if (TempData[ClaveComprobanteTitulo] is string titulo)
        {
            modelo.ResultadoTitulo = titulo;
            modelo.ResultadoValor = TempData[ClaveComprobanteValor] as string;
        }
        if (string.IsNullOrWhiteSpace(cuenta))
        {
            return await VistaAsync(modelo);
        }

        try
        {
            modelo.Consulta = await servicioOperaciones.ConsultarAsync(cuenta);
            if (operacion == TipoOperacion.Movimientos && modelo.ResultadoTitulo is null)
            {
                CuentaDto datos = modelo.Consulta.Cuenta!;
                modelo.ResultadoTitulo = $"Saldo disponible de la cuenta {datos.Numero} ({datos.Estado})";
                modelo.ResultadoValor = FormatoUtil.FormatearMonto(datos.Saldo, datos.SimboloMoneda);
            }
        }
        catch (ArgumentException ex)
        {
            modelo.Error = ex.Message;
            modelo.CampoConError = "cuenta";
        }
        catch (ExcepcionClienteWS ex)
        {
            return await ManejarErrorServicioAsync(ex, modelo);
        }
        return await VistaAsync(modelo);
    }

    [HttpPost]
    public async Task<IActionResult> Ejecutar(OperacionesModelo modelo)
    {
        if (modelo.Operacion == TipoOperacion.Movimientos)
        {
            try
            {
                string cuenta = ValidacionUtil.ValidarCuenta(modelo.Cuenta);
                return RedirectToAction(nameof(Index), new { operacion = TipoOperacion.Movimientos, cuenta });
            }
            catch (ArgumentException ex)
            {
                return await VistaConErrorAsync(modelo, ex.Message, "cuenta");
            }
        }

        try
        {
            RespuestaTransaccion respuesta = await servicioOperaciones.RegistrarAsync(modelo);
            string cuenta = ValidacionUtil.ValidarCuenta(modelo.Cuenta);

            TempData["Exito"] = respuesta.Mensaje + ".";
            TempData[ClaveComprobanteTitulo] = $"Movimiento Nro {respuesta.NumeroMovimiento} · nuevo saldo de la cuenta {cuenta}:";
            TempData[ClaveComprobanteValor] = FormatoUtil.FormatearMonto(respuesta.Saldo);
            return RedirectToAction(nameof(Index), new { operacion = modelo.Operacion, cuenta });
        }
        catch (ExcepcionCampo ex)
        {
            return await VistaConErrorAsync(modelo, ex.Message, ex.Campo);
        }
        catch (ExcepcionClienteWS ex)
        {
            return await ManejarErrorServicioAsync(ex, modelo);
        }
    }

    private Task<ViewResult> VistaConErrorAsync(OperacionesModelo modelo, string mensaje, string? campo)
    {
        modelo.Error = mensaje.EndsWith('.') ? mensaje : mensaje + ".";
        modelo.CampoConError = campo;
        return VistaAsync(modelo);
    }

    // Carga las cuentas de la lista desplegable. La lista es una ayuda: si el servidor no responde,
    // los campos siguen aceptando el número escrito (y sin conexión ni se intenta, para no esperar otra vez)
    private async Task<ViewResult> VistaAsync(OperacionesModelo modelo)
    {
        if (!modelo.SinConexion)
        {
            try
            {
                modelo.Cuentas = (await servicioOperaciones.ListarCuentasAsync()).Cuentas;
            }
            catch (ExcepcionClienteWS)
            {
                modelo.Cuentas = [];
            }
        }
        return View(nameof(Index), modelo);
    }

    // 401: token inválido o sesión vencida (CU-10) → inicio de sesión.
    // Sin conexión: se muestra el error y se ofrece cerrar sesión para configurar el servidor.
    private async Task<IActionResult> ManejarErrorServicioAsync(ExcepcionClienteWS ex, OperacionesModelo modelo)
    {
        if (ex.EsErrorAutenticacion)
        {
            sesion.Limpiar();
            TempData["Aviso"] = ex.Message + " Inicie sesión nuevamente.";
            return RedirectToAction("Login", "Sesion");
        }
        modelo.SinConexion = ex.EsErrorConexion;
        modelo.CampoConError = ex.CodigoEstado == 404 ? "cuenta" : null;
        return await VistaConErrorAsync(modelo, ex.Message, modelo.CampoConError);
    }
}
