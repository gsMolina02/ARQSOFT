using ec.edu.monster.Cliente;

namespace ec.edu.monster.Sesion;

/// <summary>
/// Datos de la sesión del navegador de cada usuario: token del servicio y nombre del empleado.
/// Viven en memoria del servidor web mientras la sesión está activa. Las cuentas y movimientos
/// no se guardan aquí: los guarda el servidor REST en MySQL.
/// </summary>
public class AlmacenSesion : IAlmacenToken
{
    private const string ClaveToken = "Token";
    private const string ClaveEmpleado = "Empleado";

    private readonly IHttpContextAccessor accesoContexto;

    public AlmacenSesion(IHttpContextAccessor accesoContexto)
    {
        this.accesoContexto = accesoContexto;
    }

    private ISession Sesion => accesoContexto.HttpContext?.Session
            ?? throw new InvalidOperationException("No hay una petición HTTP activa.");

    public string? Token
    {
        get => Sesion.GetString(ClaveToken);
        set => Guardar(ClaveToken, value);
    }

    public string? Empleado
    {
        get => Sesion.GetString(ClaveEmpleado);
        set => Guardar(ClaveEmpleado, value);
    }

    public bool HaySesionActiva => !string.IsNullOrWhiteSpace(Token);

    public void Limpiar()
    {
        Sesion.Clear();
    }

    private void Guardar(string clave, string? valor)
    {
        if (string.IsNullOrWhiteSpace(valor))
        {
            Sesion.Remove(clave);
        }
        else
        {
            Sesion.SetString(clave, valor);
        }
    }
}
