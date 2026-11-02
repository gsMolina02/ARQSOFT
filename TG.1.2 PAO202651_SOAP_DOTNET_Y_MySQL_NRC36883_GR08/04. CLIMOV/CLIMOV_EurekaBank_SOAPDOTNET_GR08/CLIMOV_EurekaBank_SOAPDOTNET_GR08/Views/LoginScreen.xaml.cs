using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Services;
using Microsoft.Maui.Controls.Shapes;
using System.Reflection;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Views
{
    public partial class LoginScreen : ContentPage
    {
        private readonly EurekaBankClient _client = new();
        private readonly Entry _usuarioEntry;
        private readonly Entry _claveEntry;
        private readonly Entry _servidorEntry;
        private readonly Label _messageLabel;
        private readonly ActivityIndicator _loading;
        private readonly Button _loginButton;

        public LoginScreen()
        {
            Title = string.Empty;
            BackgroundColor = Color.FromArgb("#F5F5F5");
            NavigationPage.SetHasNavigationBar(this, false);

            _usuarioEntry = Entry("Usuario");
            _claveEntry = Entry("Contraseña");
            _claveEntry.IsPassword = true;
            _servidorEntry = Entry("Servidor SOAP (ej. http://127.0.0.1:62278)");
            _servidorEntry.Text = EurekaBankClient.BaseUrl;
            _servidorEntry.FontSize = 13;
            _messageLabel = new Label { FontSize = 14, HorizontalTextAlignment = TextAlignment.Center };
            _loading = new ActivityIndicator { Color = Color.FromArgb("#1976D2"), IsVisible = false };
            _loginButton = new Button
            {
                Text = "Iniciar Sesión",
                HeightRequest = 56,
                CornerRadius = 12,
                FontSize = 16,
                FontAttributes = FontAttributes.Bold,
                BackgroundColor = Color.FromArgb("#1976D2"),
                TextColor = Colors.White,
                Margin = new Thickness(0, 8, 0, 0)
            };
            _loginButton.Clicked += OnLoginClicked;

            Content = new ScrollView
            {
                Content = new Grid
                {
                    Padding = 24,
                    MinimumHeightRequest = 640,
                    Children =
                    {
                        new VerticalStackLayout
                        {
                            Spacing = 16,
                            VerticalOptions = LayoutOptions.Center,
                            Children =
                            {
                                new Image { Source = EmbeddedImage("sulli_logo.png"), HeightRequest = 120, WidthRequest = 120, HorizontalOptions = LayoutOptions.Center, Margin = new Thickness(0, 0, 0, 12) },
                                new Image { Source = EmbeddedImage("soap_java.png"), HeightRequest = 180, Aspect = Aspect.AspectFit, HorizontalOptions = LayoutOptions.Center },
                                new Label { Text = "Sistema de Gestión Bancaria", FontSize = 18, FontAttributes = FontAttributes.Bold, TextColor = Color.FromArgb("#1976D2"), HorizontalTextAlignment = TextAlignment.Center, Margin = new Thickness(0, 0, 0, 14) },
                                Field(_usuarioEntry),
                                Field(_claveEntry),
                                Field(_servidorEntry),
                                _loginButton,
                                _loading,
                                _messageLabel
                            }
                        }
                    }
                }
            };
        }

        private async void OnLoginClicked(object? sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(_usuarioEntry.Text) || string.IsNullOrWhiteSpace(_claveEntry.Text))
            {
                ShowMessage("âš ï¸ Complete todos los campos", false);
                return;
            }

            if (!string.IsNullOrWhiteSpace(_servidorEntry.Text)) { EurekaBankClient.BaseUrl = _servidorEntry.Text.Trim(); }
            SetLoading(true);
            try
            {
                var ok = await _client.LoginAsync(_usuarioEntry.Text.Trim(), _claveEntry.Text);
                if (!ok)
                {
                    ShowMessage("âŒ Usuario o contraseÃ±a incorrectos", false);
                    return;
                }

                ShowMessage("âœ“ Inicio de sesiÃ³n exitoso", true);
                await Task.Delay(800);
                MainThread.BeginInvokeOnMainThread(() =>
                {
                    var flyout = new MainFlyoutPage(_client, _usuarioEntry.Text.Trim());
                    if (Application.Current != null)
                    {
                        if (Application.Current.Windows.Count > 0)
                            Application.Current.Windows[0].Page = flyout;
                        else
                            Application.Current.MainPage = flyout;
                    }
                });
            }
            catch (HttpRequestException ex)
            {
                ShowMessage(ex.Message, false);
            }
            catch (Exception ex)
            {
                ShowMessage($"âŒ Error: {ex.Message}", false);
            }
            finally
            {
                SetLoading(false);
            }
        }

        private static Entry Entry(string placeholder) => new()
        {
            Placeholder = placeholder,
            HeightRequest = 52,
            FontSize = 16,
            BackgroundColor = Colors.Transparent,
            TextColor = Color.FromArgb("#202124"),
            PlaceholderColor = Color.FromArgb("#777777"),
            ClearButtonVisibility = ClearButtonVisibility.WhileEditing
        };

        private static Border Field(View content) => new()
        {
            Stroke = Color.FromArgb("#BDBDBD"),
            StrokeThickness = 1,
            BackgroundColor = Colors.White,
            Padding = new Thickness(14, 0),
            StrokeShape = new RoundRectangle { CornerRadius = 4 },
            Content = content
        };

        private static ImageSource EmbeddedImage(string fileName)
        {
            var assembly = typeof(LoginScreen).GetTypeInfo().Assembly;
            var resourceName = assembly.GetManifestResourceNames()
                .FirstOrDefault(name => name.EndsWith($".Resources.Images.{fileName}", StringComparison.OrdinalIgnoreCase));

            return resourceName is null
                ? ImageSource.FromFile(fileName)
                : ImageSource.FromStream(() => assembly.GetManifestResourceStream(resourceName)!);
        }

        private void SetLoading(bool loading)
        {
            _loading.IsRunning = loading;
            _loading.IsVisible = loading;
            _loginButton.IsEnabled = !loading;
        }

        private void ShowMessage(string message, bool success)
        {
            _messageLabel.Text = message;
            _messageLabel.TextColor = success ? Color.FromArgb("#388E3C") : Color.FromArgb("#D32F2F");
        }
    }
}
