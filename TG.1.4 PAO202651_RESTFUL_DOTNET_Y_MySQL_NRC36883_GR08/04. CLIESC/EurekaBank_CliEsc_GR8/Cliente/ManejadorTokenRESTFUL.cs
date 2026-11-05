using System.Net.Http.Headers;

namespace ec.edu.monster.Cliente;

/// <summary>
/// Guarda el token de la sesión y lo adjunta a cada petición protegida
/// en el encabezado "Authorization: Bearer".
/// </summary>
public class ManejadorTokenRESTFUL
{
    private string? token;

    public void GuardarToken(string token)
    {
        this.token = token;
    }

    public string? ObtenerToken() => token;

    public bool HaySesionActiva => !string.IsNullOrWhiteSpace(token);

    public void LimpiarToken()
    {
        token = null;
    }

    public void AdjuntarToken(HttpRequestMessage peticion)
    {
        if (!HaySesionActiva)
        {
            throw new ExcepcionClienteWS(401, "No hay sesión activa. Debe autenticarse primero.");
        }
        peticion.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
    }
}
