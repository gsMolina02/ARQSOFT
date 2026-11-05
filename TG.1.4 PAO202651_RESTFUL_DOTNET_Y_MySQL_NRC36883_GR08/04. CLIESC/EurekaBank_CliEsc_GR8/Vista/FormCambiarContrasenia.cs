using ec.edu.monster.Cliente;
using ec.edu.monster.Controlador;
using ec.edu.monster.Vista.Controles;

namespace ec.edu.monster.Vista
{
    /// <summary>
    /// Pantalla de cambio de contraseña. Tiene el mismo tamaño que la pantalla de operaciones y se abre en su misma
    /// posición, de modo que se ve como otra pantalla de la aplicación y no como una ventana flotante.
    /// </summary>
    public partial class FormCambiarContrasenia : Form
    {
        private readonly LoginControlador loginControlador;
        private readonly string textoEstado;

        // Si el servidor responde 401, la ventana principal debe volver al inicio de sesión
        public bool SesionExpirada { get; private set; }

        public FormCambiarContrasenia(LoginControlador loginControlador)
        {
            InitializeComponent();
            this.loginControlador = loginControlador;
            textoEstado = $"Conectado a {loginControlador.UrlServidor}";
            lblEstado.Text = textoEstado;
        }

        private async void btnGuardar_Click(object? sender, EventArgs e)
        {
            lblError.Text = string.Empty;

            if (string.IsNullOrWhiteSpace(txtActual.Text) || string.IsNullOrWhiteSpace(txtNueva.Text)
                    || string.IsNullOrWhiteSpace(txtConfirmacion.Text))
            {
                lblError.Text = "Complete los tres campos.";
                PrimerCampoVacio().Focus();
                return;
            }
            if (txtNueva.Text != txtConfirmacion.Text)
            {
                lblError.Text = "La confirmación no coincide con la nueva contraseña.";
                txtConfirmacion.SelectAll();
                txtConfirmacion.Focus();
                return;
            }

            CambiarEstadoOcupado(true);
            try
            {
                string mensaje = await loginControlador.CambiarClaveAsync(txtActual.Text, txtNueva.Text, txtConfirmacion.Text);
                DialogoConfirmacion.Informar(this, "¡Contraseña actualizada!",
                        $"{mensaje}. Use la nueva contraseña la próxima vez que inicie sesión.");
                DialogResult = DialogResult.OK;
            }
            catch (ExcepcionClienteWS ex) when (ex.EsErrorAutenticacion)
            {
                DialogoConfirmacion.Informar(this, "Sesión no válida",
                        ex.Message + " Debe iniciar sesión nuevamente.", icono: "!");
                SesionExpirada = true;
                DialogResult = DialogResult.Abort;
            }
            catch (ExcepcionClienteWS ex)
            {
                lblError.Text = ex.Message;
            }
            catch (ArgumentException ex)
            {
                lblError.Text = ex.Message;
            }
            finally
            {
                CambiarEstadoOcupado(false);
            }
        }

        private CampoTexto PrimerCampoVacio()
        {
            if (string.IsNullOrWhiteSpace(txtActual.Text))
            {
                return txtActual;
            }
            return string.IsNullOrWhiteSpace(txtNueva.Text) ? txtNueva : txtConfirmacion;
        }

        private void CambiarEstadoOcupado(bool ocupado)
        {
            txtActual.Enabled = !ocupado;
            txtNueva.Enabled = !ocupado;
            txtConfirmacion.Enabled = !ocupado;
            btnGuardar.Enabled = !ocupado;
            btnCancelar.Enabled = !ocupado;
            btnVolver.Enabled = !ocupado;
            btnGuardar.Text = ocupado ? "Guardando..." : "Guardar cambios";
            lblEstado.Text = ocupado ? "Guardando la nueva contraseña..." : textoEstado;
            UseWaitCursor = ocupado;
        }
    }
}
