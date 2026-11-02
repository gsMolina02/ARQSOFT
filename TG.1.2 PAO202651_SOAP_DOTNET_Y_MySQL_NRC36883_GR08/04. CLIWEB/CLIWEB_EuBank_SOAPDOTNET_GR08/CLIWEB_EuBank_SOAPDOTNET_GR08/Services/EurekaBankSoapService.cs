using System.ServiceModel;
using System.ServiceModel.Channels;
using LoginServiceRef;
using MovimientoServiceRef;

namespace CLIWEB_EuBank_SOAPDOTNET_GR08.Services;

public interface IEurekaBankSoapService
{
    string CurrentServerUrl { get; }
    Task<bool> LoginAsync(string usuario, string clave);
    Task<IReadOnlyList<CuentaData>> GetCuentasAsync();
    Task<IReadOnlyList<string>> GetTiposMovimientoAsync();
    Task<IReadOnlyList<MovimientoData>> GetMovimientosAsync(string cuenta);
    Task<IReadOnlyList<MovimientoData>> GetTodosMovimientosAsync();
    Task<ResultadoOperacion> ProcesarMovimientoAsync(string tipo, string cuentaOrigen, string? cuentaDestino, decimal importe);
}

public sealed class EurekaBankSoapService : IEurekaBankSoapService
{
    private readonly string _baseUrl;

    public EurekaBankSoapService(IConfiguration configuration)
    {
        string? envUrl = Environment.GetEnvironmentVariable("EUREKABANK_SOAP_URL");
        string ipFile = Path.Combine(AppContext.BaseDirectory, "servidor_ip.txt");
        if (!string.IsNullOrWhiteSpace(envUrl))
        {
            _baseUrl = envUrl.Trim().TrimEnd('/');
        }
        else if (File.Exists(ipFile))
        {
            string host = File.ReadAllText(ipFile).Trim();
            _baseUrl = host.StartsWith("http") ? host.TrimEnd('/') : $"http://{host}:62278";
        }
        else
        {
            _baseUrl = (configuration["SoapServices:BaseUrl"] ?? "http://localhost:62278").TrimEnd('/');
        }
    }

    public string CurrentServerUrl => _baseUrl;

    public async Task<bool> LoginAsync(string usuario, string clave)
    {
        var client = CreateLoginClient();
        try
        {
            return await client.LoginAsync(usuario, clave);
        }
        finally
        {
            await CloseOrAbortAsync(client);
        }
    }

    public async Task<IReadOnlyList<CuentaData>> GetCuentasAsync()
    {
        var client = CreateMovimientosClient();
        try
        {
            return await client.GetDatosCuentasAsync();
        }
        finally
        {
            await CloseOrAbortAsync(client);
        }
    }

    public async Task<IReadOnlyList<string>> GetTiposMovimientoAsync()
    {
        var client = CreateMovimientosClient();
        try
        {
            return await client.GetTiposMovimientoAsync();
        }
        finally
        {
            await CloseOrAbortAsync(client);
        }
    }

    public async Task<IReadOnlyList<MovimientoData>> GetMovimientosAsync(string cuenta)
    {
        var client = CreateMovimientosClient();
        try
        {
            return await client.GetMovimientosAsync(cuenta);
        }
        finally
        {
            await CloseOrAbortAsync(client);
        }
    }

    public async Task<IReadOnlyList<MovimientoData>> GetTodosMovimientosAsync()
    {
        var cuentas = await GetCuentasAsync();
        var movimientos = new List<MovimientoData>();

        foreach (var cuenta in cuentas.Where(c => !string.IsNullOrWhiteSpace(c.Codigo)))
        {
            var movimientosCuenta = await GetMovimientosAsync(cuenta.Codigo);
            movimientos.AddRange(movimientosCuenta);
        }

        return movimientos;
    }

    public async Task<ResultadoOperacion> ProcesarMovimientoAsync(string tipo, string cuentaOrigen, string? cuentaDestino, decimal importe)
    {
        var client = CreateMovimientosClient();
        try
        {
            return await client.ProcesarMovimientoAsync(tipo, cuentaOrigen, cuentaDestino ?? string.Empty, importe);
        }
        finally
        {
            await CloseOrAbortAsync(client);
        }
    }

    private LoginServiceClient CreateLoginClient()
    {
        string url = BuildServiceUrl("Services/LoginService.svc");
        return new LoginServiceClient(CreateBinding(url), new EndpointAddress(url));
    }

    private MovimientosServiceClient CreateMovimientosClient()
    {
        string url = BuildServiceUrl("Services/MovimientosService.svc");
        return new MovimientosServiceClient(CreateBinding(url), new EndpointAddress(url));
    }

    private string BuildServiceUrl(string relativePath)
    {
        return $"{_baseUrl}/{relativePath.TrimStart('/')}";
    }

    private static Binding CreateBinding(string url)
    {
        if (url.StartsWith("https://", StringComparison.OrdinalIgnoreCase))
        {
            return new BasicHttpsBinding(BasicHttpsSecurityMode.Transport)
            {
                MaxBufferSize = int.MaxValue,
                MaxReceivedMessageSize = int.MaxValue,
                ReaderQuotas = System.Xml.XmlDictionaryReaderQuotas.Max
            };
        }

        return new BasicHttpBinding(BasicHttpSecurityMode.None)
        {
            MaxBufferSize = int.MaxValue,
            MaxReceivedMessageSize = int.MaxValue,
            ReaderQuotas = System.Xml.XmlDictionaryReaderQuotas.Max
        };
    }

    private static async Task CloseOrAbortAsync(ICommunicationObject client)
    {
        try
        {
            if (client.State == CommunicationState.Faulted)
            {
                client.Abort();
                return;
            }

            await Task.Factory.FromAsync(client.BeginClose(null, null), client.EndClose);
        }
        catch
        {
            client.Abort();
        }
    }
}
