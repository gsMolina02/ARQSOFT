using System.Drawing.Drawing2D;
using ec.edu.monster.Controlador;
using ec.edu.monster.Modelo;

namespace ec.edu.monster.Vista
{
    /// <summary>
    /// Ventana para escribir la dirección del servidor REST EurekaBank cuando corre en otra computadora de la red.
    /// Tiene el mismo estilo que DialogoConfirmacion. Devuelve OK cuando se guardó una dirección nueva.
    /// </summary>
    public partial class FormServidor : Form
    {
        private static readonly Color ColorBorde = Color.FromArgb(206, 212, 218);
        private static readonly Color ColorIconoFondo = Color.FromArgb(225, 234, 244);
        private static readonly Color ColorIconoTexto = Color.FromArgb(0, 84, 147);
        private static readonly Color ColorExito = Color.FromArgb(25, 135, 84);
        private static readonly Color ColorError = Color.FromArgb(192, 0, 0);

        private readonly ServidorControlador servidorControlador;

        public FormServidor(ServidorControlador servidorControlador)
        {
            InitializeComponent();
            this.servidorControlador = servidorControlador;
            txtServidor.Text = servidorControlador.UrlActual;
        }

        // Sombra alrededor de la ventana (no tiene barra de título)
        protected override CreateParams CreateParams
        {
            get
            {
                const int SombraVentana = 0x00020000;
                CreateParams parametros = base.CreateParams;
                parametros.ClassStyle |= SombraVentana;
                return parametros;
            }
        }

        protected override void OnShown(EventArgs e)
        {
            base.OnShown(e);
            txtServidor.Focus();
            txtServidor.SelectAll();
        }

        // Contorno fino gris para separar la ventana del fondo
        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);
            using var lapiz = new Pen(ColorBorde);
            e.Graphics.DrawRectangle(lapiz, 0, 0, ClientSize.Width - 1, ClientSize.Height - 1);
        }

        private async void btnProbar_Click(object sender, EventArgs e)
        {
            await ProbarAsync();
        }

        private async void btnGuardar_Click(object sender, EventArgs e)
        {
            ResultadoConexion? resultado = await ProbarAsync();
            if (resultado is null)
            {
                return;
            }
            if (!resultado.Exitosa && !DialogoConfirmacion.Confirmar(this, "Sin conexión",
                    "No se pudo conectar con esa dirección. ¿Desea guardarla de todas formas?", "Guardar"))
            {
                return;
            }

            servidorControlador.Guardar(txtServidor.Text);
            DialogResult = DialogResult.OK;
            Close();
        }

        /// <returns>El resultado de la prueba, o null si la dirección escrita no es válida.</returns>
        private async Task<ResultadoConexion?> ProbarAsync()
        {
            string url;
            try
            {
                url = servidorControlador.CompletarUrl(txtServidor.Text);
            }
            catch (ArgumentException ex)
            {
                MostrarEstado(ex.Message, ColorError);
                txtServidor.Focus();
                return null;
            }

            txtServidor.Text = url;
            CambiarEstadoOcupado(true);
            MostrarEstado("Probando la conexión con " + url + " ...", Color.FromArgb(108, 117, 125));
            ResultadoConexion resultado = await servidorControlador.ProbarAsync(url);
            CambiarEstadoOcupado(false);

            MostrarEstado(resultado.Mensaje, resultado.Exitosa ? ColorExito : ColorError);
            return resultado;
        }

        private void MostrarEstado(string mensaje, Color color)
        {
            lblEstado.ForeColor = color;
            lblEstado.Text = mensaje;
        }

        private void CambiarEstadoOcupado(bool ocupado)
        {
            txtServidor.Enabled = !ocupado;
            btnProbar.Enabled = !ocupado;
            btnGuardar.Enabled = !ocupado;
            btnCancelar.Enabled = !ocupado;
            btnProbar.Text = ocupado ? "Probando..." : "Probar conexión";
            UseWaitCursor = ocupado;
        }

        private void pnlIcono_Paint(object? sender, PaintEventArgs e)
        {
            e.Graphics.SmoothingMode = SmoothingMode.AntiAlias;
            e.Graphics.TextRenderingHint = System.Drawing.Text.TextRenderingHint.AntiAliasGridFit;

            var area = new Rectangle(0, 0, pnlIcono.Width - 1, pnlIcono.Height - 1);
            using var fondo = new SolidBrush(ColorIconoFondo);
            e.Graphics.FillEllipse(fondo, area);

            using var fuente = new Font("Segoe UI Symbol", 22F, FontStyle.Bold);
            TextRenderer.DrawText(e.Graphics, "⚙", fuente, area, ColorIconoTexto,
                    TextFormatFlags.HorizontalCenter | TextFormatFlags.VerticalCenter | TextFormatFlags.NoPadding);
        }
    }
}
