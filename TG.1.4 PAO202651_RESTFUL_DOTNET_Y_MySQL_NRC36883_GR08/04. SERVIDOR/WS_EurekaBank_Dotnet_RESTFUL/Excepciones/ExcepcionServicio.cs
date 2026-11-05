namespace ec.edu.monster.Excepciones;

/// <summary>
/// Error esperado del servicio: lleva el código HTTP con el que se responde al cliente
/// (400 validación, 401 sesión o credenciales, 404 cuenta inexistente, 409 regla de negocio).
/// </summary>
public abstract class ExcepcionServicio(int codigo, string mensaje) : Exception(mensaje)
{
    public int Codigo { get; } = codigo;
}

/// <summary>Datos con formato incorrecto (código 400).</summary>
public class ExcepcionValidacion(string mensaje) : ExcepcionServicio(StatusCodes.Status400BadRequest, mensaje);

/// <summary>Credenciales incorrectas o token ausente, inválido o vencido (código 401).</summary>
public class ExcepcionAutenticacion(string mensaje) : ExcepcionServicio(StatusCodes.Status401Unauthorized, mensaje);

/// <summary>La cuenta solicitada no existe (código 404).</summary>
public class ExcepcionNoEncontrado(string mensaje) : ExcepcionServicio(StatusCodes.Status404NotFound, mensaje);

/// <summary>La operación incumple una regla de negocio: cuenta no activa, saldo insuficiente... (código 409).</summary>
public class ExcepcionReglaNegocio(string mensaje) : ExcepcionServicio(StatusCodes.Status409Conflict, mensaje);
