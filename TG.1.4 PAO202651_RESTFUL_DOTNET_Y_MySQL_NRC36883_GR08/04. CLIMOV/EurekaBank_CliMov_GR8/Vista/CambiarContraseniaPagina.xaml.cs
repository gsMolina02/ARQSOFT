using ec.edu.monster.Cliente;
using ec.edu.monster.Controlador;

namespace ec.edu.monster.Vista;

public partial class CambiarContraseniaPagina : ContentPage
{
    private readonly LoginControlador loginControlador;
    private readonly Dictionary<Button, Entry> botonesVer;

    public CambiarContraseniaPagina(LoginControlador loginControlador)
    {
        InitializeComponent();
        this.loginControlador = loginControlador;

        botonesVer = new Dictionary<Button, Entry>
        {
            [btnVerActual] = txtActual,
            [btnVerNueva] = txtNueva,
            [btnVerConfirmacion] = txtConfirmacion
        };

        ResaltadoCampo.Aplicar(txtActual, bordeActual);
        ResaltadoCampo.Aplicar(txtNueva, bordeNueva);
        ResaltadoCampo.Aplicar(txtConfirmacion, bordeConfirmacion);
    }

    private void TxtActual_Completed(object? sender, EventArgs e) => txtNueva.Focus();

    private void TxtNueva_Completed(object? sender, EventArgs e) => txtConfirmacion.Focus();

    // Cada campo tiene su propio botón para mostrar u ocultar lo escrito
    private void BtnVer_Clicked(object? sender, EventArgs e)
    {
        if (sender is Button boton && botonesVer.TryGetValue(boton, out Entry? campo))
        {
            campo.IsPassword = !campo.IsPassword;
            boton.Text = campo.IsPassword ? "Ver" : "Ocultar";
        }
    }

    private async void BtnCancelar_Clicked(object? sender, EventArgs e)
    {
        await Navigation.PopAsync();
    }

    private async void BtnGuardar_Clicked(object? sender, EventArgs e)
    {
        MostrarError(null);

        if (string.IsNullOrWhiteSpace(txtActual.Text) || string.IsNullOrWhiteSpace(txtNueva.Text)
                || string.IsNullOrWhiteSpace(txtConfirmacion.Text))
        {
            MostrarError("Complete los tres campos.");
            return;
        }
        if (txtNueva.Text != txtConfirmacion.Text)
        {
            MostrarError("La confirmación no coincide con la nueva contraseña.");
            ResaltadoCampo.MarcarError(bordeConfirmacion);
            return;
        }

        CambiarEstadoOcupado(true);
        try
        {
            string mensaje = await loginControlador.CambiarClaveAsync(txtActual.Text, txtNueva.Text, txtConfirmacion.Text);
            await DisplayAlertAsync("Contraseña actualizada", mensaje, "Aceptar");
            await Navigation.PopAsync();
        }
        catch (ExcepcionClienteWS ex) when (ex.EsErrorAutenticacion)
        {
            await DisplayAlertAsync("Sesión no válida", ex.Message + "\n\nDebe iniciar sesión nuevamente.", "Entendido");
            App.Actual.MostrarInicioSesion("Inicie sesión para continuar.");
        }
        catch (Exception ex) when (ex is ExcepcionClienteWS or ArgumentException)
        {
            MostrarError(ex.Message);
        }
        finally
        {
            CambiarEstadoOcupado(false);
        }
    }

    private void MostrarError(string? mensaje)
    {
        lblError.Text = mensaje;
        pnlError.IsVisible = !string.IsNullOrEmpty(mensaje);
    }

    private void CambiarEstadoOcupado(bool ocupado)
    {
        txtActual.IsEnabled = !ocupado;
        txtNueva.IsEnabled = !ocupado;
        txtConfirmacion.IsEnabled = !ocupado;
        btnGuardar.IsEnabled = !ocupado;
        btnCancelar.IsEnabled = !ocupado;
        btnGuardar.Text = ocupado ? "Guardando..." : "Guardar";
    }
}
