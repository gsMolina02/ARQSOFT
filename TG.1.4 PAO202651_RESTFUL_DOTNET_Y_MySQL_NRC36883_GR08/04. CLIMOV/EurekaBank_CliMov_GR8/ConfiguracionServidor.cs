namespace ec.edu.monster;

/// <summary>
/// Dirección del servicio RESTful EurekaBank (.NET). Se guarda en las preferencias del teléfono para que el
/// usuario pueda cambiarla (por ejemplo, para usar la IP de la PC desde un teléfono real).
/// </summary>
public static class ConfiguracionServidor
{
    private const string ClavePreferencia = "UrlServidor";
    private const string Ruta = ":5000/api";

    // En el emulador de Android, 10.0.2.2 apunta al localhost de la PC donde corre el servidor REST
    public static string UrlPorDefecto => DeviceInfo.Platform == DevicePlatform.Android
            ? "http://10.0.2.2" + Ruta
            : "http://localhost" + Ruta;

    public static string UrlActual
    {
        get => Preferences.Default.Get(ClavePreferencia, UrlPorDefecto);
        set
        {
            if (string.IsNullOrWhiteSpace(value))
            {
                Preferences.Default.Remove(ClavePreferencia);
            }
            else
            {
                Preferences.Default.Set(ClavePreferencia, value.Trim().TrimEnd('/'));
            }
        }
    }

    public static bool EsUrlValida(string? url) => Util.DireccionServidor.EsUrlValida(url);
}
