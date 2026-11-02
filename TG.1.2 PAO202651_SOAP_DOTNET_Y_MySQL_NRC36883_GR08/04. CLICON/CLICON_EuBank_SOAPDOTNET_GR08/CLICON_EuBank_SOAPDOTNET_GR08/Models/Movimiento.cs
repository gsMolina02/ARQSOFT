namespace CLICON_EuBank_SOAPDOTNET_GR08.Models;

public class Movimiento
{
    public string? CodigoCuenta { get; set; }
    public string? NombreCliente { get; set; }
    public string? Numero { get; set; }
    public DateTime Fecha { get; set; }
    public string? Tipo { get; set; }
    public string? Referencia { get; set; }
    public decimal Importe { get; set; }
    public decimal SaldoActual { get; set; }
}