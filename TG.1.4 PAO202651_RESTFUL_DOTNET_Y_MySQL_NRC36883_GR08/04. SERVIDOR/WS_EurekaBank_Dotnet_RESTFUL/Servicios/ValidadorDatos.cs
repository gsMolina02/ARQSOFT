using System.Text.RegularExpressions;
using ec.edu.monster.Excepciones;

namespace ec.edu.monster.Servicios;

/// <summary>
/// CU-09: formato del número de cuenta y del importe (código 400 si no se cumple).
/// </summary>
public static partial class ValidadorDatos
{
    /// <summary>Mayor valor que admite la columna decimal(12,2) de saldo e importe.</summary>
    public const decimal ImporteMaximo = 9_999_999_999.99m;

    public const string MensajeCuenta = "El número de cuenta debe tener 8 dígitos";
    public const string MensajeImporte = "El importe debe ser un número mayor a cero con hasta dos decimales";
    public const string MensajeImporteMaximo = "El importe no puede superar 9999999999.99";

    [GeneratedRegex(@"^[0-9]{8}$")]
    private static partial Regex PatronCuenta();

    public static string ValidarCuenta(string? numero)
    {
        string texto = numero?.Trim() ?? string.Empty;
        if (!PatronCuenta().IsMatch(texto))
        {
            throw new ExcepcionValidacion(MensajeCuenta);
        }
        return texto;
    }

    public static decimal ValidarImporte(decimal? importe)
    {
        if (importe is not decimal valor || valor <= 0 || valor != decimal.Round(valor, 2))
        {
            throw new ExcepcionValidacion(MensajeImporte);
        }
        if (valor > ImporteMaximo)
        {
            throw new ExcepcionValidacion(MensajeImporteMaximo);
        }
        return valor;
    }
}
