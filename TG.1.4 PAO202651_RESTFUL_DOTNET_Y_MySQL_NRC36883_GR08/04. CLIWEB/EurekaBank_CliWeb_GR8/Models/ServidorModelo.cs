namespace ec.edu.monster.Models;

public class ServidorModelo
{
    public string? Direccion { get; set; }
    public bool GuardarSinConexion { get; set; }

    // Datos solo para mostrar en la vista
    public string? UrlActual { get; set; }
    public string? Error { get; set; }
    public string? Exito { get; set; }
    public bool UsaVariableEntorno { get; set; }

    // Después de una prueba fallida se ofrece guardar la dirección de todas formas
    public bool OfrecerGuardarSinConexion { get; set; }
}
