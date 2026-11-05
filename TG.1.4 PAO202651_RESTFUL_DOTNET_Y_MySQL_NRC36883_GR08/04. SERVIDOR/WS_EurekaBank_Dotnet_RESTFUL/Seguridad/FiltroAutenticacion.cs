using ec.edu.monster.Excepciones;
using ec.edu.monster.Modelos;
using ec.edu.monster.Modelos.Dto;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

namespace ec.edu.monster.Seguridad;

/// <summary>
/// Exige el header "Authorization: Bearer &lt;token&gt;" en las acciones marcadas con [Asegurado].
/// Corre antes de leer el cuerpo de la petición: sin sesión válida no se valida ni se toca ninguna cuenta.
/// Si falla, el propio filtro responde 401 sin ejecutar la acción.
/// </summary>
public class FiltroAutenticacion(ServicioSeguridad seguridad) : IAsyncAuthorizationFilter
{
    private const string PrefijoBearer = "Bearer ";
    private const string ClaveSesion = "EurekaBank.SesionActiva";

    public async Task OnAuthorizationAsync(AuthorizationFilterContext contexto)
    {
        string? encabezado = contexto.HttpContext.Request.Headers.Authorization;
        if (encabezado is null || !encabezado.StartsWith(PrefijoBearer, StringComparison.OrdinalIgnoreCase))
        {
            contexto.Result = Rechazo(ServicioSeguridad.MensajeSinToken);
            return;
        }

        try
        {
            SesionActiva sesion = await seguridad.ValidarTokenAsync(encabezado[PrefijoBearer.Length..]);
            contexto.HttpContext.Items[ClaveSesion] = sesion;
        }
        catch (ExcepcionAutenticacion ex)
        {
            // Token inválido o vencido: se responde aquí mismo para que la excepción no salga al pipeline
            // (Visual Studio la detendría como "no controlada por el usuario" al depurar)
            contexto.Result = Rechazo(ex.Message);
        }
    }

    /// <summary>Respuesta 401 con el mismo JSON {"codigo", "mensaje"} que arma el ManejadorExcepciones.</summary>
    private static ObjectResult Rechazo(string mensaje) =>
            new(new Respuesta { Codigo = StatusCodes.Status401Unauthorized, Mensaje = mensaje })
            {
                StatusCode = StatusCodes.Status401Unauthorized
            };

    /// <summary>Sesión que validó el filtro para la petición actual.</summary>
    public static SesionActiva SesionDe(HttpContext contexto) =>
            contexto.Items[ClaveSesion] as SesionActiva
            ?? throw new InvalidOperationException("La acción no está marcada con [Asegurado].");
}

/// <summary>
/// Marca las acciones que requieren una sesión vigente.
/// </summary>
public class AseguradoAttribute : TypeFilterAttribute
{
    public AseguradoAttribute() : base(typeof(FiltroAutenticacion))
    {
    }
}
