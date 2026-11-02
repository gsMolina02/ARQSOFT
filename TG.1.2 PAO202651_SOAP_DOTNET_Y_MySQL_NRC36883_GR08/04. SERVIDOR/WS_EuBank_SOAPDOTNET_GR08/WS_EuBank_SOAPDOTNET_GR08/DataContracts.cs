using System;
using System.Runtime.Serialization;

namespace WS_EuBank_SOAPDOTNET_GR08
{
    [DataContract]
    public class CuentaData
    {
        [DataMember]
        public string Codigo { get; set; }
        [DataMember]
        public string Moneda { get; set; }
        [DataMember]
        public decimal Saldo { get; set; }
        [DataMember]
        public string Estado { get; set; }
        [DataMember]
        public string NombreCliente { get; set; }
        [DataMember]
        public string EmailCliente { get; set; }
        [DataMember]
        public string TelefonoCliente { get; set; }
    }

    [DataContract]
    public class MovimientoData
    {
        [DataMember]
        public int Numero { get; set; }
        [DataMember]
        public DateTime Fecha { get; set; }
        [DataMember]
        public string Tipo { get; set; }
        [DataMember]
        public decimal Importe { get; set; }
        [DataMember]
        public string Referencia { get; set; }
        [DataMember]
        public string CodigoCuenta { get; set; }
        [DataMember]
        public string NombreCliente { get; set; }
        [DataMember]
        public decimal SaldoActual { get; set; }
    }

    [DataContract]
    public class ResultadoOperacion
    {
        [DataMember]
        public int Codigo { get; set; }  // 1 = éxito, -1 = error
        [DataMember]
        public string Mensaje { get; set; }

        public ResultadoOperacion() { }

        public ResultadoOperacion(int codigo, string mensaje)
        {
            Codigo = codigo;
            Mensaje = mensaje;
        }
    }
}
