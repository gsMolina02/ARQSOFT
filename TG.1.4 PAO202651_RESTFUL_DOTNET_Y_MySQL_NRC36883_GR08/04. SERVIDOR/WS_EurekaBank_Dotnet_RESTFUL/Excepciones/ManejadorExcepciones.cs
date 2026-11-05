using System.Data.Common;
using ec.edu.monster.Modelos.Dto;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Mvc.ModelBinding;

namespace ec.edu.monster.Excepciones;

/// <summary>
/// Traduce cualquier excepción al JSON {"codigo": ..., "mensaje": "..."} con su código HTTP.
/// Los clientes siempre reciben el mismo formato, sea éxito o error.
/// </summary>
public class ManejadorExcepciones(ILogger<ManejadorExcepciones> registro) : IExceptionHandler
{
    public const string MensajeBaseDatos = "El servicio no está disponible, intente más tarde";
    public const string MensajeInterno = "Error interno del servidor";
    public const string MensajeJsonInvalido = "El cuerpo de la solicitud no es un JSON válido";

    public async ValueTask<bool> TryHandleAsync(HttpContext contexto, Exception excepcion, CancellationToken cancelacion)
    {
        Respuesta respuesta = Traducir(excepcion);
        if (respuesta.Codigo >= 500)
        {
            registro.LogError(excepcion, "Error al atender {Metodo} {Ruta}", contexto.Request.Method, contexto.Request.Path);
        }
        contexto.Response.StatusCode = respuesta.Codigo;
        await contexto.Response.WriteAsJsonAsync(respuesta, cancelacion);
        return true;
    }

    public static Respuesta Traducir(Exception excepcion) => excepcion switch
    {
        ExcepcionServicio propia => new Respuesta { Codigo = propia.Codigo, Mensaje = propia.Message },
        // MySQL apagado, credenciales de la base incorrectas o caída de la conexión
        DbException => new Respuesta { Codigo = StatusCodes.Status500InternalServerError, Mensaje = MensajeBaseDatos },
        _ => new Respuesta { Codigo = StatusCodes.Status500InternalServerError, Mensaje = MensajeInterno }
    };

    /// <summary>
    /// Mensaje para un cuerpo que no se pudo leer: JSON mal formado o un importe que no es número ("abc").
    /// </summary>
    public static string MensajeModeloInvalido(ModelStateDictionary estado)
    {
        bool importeInvalido = estado.Where(campo => campo.Value?.Errors.Count > 0)
                .Any(campo => campo.Key.Contains("importe", StringComparison.OrdinalIgnoreCase));
        return importeInvalido ? Servicios.ValidadorDatos.MensajeImporte : MensajeJsonInvalido;
    }

    /// <summary>
    /// Respuestas sin cuerpo que genera ASP.NET Core (ruta inexistente, método no permitido...).
    /// </summary>
    public static async Task EscribirCodigoEstadoAsync(StatusCodeContext contexto)
    {
        HttpResponse respuesta = contexto.HttpContext.Response;
        await respuesta.WriteAsJsonAsync(new Respuesta { Codigo = respuesta.StatusCode, Mensaje = MensajeHttp(respuesta.StatusCode) });
    }

    public static string MensajeHttp(int estado) => estado switch
    {
        400 => "Solicitud inválida",
        404 => "Recurso no encontrado",
        405 => "Método HTTP no permitido",
        406 => "Formato de respuesta no soportado, use application/json",
        415 => "Tipo de contenido no soportado, use application/json",
        _ => estado >= 500 ? MensajeInterno : "Error en la solicitud"
    };
}
