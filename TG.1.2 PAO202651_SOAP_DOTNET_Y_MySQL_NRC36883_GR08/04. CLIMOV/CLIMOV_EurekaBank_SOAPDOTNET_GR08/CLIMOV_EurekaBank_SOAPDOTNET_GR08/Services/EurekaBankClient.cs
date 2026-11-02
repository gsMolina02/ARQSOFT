using System.Globalization;
using System.ServiceModel;
using LoginServiceRef;
using MovimientosServiceRef;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Services
{
    public sealed class EurekaBankClient
    {
        // Configurable Server URL for LAN multi-computer / mobile connection
        public static string BaseUrl { get; set; } = "http://192.168.100.15:62278";

        private static LoginServiceClient CreateLoginClient(string endpoint)
        {
            var client = new LoginServiceClient(LoginServiceClient.EndpointConfiguration.BasicHttpBinding_ILoginService, endpoint);
            client.Endpoint.Binding.OpenTimeout = TimeSpan.FromSeconds(60);
            client.Endpoint.Binding.SendTimeout = TimeSpan.FromSeconds(60);
            client.Endpoint.Binding.ReceiveTimeout = TimeSpan.FromSeconds(60);
            return client;
        }

        private static MovimientosServiceClient CreateMovimientosClient(string endpoint)
        {
            var client = new MovimientosServiceClient(MovimientosServiceClient.EndpointConfiguration.BasicHttpBinding_IMovimientosService, endpoint);
            client.Endpoint.Binding.OpenTimeout = TimeSpan.FromSeconds(60);
            client.Endpoint.Binding.SendTimeout = TimeSpan.FromSeconds(60);
            client.Endpoint.Binding.ReceiveTimeout = TimeSpan.FromSeconds(60);
            return client;
        }

        public async Task<bool> LoginAsync(string usuario, string clave)
        {
            try
            {
                string endpoint = $"{BaseUrl.TrimEnd('/')}/Services/LoginService.svc";
                var client = CreateLoginClient(endpoint);
                return await client.LoginAsync(usuario, clave);
            }
            catch (TimeoutException ex)
            {
                throw new HttpRequestException($"Tiempo de espera agotado al conectar a {BaseUrl}. Verifica que el servidor esté activo.", ex);
            }
            catch (Exception ex)
            {
                throw new Exception($"Error al conectar a {BaseUrl}: {ex.Message}", ex);
            }
        }

        public async Task<List<AccountSummary>> GetAccountsAsync()
        {
            try
            {
                string endpoint = $"{BaseUrl.TrimEnd('/')}/Services/MovimientosService.svc";
                var client = CreateMovimientosClient(endpoint);
                var cuentas = await client.GetDatosCuentasAsync();
                return cuentas
                    .Where(c => !string.IsNullOrWhiteSpace(c.Codigo))
                    .Select(c => new AccountSummary(
                        c.Codigo,
                        c.NombreCliente,
                        c.Moneda,
                        c.Saldo,
                        c.Estado,
                        c.EmailCliente))
                    .ToList();
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"[EurekaBankClient] Error al obtener cuentas: {ex.Message}");
                throw;
            }
        }

        public async Task<List<MovementSummary>> GetMovementsAsync(string account)
        {
            try
            {
                string endpoint = $"{BaseUrl.TrimEnd('/')}/Services/MovimientosService.svc";
                var client = CreateMovimientosClient(endpoint);
                var movimientos = await client.GetMovimientosAsync(account);
                return movimientos
                    .Where(m => !string.IsNullOrWhiteSpace(m.Tipo))
                    .Select(m => new MovementSummary(
                        m.Tipo,
                        m.Importe,
                        m.Fecha,
                        m.Referencia,
                        m.SaldoActual))
                    .ToList();
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"[EurekaBankClient] Error al obtener movimientos: {ex.Message}");
                throw;
            }
        }

        public async Task<List<string>> GetMovementTypesAsync()
        {
            try
            {
                string endpoint = $"{BaseUrl.TrimEnd('/')}/Services/MovimientosService.svc";
                var client = CreateMovimientosClient(endpoint);
                var tipos = await client.GetTiposMovimientoAsync();
                return tipos
                    .Where(t => !string.IsNullOrWhiteSpace(t))
                    .Distinct(StringComparer.OrdinalIgnoreCase)
                    .ToList();
            }
            catch
            {
                return ["DEPOSITO", "RETIRO", "TRANSFERENCIA"];
            }
        }

        public async Task<OperationResult> ProcessMovementAsync(string tipo, string cuentaOrigen, string cuentaDestino, decimal importe)
        {
            try
            {
                string endpoint = $"{BaseUrl.TrimEnd('/')}/Services/MovimientosService.svc";
                var client = CreateMovimientosClient(endpoint);
                var resultado = await client.ProcesarMovimientoAsync(tipo, cuentaOrigen, cuentaDestino, importe);
                return new OperationResult(resultado.Codigo, resultado.Mensaje);
            }
            catch (Exception ex)
            {
                return new OperationResult(-1, ex.Message ?? "No se pudo conectar al servicio SOAP.");
            }
        }
    }

    public sealed record AccountSummary(string Codigo, string NombreCliente, string Moneda, decimal Saldo, string Estado, string Email)
    {
        public bool EsSoles => Moneda == "01" || Moneda.Equals("Soles", StringComparison.OrdinalIgnoreCase);
        public string SimboloMoneda => EsSoles ? "S/." : "$";
        public string NombreMoneda => EsSoles ? "Soles" : "Dólares";
        public string MonedaCompleta => $"{NombreMoneda} ({SimboloMoneda})";

        public string ClienteMoneda => $"{NombreCliente} • {MonedaCompleta}";
        public string SaldoTexto => $"Saldo: {SimboloMoneda} {Saldo:N2}";
        public string EstadoEmail => $"{Estado} • {Email}";

        public string DisplayTransferencia => $"{Codigo} - {NombreCliente} [{MonedaCompleta}] • Saldo: {SimboloMoneda} {Saldo:N2}";
        public string DisplayPicker => $"{Codigo} - {NombreCliente} [{MonedaCompleta}]";
    }

    public sealed record MovementSummary(string Tipo, decimal Importe, DateTime FechaReal, string ReferenciaBase, decimal Saldo)
    {
        public string TipoImporte => $"{Tipo} {(Importe >= 0 ? "+" : string.Empty)}{Importe:N2}";
        public string Fecha => FechaReal == DateTime.MinValue ? string.Empty : FechaReal.ToString("dd/MM/yyyy HH:mm", CultureInfo.CurrentCulture);
        public string Referencia => string.IsNullOrWhiteSpace(ReferenciaBase) ? string.Empty : $"Ref: {ReferenciaBase}";
        public string SaldoTexto => $"Saldo: $ {Saldo:N2}";
        public Color Color => Importe >= 0 ? Color.FromArgb("#388E3C") : Color.FromArgb("#D32F2F");
    }

    public sealed record OperationResult(int Codigo, string Mensaje)
    {
        public bool Success => Codigo == 1;
    }
}
