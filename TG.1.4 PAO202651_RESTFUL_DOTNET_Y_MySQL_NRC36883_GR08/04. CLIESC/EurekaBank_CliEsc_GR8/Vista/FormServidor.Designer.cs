namespace ec.edu.monster.Vista
{
    partial class FormServidor
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
            txtServidor = new ec.edu.monster.Vista.Controles.CampoTexto();
            lblEstado = new Label();
            btnProbar = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            btnCancelar = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            btnGuardar = new ec.edu.monster.Vista.Controles.BotonRedondeado();
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
            lblTitulo.Size = new Size(378, 36);
            lblTitulo.TabIndex = 1;
            lblTitulo.Text = "Servidor REST";
            //
            // lblMensaje
            //
            lblMensaje.Font = new Font("Segoe UI", 10F);
            lblMensaje.ForeColor = Color.FromArgb(73, 80, 87);
            lblMensaje.Location = new Point(112, 70);
            lblMensaje.Name = "lblMensaje";
            lblMensaje.Size = new Size(376, 66);
            lblMensaje.TabIndex = 2;
            lblMensaje.Text = "Escriba la IP de la computadora donde corre el servidor (ej.: 192.168.1.25) " +
                "o la dirección completa. Para usar esta computadora escriba localhost.";
            //
            // txtServidor
            //
            txtServidor.Font = new Font("Segoe UI", 11F);
            txtServidor.Location = new Point(32, 144);
            txtServidor.MaxLength = 200;
            txtServidor.Name = "txtServidor";
            txtServidor.PlaceholderText = "Ej.: 192.168.1.25";
            txtServidor.Size = new Size(456, 46);
            txtServidor.TabIndex = 3;
            //
            // lblEstado
            //
            lblEstado.Font = new Font("Segoe UI", 9.5F);
            lblEstado.ForeColor = Color.FromArgb(108, 117, 125);
            lblEstado.Location = new Point(32, 196);
            lblEstado.Name = "lblEstado";
            lblEstado.Size = new Size(456, 58);
            lblEstado.TabIndex = 7;
            //
            // btnProbar
            //
            btnProbar.BackColor = Color.FromArgb(233, 236, 239);
            btnProbar.ColorHover = Color.FromArgb(222, 226, 230);
            btnProbar.ColorPresionado = Color.FromArgb(206, 212, 218);
            btnProbar.Font = new Font("Segoe UI", 10.5F);
            btnProbar.ForeColor = Color.FromArgb(0, 84, 147);
            btnProbar.Location = new Point(32, 262);
            btnProbar.Name = "btnProbar";
            btnProbar.RadioBorde = 12;
            btnProbar.Size = new Size(160, 46);
            btnProbar.TabIndex = 4;
            btnProbar.Text = "Probar conexión";
            btnProbar.Click += btnProbar_Click;
            //
            // btnCancelar
            //
            btnCancelar.BackColor = Color.FromArgb(233, 236, 239);
            btnCancelar.ColorHover = Color.FromArgb(222, 226, 230);
            btnCancelar.ColorPresionado = Color.FromArgb(206, 212, 218);
            btnCancelar.DialogResult = DialogResult.Cancel;
            btnCancelar.Font = new Font("Segoe UI", 10.5F);
            btnCancelar.ForeColor = Color.FromArgb(73, 80, 87);
            btnCancelar.Location = new Point(220, 262);
            btnCancelar.Name = "btnCancelar";
            btnCancelar.RadioBorde = 12;
            btnCancelar.Size = new Size(124, 46);
            btnCancelar.TabIndex = 6;
            btnCancelar.Text = "Cancelar";
            //
            // btnGuardar
            //
            btnGuardar.Font = new Font("Segoe UI", 10.5F, FontStyle.Bold);
            btnGuardar.Location = new Point(356, 262);
            btnGuardar.Name = "btnGuardar";
            btnGuardar.RadioBorde = 12;
            btnGuardar.Size = new Size(132, 46);
            btnGuardar.TabIndex = 5;
            btnGuardar.Text = "Guardar";
            btnGuardar.Click += btnGuardar_Click;
            //
            // FormServidor
            //
            AcceptButton = btnGuardar;
            AutoScaleDimensions = new SizeF(96F, 96F);
            AutoScaleMode = AutoScaleMode.Dpi;
            BackColor = Color.White;
            CancelButton = btnCancelar;
            ClientSize = new Size(520, 336);
            Controls.Add(btnGuardar);
            Controls.Add(btnCancelar);
            Controls.Add(btnProbar);
            Controls.Add(lblEstado);
            Controls.Add(txtServidor);
            Controls.Add(lblMensaje);
            Controls.Add(lblTitulo);
            Controls.Add(pnlIcono);
            Font = new Font("Segoe UI", 10F);
            FormBorderStyle = FormBorderStyle.None;
            Name = "FormServidor";
            ShowInTaskbar = false;
            StartPosition = FormStartPosition.CenterParent;
            Text = "EurekaBank REST GR08 - Servidor";
            ResumeLayout(false);
        }

        #endregion

        private Panel pnlIcono;
        private Label lblTitulo;
        private Label lblMensaje;
        private Controles.CampoTexto txtServidor;
        private Label lblEstado;
        private Controles.BotonRedondeado btnProbar;
        private Controles.BotonRedondeado btnCancelar;
        private Controles.BotonRedondeado btnGuardar;
    }
}
