using ec.edu.monster.Cliente;
using ec.edu.monster.Servicios;
using ec.edu.monster.Sesion;
using ec.edu.monster.Util;
using Microsoft.AspNetCore.Mvc;

var builder = WebApplication.CreateBuilder(args);

// Sesión del navegador: guarda el token del servicio y el nombre del empleado de cada usuario
builder.Services.AddDistributedMemoryCache();
builder.Services.AddSession(opciones =>
{
    opciones.IdleTimeout = TimeSpan.FromMinutes(30);
    opciones.Cookie.Name = ".EurekaBank.Sesion";
    opciones.Cookie.HttpOnly = true;
    opciones.Cookie.IsEssential = true;
});
builder.Services.AddHttpContextAccessor();
builder.Services.AddScoped<AlmacenSesion>();
builder.Services.AddScoped<IAlmacenToken>(proveedor => proveedor.GetRequiredService<AlmacenSesion>());
builder.Services.AddScoped<ManejadorTokenRESTFUL>();

// Dirección del servidor REST: variable WS_BASE_URL, la guardada en "Configurar servidor"
// o la de appsettings.json (ServidorRest:UrlBase)
builder.Services.AddSingleton(new ConfiguracionServidor(builder.Configuration["ServidorRest:UrlBase"]));
builder.Services.AddSingleton<ProbadorConexion>();

// Cliente HTTP del servicio RESTful EurekaBank (.NET)
builder.Services.AddHttpClient<IContratoWSEurekaBank, ClienteWSEurekaBank>(cliente =>
{
    cliente.Timeout = TimeSpan.FromSeconds(10);
});

// Lógica de la aplicación: validaciones y llamadas al servicio
builder.Services.AddScoped<ServicioSesion>();
builder.Services.AddScoped<ServicioOperaciones>();
builder.Services.AddScoped<ServicioServidor>();

// Todos los formularios POST exigen el token antifalsificación
builder.Services.AddControllersWithViews(opciones =>
{
    opciones.Filters.Add(new AutoValidateAntiforgeryTokenAttribute());
});

var app = builder.Build();

if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Sesion/Error");
}

app.UseRouting();
app.UseSession();
app.UseAuthorization();

app.MapStaticAssets();

// La página inicial son las operaciones; sin sesión, el filtro RequiereSesion redirige al inicio de sesión
app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Operaciones}/{action=Index}/{id?}")
    .WithStaticAssets();

app.Run();
