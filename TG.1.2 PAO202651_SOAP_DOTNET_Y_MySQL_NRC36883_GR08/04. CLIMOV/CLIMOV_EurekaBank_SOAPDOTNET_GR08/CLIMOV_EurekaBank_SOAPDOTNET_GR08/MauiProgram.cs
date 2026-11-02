using CLIMOV_EurekaBank_SOAPDOTNET_GR08.ViewModels;
using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Views;
using Microsoft.Extensions.Logging;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08
{
    public static class MauiProgram
    {
        public static MauiApp CreateMauiApp()
        {
            var builder = MauiApp.CreateBuilder();
            builder
                .UseMauiApp<App>()
                .ConfigureFonts(fonts =>
                {
                    fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
                    fonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
                });

#if DEBUG
    		builder.Logging.AddDebug();
#endif         

            return builder.Build();
        }
    }
}
