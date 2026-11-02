namespace CLICON_EuBank_SOAPDOTNET_GR08.Models;

public class Cuenta
{
    public string? Codigo { get; set; }
    public string? NombreCliente { get; set; }
    public string? EmailCliente { get; set; }
    public string? TelefonoCliente { get; set; }
    public string? Moneda { get; set; }
    public decimal Saldo { get; set; }
    public string? Estado { get; set; }
}