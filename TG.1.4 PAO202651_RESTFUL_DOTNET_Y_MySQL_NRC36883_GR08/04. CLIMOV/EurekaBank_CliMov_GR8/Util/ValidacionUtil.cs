using System.Globalization;
using System.Text.RegularExpressions;

namespace ec.edu.monster.Util;

/// <summary>
/// Validaciones que el cliente hace antes de llamar al servicio (las mismas reglas y mensajes del servidor),
/// para avisar al usuario sin esperar la respuesta de la red.
/// </summary>
public static class ValidacionUtil
{
    public const int LongitudMinimaClave = 6;
    public const decimal ImporteMaximo = 9_999_999_999.99m;

    public const string MensajeCuenta = "El número de cuenta debe tener 8 dígitos";
    public const string MensajeImporte = "El importe debe ser un número mayor a cero con hasta dos decimales";
    public const string MensajeImporteMaximo = "El importe no puede superar 9999999999.99";
    public const string MensajeCuentasIguales = "La cuenta de origen y la de destino deben ser distintas";
    public const string MensajeClaveNueva =
            "La nueva contraseña debe tener al menos 6 caracteres y ser distinta de la actual";
    public const string MensajeConfirmacion = "La confirmación no coincide con la nueva contraseña";

    // [0-9] y no \d: en .NET \d también acepta dígitos de otros alfabetos (por ejemplo, ٣)
    private static readonly Regex PatronCuenta = new(@"^[0-9]{8}$");
    private static readonly Regex PatronImporte = new(@"^[0-9]{1,13}([.,][0-9]{1,2})?$");

    public static string ValidarCuenta(string? numero)
    {
        string texto = numero?.Trim() ?? string.Empty;
        if (!PatronCuenta.IsMatch(texto))
        {
            throw new ArgumentException(MensajeCuenta);
        }
        return texto;
    }

    /// <summary>
    /// Acepta importes como 100, 25.5 o 25,50: solo dígitos, punto o coma decimal y hasta dos decimales.
    /// Rechaza texto, signos, separadores de miles (1,000.50) y notación científica.
    /// </summary>
    public static decimal InterpretarImporte(string? texto)
    {
        string valor = texto?.Trim() ?? string.Empty;
        if (!PatronImporte.IsMatch(valor))
        {
            throw new ArgumentException(MensajeImporte);
        }
        return ValidarImporte(decimal.Parse(valor.Replace(',', '.'), NumberStyles.AllowDecimalPoint, CultureInfo.InvariantCulture));
    }

    public static decimal ValidarImporte(decimal importe)
    {
        if (importe <= 0 || importe != decimal.Round(importe, 2))
        {
            throw new ArgumentException(MensajeImporte);
        }
        if (importe > ImporteMaximo)
        {
            throw new ArgumentException(MensajeImporteMaximo);
        }
        return importe;
    }

    // Texto que puede estar a medio escribir (móvil: si deja de cumplirse, se restaura el texto anterior)
    private static readonly Regex PatronCuentaParcial = new(@"^[0-9]{0,8}$");
    private static readonly Regex PatronImporteParcial = new(@"^[0-9]{0,13}([.,][0-9]{0,2})?$");

    public static bool EsCuentaParcial(string? texto) => PatronCuentaParcial.IsMatch(texto ?? string.Empty);

    public static bool EsImporteParcial(string? texto) => PatronImporteParcial.IsMatch(texto ?? string.Empty);

    /// <summary>Campo de número de cuenta: solo dígitos mientras se escribe.</summary>
    public static bool EsCaracterCuentaPermitido(char caracter) => char.IsControl(caracter) || caracter is >= '0' and <= '9';

    /// <summary>
    /// Campo de importe: dígitos y un solo separador decimal (punto o coma), con hasta dos decimales.
    /// Se usa para bloquear letras y símbolos mientras el usuario escribe; lo pegado se valida al enviar.
    /// </summary>
    public static bool EsCaracterImportePermitido(char caracter, string textoActual, int posicionCursor)
    {
        if (char.IsControl(caracter))
        {
            return true;
        }
        int separador = textoActual.IndexOfAny(['.', ',']);
        if (caracter is '.' or ',')
        {
            return separador < 0 && textoActual.Length - posicionCursor <= 2;
        }
        if (caracter is < '0' or > '9')
        {
            return false;
        }
        // Un dígito después del separador solo si quedan menos de dos decimales
        return separador < 0 || posicionCursor <= separador || textoActual.Length - separador - 1 < 2;
    }

    public static void ValidarCuentasDistintas(string origen, string destino)
    {
        if (origen == destino)
        {
            throw new ArgumentException(MensajeCuentasIguales);
        }
    }

    public static void ValidarCambioClave(string? actual, string? nueva, string? confirmacion)
    {
        if (string.IsNullOrEmpty(actual) || string.IsNullOrEmpty(nueva) || string.IsNullOrEmpty(confirmacion))
        {
            throw new ArgumentException("Complete la contraseña actual, la nueva y su confirmación");
        }
        if (nueva != confirmacion)
        {
            throw new ArgumentException(MensajeConfirmacion);
        }
        if (nueva.Length < LongitudMinimaClave || nueva == actual)
        {
            throw new ArgumentException(MensajeClaveNueva);
        }
    }
}
