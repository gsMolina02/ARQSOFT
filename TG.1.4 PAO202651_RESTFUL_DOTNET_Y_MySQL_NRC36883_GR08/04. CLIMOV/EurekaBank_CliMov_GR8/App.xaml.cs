using ec.edu.monster.Cliente;
using ec.edu.monster.Controlador;
using ec.edu.monster.Vista;

namespace ec.edu.monster
{
    public partial class App : Application
    {
        private ClienteWSEurekaBank? clienteWs;

        public App()
        {
            InitializeComponent();
            // La app usa siempre el tema claro, igual que los clientes de escritorio y web
            UserAppTheme = AppTheme.Light;
        }

        protected override Window CreateWindow(IActivationState? activationState)
        {
            return new Window(new LoginPagina(CrearLoginControlador())) { Title = "EurekaBank REST GR08" };
        }

        public static App Actual => (App)Current!;

        // Crea el cliente HTTP con la dirección del servidor configurada en ese momento
        public LoginControlador CrearLoginControlador()
        {
            clienteWs?.Dispose();
            clienteWs = new ClienteWSEurekaBank(ConfiguracionServidor.UrlActual);
            return new LoginControlador(clienteWs);
        }

        public void MostrarInicioSesion(string? aviso = null)
        {
            Windows[0].Page = new LoginPagina(CrearLoginControlador(), aviso);
        }

        public void MostrarOperaciones(LoginControlador loginControlador)
        {
            Windows[0].Page = new NavigationPage(new OperacionesPagina(loginControlador))
            {
                BarBackgroundColor = Color.FromArgb("#005493"),
                BarTextColor = Colors.White
            };
        }
    }
}
