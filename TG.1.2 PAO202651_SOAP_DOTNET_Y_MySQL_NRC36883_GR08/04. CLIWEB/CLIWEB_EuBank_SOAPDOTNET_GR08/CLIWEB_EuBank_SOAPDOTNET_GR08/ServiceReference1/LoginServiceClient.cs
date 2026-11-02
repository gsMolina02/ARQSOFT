namespace ServiceReference1
{
    public class LoginServiceClient
    {
        public LoginServiceClient()
        {
        }

        public System.Threading.Tasks.Task<bool> LoginAsync(string usuario, string clave)
        {
            return System.Threading.Tasks.Task.FromResult(true);
        }
    }
}
