namespace ec.edu.monster.Modelos;

/// <summary>
/// Cuenta de ahorro con los datos de su cliente, moneda y sucursal.
/// </summary>
public class Cuenta
{
    public const string EstadoActivo = "ACTIVO";

    public required string Numero { get; init; }
    public decimal Saldo { get; set; }
    public DateTime FechaApertura { get; init; }
    public required string Estado { get; init; }
    public int ContadorMovimientos { get; set; }

    public required string CodigoCliente { get; init; }
    public required string CedulaCliente { get; init; }
    public required string NombreCliente { get; init; }
    public required string CodigoMoneda { get; init; }
    public required string NombreMoneda { get; init; }
    public required string SimboloMoneda { get; init; }
    public required string CodigoSucursal { get; init; }
    public required string NombreSucursal { get; init; }

    public bool EstaActiva() => Estado == EstadoActivo;

    public bool TieneSaldo(decimal importe) => Saldo >= importe;

    /// <summary>Suma el importe y avanza el contador: el nuevo valor es el número del movimiento.</summary>
    public void Acreditar(decimal importe)
    {
        Saldo += importe;
        ContadorMovimientos++;
    }

    /// <summary>Resta el importe y avanza el contador: el nuevo valor es el número del movimiento.</summary>
    public void Debitar(decimal importe)
    {
        Saldo -= importe;
        ContadorMovimientos++;
    }
}
