using Microsoft.Extensions.Logging;
using Microsoft.Maui.Handlers;

namespace ec.edu.monster
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

            QuitarSubrayadoDeLosCampos();

#if DEBUG
            builder.Logging.AddDebug();
#endif

            return builder.Build();
        }

        // Android dibuja una línea debajo de los campos de texto y listas; los campos de la app
        // ya tienen su propio borde redondeado, así que se quita esa línea
        private static void QuitarSubrayadoDeLosCampos()
        {
            EntryHandler.Mapper.AppendToMapping("SinSubrayado", (manejador, _) =>
            {
#if ANDROID
                manejador.PlatformView.BackgroundTintList =
                        Android.Content.Res.ColorStateList.ValueOf(Android.Graphics.Color.Transparent);
#elif WINDOWS
                manejador.PlatformView.BorderThickness = new Microsoft.UI.Xaml.Thickness(0);
#endif
            });

            PickerHandler.Mapper.AppendToMapping("SinSubrayado", (manejador, _) =>
            {
#if ANDROID
                manejador.PlatformView.BackgroundTintList =
                        Android.Content.Res.ColorStateList.ValueOf(Android.Graphics.Color.Transparent);
#elif WINDOWS
                manejador.PlatformView.BorderThickness = new Microsoft.UI.Xaml.Thickness(0);
#endif
            });
        }
    }
}
