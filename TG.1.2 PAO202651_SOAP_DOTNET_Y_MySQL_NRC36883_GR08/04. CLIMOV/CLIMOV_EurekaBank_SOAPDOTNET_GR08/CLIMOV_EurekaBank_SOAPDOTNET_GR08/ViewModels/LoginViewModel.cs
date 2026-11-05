using System.ComponentModel;
using System.Windows.Input;
using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Services;
using CLIMOV_EurekaBank_SOAPDOTNET_GR08.Views;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.ViewModels
{
    public class LoginViewModel : INotifyPropertyChanged
    {
        private string _usuario = string.Empty;
        private string _clave = string.Empty;
        private string _servidor = EurekaBankClient.BaseUrl;
        private bool _isLoading;
        private string _mensaje = string.Empty;
        private bool _esExitoso;

        public string Usuario
        {
            get => _usuario;
            set { _usuario = value; OnPropertyChanged(nameof(Usuario)); }
        }

        public string Clave
        {
            get => _clave;
            set { _clave = value; OnPropertyChanged(nameof(Clave)); }
        }

        public string Servidor
        {
            get => _servidor;
            set { _servidor = value; OnPropertyChanged(nameof(Servidor)); }
        }

        public bool IsLoading
        {
            get => _isLoading;
            set { _isLoading = value; OnPropertyChanged(nameof(IsLoading)); }
        }

        public string Mensaje
        {
            get => _mensaje;
            set { _mensaje = value; OnPropertyChanged(nameof(Mensaje)); }
        }

        public bool EsExitoso
        {
            get => _esExitoso;
            set { _esExitoso = value; OnPropertyChanged(nameof(EsExitoso)); }
        }

        public ICommand LoginCommand { get; }

        public LoginViewModel()
        {
            LoginCommand = new Microsoft.Maui.Controls.Command(async () =>
            {
                if (string.IsNullOrWhiteSpace(Usuario) || string.IsNullOrWhiteSpace(Clave))
                {
                    EsExitoso = false;
                    Mensaje = "Complete todos los campos";
                    return;
                }

                if (!string.IsNullOrWhiteSpace(Servidor))
                {
                    string s = Servidor.Trim();
                    if (!s.StartsWith("http://", StringComparison.OrdinalIgnoreCase) &&
                        !s.StartsWith("https://", StringComparison.OrdinalIgnoreCase))
                    {
                        if (s.StartsWith("://"))
                            s = "https" + s;
                        else if (s.Contains(".trycloudflare.com"))
                            s = "https://" + s;
                        else
                            s = "http://" + s;
                    }
                    EurekaBankClient.BaseUrl = s.TrimEnd('/');
                }

                IsLoading = true;
                Mensaje = string.Empty;

                try
                {
                    var client = new EurekaBankClient();
                    bool ok = await client.LoginAsync(Usuario.Trim(), Clave);
                    if (!ok)
                    {
                        EsExitoso = false;
                        Mensaje = "Usuario o contraseña incorrectos";
                        return;
                    }

                    EsExitoso = true;
                    Mensaje = "Inicio de sesión exitoso";
                    await Task.Delay(400);

                    MainThread.BeginInvokeOnMainThread(() =>
                    {
                        var mainFlyout = new MainFlyoutPage(client, Usuario.Trim());
                        if (Application.Current != null)
                        {
                            if (Application.Current.Windows.Count > 0)
                                Application.Current.Windows[0].Page = mainFlyout;
                            else
                                Application.Current.MainPage = mainFlyout;
                        }
                    });
                }
                catch (HttpRequestException ex)
                {
                    EsExitoso = false;
                    Mensaje = ex.Message;
                }
                catch (Exception ex)
                {
                    EsExitoso = false;
                    Mensaje = $"Error de conexión: {ex.Message}";
                }
                finally
                {
                    IsLoading = false;
                }
            });
        }

        public event PropertyChangedEventHandler? PropertyChanged;

        protected void OnPropertyChanged(string propertyName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
