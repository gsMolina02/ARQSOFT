using System.Text.RegularExpressions;

namespace ec.edu.monster.Util;

/// <summary>
/// Convierte lo que escribe el usuario en la dirección completa del servicio RESTful. Está separada de
/// ConfiguracionServidor (que usa las preferencias del teléfono) para poder probarla en la PC.
/// </summary>
public static class DireccionServidor
{
    private const int PuertoPorDefecto = 5000;
    private const string RutaServicio = "/api";

    private const string MensajeFormato =
            "Use la IP de la computadora servidor (por ejemplo 192.168.1.25) " +
            "o la dirección completa (http://192.168.1.25:5000/api).";

    private static readonly Regex PatronNumerico = new(@"^[0-9.]+$");
    private static readonly Regex PatronIPv4 = new(@"^([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})\.([0-9]{1,3})$");

    /// <summary>
    /// Con solo la IP o el nombre del equipo se completan el puerto 5000 y la ruta /api
    /// (192.168.1.25 → http://192.168.1.25:5000/api). Una dirección con ruta se respeta.
    /// </summary>
    public static string Completar(string? entrada)
    {
        string texto = entrada?.Trim() ?? string.Empty;
        if (texto.Length == 0)
        {
            throw new ArgumentException("Escriba la dirección del servidor. " + MensajeFormato);
        }
        if (!texto.Contains("://"))
        {
            texto = "http://" + texto;
        }

        string hostEscrito = ExtraerHost(texto, out bool tienePuerto);
        if (!Uri.TryCreate(texto, UriKind.Absolute, out Uri? uri) || !EsEsquemaHttp(uri)
                || hostEscrito.Length == 0 || !EsHostValido(hostEscrito))
        {
            throw new ArgumentException("La dirección del servidor no es válida. " + MensajeFormato);
        }

        var constructor = new UriBuilder(uri);
        if (uri.AbsolutePath == "/" && uri.Query.Length == 0)
        {
            if (!tienePuerto)
            {
                constructor.Port = PuertoPorDefecto;
            }
            constructor.Path = RutaServicio;
        }
        return constructor.Uri.ToString().TrimEnd('/');
    }

    public static bool EsUrlValida(string? url)
    {
        return Uri.TryCreate(url?.Trim(), UriKind.Absolute, out Uri? uri)
                && EsEsquemaHttp(uri)
                && uri.Host.Length > 0;
    }

    private static bool EsEsquemaHttp(Uri uri) => uri.Scheme == Uri.UriSchemeHttp || uri.Scheme == Uri.UriSchemeHttps;

    // Uri acepta IP incompletas (192.168.1 → 192.168.0.1): se exigen los cuatro números de 0 a 255
    private static bool EsHostValido(string host)
    {
        if (!PatronNumerico.IsMatch(host))
        {
            return true;
        }
        Match coincidencia = PatronIPv4.Match(host);
        return coincidencia.Success && coincidencia.Groups.Values.Skip(1).All(grupo => int.Parse(grupo.Value) <= 255);
    }

    // Host tal como lo escribió el usuario (antes de que Uri lo normalice) y si incluye el puerto
    private static string ExtraerHost(string texto, out bool tienePuerto)
    {
        string resto = texto[(texto.IndexOf("://", StringComparison.Ordinal) + 3)..];
        int fin = resto.IndexOfAny(['/', '?', '#']);
        string autoridad = fin >= 0 ? resto[..fin] : resto;
        autoridad = autoridad[(autoridad.LastIndexOf('@') + 1)..];

        int dosPuntos = autoridad.LastIndexOf(':');
        tienePuerto = dosPuntos >= 0 && !autoridad.EndsWith(']');
        return tienePuerto ? autoridad[..dosPuntos] : autoridad;
    }
}
