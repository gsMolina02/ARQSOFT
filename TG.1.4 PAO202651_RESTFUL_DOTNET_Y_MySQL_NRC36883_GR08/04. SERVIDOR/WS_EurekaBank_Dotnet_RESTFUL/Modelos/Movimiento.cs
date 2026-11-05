namespace ec.edu.monster.Modelos;

/// <summary>
/// Operación registrada sobre una cuenta. El número es correlativo dentro de la cuenta.
/// </summary>
public class Movimiento
{
    public required string NumeroCuenta { get; init; }
    public int Numero { get; init; }
    public required string CodigoTipo { get; init; }
    public string DescripcionTipo { get; init; } = string.Empty;
    public string Accion { get; init; } = string.Empty;
    public required string CodigoEmpleado { get; init; }
    /// <summary>La otra cuenta de una transferencia; null en depósitos, retiros y apertura.</summary>
    public string? CuentaReferencia { get; init; }
    public DateTime FechaHora { get; init; }
    public decimal Importe { get; init; }
}
