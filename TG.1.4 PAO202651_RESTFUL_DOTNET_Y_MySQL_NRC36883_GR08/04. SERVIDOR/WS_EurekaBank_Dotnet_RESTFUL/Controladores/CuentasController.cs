using ec.edu.monster.Modelos.Dto;
using ec.edu.monster.Seguridad;
using ec.edu.monster.Servicios;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.Controladores;

/// <summary>
/// CU-04 Consultar movimientos, CU-05 Registrar depósito y CU-06 Registrar retiro,
/// más la lista de cuentas que usan los clientes para elegir el número.
/// </summary>
[ApiController]
[Route("api/cuentas")]
[Produces("application/json")]
[Asegurado]
public class CuentasController(ServicioCuenta servicioCuenta, ServicioTransaccion servicioTransaccion) : ControllerBase
{
    /// <summary>GET /api/cuentas</summary>
    [HttpGet]
    public async Task<ActionResult<RespuestaCuentas>> ListarCuentas()
    {
        return Ok(await servicioCuenta.ListarCuentasAsync());
    }

    /// <summary>GET /api/cuentas/{cuenta}/movimientos</summary>
    [HttpGet("{cuenta}/movimientos")]
    public async Task<ActionResult<RespuestaMovimientos>> ConsultarMovimientos(string cuenta)
    {
        return Ok(await servicioCuenta.ConsultarMovimientosAsync(cuenta));
    }

    /// <summary>POST /api/cuentas/{cuenta}/depositos con {"importe": 100.00}</summary>
    [HttpPost("{cuenta}/depositos")]
    public async Task<ActionResult<RespuestaTransaccion>> RegistrarDeposito(string cuenta, [FromBody] SolicitudImporte solicitud)
    {
        RespuestaTransaccion respuesta = await servicioTransaccion.RegistrarDepositoAsync(cuenta, solicitud.Importe,
                FiltroAutenticacion.SesionDe(HttpContext));
        return StatusCode(StatusCodes.Status201Created, respuesta);
    }

    /// <summary>POST /api/cuentas/{cuenta}/retiros con {"importe": 50.00}</summary>
    [HttpPost("{cuenta}/retiros")]
    public async Task<ActionResult<RespuestaTransaccion>> RegistrarRetiro(string cuenta, [FromBody] SolicitudImporte solicitud)
    {
        RespuestaTransaccion respuesta = await servicioTransaccion.RegistrarRetiroAsync(cuenta, solicitud.Importe,
                FiltroAutenticacion.SesionDe(HttpContext));
        return StatusCode(StatusCodes.Status201Created, respuesta);
    }
}
