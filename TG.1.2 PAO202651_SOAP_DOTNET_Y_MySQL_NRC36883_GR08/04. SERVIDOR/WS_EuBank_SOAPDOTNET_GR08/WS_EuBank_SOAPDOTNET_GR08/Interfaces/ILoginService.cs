using System.ServiceModel;

namespace WS_EuBank_SOAPDOTNET_GR08
{
    [ServiceContract]
    public interface ILoginService
    {
        [OperationContract]
        bool Login(string usuario, string clave);
    }
}
