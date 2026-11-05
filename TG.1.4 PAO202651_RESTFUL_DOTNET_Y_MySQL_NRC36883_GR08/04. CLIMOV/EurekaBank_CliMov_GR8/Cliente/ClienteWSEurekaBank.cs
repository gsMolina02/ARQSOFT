using System.Net.Http.Json;
using System.Text.Json;
using ec.edu.monster.Cliente.Dto;

namespace ec.edu.monster.Cliente;

/// <summary>
/// Cliente HTTP del servicio RESTful EurekaBank. Envía y recibe JSON y adjunta el token Bearer.
/// </summary>
public class ClienteWSEurekaBank : IContratoWSEurekaBank, IDisposable
{
    private static readonly TimeSpan TiempoEspera = TimeSpan.FromSeconds(10);

    private readonly HttpClient clienteHttp;
    private readonly ManejadorTokenRESTFUL manejadorToken = new();

    public string BaseUrl { get; }

    public string? Empleado { get; private set; }

    public bool HaySesionActiva => manejadorToken.HaySesionActiva;

    public string? TokenSesion => manejadorToken.ObtenerToken();

    // El manejador HTTP se puede reemplazar en las pruebas para no depender del servidor
    public ClienteWSEurekaBank(string baseUrl, HttpMessageHandler? manejadorHttp = null)
    {
        BaseUrl = baseUrl.Trim().TrimEnd('/');
        clienteHttp = manejadorHttp is null ? new HttpClient() : new HttpClient(manejadorHttp);
        clienteHttp.Timeout = TiempoEspera;
    }

    public async Task<RespuestaSesion> IniciarSesionAsync(string usuario, string clave)
    {
        var solicitud = new SolicitudLogin { Usuario = usuario, Clave = clave };
        RespuestaSesion respuesta = await EnviarAsync<RespuestaSesion>(Crear(HttpMethod.Post, "/sesion/login", solicitud));

        manejadorToken.GuardarToken(respuesta.Token);
        Empleado = respuesta.Empleado;
        return respuesta;
    }

    public async Task<string> CambiarClaveAsync(string claveActual, string claveNueva)
    {
        var solicitud = new SolicitudCambioClave { ClaveActual = claveActual, ClaveNueva = claveNueva };
        Respuesta respuesta = await EnviarAsync<Respuesta>(CrearProtegida(HttpMethod.Put, "/sesion/clave", solicitud));
        return respuesta.Mensaje ?? "Contraseña actualizada correctamente";
    }

    /// <summary>
    /// CU-03: el token local se borra siempre, aunque el servidor no responda o la sesión ya hubiera vencido.
    /// </summary>
    public async Task<string> CerrarSesionAsync()
    {
        try
        {
            Respuesta respuesta = await EnviarAsync<Respuesta>(CrearProtegida<object?>(HttpMethod.Post, "/sesion/logout", null));
            return respuesta.Mensaje ?? "Sesión cerrada correctamente";
        }
        finally
        {
            LimpiarSesion();
        }
    }

    public Task<RespuestaCuentas> ListarCuentasAsync() =>
            EnviarAsync<RespuestaCuentas>(CrearProtegida<object?>(HttpMethod.Get, "/cuentas", null));

    public Task<RespuestaMovimientos> ConsultarMovimientosAsync(string cuenta) =>
            EnviarAsync<RespuestaMovimientos>(CrearProtegida<object?>(HttpMethod.Get, $"/cuentas/{Ruta(cuenta)}/movimientos", null));

    public Task<RespuestaTransaccion> RegistrarDepositoAsync(string cuenta, decimal importe) =>
            EnviarAsync<RespuestaTransaccion>(CrearProtegida(HttpMethod.Post, $"/cuentas/{Ruta(cuenta)}/depositos",
                    new SolicitudImporte { Importe = importe }));

    public Task<RespuestaTransaccion> RegistrarRetiroAsync(string cuenta, decimal importe) =>
            EnviarAsync<RespuestaTransaccion>(CrearProtegida(HttpMethod.Post, $"/cuentas/{Ruta(cuenta)}/retiros",
                    new SolicitudImporte { Importe = importe }));

    public Task<RespuestaTransaccion> RegistrarTransferenciaAsync(string cuentaOrigen, string cuentaDestino, decimal importe) =>
            EnviarAsync<RespuestaTransaccion>(CrearProtegida(HttpMethod.Post, "/transferencias", new SolicitudTransferencia
            {
                CuentaOrigen = cuentaOrigen,
                CuentaDestino = cuentaDestino,
                Importe = importe
            }));

    public void Dispose()
    {
        clienteHttp.Dispose();
    }

    private void LimpiarSesion()
    {
        manejadorToken.LimpiarToken();
        Empleado = null;
    }

    private static string Ruta(string valor) => Uri.EscapeDataString(valor.Trim());

    private HttpRequestMessage Crear<T>(HttpMethod metodo, string ruta, T cuerpo)
    {
        var peticion = new HttpRequestMessage(metodo, BaseUrl + ruta);
        peticion.Headers.Accept.ParseAdd("application/json");
        if (cuerpo is not null)
        {
            peticion.Content = JsonContent.Create(cuerpo);
        }
        return peticion;
    }

    private HttpRequestMessage CrearProtegida<T>(HttpMethod metodo, string ruta, T cuerpo)
    {
        HttpRequestMessage peticion = Crear(metodo, ruta, cuerpo);
        manejadorToken.AdjuntarToken(peticion);
        return peticion;
    }

    private async Task<T> EnviarAsync<T>(HttpRequestMessage peticion)
    {
        HttpResponseMessage respuesta;
        try
        {
            using (peticion)
            {
                respuesta = await clienteHttp.SendAsync(peticion);
            }
        }
        catch (Exception ex) when (ex is HttpRequestException or TaskCanceledException)
        {
            throw new ExcepcionClienteWS(ExcepcionClienteWS.SinConexion,
                $"No fue posible conectar con el servidor REST en {BaseUrl}. " +
                "Verifique que el servidor esté encendido, la IP configurada y que ambas computadoras estén en la misma red.");
        }

        using (respuesta)
        {
            int codigo = (int)respuesta.StatusCode;

            if (!respuesta.IsSuccessStatusCode)
            {
                if (codigo == 401)
                {
                    // Token inválido o sesión vencida (CU-10): el cliente vuelve al inicio de sesión
                    LimpiarSesion();
                }
                throw new ExcepcionClienteWS(codigo, await ExtraerMensajeAsync(respuesta));
            }

            try
            {
                T? datos = await respuesta.Content.ReadFromJsonAsync<T>();
                return datos ?? throw new ExcepcionClienteWS(codigo, "El servidor devolvió una respuesta vacía.");
            }
            catch (Exception ex) when (ex is JsonException or NotSupportedException)
            {
                throw new ExcepcionClienteWS(codigo, "El servidor devolvió una respuesta con formato inválido.");
            }
        }
    }

    // Los errores del servidor llegan como {"codigo": 409, "mensaje": "..."}
    private static async Task<string> ExtraerMensajeAsync(HttpResponseMessage respuesta)
    {
        try
        {
            Respuesta? error = await respuesta.Content.ReadFromJsonAsync<Respuesta>();
            if (!string.IsNullOrWhiteSpace(error?.Mensaje))
            {
                return error.Mensaje;
            }
        }
        catch (Exception ex) when (ex is JsonException or NotSupportedException)
        {
            // El cuerpo no es JSON (por ejemplo, una página de error de otro programa en ese puerto)
        }
        return $"El servidor respondió con el código HTTP {(int)respuesta.StatusCode}.";
    }
}
