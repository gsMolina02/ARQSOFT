namespace ec.edu.monster.Cliente;

/// <summary>
/// Lugar donde se guarda la sesión con el servicio. En la aplicación web es la sesión del navegador
/// de cada usuario (así no se mezclan los tokens de distintas personas); en las pruebas, memoria.
/// </summary>
public interface IAlmacenToken
{
    string? Token { get; set; }

    /// <summary>Nombre del empleado que inició sesión (se muestra en el encabezado).</summary>
    string? Empleado { get; set; }
}
