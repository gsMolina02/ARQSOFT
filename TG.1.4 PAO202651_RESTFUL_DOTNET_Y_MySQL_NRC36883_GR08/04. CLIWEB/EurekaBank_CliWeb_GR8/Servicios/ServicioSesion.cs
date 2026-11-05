using ec.edu.monster.Cliente;
using ec.edu.monster.Cliente.Dto;
using ec.edu.monster.Models;
using ec.edu.monster.Util;

namespace ec.edu.monster.Servicios;

/// <summary>
/// CU-01 Autenticarse, CU-02 Cambiar contraseña y CU-03 Cerrar sesión: valida los datos y llama al servicio RESTful.
/// </summary>
public class ServicioSesion
{
    public const string MensajeCredencialesVacias = "El usuario y la contraseña son obligatorios";

    private readonly IContratoWSEurekaBank clienteWs;

    public ServicioSesion(IContratoWSEurekaBank clienteWs)
    {
        this.clienteWs = clienteWs;
    }

    public async Task<RespuestaSesion> IniciarSesionAsync(LoginModelo modelo)
    {
        if (string.IsNullOrWhiteSpace(modelo.Usuario) || string.IsNullOrEmpty(modelo.Contrasenia))
        {
            throw new ArgumentException(MensajeCredencialesVacias);
        }
        return await clienteWs.IniciarSesionAsync(modelo.Usuario.Trim(), modelo.Contrasenia);
    }

    public async Task<string> CambiarClaveAsync(CambioContraseniaModelo modelo)
    {
        ValidacionUtil.ValidarCambioClave(modelo.ContraseniaActual, modelo.ContraseniaNueva, modelo.Confirmacion);
        return await clienteWs.CambiarClaveAsync(modelo.ContraseniaActual!, modelo.ContraseniaNueva!);
    }

    /// <returns>false si no se pudo avisar al servidor (sin conexión); la sesión local se borra igual.</returns>
    public async Task<bool> CerrarSesionAsync()
    {
        try
        {
            await clienteWs.CerrarSesionAsync();
            return true;
        }
        catch (ExcepcionClienteWS ex)
        {
            // Si la sesión ya había vencido en el servidor no hay nada más que cerrar
            return !ex.EsErrorConexion;
        }
    }
}
