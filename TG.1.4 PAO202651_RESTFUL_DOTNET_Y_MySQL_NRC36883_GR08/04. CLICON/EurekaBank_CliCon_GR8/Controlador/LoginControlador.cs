using ec.edu.monster.Cliente;
using ec.edu.monster.Cliente.Dto;
using ec.edu.monster.Modelo;
using ec.edu.monster.Util;

namespace ec.edu.monster.Controlador;

/// <summary>
/// CU-01 Autenticarse, CU-02 Cambiar contraseña y CU-03 Cerrar sesión.
/// </summary>
public class LoginControlador
{
    public const string MensajeCredencialesVacias = "El usuario y la contraseña son obligatorios";

    private readonly IContratoWSEurekaBank clienteWs;

    /// <summary>Dirección del servidor REST que usa este controlador (se muestra en la barra de estado).</summary>
    public string UrlServidor { get; }

    public LoginControlador(ClienteWSEurekaBank clienteWs) : this(clienteWs, clienteWs.BaseUrl)
    {
    }

    public LoginControlador(IContratoWSEurekaBank clienteWs, string urlServidor)
    {
        this.clienteWs = clienteWs;
        UrlServidor = urlServidor;
    }

    public string? Empleado => clienteWs.Empleado;

    public bool HaySesionActiva => clienteWs.HaySesionActiva;

    public async Task<RespuestaSesion> IniciarSesionAsync(LoginModelo modelo)
    {
        if (string.IsNullOrWhiteSpace(modelo.Usuario) || string.IsNullOrEmpty(modelo.Clave))
        {
            throw new ArgumentException(MensajeCredencialesVacias);
        }
        return await clienteWs.IniciarSesionAsync(modelo.Usuario.Trim(), modelo.Clave);
    }

    public async Task<string> CambiarClaveAsync(string? claveActual, string? claveNueva, string? confirmacion)
    {
        ValidacionUtil.ValidarCambioClave(claveActual, claveNueva, confirmacion);
        return await clienteWs.CambiarClaveAsync(claveActual!, claveNueva!);
    }

    public Task<string> CerrarSesionAsync() => clienteWs.CerrarSesionAsync();

    public CuentaControlador CrearControladorCuenta() => new(clienteWs);
}
