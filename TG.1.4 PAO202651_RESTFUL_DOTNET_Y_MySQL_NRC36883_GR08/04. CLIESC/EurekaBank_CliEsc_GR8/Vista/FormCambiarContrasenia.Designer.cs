namespace ec.edu.monster.Vista
{
    partial class FormCambiarContrasenia
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            pnlEncabezado = new Panel();
            btnVolver = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            lblIndicacion = new Label();
            lblTitulo = new Label();
            pnlTarjeta = new Panel();
            lblSeccion = new Label();
            lblAyuda = new Label();
            lblActual = new Label();
            txtActual = new ec.edu.monster.Vista.Controles.CampoTexto();
            lblNueva = new Label();
            txtNueva = new ec.edu.monster.Vista.Controles.CampoTexto();
            lblConfirmacion = new Label();
            txtConfirmacion = new ec.edu.monster.Vista.Controles.CampoTexto();
            lblError = new Label();
            btnCancelar = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            btnGuardar = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            barraEstado = new StatusStrip();
            lblEstado = new ToolStripStatusLabel();
            pnlEncabezado.SuspendLayout();
            pnlTarjeta.SuspendLayout();
            barraEstado.SuspendLayout();
            SuspendLayout();
            //
            // pnlEncabezado
            //
            pnlEncabezado.BackColor = Color.FromArgb(0, 84, 147);
            pnlEncabezado.Controls.Add(btnVolver);
            pnlEncabezado.Controls.Add(lblIndicacion);
            pnlEncabezado.Controls.Add(lblTitulo);
            pnlEncabezado.Dock = DockStyle.Top;
            pnlEncabezado.Location = new Point(0, 0);
            pnlEncabezado.Name = "pnlEncabezado";
            pnlEncabezado.Size = new Size(1060, 92);
            pnlEncabezado.TabIndex = 0;
            //
            // btnVolver
            //
            btnVolver.Anchor = AnchorStyles.Top | AnchorStyles.Right;
            btnVolver.BackColor = Color.FromArgb(16, 105, 170);
            btnVolver.ColorHover = Color.FromArgb(34, 124, 192);
            btnVolver.ColorPresionado = Color.FromArgb(0, 70, 122);
            btnVolver.DialogResult = DialogResult.Cancel;
            btnVolver.Font = new Font("Segoe UI", 10.5F);
            btnVolver.Location = new Point(828, 26);
            btnVolver.Name = "btnVolver";
            btnVolver.RadioBorde = 20;
            btnVolver.Size = new Size(200, 40);
            btnVolver.TabIndex = 2;
            btnVolver.Text = "←  Volver a las operaciones";
            //
            // lblIndicacion
            //
            lblIndicacion.AutoSize = true;
            lblIndicacion.Font = new Font("Segoe UI", 10F);
            lblIndicacion.ForeColor = Color.FromArgb(214, 228, 240);
            lblIndicacion.Location = new Point(34, 54);
            lblIndicacion.Name = "lblIndicacion";
            lblIndicacion.Size = new Size(380, 23);
            lblIndicacion.TabIndex = 1;
            lblIndicacion.Text = "EurekaBank REST GR08 · Seguridad de la cuenta";
            //
            // lblTitulo
            //
            lblTitulo.AutoSize = true;
            lblTitulo.Font = new Font("Segoe UI", 19F, FontStyle.Bold);
            lblTitulo.ForeColor = Color.White;
            lblTitulo.Location = new Point(30, 10);
            lblTitulo.Name = "lblTitulo";
            lblTitulo.Size = new Size(296, 45);
            lblTitulo.TabIndex = 0;
            lblTitulo.Text = "Cambiar contraseña";
            //
            // pnlTarjeta
            //
            pnlTarjeta.Anchor = AnchorStyles.None;
            pnlTarjeta.BackColor = Color.White;
            pnlTarjeta.Controls.Add(lblSeccion);
            pnlTarjeta.Controls.Add(lblAyuda);
            pnlTarjeta.Controls.Add(lblActual);
            pnlTarjeta.Controls.Add(txtActual);
            pnlTarjeta.Controls.Add(lblNueva);
            pnlTarjeta.Controls.Add(txtNueva);
            pnlTarjeta.Controls.Add(lblConfirmacion);
            pnlTarjeta.Controls.Add(txtConfirmacion);
            pnlTarjeta.Controls.Add(lblError);
            pnlTarjeta.Controls.Add(btnCancelar);
            pnlTarjeta.Controls.Add(btnGuardar);
            pnlTarjeta.Location = new Point(250, 112);
            pnlTarjeta.Name = "pnlTarjeta";
            pnlTarjeta.Size = new Size(560, 482);
            pnlTarjeta.TabIndex = 1;
            //
            // lblSeccion
            //
            lblSeccion.AutoSize = true;
            lblSeccion.Font = new Font("Segoe UI", 14F, FontStyle.Bold);
            lblSeccion.ForeColor = Color.FromArgb(33, 37, 41);
            lblSeccion.Location = new Point(38, 24);
            lblSeccion.Name = "lblSeccion";
            lblSeccion.Size = new Size(281, 32);
            lblSeccion.TabIndex = 0;
            lblSeccion.Text = "Actualice su contraseña";
            //
            // lblAyuda
            //
            lblAyuda.Font = new Font("Segoe UI", 10F);
            lblAyuda.ForeColor = Color.FromArgb(108, 117, 125);
            lblAyuda.Location = new Point(40, 58);
            lblAyuda.Name = "lblAyuda";
            lblAyuda.Size = new Size(480, 24);
            lblAyuda.TabIndex = 1;
            lblAyuda.Text = "Mínimo 6 caracteres y diferente a la contraseña actual.";
            //
            // lblActual
            //
            lblActual.AutoSize = true;
            lblActual.Font = new Font("Segoe UI", 10.5F);
            lblActual.ForeColor = Color.FromArgb(73, 80, 87);
            lblActual.Location = new Point(40, 98);
            lblActual.Name = "lblActual";
            lblActual.Size = new Size(155, 25);
            lblActual.TabIndex = 2;
            lblActual.Text = "Contraseña actual";
            //
            // txtActual
            //
            txtActual.EsContrasena = true;
            txtActual.Font = new Font("Segoe UI", 12F);
            txtActual.Location = new Point(40, 126);
            txtActual.MaxLength = 50;
            txtActual.Name = "txtActual";
            txtActual.PlaceholderText = "Escriba su contraseña actual";
            txtActual.Size = new Size(480, 48);
            txtActual.TabIndex = 3;
            //
            // lblNueva
            //
            lblNueva.AutoSize = true;
            lblNueva.Font = new Font("Segoe UI", 10.5F);
            lblNueva.ForeColor = Color.FromArgb(73, 80, 87);
            lblNueva.Location = new Point(40, 188);
            lblNueva.Name = "lblNueva";
            lblNueva.Size = new Size(152, 25);
            lblNueva.TabIndex = 4;
            lblNueva.Text = "Nueva contraseña";
            //
            // txtNueva
            //
            txtNueva.EsContrasena = true;
            txtNueva.Font = new Font("Segoe UI", 12F);
            txtNueva.Location = new Point(40, 216);
            txtNueva.MaxLength = 50;
            txtNueva.Name = "txtNueva";
            txtNueva.PlaceholderText = "Escriba la nueva contraseña";
            txtNueva.Size = new Size(480, 48);
            txtNueva.TabIndex = 5;
            //
            // lblConfirmacion
            //
            lblConfirmacion.AutoSize = true;
            lblConfirmacion.Font = new Font("Segoe UI", 10.5F);
            lblConfirmacion.ForeColor = Color.FromArgb(73, 80, 87);
            lblConfirmacion.Location = new Point(40, 278);
            lblConfirmacion.Name = "lblConfirmacion";
            lblConfirmacion.Size = new Size(234, 25);
            lblConfirmacion.TabIndex = 6;
            lblConfirmacion.Text = "Confirmar nueva contraseña";
            //
            // txtConfirmacion
            //
            txtConfirmacion.EsContrasena = true;
            txtConfirmacion.Font = new Font("Segoe UI", 12F);
            txtConfirmacion.Location = new Point(40, 306);
            txtConfirmacion.MaxLength = 50;
            txtConfirmacion.Name = "txtConfirmacion";
            txtConfirmacion.PlaceholderText = "Repita la nueva contraseña";
            txtConfirmacion.Size = new Size(480, 48);
            txtConfirmacion.TabIndex = 7;
            //
            // lblError
            //
            lblError.Font = new Font("Segoe UI", 10F);
            lblError.ForeColor = Color.FromArgb(192, 0, 0);
            lblError.Location = new Point(40, 360);
            lblError.Name = "lblError";
            lblError.Size = new Size(480, 42);
            lblError.TabIndex = 8;
            //
            // btnCancelar
            //
            btnCancelar.BackColor = Color.FromArgb(233, 236, 239);
            btnCancelar.ColorHover = Color.FromArgb(222, 226, 230);
            btnCancelar.ColorPresionado = Color.FromArgb(206, 212, 218);
            btnCancelar.DialogResult = DialogResult.Cancel;
            btnCancelar.Font = new Font("Segoe UI", 11F);
            btnCancelar.ForeColor = Color.FromArgb(73, 80, 87);
            btnCancelar.Location = new Point(40, 406);
            btnCancelar.Name = "btnCancelar";
            btnCancelar.RadioBorde = 12;
            btnCancelar.Size = new Size(232, 52);
            btnCancelar.TabIndex = 10;
            btnCancelar.Text = "Cancelar";
            //
            // btnGuardar
            //
            btnGuardar.Font = new Font("Segoe UI", 11F, FontStyle.Bold);
            btnGuardar.Location = new Point(288, 406);
            btnGuardar.Name = "btnGuardar";
            btnGuardar.RadioBorde = 12;
            btnGuardar.Size = new Size(232, 52);
            btnGuardar.TabIndex = 9;
            btnGuardar.Text = "Guardar cambios";
            btnGuardar.Click += btnGuardar_Click;
            //
            // barraEstado
            //
            barraEstado.BackColor = Color.White;
            barraEstado.ImageScalingSize = new Size(20, 20);
            barraEstado.Items.AddRange(new ToolStripItem[] { lblEstado });
            barraEstado.Location = new Point(0, 614);
            barraEstado.Name = "barraEstado";
            barraEstado.Padding = new Padding(12, 0, 16, 0);
            barraEstado.Size = new Size(1060, 26);
            barraEstado.SizingGrip = false;
            barraEstado.TabIndex = 2;
            //
            // lblEstado
            //
            lblEstado.ForeColor = Color.DimGray;
            lblEstado.Name = "lblEstado";
            lblEstado.Size = new Size(41, 20);
            lblEstado.Text = "Listo";
            //
            // FormCambiarContrasenia
            //
            AcceptButton = btnGuardar;
            AutoScaleDimensions = new SizeF(96F, 96F);
            AutoScaleMode = AutoScaleMode.Dpi;
            BackColor = Color.FromArgb(244, 246, 249);
            CancelButton = btnCancelar;
            ClientSize = new Size(1060, 640);
            Controls.Add(pnlTarjeta);
            Controls.Add(barraEstado);
            Controls.Add(pnlEncabezado);
            Font = new Font("Segoe UI", 10F);
            FormBorderStyle = FormBorderStyle.FixedSingle;
            MaximizeBox = false;
            Name = "FormCambiarContrasenia";
            ShowInTaskbar = false;
            StartPosition = FormStartPosition.CenterScreen;
            Text = "EurekaBank REST GR08 - Cambiar contraseña";
            pnlEncabezado.ResumeLayout(false);
            pnlEncabezado.PerformLayout();
            pnlTarjeta.ResumeLayout(false);
            pnlTarjeta.PerformLayout();
            barraEstado.ResumeLayout(false);
            barraEstado.PerformLayout();
            ResumeLayout(false);
            PerformLayout();
        }

        #endregion

        private Panel pnlEncabezado;
        private Controles.BotonRedondeado btnVolver;
        private Label lblIndicacion;
        private Label lblTitulo;
        private Panel pnlTarjeta;
        private Label lblSeccion;
        private Label lblAyuda;
        private Label lblActual;
        private Controles.CampoTexto txtActual;
        private Label lblNueva;
        private Controles.CampoTexto txtNueva;
        private Label lblConfirmacion;
        private Controles.CampoTexto txtConfirmacion;
        private Label lblError;
        private Controles.BotonRedondeado btnCancelar;
        private Controles.BotonRedondeado btnGuardar;
        private StatusStrip barraEstado;
        private ToolStripStatusLabel lblEstado;
    }
}
