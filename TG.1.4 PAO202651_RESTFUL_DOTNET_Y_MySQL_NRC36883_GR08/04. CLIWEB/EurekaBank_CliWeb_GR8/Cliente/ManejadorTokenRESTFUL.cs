using System.Net.Http.Headers;

namespace ec.edu.monster.Cliente;

/// <summary>
/// Guarda el token de la sesión (en la sesión del navegador) y lo adjunta a cada petición protegida
/// en el encabezado "Authorization: Bearer".
/// </summary>
public class ManejadorTokenRESTFUL
{
    private readonly IAlmacenToken almacen;

    public ManejadorTokenRESTFUL(IAlmacenToken almacen)
    {
        this.almacen = almacen;
    }

    public void GuardarSesion(string token, string empleado)
    {
        almacen.Token = token;
        almacen.Empleado = empleado;
    }

    public string? ObtenerToken() => almacen.Token;

    public string? Empleado => almacen.Empleado;

    public bool HaySesionActiva => !string.IsNullOrWhiteSpace(almacen.Token);

    public void LimpiarToken()
    {
        almacen.Token = null;
        almacen.Empleado = null;
    }

    public void AdjuntarToken(HttpRequestMessage peticion)
    {
        if (!HaySesionActiva)
        {
            throw new ExcepcionClienteWS(401, "No hay sesión activa. Debe autenticarse primero.");
        }
        peticion.Headers.Authorization = new AuthenticationHeaderValue("Bearer", almacen.Token);
    }
}
