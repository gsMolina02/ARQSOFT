using System.Drawing.Drawing2D;

namespace ec.edu.monster.Vista
{
    /// <summary>
    /// Ventana de confirmación con el estilo de la aplicación (reemplaza al MessageBox de Windows):
    /// ícono en un círculo, título, explicación y botones redondeados.
    /// </summary>
    public partial class DialogoConfirmacion : Form
    {
        private static readonly Color ColorBorde = Color.FromArgb(206, 212, 218);
        private static readonly Color ColorIconoFondo = Color.FromArgb(225, 234, 244);
        private static readonly Color ColorIconoTexto = Color.FromArgb(0, 84, 147);

        private readonly string icono;

        // Diálogo con dos botones (confirmar y cancelar) o con uno solo si textoCancelar es null
        public DialogoConfirmacion(string titulo, string mensaje, string textoAceptar,
                string? textoCancelar = "Cancelar", string icono = "?")
        {
            InitializeComponent();
            this.icono = icono;
            lblTitulo.Text = titulo;
            lblMensaje.Text = mensaje;
            btnAceptar.Text = textoAceptar;

            if (textoCancelar is null)
            {
                // Un solo botón: ocupa el lugar del botón de la derecha
                btnCancelar.Visible = false;
                CancelButton = btnAceptar;
            }
            else
            {
                btnCancelar.Text = textoCancelar;
            }
        }

        // Pregunta al usuario y devuelve true si confirmó
        public static bool Confirmar(IWin32Window duenio, string titulo, string mensaje, string textoAceptar)
        {
            using var dialogo = new DialogoConfirmacion(titulo, mensaje, textoAceptar);
            return dialogo.ShowDialog(duenio) == DialogResult.OK;
        }

        // Muestra un aviso con un solo botón "Entendido"
        public static void Informar(IWin32Window duenio, string titulo, string mensaje, string icono = "✓")
        {
            using var dialogo = new DialogoConfirmacion(titulo, mensaje, "Entendido", textoCancelar: null, icono);
            dialogo.ShowDialog(duenio);
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

        // Contorno fino gris para separar la ventana del fondo
        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);
            using var lapiz = new Pen(ColorBorde);
            e.Graphics.DrawRectangle(lapiz, 0, 0, ClientSize.Width - 1, ClientSize.Height - 1);
        }

        private void pnlIcono_Paint(object? sender, PaintEventArgs e)
        {
            e.Graphics.SmoothingMode = SmoothingMode.AntiAlias;
            e.Graphics.TextRenderingHint = System.Drawing.Text.TextRenderingHint.AntiAliasGridFit;

            var area = new Rectangle(0, 0, pnlIcono.Width - 1, pnlIcono.Height - 1);
            using var fondo = new SolidBrush(ColorIconoFondo);
            e.Graphics.FillEllipse(fondo, area);

            using var fuente = new Font("Segoe UI", 22F, FontStyle.Bold);
            TextRenderer.DrawText(e.Graphics, icono, fuente, area, ColorIconoTexto,
                    TextFormatFlags.HorizontalCenter | TextFormatFlags.VerticalCenter | TextFormatFlags.NoPadding);
        }
    }
}
