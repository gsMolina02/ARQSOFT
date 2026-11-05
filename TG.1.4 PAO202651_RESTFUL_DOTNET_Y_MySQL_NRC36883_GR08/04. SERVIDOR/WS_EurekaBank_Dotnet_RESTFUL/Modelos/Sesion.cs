namespace ec.edu.monster.Modelos;

/// <summary>
/// Registro de una sesión iniciada por un usuario (tabla sesion).
/// </summary>
public class Sesion
{
    public required string Token { get; init; }
    public required string NombreUsuario { get; init; }
    public DateTime FechaIngreso { get; init; }
    public DateTime UltimaActividad { get; init; }
    public DateTime? FechaSalida { get; init; }
    public required string Ip { get; init; }
}

/// <summary>
/// Sesión abierta y vigente que acompaña a cada petición autenticada: identifica al empleado
/// que se registra en los movimientos.
/// </summary>
public class SesionActiva
{
    public required string Token { get; init; }
    public required string NombreUsuario { get; init; }
    public required string CodigoEmpleado { get; init; }
    public required string NombreEmpleado { get; init; }
    public DateTime UltimaActividad { get; init; }
}
