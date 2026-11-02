using Microsoft.UI.Xaml;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.WinUI
{
    public partial class App : MauiWinUIApplication
    {
        public App()
        {
            this.InitializeComponent();
        }

        protected override MauiApp CreateMauiApp() => MauiProgram.CreateMauiApp();
    }
}
