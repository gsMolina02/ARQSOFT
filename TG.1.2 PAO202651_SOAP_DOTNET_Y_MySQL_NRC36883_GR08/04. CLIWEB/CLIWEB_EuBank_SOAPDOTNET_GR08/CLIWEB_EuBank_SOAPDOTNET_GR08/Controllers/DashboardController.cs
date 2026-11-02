using CLIWEB_EuBank_SOAPDOTNET_GR08.Services;
using Microsoft.AspNetCore.Mvc;
using MovimientoServiceRef;

namespace CLIWEB_EuBank_SOAPDOTNET_GR08.Controllers
{
    public class DashboardController : Controller
    {
        private readonly IEurekaBankSoapService _eurekaBankService;

        public DashboardController(IEurekaBankSoapService eurekaBankService)
        {
            _eurekaBankService = eurekaBankService;
        }

        public IActionResult Index()
        {
            if (HttpContext.Session.GetString("Usuario") == null)
            {
                return RedirectToAction("Login", "Auth");
            }

            ViewBag.Usuario = HttpContext.Session.GetString("Usuario");
            ViewBag.NombreCompleto = HttpContext.Session.GetString("NombreCompleto") ?? HttpContext.Session.GetString("Usuario");
            ViewBag.CuentaPrincipal = HttpContext.Session.GetString("CuentaPrincipal") ?? "00200001";
            ViewBag.CuentasUsuario = HttpContext.Session.GetString("CuentasUsuario") ?? "";

            return View();
        }

        [HttpGet]
        public async Task<IActionResult> TestService()
        {
            try
            {
                await _eurekaBankService.GetTiposMovimientoAsync();
                return Json(new { status = "ok", message = "Servicio SOAP disponible" });
            }
            catch (Exception ex)
            {
                Response.StatusCode = StatusCodes.Status503ServiceUnavailable;
                return Json(new { status = "error", message = $"No se pudo conectar con el servicio SOAP: {ex.Message}" });
            }
        }

        [HttpGet]
        public async Task<IActionResult> GetCuentas()
        {
            try
            {
                var cuentas = await _eurekaBankService.GetCuentasAsync();
                return Json(cuentas.Select(c => new
                {
                    codigo = c.Codigo,
                    nombreCliente = c.NombreCliente,
                    emailCliente = c.EmailCliente,
                    telefonoCliente = c.TelefonoCliente,
                    moneda = c.Moneda,
                    saldo = c.Saldo,
                    estado = c.Estado
                }));
            }
            catch (Exception ex)
            {
                Response.StatusCode = StatusCodes.Status503ServiceUnavailable;
                return Json(new { error = $"No se pudieron obtener las cuentas: {ex.Message}" });
            }
        }

        [HttpGet]
        public async Task<IActionResult> GetTiposMovimiento()
        {
            try
            {
                var tipos = await _eurekaBankService.GetTiposMovimientoAsync();
                return Json(tipos);
            }
            catch (Exception ex)
            {
                Response.StatusCode = StatusCodes.Status503ServiceUnavailable;
                return Json(new { error = $"No se pudieron obtener los tipos de movimiento: {ex.Message}" });
            }
        }

        [HttpGet]
        public async Task<IActionResult> GetMovimientos(string cuenta)
        {
            if (string.IsNullOrWhiteSpace(cuenta))
            {
                return Json(new { error = "Debe especificar una cuenta" });
            }

            try
            {
                var movimientos = await _eurekaBankService.GetMovimientosAsync(cuenta);
                return Json(MapMovimientos(movimientos));
            }
            catch (Exception ex)
            {
                Response.StatusCode = StatusCodes.Status503ServiceUnavailable;
                return Json(new { error = $"No se pudieron obtener los movimientos: {ex.Message}" });
            }
        }

        [HttpGet]
        public async Task<IActionResult> GetTodosMovimientos()
        {
            try
            {
                var movimientos = await _eurekaBankService.GetTodosMovimientosAsync();
                return Json(MapMovimientos(movimientos));
            }
            catch (Exception ex)
            {
                Response.StatusCode = StatusCodes.Status503ServiceUnavailable;
                return Json(new { error = $"No se pudieron obtener los movimientos: {ex.Message}" });
            }
        }

        [HttpPost]
        public async Task<IActionResult> CrearMovimiento([FromBody] CrearMovimientoRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.Tipo) || string.IsNullOrWhiteSpace(request.CuentaOrigen) || request.Importe <= 0)
            {
                return Json(new { success = false, message = "Por favor complete todos los campos requeridos" });
            }

            if (request.Tipo == "TRANSFERENCIA" && string.IsNullOrWhiteSpace(request.CuentaDestino))
            {
                return Json(new { success = false, message = "Para transferencias se requiere la cuenta destino" });
            }

            try
            {
                var result = await _eurekaBankService.ProcesarMovimientoAsync(
                    request.Tipo,
                    request.CuentaOrigen,
                    request.CuentaDestino,
                    request.Importe);

                return Json(new { success = result.Codigo == 1, message = result.Mensaje });
            }
            catch (Exception ex)
            {
                Response.StatusCode = StatusCodes.Status503ServiceUnavailable;
                return Json(new { success = false, message = $"No se pudo procesar el movimiento: {ex.Message}" });
            }
        }

        public class CrearMovimientoRequest
        {
            public string Tipo { get; set; } = string.Empty;
            public string CuentaOrigen { get; set; } = string.Empty;
            public string CuentaDestino { get; set; } = string.Empty;
            public decimal Importe { get; set; }
        }

        private static IEnumerable<object> MapMovimientos(IEnumerable<MovimientoData> movimientos)
        {
            return movimientos.Select(m => new
            {
                codigoCuenta = m.CodigoCuenta,
                nombreCliente = m.NombreCliente,
                numero = m.Numero,
                fecha = m.Fecha.ToString("yyyy-MM-ddTHH:mm:ss"),
                tipo = m.Tipo,
                referencia = m.Referencia,
                importe = m.Importe,
                saldoActual = m.SaldoActual
            });
        }
    }
}
