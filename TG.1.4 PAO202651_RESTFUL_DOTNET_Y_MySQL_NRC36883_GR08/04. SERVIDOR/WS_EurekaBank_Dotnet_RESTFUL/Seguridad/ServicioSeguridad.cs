using ec.edu.monster.Datos;
using ec.edu.monster.Excepciones;
using ec.edu.monster.Modelos;
using ec.edu.monster.Modelos.Dto;
using ec.edu.monster.Util;

namespace ec.edu.monster.Seguridad;

/// <summary>
/// Autenticación, sesiones y contraseñas (CU-01, CU-02, CU-03, CU-08 y CU-10).
/// Las sesiones viven en la base de datos: el cambio de contraseña y el cierre de sesión se aplican
/// de inmediato en todos los clientes y sobreviven a un reinicio del servidor.
/// </summary>
public class ServicioSeguridad(IFabricaConexion fabrica, IRepositorioUsuario usuarios, IRepositorioSesion sesiones,
        TimeProvider reloj)
{
    public const int MinutosVigencia = 30;
    public const int LongitudMinimaClave = 6;

    public const string MensajeCredencialesIncorrectas = "Usuario o contraseña incorrectos";
    public const string MensajeCredencialesVacias = "El usuario y la contraseña son obligatorios";
    public const string MensajeSinToken = "Acceso denegado: no se encontró el token. Inicie sesión primero.";
    public const string MensajeTokenInvalido = "Acceso denegado: token inválido.";
    public const string MensajeSesionExpirada = "La sesión ha expirado. Inicie sesión nuevamente.";
    public const string MensajeClaveActualIncorrecta = "La contraseña actual es incorrecta";
    public const string MensajeClaveNuevaInvalida =
            "La nueva contraseña debe tener al menos 6 caracteres y ser distinta de la actual";

    public async Task<RespuestaSesion> IniciarSesionAsync(string? nombreUsuario, string? clave, string ip)
    {
        if (string.IsNullOrWhiteSpace(nombreUsuario) || string.IsNullOrEmpty(clave))
        {
            throw new ExcepcionValidacion(MensajeCredencialesVacias);
        }

        await using IUnidadTrabajo unidad = await fabrica.AbrirAsync();
        Usuario? usuario = await usuarios.BuscarAsync(unidad, nombreUsuario.Trim());
        // El mismo mensaje para usuario inexistente, inactivo o clave errada: no revela cuál falló
        if (usuario is null || !usuario.EstaActivo() || !usuario.VerificarClave(clave))
        {
            throw new ExcepcionAutenticacion(MensajeCredencialesIncorrectas);
        }

        DateTime ahora = reloj.AhoraLocal();
        var sesion = new Sesion
        {
            Token = Guid.NewGuid().ToString(),
            NombreUsuario = usuario.NombreUsuario,
            FechaIngreso = ahora,
            UltimaActividad = ahora,
            Ip = ip
        };
        await sesiones.RegistrarAsync(unidad, sesion);

        return new RespuestaSesion
        {
            Codigo = StatusCodes.Status200OK,
            Mensaje = $"Bienvenido, {usuario.NombreEmpleado}",
            Token = sesion.Token,
            Usuario = usuario.NombreUsuario,
            Empleado = usuario.NombreEmpleado,
            MinutosVigencia = MinutosVigencia
        };
    }

    /// <summary>
    /// CU-08: el token debe pertenecer a una sesión abierta con menos de 30 minutos sin uso.
    /// Si venció, la sesión queda cerrada (CU-10). Si es válido, se renueva la última actividad.
    /// </summary>
    public async Task<SesionActiva> ValidarTokenAsync(string? token)
    {
        if (string.IsNullOrWhiteSpace(token))
        {
            throw new ExcepcionAutenticacion(MensajeSinToken);
        }

        await using IUnidadTrabajo unidad = await fabrica.AbrirAsync();
        SesionActiva sesion = await sesiones.BuscarAbiertaAsync(unidad, token.Trim())
                ?? throw new ExcepcionAutenticacion(MensajeTokenInvalido);

        DateTime ahora = reloj.AhoraLocal();
        if (ahora - sesion.UltimaActividad > TimeSpan.FromMinutes(MinutosVigencia))
        {
            await sesiones.CerrarAsync(unidad, sesion.Token, ahora);
            throw new ExcepcionAutenticacion(MensajeSesionExpirada);
        }
        await sesiones.ActualizarActividadAsync(unidad, sesion.Token, ahora);
        return sesion;
    }

    public async Task<Respuesta> CambiarClaveAsync(SesionActiva sesion, string? claveActual, string? claveNueva)
    {
        await using IUnidadTrabajo unidad = await fabrica.AbrirAsync();
        Usuario usuario = await usuarios.BuscarAsync(unidad, sesion.NombreUsuario)
                ?? throw new ExcepcionAutenticacion(MensajeTokenInvalido);

        if (string.IsNullOrEmpty(claveActual) || !usuario.VerificarClave(claveActual))
        {
            throw new ExcepcionValidacion(MensajeClaveActualIncorrecta);
        }
        if (claveNueva is null || claveNueva.Length < LongitudMinimaClave || claveNueva == claveActual)
        {
            throw new ExcepcionValidacion(MensajeClaveNuevaInvalida);
        }

        await usuarios.ActualizarClaveAsync(unidad, usuario.NombreUsuario, Usuario.CalcularHash(claveNueva));
        return new Respuesta { Codigo = StatusCodes.Status200OK, Mensaje = "Contraseña actualizada correctamente" };
    }

    public async Task<Respuesta> CerrarSesionAsync(SesionActiva sesion)
    {
        await using IUnidadTrabajo unidad = await fabrica.AbrirAsync();
        await sesiones.CerrarAsync(unidad, sesion.Token, reloj.AhoraLocal());
        return new Respuesta { Codigo = StatusCodes.Status200OK, Mensaje = "Sesión cerrada correctamente" };
    }
}
