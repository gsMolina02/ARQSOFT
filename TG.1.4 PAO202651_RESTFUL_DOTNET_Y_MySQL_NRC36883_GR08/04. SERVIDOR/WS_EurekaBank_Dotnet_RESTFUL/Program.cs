using ec.edu.monster.Datos;
using ec.edu.monster.Excepciones;
using ec.edu.monster.Modelos.Dto;
using ec.edu.monster.Seguridad;
using ec.edu.monster.Servicios;
using ec.edu.monster.Util;
using Microsoft.AspNetCore.Mvc;

// Escucha en todas las interfaces de red (appsettings.json → Kestrel → http://*:5000):
// los clientes de esta PC usan localhost y los de otras computadoras usan la IP que se muestra al arrancar.
// Solo este servidor se conecta a MySQL (usuario Monster, cadena "EurekaBank" de appsettings.json).
var builder = WebApplication.CreateBuilder(args);

string cadenaConexion = builder.Configuration.GetConnectionString("EurekaBank")
        ?? throw new InvalidOperationException("Falta la cadena de conexión \"EurekaBank\" en appsettings.json.");

builder.Services.AddSingleton(TimeProvider.System);
builder.Services.AddSingleton<IFabricaConexion>(new FabricaConexionMySql(cadenaConexion));
builder.Services.AddSingleton<IRepositorioUsuario, RepositorioUsuario>();
builder.Services.AddSingleton<IRepositorioSesion, RepositorioSesion>();
builder.Services.AddSingleton<IRepositorioCuenta, RepositorioCuenta>();
builder.Services.AddSingleton<IRepositorioMovimiento, RepositorioMovimiento>();
builder.Services.AddSingleton<ServicioSeguridad>();
builder.Services.AddSingleton<ServicioCuenta>();
builder.Services.AddSingleton<ServicioTransaccion>();

// Errores y cuerpos inválidos responden con el mismo JSON {"codigo", "mensaje"} que las operaciones
builder.Services.AddExceptionHandler<ManejadorExcepciones>();
builder.Services.AddControllers().ConfigureApiBehaviorOptions(opciones =>
        opciones.InvalidModelStateResponseFactory = contexto => new BadRequestObjectResult(new Respuesta
        {
            Codigo = StatusCodes.Status400BadRequest,
            Mensaje = ManejadorExcepciones.MensajeModeloInvalido(contexto.ModelState)
        }));

var app = builder.Build();

app.UseExceptionHandler(_ => { });
app.UseStatusCodePages(ManejadorExcepciones.EscribirCodigoEstadoAsync);
app.MapControllers();

app.Lifetime.ApplicationStarted.Register(() =>
{
    int puerto = app.Urls.Select(DireccionesRed.ObtenerPuerto).FirstOrDefault(p => p is not null) ?? 5000;
    IReadOnlyList<string> urls = DireccionesRed.CrearUrlsServicio(DireccionesRed.ObtenerIPv4Locales(), puerto);

    app.Logger.LogInformation("Clientes de esta computadora: http://localhost:{Puerto}{Ruta}", puerto, DireccionesRed.RutaServicio);
    foreach (string url in urls)
    {
        app.Logger.LogInformation("Clientes de otras computadoras de la red: {Url}", url);
    }
    if (urls.Count == 0)
    {
        app.Logger.LogWarning("No se encontró una IP de red: conecte la computadora al Wi-Fi o al cable de red.");
    }
    _ = ComprobarBaseDatosAsync(app);
});

app.Run();

// Avisa al arrancar si MySQL no responde, para no descubrirlo recién cuando un cliente inicie sesión
static async Task ComprobarBaseDatosAsync(WebApplication app)
{
    try
    {
        await using IUnidadTrabajo unidad = await app.Services.GetRequiredService<IFabricaConexion>().AbrirAsync();
        app.Logger.LogInformation("Conexión con MySQL correcta (base de datos eurekabank).");
    }
    catch (Exception ex)
    {
        app.Logger.LogError("No se pudo conectar con MySQL: {Mensaje} Verifique que el servicio MySQL80 esté iniciado " +
                "y que se ejecutaron los scripts de 03. BDD (01, 02 y 03).", ex.Message);
    }
}
