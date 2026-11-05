using System.Diagnostics;
using ec.edu.monster.Cliente;
using ec.edu.monster.Filtros;
using ec.edu.monster.Models;
using ec.edu.monster.Servicios;
using ec.edu.monster.Sesion;
using ec.edu.monster.Util;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.Controllers;

/// <summary>
/// Inicio de sesión, configuración del servidor, cambio de contraseña y cierre de sesión.
/// </summary>
public class SesionController : Controller
{
    private readonly ServicioSesion servicioSesion;
    private readonly ServicioServidor servicioServidor;
    private readonly AlmacenSesion sesion;
    private readonly string urlServidor;

    public SesionController(ServicioSesion servicioSesion, ServicioServidor servicioServidor, AlmacenSesion sesion)
    {
        this.servicioSesion = servicioSesion;
        this.servicioServidor = servicioServidor;
        this.sesion = sesion;
        urlServidor = servicioServidor.UrlActual;
    }

    [HttpGet]
    public IActionResult Login()
    {
        if (sesion.HaySesionActiva)
        {
            return RedirectToAction("Index", "Operaciones");
        }
        return View(new LoginModelo { UrlServidor = urlServidor });
    }

    [HttpPost]
    public async Task<IActionResult> Login(LoginModelo modelo)
    {
        try
        {
            await servicioSesion.IniciarSesionAsync(modelo);
            return RedirectToAction("Index", "Operaciones");
        }
        catch (Exception ex) when (ex is ExcepcionClienteWS or ArgumentException)
        {
            modelo.Error = ex.Message;
        }

        // Por seguridad, la contraseña no se devuelve a la página
        modelo.Contrasenia = null;
        modelo.UrlServidor = urlServidor;
        return View(modelo);
    }

    // La dirección del servidor solo se cambia sin sesión: el token es válido únicamente en el servidor que lo emitió
    [HttpGet]
    public IActionResult Servidor()
    {
        if (sesion.HaySesionActiva)
        {
            return RedirectToAction("Index", "Operaciones");
        }
        return View(CrearModeloServidor(servicioServidor.UrlActual));
    }

    [HttpPost]
    public async Task<IActionResult> ProbarServidor(ServidorModelo modelo)
    {
        if (sesion.HaySesionActiva)
        {
            return RedirectToAction("Index", "Operaciones");
        }
        try
        {
            string url = servicioServidor.CompletarUrl(modelo.Direccion);
            ResultadoConexion resultado = await servicioServidor.ProbarAsync(url);
            ServidorModelo vista = CrearModeloServidor(url);
            if (resultado.Exitosa)
            {
                vista.Exito = resultado.Mensaje;
            }
            else
            {
                vista.Error = resultado.Mensaje;
            }
            return View(nameof(Servidor), vista);
        }
        catch (ArgumentException ex)
        {
            ServidorModelo vista = CrearModeloServidor(modelo.Direccion);
            vista.Error = ex.Message;
            return View(nameof(Servidor), vista);
        }
    }

    [HttpPost]
    public async Task<IActionResult> GuardarServidor(ServidorModelo modelo)
    {
        if (sesion.HaySesionActiva)
        {
            return RedirectToAction("Index", "Operaciones");
        }
        try
        {
            string url = servicioServidor.CompletarUrl(modelo.Direccion);
            ResultadoConexion resultado = await servicioServidor.ProbarAsync(url);
            if (!resultado.Exitosa && !modelo.GuardarSinConexion)
            {
                ServidorModelo vista = CrearModeloServidor(url);
                vista.Error = resultado.Mensaje;
                vista.OfrecerGuardarSinConexion = true;
                return View(nameof(Servidor), vista);
            }

            servicioServidor.Guardar(url);
            TempData["Exito"] = "Servidor guardado: " + url;
            return RedirectToAction(nameof(Login));
        }
        catch (ArgumentException ex)
        {
            ServidorModelo vista = CrearModeloServidor(modelo.Direccion);
            vista.Error = ex.Message;
            return View(nameof(Servidor), vista);
        }
    }

    /// <param name="configurar">true cuando se perdió la conexión: después de cerrar se abre "Configurar servidor".</param>
    [HttpPost]
    public async Task<IActionResult> CerrarSesion(bool configurar = false)
    {
        bool notificado = await servicioSesion.CerrarSesionAsync();
        sesion.Limpiar();

        if (notificado)
        {
            TempData["Exito"] = "Sesión cerrada correctamente.";
        }
        else
        {
            TempData["Aviso"] = "Sesión cerrada en este equipo, pero no se pudo avisar al servidor; vencerá sola tras 30 minutos sin uso.";
        }
        return RedirectToAction(configurar ? nameof(Servidor) : nameof(Login));
    }

    [HttpGet]
    [RequiereSesion]
    public IActionResult CambiarContrasenia()
    {
        return View(new CambioContraseniaModelo());
    }

    [HttpPost]
    [RequiereSesion]
    public async Task<IActionResult> CambiarContrasenia(CambioContraseniaModelo modelo)
    {
        try
        {
            string mensaje = await servicioSesion.CambiarClaveAsync(modelo);
            TempData["Exito"] = mensaje + ". Use la nueva contraseña la próxima vez que inicie sesión.";
            return RedirectToAction("Index", "Operaciones");
        }
        catch (ExcepcionClienteWS ex) when (ex.EsErrorAutenticacion)
        {
            sesion.Limpiar();
            TempData["Aviso"] = ex.Message + " Inicie sesión nuevamente.";
            return RedirectToAction(nameof(Login));
        }
        catch (Exception ex) when (ex is ExcepcionClienteWS or ArgumentException)
        {
            return VistaConError(ex.Message);
        }
    }

    [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
    public IActionResult Error()
    {
        return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
    }

    private ServidorModelo CrearModeloServidor(string? direccion)
    {
        return new ServidorModelo
        {
            Direccion = direccion,
            UrlActual = servicioServidor.UrlActual,
            UsaVariableEntorno = servicioServidor.Origen == OrigenUrl.VariableEntorno
        };
    }

    // Las contraseñas no se devuelven a la página: el usuario las vuelve a escribir
    private ViewResult VistaConError(string mensaje)
    {
        return View(nameof(CambiarContrasenia), new CambioContraseniaModelo { Error = mensaje });
    }
}
