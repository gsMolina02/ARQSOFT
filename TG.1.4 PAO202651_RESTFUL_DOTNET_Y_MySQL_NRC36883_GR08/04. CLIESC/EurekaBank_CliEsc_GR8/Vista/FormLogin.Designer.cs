namespace ec.edu.monster.Vista
{
    partial class FormLogin
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
            pnlLateral = new Panel();
            picImagen = new ec.edu.monster.Vista.Controles.ImagenRecortada();
            pnlMarca = new Panel();
            lblSubtitulo = new Label();
            lblTitulo = new Label();
            lblIniciarSesion = new Label();
            lblIndicacion = new Label();
            lblUsuario = new Label();
            txtUsuario = new ec.edu.monster.Vista.Controles.CampoTexto();
            lblContrasenia = new Label();
            txtContrasenia = new ec.edu.monster.Vista.Controles.CampoTexto();
            lblError = new Label();
            btnIngresar = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            lblServidor = new Label();
            lnkServidor = new LinkLabel();
            pnlLateral.SuspendLayout();
            pnlMarca.SuspendLayout();
            SuspendLayout();
            //
            // pnlLateral
            //
            pnlLateral.BackColor = Color.FromArgb(0, 84, 147);
            pnlLateral.Controls.Add(picImagen);
            pnlLateral.Controls.Add(pnlMarca);
            pnlLateral.Dock = DockStyle.Left;
            pnlLateral.Location = new Point(0, 0);
            pnlLateral.Name = "pnlLateral";
            pnlLateral.Size = new Size(420, 560);
            pnlLateral.TabIndex = 0;
            //
            // picImagen
            //
            picImagen.BackColor = Color.FromArgb(0, 84, 147);
            picImagen.Dock = DockStyle.Fill;
            picImagen.Imagen = Properties.Resources.ImagenLogin;
            picImagen.Location = new Point(0, 0);
            picImagen.Name = "picImagen";
            picImagen.Size = new Size(420, 420);
            picImagen.TabIndex = 0;
            //
            // pnlMarca
            //
            pnlMarca.Controls.Add(lblSubtitulo);
            pnlMarca.Controls.Add(lblTitulo);
            pnlMarca.Dock = DockStyle.Bottom;
            pnlMarca.Location = new Point(0, 420);
            pnlMarca.Name = "pnlMarca";
            pnlMarca.Size = new Size(420, 140);
            pnlMarca.TabIndex = 1;
            //
            // lblSubtitulo
            //
            lblSubtitulo.Font = new Font("Segoe UI", 10.5F);
            lblSubtitulo.ForeColor = Color.FromArgb(214, 228, 240);
            lblSubtitulo.Location = new Point(38, 72);
            lblSubtitulo.Name = "lblSubtitulo";
            lblSubtitulo.Size = new Size(350, 54);
            lblSubtitulo.TabIndex = 1;
            lblSubtitulo.Text = "Sistema de Cuentas de Ahorro\r\nMovimientos · Depósitos · Retiros · Transferencias";
            //
            // lblTitulo
            //
            lblTitulo.AutoSize = true;
            lblTitulo.Font = new Font("Segoe UI", 24F, FontStyle.Bold);
            lblTitulo.ForeColor = Color.White;
            lblTitulo.Location = new Point(32, 14);
            lblTitulo.Name = "lblTitulo";
            lblTitulo.Size = new Size(262, 54);
            lblTitulo.TabIndex = 0;
            lblTitulo.Text = "EurekaBank REST GR08";
            //
            // lblIniciarSesion
            //
            lblIniciarSesion.AutoSize = true;
            lblIniciarSesion.Font = new Font("Segoe UI", 20F, FontStyle.Bold);
            lblIniciarSesion.ForeColor = Color.FromArgb(33, 37, 41);
            lblIniciarSesion.Location = new Point(466, 66);
            lblIniciarSesion.Name = "lblIniciarSesion";
            lblIniciarSesion.Size = new Size(219, 46);
            lblIniciarSesion.TabIndex = 1;
            lblIniciarSesion.Text = "Iniciar sesión";
            //
            // lblIndicacion
            //
            lblIndicacion.AutoSize = true;
            lblIndicacion.ForeColor = Color.FromArgb(108, 117, 125);
            lblIndicacion.Location = new Point(470, 116);
            lblIndicacion.Name = "lblIndicacion";
            lblIndicacion.Size = new Size(293, 23);
            lblIndicacion.TabIndex = 2;
            lblIndicacion.Text = "Ingrese sus credenciales para continuar";
            //
            // lblUsuario
            //
            lblUsuario.AutoSize = true;
            lblUsuario.ForeColor = Color.FromArgb(73, 80, 87);
            lblUsuario.Location = new Point(470, 170);
            lblUsuario.Name = "lblUsuario";
            lblUsuario.Size = new Size(64, 23);
            lblUsuario.TabIndex = 3;
            lblUsuario.Text = "Usuario";
            //
            // txtUsuario
            //
            txtUsuario.Font = new Font("Segoe UI", 12F);
            txtUsuario.Location = new Point(470, 196);
            txtUsuario.MaxLength = 50;
            txtUsuario.Name = "txtUsuario";
            txtUsuario.PlaceholderText = "Ej.: Monster";
            txtUsuario.Size = new Size(390, 48);
            txtUsuario.TabIndex = 4;
            //
            // lblContrasenia
            //
            lblContrasenia.AutoSize = true;
            lblContrasenia.ForeColor = Color.FromArgb(73, 80, 87);
            lblContrasenia.Location = new Point(470, 260);
            lblContrasenia.Name = "lblContrasenia";
            lblContrasenia.Size = new Size(92, 23);
            lblContrasenia.TabIndex = 5;
            lblContrasenia.Text = "Contraseña";
            //
            // txtContrasenia
            //
            txtContrasenia.Font = new Font("Segoe UI", 12F);
            txtContrasenia.Location = new Point(470, 286);
            txtContrasenia.MaxLength = 50;
            txtContrasenia.Name = "txtContrasenia";
            txtContrasenia.Size = new Size(390, 48);
            txtContrasenia.TabIndex = 6;
            txtContrasenia.EsContrasena = true;
            txtContrasenia.PlaceholderText = "Escriba su contraseña";
            //
            // lblError
            //
            lblError.Font = new Font("Segoe UI", 9.5F);
            lblError.ForeColor = Color.FromArgb(192, 0, 0);
            lblError.Location = new Point(470, 348);
            lblError.Name = "lblError";
            lblError.Size = new Size(390, 42);
            lblError.TabIndex = 8;
            //
            // btnIngresar
            //
            btnIngresar.Font = new Font("Segoe UI", 12F, FontStyle.Bold);
            btnIngresar.Location = new Point(470, 394);
            btnIngresar.Name = "btnIngresar";
            btnIngresar.RadioBorde = 12;
            btnIngresar.Size = new Size(390, 52);
            btnIngresar.TabIndex = 9;
            btnIngresar.Text = "Iniciar sesión";
            btnIngresar.Click += btnIngresar_Click;
            //
            // lblServidor
            //
            lblServidor.Font = new Font("Segoe UI", 8.5F);
            lblServidor.ForeColor = Color.FromArgb(150, 156, 162);
            lblServidor.Location = new Point(470, 490);
            lblServidor.Name = "lblServidor";
            lblServidor.Size = new Size(390, 44);
            lblServidor.TabIndex = 10;
            lblServidor.Text = "Servidor:";
            //
            // lnkServidor
            //
            lnkServidor.ActiveLinkColor = Color.FromArgb(0, 62, 110);
            lnkServidor.AutoSize = true;
            lnkServidor.Font = new Font("Segoe UI", 9F);
            lnkServidor.LinkBehavior = LinkBehavior.HoverUnderline;
            lnkServidor.LinkColor = Color.FromArgb(0, 84, 147);
            lnkServidor.Location = new Point(470, 462);
            lnkServidor.Name = "lnkServidor";
            lnkServidor.Size = new Size(140, 20);
            lnkServidor.TabIndex = 11;
            lnkServidor.TabStop = true;
            lnkServidor.Text = "Configurar servidor";
            lnkServidor.LinkClicked += lnkServidor_LinkClicked;
            //
            // FormLogin
            //
            AcceptButton = btnIngresar;
            AutoScaleDimensions = new SizeF(96F, 96F);
            AutoScaleMode = AutoScaleMode.Dpi;
            BackColor = Color.White;
            ClientSize = new Size(900, 560);
            Controls.Add(lnkServidor);
            Controls.Add(lblServidor);
            Controls.Add(btnIngresar);
            Controls.Add(lblError);
            Controls.Add(txtContrasenia);
            Controls.Add(lblContrasenia);
            Controls.Add(txtUsuario);
            Controls.Add(lblUsuario);
            Controls.Add(lblIndicacion);
            Controls.Add(lblIniciarSesion);
            Controls.Add(pnlLateral);
            Font = new Font("Segoe UI", 10F);
            FormBorderStyle = FormBorderStyle.FixedSingle;
            MaximizeBox = false;
            Name = "FormLogin";
            StartPosition = FormStartPosition.CenterScreen;
            Text = "EurekaBank REST GR08 - Iniciar sesión";
            pnlLateral.ResumeLayout(false);
            pnlMarca.ResumeLayout(false);
            pnlMarca.PerformLayout();
            ResumeLayout(false);
            PerformLayout();
        }

        #endregion

        private Panel pnlLateral;
        private ec.edu.monster.Vista.Controles.ImagenRecortada picImagen;
        private Panel pnlMarca;
        private Label lblSubtitulo;
        private Label lblTitulo;
        private Label lblIniciarSesion;
        private Label lblIndicacion;
        private Label lblUsuario;
        private ec.edu.monster.Vista.Controles.CampoTexto txtUsuario;
        private Label lblContrasenia;
        private ec.edu.monster.Vista.Controles.CampoTexto txtContrasenia;
        private Label lblError;
        private ec.edu.monster.Vista.Controles.BotonRedondeado btnIngresar;
        private Label lblServidor;
        private LinkLabel lnkServidor;
    }
}
