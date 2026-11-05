using ec.edu.monster.Modelos.Dto;
using ec.edu.monster.Seguridad;
using ec.edu.monster.Servicios;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.Controladores;

/// <summary>
/// CU-07 Registrar transferencia.
/// </summary>
[ApiController]
[Route("api/transferencias")]
[Produces("application/json")]
[Asegurado]
public class TransferenciasController(ServicioTransaccion servicioTransaccion) : ControllerBase
{
    /// <summary>POST /api/transferencias con {"cuentaOrigen": "...", "cuentaDestino": "...", "importe": 25.00}</summary>
    [HttpPost]
    public async Task<ActionResult<RespuestaTransaccion>> RegistrarTransferencia([FromBody] SolicitudTransferencia solicitud)
    {
        RespuestaTransaccion respuesta = await servicioTransaccion.RegistrarTransferenciaAsync(solicitud.CuentaOrigen,
                solicitud.CuentaDestino, solicitud.Importe, FiltroAutenticacion.SesionDe(HttpContext));
        return StatusCode(StatusCodes.Status201Created, respuesta);
    }
}
