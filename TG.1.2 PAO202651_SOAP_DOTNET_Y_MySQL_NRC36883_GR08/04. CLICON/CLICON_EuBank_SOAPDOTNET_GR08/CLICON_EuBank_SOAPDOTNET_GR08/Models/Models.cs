using System;

namespace CLICON_EuBank_SOAPDOTNET_GR08.Models
{
    public class LoginRequest
    {
        public string Usuario { get; set; }
        public string Clave { get; set; }
    }

    public class LoginResponse
    {
        public bool Success { get; set; }
        public string Token { get; set; }
        public string Message { get; set; }
    }

    public class Account
    {
        public string Codigo { get; set; }
        public string NombreCliente { get; set; }
        public string EmailCliente { get; set; }
        public string TelefonoCliente { get; set; }
        public string Moneda { get; set; }
        public decimal Saldo { get; set; }
        public string Estado { get; set; }
    }

    public class Movement
    {
        public int Numero { get; set; }
        public DateTime Fecha { get; set; }
        public string Tipo { get; set; }
        public decimal Importe { get; set; }
        public string Referencia { get; set; }
        public string CodigoCuenta { get; set; }
        public decimal SaldoActual { get; set; }
        public string NombreCliente { get; set; }
    }

    public class MovementRequest
    {
        public string Tipo { get; set; }
        public string CuentaOrigen { get; set; }
        public string CuentaDestino { get; set; }
        public decimal Importe { get; set; }
    }

    public class MovementType
    {
        public string Codigo { get; set; }
        public string Descripcion { get; set; }
    }

    public class CrearMovimientoResponse
    {
        public bool Success { get; set; }
        public string Message { get; set; }
    }
}
