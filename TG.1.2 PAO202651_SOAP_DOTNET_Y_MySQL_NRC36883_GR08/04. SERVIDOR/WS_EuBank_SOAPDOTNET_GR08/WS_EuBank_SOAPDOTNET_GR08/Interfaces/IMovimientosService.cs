using System.Collections.Generic;
using System.ServiceModel;

namespace WS_EuBank_SOAPDOTNET_GR08
{
    [ServiceContract]
    public interface IMovimientosService
    {
        [OperationContract]
        List<CuentaData> GetDatosCuentas();

        [OperationContract]
        List<MovimientoData> GetMovimientos(string cuenta = null);

        [OperationContract]
        ResultadoOperacion ProcesarMovimiento(string tipo, string cuentaOrigen, string cuentaDestino, decimal importe);

        [OperationContract]
        List<string> GetTiposMovimiento();
    }
}
