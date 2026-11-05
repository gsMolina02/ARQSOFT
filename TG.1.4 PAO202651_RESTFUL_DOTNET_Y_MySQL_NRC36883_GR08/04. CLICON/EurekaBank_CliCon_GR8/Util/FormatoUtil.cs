using System.Globalization;
using ec.edu.monster.Cliente.Dto;

namespace ec.edu.monster.Util;

public static class FormatoUtil
{
    private static readonly CultureInfo Cultura = CultureInfo.InvariantCulture;

    /// <summary>Por ejemplo $1,250.50 (siempre con dos decimales).</summary>
    public static string FormatearMonto(decimal valor, string simbolo = "$") =>
            (valor < 0 ? "-" : string.Empty) + simbolo + Math.Abs(valor).ToString("N2", Cultura);

    /// <summary>Monto con signo según la acción del movimiento: +$100.00 o -$50.00.</summary>
    public static string FormatearMovimiento(decimal importe, bool esIngreso, string simbolo = "$") =>
            (esIngreso ? "+" : "-") + simbolo + importe.ToString("N2", Cultura);

    public static string FormatearFechaHora(DateTime fechaHora) => fechaHora.ToString("dd/MM/yyyy HH:mm:ss", Cultura);

    public static string FormatearFecha(DateOnly fecha) => fecha.ToString("dd/MM/yyyy", Cultura);

    /// <summary>Texto de la cuenta en la lista desplegable: 00100001 · Carlos Pérez (y el estado si no está activa).</summary>
    public static string FormatearOpcionCuenta(CuentaDto cuenta) =>
            $"{cuenta.Numero} · {cuenta.Cliente}" + (cuenta.EstaActiva ? string.Empty : $" ({cuenta.Estado})");
}
