using ec.edu.monster.Cliente;
using ec.edu.monster.Controlador;
using ec.edu.monster.Modelo;

namespace ec.edu.monster.Vista;

public partial class LoginPagina : ContentPage
{
    private readonly LoginControlador loginControlador;
    private readonly ServidorControlador servidorControlador = new();

    public LoginPagina(LoginControlador loginControlador, string? aviso = null)
    {
        InitializeComponent();
        this.loginControlador = loginControlador;

        lblServidor.Text = "Servidor REST: " + loginControlador.UrlServidor;
        ResaltadoCampo.Aplicar(txtUsuario, bordeUsuario);
        ResaltadoCampo.Aplicar(txtContrasenia, bordeContrasenia);

        if (!string.IsNullOrEmpty(aviso))
        {
            lblAviso.Text = aviso;
            pnlAviso.IsVisible = true;
        }
    }

    private void TxtUsuario_Completed(object? sender, EventArgs e)
    {
        txtContrasenia.Focus();
    }

    private void BtnVerContrasenia_Clicked(object? sender, EventArgs e)
    {
        txtContrasenia.IsPassword = !txtContrasenia.IsPassword;
        btnVerContrasenia.Text = txtContrasenia.IsPassword ? "Ver" : "Ocultar";
    }

    private async void BtnIngresar_Clicked(object? sender, EventArgs e)
    {
        MostrarError(null);
        pnlAviso.IsVisible = false;

        if (string.IsNullOrWhiteSpace(txtUsuario.Text) || string.IsNullOrWhiteSpace(txtContrasenia.Text))
        {
            MostrarError("Ingrese el usuario y la contraseña.");
            return;
        }

        string usuario = txtUsuario.Text.Trim();
        CambiarEstadoOcupado(true);
        try
        {
            await loginControlador.IniciarSesionAsync(new LoginModelo(usuario, txtContrasenia.Text));
        }
        catch (Exception ex) when (ex is ExcepcionClienteWS or ArgumentException)
        {
            CambiarEstadoOcupado(false);
            MostrarError(ex.Message);
            txtContrasenia.Text = string.Empty;
            return;
        }

        CambiarEstadoOcupado(false);
        App.Actual.MostrarOperaciones(loginControlador);
    }

    // Permite usar la app desde un teléfono real: allí se escribe la IP de la PC donde corre el servidor REST
    private async void BtnCambiarServidor_Clicked(object? sender, EventArgs e)
    {
        string? entrada = await DisplayPromptAsync(
                "Dirección del servidor",
                "Escriba la IP de la PC donde corre el servidor REST (por ejemplo 192.168.1.25) o la URL completa. "
                + "En el emulador de Android, 10.0.2.2 es la PC donde corre el emulador.",
                accept: "Guardar",
                cancel: "Cancelar",
                initialValue: ConfiguracionServidor.UrlActual,
                keyboard: Keyboard.Url);

        if (entrada is null)
        {
            return;
        }

        string url;
        try
        {
            url = servidorControlador.CompletarUrl(entrada);
        }
        catch (ArgumentException ex)
        {
            await DisplayAlertAsync("Dirección no válida", ex.Message, "Entendido");
            return;
        }

        // Se prueba antes de guardar: así se sabe en el momento si falla la red, el firewall o la dirección
        CambiarEstadoOcupado(true);
        ResultadoConexion resultado = await servidorControlador.ProbarAsync(url);
        CambiarEstadoOcupado(false);

        if (!resultado.Exitosa && !await DisplayAlertAsync("Sin conexión",
                resultado.Mensaje + " ¿Desea guardar la dirección de todas formas?", "Guardar", "Cancelar"))
        {
            return;
        }

        ConfiguracionServidor.UrlActual = url;
        App.Actual.MostrarInicioSesion(resultado.Exitosa
                ? "Conexión correcta. Servidor actualizado, ya puede iniciar sesión."
                : "Servidor actualizado.");
    }

    private void MostrarError(string? mensaje)
    {
        lblError.Text = mensaje;
        pnlError.IsVisible = !string.IsNullOrEmpty(mensaje);
    }

    private void CambiarEstadoOcupado(bool ocupado)
    {
        btnIngresar.IsEnabled = !ocupado;
        txtUsuario.IsEnabled = !ocupado;
        txtContrasenia.IsEnabled = !ocupado;
        btnIngresar.Text = ocupado ? "Conectando..." : "Iniciar sesión";
        indicadorCarga.IsRunning = ocupado;
        indicadorCarga.IsVisible = ocupado;
    }
}
