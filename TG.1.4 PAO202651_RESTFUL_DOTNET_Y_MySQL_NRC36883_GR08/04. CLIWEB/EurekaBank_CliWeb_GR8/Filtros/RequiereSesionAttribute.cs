using ec.edu.monster.Sesion;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

namespace ec.edu.monster.Filtros;

/// <summary>
/// Protege las páginas que requieren haber iniciado sesión: sin token, redirige al inicio de sesión.
/// </summary>
public class RequiereSesionAttribute : ActionFilterAttribute
{
    public override void OnActionExecuting(ActionExecutingContext contexto)
    {
        var sesion = contexto.HttpContext.RequestServices.GetRequiredService<AlmacenSesion>();

        if (!sesion.HaySesionActiva)
        {
            if (contexto.Controller is Controller controlador)
            {
                controlador.TempData["Aviso"] = "Inicie sesión para continuar.";
            }
            contexto.Result = new RedirectToActionResult("Login", "Sesion", null);
        }
    }
}
