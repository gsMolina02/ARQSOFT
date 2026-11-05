namespace ec.edu.monster.Models;

public class LoginModelo
{
    public string? Usuario { get; set; }
    public string? Contrasenia { get; set; }

    // Datos solo para mostrar en la vista
    public string? Error { get; set; }
    public string? UrlServidor { get; set; }

    public LoginModelo()
    {
    }

    public LoginModelo(string? usuario, string? contrasenia)
    {
        Usuario = usuario;
        Contrasenia = contrasenia;
    }
}
