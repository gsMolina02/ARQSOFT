namespace ec.edu.monster.Vista
{
    partial class DialogoConfirmacion
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
            pnlIcono = new Panel();
            lblTitulo = new Label();
            lblMensaje = new Label();
            btnCancelar = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            btnAceptar = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            SuspendLayout();
            //
            // pnlIcono
            //
            pnlIcono.BackColor = Color.White;
            pnlIcono.Location = new Point(32, 32);
            pnlIcono.Name = "pnlIcono";
            pnlIcono.Size = new Size(60, 60);
            pnlIcono.TabIndex = 0;
            pnlIcono.Paint += pnlIcono_Paint;
            //
            // lblTitulo
            //
            lblTitulo.Font = new Font("Segoe UI", 15F, FontStyle.Bold);
            lblTitulo.ForeColor = Color.FromArgb(33, 37, 41);
            lblTitulo.Location = new Point(110, 30);
            lblTitulo.Name = "lblTitulo";
            lblTitulo.Size = new Size(330, 36);
            lblTitulo.TabIndex = 1;
            lblTitulo.Text = "Título";
            //
            // lblMensaje
            //
            lblMensaje.Font = new Font("Segoe UI", 10.5F);
            lblMensaje.ForeColor = Color.FromArgb(73, 80, 87);
            lblMensaje.Location = new Point(112, 70);
            lblMensaje.Name = "lblMensaje";
            lblMensaje.Size = new Size(328, 62);
            lblMensaje.TabIndex = 2;
            lblMensaje.Text = "Mensaje";
            //
            // btnCancelar
            //
            btnCancelar.BackColor = Color.FromArgb(233, 236, 239);
            btnCancelar.ColorHover = Color.FromArgb(222, 226, 230);
            btnCancelar.ColorPresionado = Color.FromArgb(206, 212, 218);
            btnCancelar.DialogResult = DialogResult.Cancel;
            btnCancelar.Font = new Font("Segoe UI", 10.5F);
            btnCancelar.ForeColor = Color.FromArgb(73, 80, 87);
            btnCancelar.Location = new Point(132, 150);
            btnCancelar.Name = "btnCancelar";
            btnCancelar.RadioBorde = 12;
            btnCancelar.Size = new Size(144, 46);
            btnCancelar.TabIndex = 4;
            btnCancelar.Text = "Cancelar";
            //
            // btnAceptar
            //
            btnAceptar.DialogResult = DialogResult.OK;
            btnAceptar.Font = new Font("Segoe UI", 10.5F, FontStyle.Bold);
            btnAceptar.Location = new Point(288, 150);
            btnAceptar.Name = "btnAceptar";
            btnAceptar.RadioBorde = 12;
            btnAceptar.Size = new Size(152, 46);
            btnAceptar.TabIndex = 3;
            btnAceptar.Text = "Aceptar";
            //
            // DialogoConfirmacion
            //
            AcceptButton = btnAceptar;
            AutoScaleDimensions = new SizeF(96F, 96F);
            AutoScaleMode = AutoScaleMode.Dpi;
            BackColor = Color.White;
            CancelButton = btnCancelar;
            ClientSize = new Size(472, 222);
            Controls.Add(btnAceptar);
            Controls.Add(btnCancelar);
            Controls.Add(lblMensaje);
            Controls.Add(lblTitulo);
            Controls.Add(pnlIcono);
            Font = new Font("Segoe UI", 10F);
            FormBorderStyle = FormBorderStyle.None;
            Name = "DialogoConfirmacion";
            ShowInTaskbar = false;
            StartPosition = FormStartPosition.CenterParent;
            Text = "EurekaBank REST GR08";
            ResumeLayout(false);
        }

        #endregion

        private Panel pnlIcono;
        private Label lblTitulo;
        private Label lblMensaje;
        private Controles.BotonRedondeado btnCancelar;
        private Controles.BotonRedondeado btnAceptar;
    }
}
