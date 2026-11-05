using ec.edu.monster.Excepciones;
using ec.edu.monster.Modelos;
using ec.edu.monster.Modelos.Dto;
using ec.edu.monster.Pruebas.Falsos;
using ec.edu.monster.Seguridad;
using Xunit;

namespace ec.edu.monster.Pruebas;

public class ServicioSeguridadPruebas
{
    private readonly RepositorioUsuarioFalso usuarios = new();
    private readonly RepositorioSesionFalso sesiones = new();
    private readonly RelojFalso reloj = new(DatosPrueba.Ahora);
    private readonly ServicioSeguridad servicio;

    public ServicioSeguridadPruebas()
    {
        servicio = new ServicioSeguridad(new FabricaFalsa(), usuarios, sesiones, reloj);
        usuarios.Usuarios["Monster"] = DatosPrueba.UsuarioMonster();
    }

    [Fact]
    public void DeberiaCalcularElHashSha256EnHexadecimal()
    {
        Assert.Equal("7fbdfcdd431cb77a5bce52182f863d807544fdbea2edb49dc396d5ba30481f64", Usuario.CalcularHash("Monster9"));
    }

    [Fact]
    public async Task DeberiaIniciarSesionYRegistrarla()
    {
        RespuestaSesion respuesta = await servicio.IniciarSesionAsync(" Monster ", "Monster9", "192.168.1.30");

        Assert.Equal(200, respuesta.Codigo);
        Assert.True(Guid.TryParse(respuesta.Token, out _));
        Assert.Equal(("Monster", "Monster EurekaBank", 30), (respuesta.Usuario, respuesta.Empleado, respuesta.MinutosVigencia));

        Sesion sesion = Assert.Single(sesiones.Registradas);
        Assert.Equal((respuesta.Token, "Monster", "192.168.1.30"), (sesion.Token, sesion.NombreUsuario, sesion.Ip));
        Assert.Equal((DatosPrueba.Ahora, DatosPrueba.Ahora), (sesion.FechaIngreso, sesion.UltimaActividad));
        Assert.Null(sesion.FechaSalida);
    }

    [Theory]
    [InlineData("Monster", "clave-errada")]
    [InlineData("NoExiste", "Monster9")]
    [InlineData("Monster", "monster9")]
    public async Task DeberiaRechazarCredencialesIncorrectasSinRevelarCualFallo(string usuario, string clave)
    {
        var error = await Assert.ThrowsAsync<ExcepcionAutenticacion>(() => servicio.IniciarSesionAsync(usuario, clave, "ip"));

        Assert.Equal(401, error.Codigo);
        Assert.Equal(ServicioSeguridad.MensajeCredencialesIncorrectas, error.Message);
        Assert.Empty(sesiones.Registradas);
    }

    [Fact]
    public async Task DeberiaRechazarUsuariosNoActivos()
    {
        usuarios.Usuarios["Monster"] = DatosPrueba.UsuarioMonster(estado: "INACTIVO");

        var error = await Assert.ThrowsAsync<ExcepcionAutenticacion>(() => servicio.IniciarSesionAsync("Monster", "Monster9", "ip"));

        Assert.Equal(ServicioSeguridad.MensajeCredencialesIncorrectas, error.Message);
    }

    [Theory]
    [InlineData("", "Monster9")]
    [InlineData("Monster", "")]
    [InlineData(null, null)]
    public async Task DeberiaExigirUsuarioYContrasenia(string? usuario, string? clave)
    {
        var error = await Assert.ThrowsAsync<ExcepcionValidacion>(() => servicio.IniciarSesionAsync(usuario, clave, "ip"));

        Assert.Equal(ServicioSeguridad.MensajeCredencialesVacias, error.Message);
    }

    [Theory]
    [InlineData(null)]
    [InlineData("   ")]
    public async Task DeberiaRechazarUnTokenAusente(string? token)
    {
        var error = await Assert.ThrowsAsync<ExcepcionAutenticacion>(() => servicio.ValidarTokenAsync(token));

        Assert.Equal(ServicioSeguridad.MensajeSinToken, error.Message);
    }

    [Fact]
    public async Task DeberiaRechazarUnTokenNoRegistrado()
    {
        var error = await Assert.ThrowsAsync<ExcepcionAutenticacion>(() => servicio.ValidarTokenAsync("token-inventado"));

        Assert.Equal(ServicioSeguridad.MensajeTokenInvalido, error.Message);
    }

    [Fact]
    public async Task DeberiaRenovarLaActividadDeUnaSesionVigente()
    {
        SesionActiva abierta = DatosPrueba.SesionMonster(DatosPrueba.Ahora.AddMinutes(-29));
        sesiones.Abiertas[abierta.Token] = abierta;

        SesionActiva sesion = await servicio.ValidarTokenAsync(abierta.Token);

        Assert.Equal("0001", sesion.CodigoEmpleado);
        Assert.Equal(DatosPrueba.Ahora, sesiones.Actividad[abierta.Token]);
        Assert.Empty(sesiones.Cerradas);
    }

    [Fact]
    public async Task DeberiaCerrarLaSesionTrasTreintaMinutosSinUso()
    {
        SesionActiva abierta = DatosPrueba.SesionMonster(DatosPrueba.Ahora.AddMinutes(-31));
        sesiones.Abiertas[abierta.Token] = abierta;

        var error = await Assert.ThrowsAsync<ExcepcionAutenticacion>(() => servicio.ValidarTokenAsync(abierta.Token));

        Assert.Equal(ServicioSeguridad.MensajeSesionExpirada, error.Message);
        Assert.Equal(DatosPrueba.Ahora, sesiones.Cerradas[abierta.Token]);
        Assert.Empty(sesiones.Actividad);
    }

    [Fact]
    public async Task DeberiaCambiarLaContrasenia()
    {
        Respuesta respuesta = await servicio.CambiarClaveAsync(DatosPrueba.SesionMonster(), "Monster9", "NuevaClave1");

        Assert.Equal("Contraseña actualizada correctamente", respuesta.Mensaje);
        Assert.Equal(Usuario.CalcularHash("NuevaClave1"), usuarios.Usuarios["Monster"].HashClave);
    }

    [Fact]
    public async Task DeberiaRechazarElCambioSiLaContraseniaActualEsIncorrecta()
    {
        var error = await Assert.ThrowsAsync<ExcepcionValidacion>(() =>
                servicio.CambiarClaveAsync(DatosPrueba.SesionMonster(), "otra", "NuevaClave1"));

        Assert.Equal(ServicioSeguridad.MensajeClaveActualIncorrecta, error.Message);
    }

    [Theory]
    [InlineData("corta")]
    [InlineData("Monster9")]
    [InlineData(null)]
    public async Task DeberiaRechazarContraseniasNuevasNoValidas(string? nueva)
    {
        var error = await Assert.ThrowsAsync<ExcepcionValidacion>(() =>
                servicio.CambiarClaveAsync(DatosPrueba.SesionMonster(), "Monster9", nueva));

        Assert.Equal(ServicioSeguridad.MensajeClaveNuevaInvalida, error.Message);
        Assert.Equal(Usuario.CalcularHash("Monster9"), usuarios.Usuarios["Monster"].HashClave);
    }

    [Fact]
    public async Task DeberiaCerrarLaSesion()
    {
        SesionActiva sesion = DatosPrueba.SesionMonster();

        Respuesta respuesta = await servicio.CerrarSesionAsync(sesion);

        Assert.Equal("Sesión cerrada correctamente", respuesta.Mensaje);
        Assert.Equal(DatosPrueba.Ahora, sesiones.Cerradas[sesion.Token]);
    }
}
