namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08
{
    public partial class App : Application
    {
        public App()
        {
            InitializeComponent();
        }

        protected override Window CreateWindow(IActivationState? activationState)
        {
            return new Window(new NavigationPage(new Views.LoginScreen())
            {
                BarBackgroundColor = Color.FromArgb("#1976D2"),
                BarTextColor = Colors.White
            });
        }
    }
}
