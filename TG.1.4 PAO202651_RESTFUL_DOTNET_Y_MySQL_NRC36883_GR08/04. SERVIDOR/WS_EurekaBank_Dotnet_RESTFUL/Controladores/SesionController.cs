using ec.edu.monster.Modelos.Dto;
using ec.edu.monster.Seguridad;
using ec.edu.monster.Util;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.Controladores;

/// <summary>
/// CU-01 Autenticarse, CU-02 Cambiar contraseña y CU-03 Cerrar sesión.
/// </summary>
[ApiController]
[Route("api/sesion")]
[Produces("application/json")]
public class SesionController(ServicioSeguridad seguridad) : ControllerBase
{
    /// <summary>POST /api/sesion/login → token de sesión (no requiere token).</summary>
    [HttpPost("login")]
    public async Task<ActionResult<RespuestaSesion>> IniciarSesion([FromBody] SolicitudLogin solicitud)
    {
        string ip = DireccionesRed.IpCliente(HttpContext.Connection.RemoteIpAddress);
        return Ok(await seguridad.IniciarSesionAsync(solicitud.Usuario, solicitud.Clave, ip));
    }

    /// <summary>PUT /api/sesion/clave</summary>
    [HttpPut("clave")]
    [Asegurado]
    public async Task<ActionResult<Respuesta>> CambiarClave([FromBody] SolicitudCambioClave solicitud)
    {
        return Ok(await seguridad.CambiarClaveAsync(FiltroAutenticacion.SesionDe(HttpContext),
                solicitud.ClaveActual, solicitud.ClaveNueva));
    }

    /// <summary>POST /api/sesion/logout</summary>
    [HttpPost("logout")]
    [Asegurado]
    public async Task<ActionResult<Respuesta>> CerrarSesion()
    {
        return Ok(await seguridad.CerrarSesionAsync(FiltroAutenticacion.SesionDe(HttpContext)));
    }
}
