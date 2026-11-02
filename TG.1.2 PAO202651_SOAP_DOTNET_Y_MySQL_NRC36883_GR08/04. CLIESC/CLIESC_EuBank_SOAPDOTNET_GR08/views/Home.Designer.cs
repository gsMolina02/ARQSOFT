namespace CLIESC_EuBank_SOAPDOTNET_GR08.views
{
    partial class Home
    {
        private System.ComponentModel.IContainer components = null;

        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Home));
            this.header = new System.Windows.Forms.Panel();
            this.min_button = new System.Windows.Forms.Button();
            this.btn_exit = new System.Windows.Forms.Button();
            this.pictureBox4 = new System.Windows.Forms.PictureBox();
            this.lblUsuario = new System.Windows.Forms.Label();
            this.MenuVertical = new System.Windows.Forms.Panel();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.panel_my_mov = new System.Windows.Forms.Panel();
            this.btn_my_movements = new System.Windows.Forms.Button();
            this.panel2 = new System.Windows.Forms.Panel();
            this.btn_clients = new System.Windows.Forms.Button();
            this.panel3 = new System.Windows.Forms.Panel();
            this.btn_movements = new System.Windows.Forms.Button();
            this.panel1 = new System.Windows.Forms.Panel();
            this.btn_Ingresar = new System.Windows.Forms.Button();
            this.Contenedor = new System.Windows.Forms.Panel();
            this.header.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox4)).BeginInit();
            this.MenuVertical.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.SuspendLayout();
            // 
            // header
            // 
            this.header.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.header.Controls.Add(this.min_button);
            this.header.Controls.Add(this.btn_exit);
            this.header.Controls.Add(this.pictureBox4);
            this.header.Controls.Add(this.lblUsuario);
            this.header.Dock = System.Windows.Forms.DockStyle.Top;
            this.header.Location = new System.Drawing.Point(0, 0);
            this.header.Name = "header";
            this.header.Size = new System.Drawing.Size(1100, 44);
            this.header.TabIndex = 3;
            this.header.MouseDown += new System.Windows.Forms.MouseEventHandler(this.header_MouseDown);
            // 
            // min_button
            // 
            this.min_button.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.min_button.BackColor = System.Drawing.Color.Transparent;
            this.min_button.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("min_button.BackgroundImage")));
            this.min_button.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Zoom;
            this.min_button.Cursor = System.Windows.Forms.Cursors.Hand;
            this.min_button.FlatAppearance.BorderSize = 0;
            this.min_button.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(63)))), ((int)(((byte)(38)))), ((int)(((byte)(115)))));
            this.min_button.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.min_button.ForeColor = System.Drawing.Color.Transparent;
            this.min_button.Location = new System.Drawing.Point(1020, 9);
            this.min_button.Name = "min_button";
            this.min_button.Size = new System.Drawing.Size(25, 25);
            this.min_button.TabIndex = 10;
            this.min_button.UseVisualStyleBackColor = true;
            this.min_button.Click += new System.EventHandler(this.min_button_Click);
            // 
            // btn_exit
            // 
            this.btn_exit.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btn_exit.BackColor = System.Drawing.Color.Transparent;
            this.btn_exit.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("btn_exit.BackgroundImage")));
            this.btn_exit.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Zoom;
            this.btn_exit.Cursor = System.Windows.Forms.Cursors.Hand;
            this.btn_exit.FlatAppearance.BorderSize = 0;
            this.btn_exit.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(63)))), ((int)(((byte)(38)))), ((int)(((byte)(115)))));
            this.btn_exit.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_exit.ForeColor = System.Drawing.Color.Transparent;
            this.btn_exit.Location = new System.Drawing.Point(1060, 9);
            this.btn_exit.Name = "btn_exit";
            this.btn_exit.Size = new System.Drawing.Size(25, 25);
            this.btn_exit.TabIndex = 9;
            this.btn_exit.UseVisualStyleBackColor = true;
            this.btn_exit.Click += new System.EventHandler(this.btn_exit_Click);
            // 
            // pictureBox4
            // 
            this.pictureBox4.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox4.Image")));
            this.pictureBox4.Location = new System.Drawing.Point(8, 4);
            this.pictureBox4.Name = "pictureBox4";
            this.pictureBox4.Size = new System.Drawing.Size(36, 36);
            this.pictureBox4.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox4.TabIndex = 8;
            this.pictureBox4.TabStop = false;
            // 
            // lblUsuario
            // 
            this.lblUsuario.AutoSize = true;
            this.lblUsuario.Font = new System.Drawing.Font("Leelawadee UI", 9.5F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblUsuario.ForeColor = System.Drawing.Color.White;
            this.lblUsuario.Location = new System.Drawing.Point(50, 13);
            this.lblUsuario.Name = "lblUsuario";
            this.lblUsuario.Size = new System.Drawing.Size(120, 17);
            this.lblUsuario.TabIndex = 11;
            this.lblUsuario.Text = "Bienvenido a EuBank";
            // 
            // MenuVertical
            // 
            this.MenuVertical.BackColor = System.Drawing.Color.Lavender;
            this.MenuVertical.Controls.Add(this.pictureBox1);
            this.MenuVertical.Controls.Add(this.panel_my_mov);
            this.MenuVertical.Controls.Add(this.btn_my_movements);
            this.MenuVertical.Controls.Add(this.panel2);
            this.MenuVertical.Controls.Add(this.btn_clients);
            this.MenuVertical.Controls.Add(this.panel3);
            this.MenuVertical.Controls.Add(this.btn_movements);
            this.MenuVertical.Controls.Add(this.panel1);
            this.MenuVertical.Controls.Add(this.btn_Ingresar);
            this.MenuVertical.Dock = System.Windows.Forms.DockStyle.Left;
            this.MenuVertical.Location = new System.Drawing.Point(0, 44);
            this.MenuVertical.Name = "MenuVertical";
            this.MenuVertical.Size = new System.Drawing.Size(230, 606);
            this.MenuVertical.TabIndex = 4;
            // 
            // pictureBox1
            // 
            this.pictureBox1.Image = global::CLIESC_EuBank_SOAPDOTNET_GR08.Properties.Resources.sulliLogo;
            this.pictureBox1.Location = new System.Drawing.Point(30, 10);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(170, 115);
            this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox1.TabIndex = 0;
            this.pictureBox1.TabStop = false;
            // 
            // panel_my_mov
            // 
            this.panel_my_mov.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.panel_my_mov.Location = new System.Drawing.Point(0, 138);
            this.panel_my_mov.Name = "panel_my_mov";
            this.panel_my_mov.Size = new System.Drawing.Size(12, 52);
            this.panel_my_mov.TabIndex = 17;
            // 
            // btn_my_movements
            // 
            this.btn_my_movements.BackColor = System.Drawing.Color.Lavender;
            this.btn_my_movements.Cursor = System.Windows.Forms.Cursors.Hand;
            this.btn_my_movements.FlatAppearance.BorderColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.btn_my_movements.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.btn_my_movements.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_my_movements.Font = new System.Drawing.Font("Leelawadee UI", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btn_my_movements.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.btn_my_movements.Location = new System.Drawing.Point(12, 138);
            this.btn_my_movements.Name = "btn_my_movements";
            this.btn_my_movements.Size = new System.Drawing.Size(218, 52);
            this.btn_my_movements.TabIndex = 17;
            this.btn_my_movements.Text = "⭐ Mis Movimientos";
            this.btn_my_movements.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.btn_my_movements.UseVisualStyleBackColor = false;
            this.btn_my_movements.Click += new System.EventHandler(this.btn_my_movements_Click);
            this.btn_my_movements.MouseEnter += new System.EventHandler(this.btn_my_movements_MouseEnter);
            this.btn_my_movements.MouseLeave += new System.EventHandler(this.btn_my_movements_MouseLeave);
            // 
            // panel2
            // 
            this.panel2.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.panel2.Location = new System.Drawing.Point(0, 200);
            this.panel2.Name = "panel2";
            this.panel2.Size = new System.Drawing.Size(12, 52);
            this.panel2.TabIndex = 15;
            // 
            // btn_clients
            // 
            this.btn_clients.BackColor = System.Drawing.Color.Lavender;
            this.btn_clients.Cursor = System.Windows.Forms.Cursors.Hand;
            this.btn_clients.FlatAppearance.BorderColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.btn_clients.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.btn_clients.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_clients.Font = new System.Drawing.Font("Leelawadee UI", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btn_clients.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.btn_clients.Location = new System.Drawing.Point(12, 200);
            this.btn_clients.Name = "btn_clients";
            this.btn_clients.Size = new System.Drawing.Size(218, 52);
            this.btn_clients.TabIndex = 15;
            this.btn_clients.Text = "💳 Cuentas de Clientes";
            this.btn_clients.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.btn_clients.UseVisualStyleBackColor = true;
            this.btn_clients.Click += new System.EventHandler(this.btn_clients_Click);
            this.btn_clients.MouseEnter += new System.EventHandler(this.btn_clients_MouseEnter);
            this.btn_clients.MouseLeave += new System.EventHandler(this.btn_clients_MouseLeave);
            // 
            // panel3
            // 
            this.panel3.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.panel3.Location = new System.Drawing.Point(0, 262);
            this.panel3.Name = "panel3";
            this.panel3.Size = new System.Drawing.Size(12, 52);
            this.panel3.TabIndex = 16;
            // 
            // btn_movements
            // 
            this.btn_movements.BackColor = System.Drawing.Color.Lavender;
            this.btn_movements.Cursor = System.Windows.Forms.Cursors.Hand;
            this.btn_movements.FlatAppearance.BorderColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.btn_movements.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.btn_movements.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_movements.Font = new System.Drawing.Font("Leelawadee UI", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btn_movements.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.btn_movements.Location = new System.Drawing.Point(12, 262);
            this.btn_movements.Name = "btn_movements";
            this.btn_movements.Size = new System.Drawing.Size(218, 52);
            this.btn_movements.TabIndex = 16;
            this.btn_movements.Text = "📊 Movimientos Generales";
            this.btn_movements.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.btn_movements.UseVisualStyleBackColor = false;
            this.btn_movements.Click += new System.EventHandler(this.btn_movements_Click);
            this.btn_movements.MouseEnter += new System.EventHandler(this.btn_movements_MouseEnter);
            this.btn_movements.MouseLeave += new System.EventHandler(this.btn_movements_MouseLeave);
            // 
            // panel1
            // 
            this.panel1.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.panel1.Location = new System.Drawing.Point(0, 525);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(12, 60);
            this.panel1.TabIndex = 14;
            // 
            // btn_Ingresar
            // 
            this.btn_Ingresar.BackColor = System.Drawing.Color.Lavender;
            this.btn_Ingresar.Cursor = System.Windows.Forms.Cursors.Hand;
            this.btn_Ingresar.FlatAppearance.BorderColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.btn_Ingresar.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.btn_Ingresar.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_Ingresar.Font = new System.Drawing.Font("Leelawadee UI", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btn_Ingresar.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(135)))), ((int)(((byte)(83)))), ((int)(((byte)(243)))));
            this.btn_Ingresar.Image = global::CLIESC_EuBank_SOAPDOTNET_GR08.Properties.Resources.ingresar;
            this.btn_Ingresar.ImageAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.btn_Ingresar.Location = new System.Drawing.Point(12, 525);
            this.btn_Ingresar.Name = "btn_Ingresar";
            this.btn_Ingresar.Size = new System.Drawing.Size(218, 60);
            this.btn_Ingresar.TabIndex = 13;
            this.btn_Ingresar.Text = "Cerrar Sesión";
            this.btn_Ingresar.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.btn_Ingresar.UseVisualStyleBackColor = false;
            this.btn_Ingresar.Click += new System.EventHandler(this.btn_Ingresar_Click);
            this.btn_Ingresar.MouseEnter += new System.EventHandler(this.btn_Ingresar_MouseEnter);
            this.btn_Ingresar.MouseLeave += new System.EventHandler(this.btn_Ingresar_MouseLeave);
            // 
            // Contenedor
            // 
            this.Contenedor.Dock = System.Windows.Forms.DockStyle.Fill;
            this.Contenedor.Location = new System.Drawing.Point(230, 44);
            this.Contenedor.Name = "Contenedor";
            this.Contenedor.Size = new System.Drawing.Size(870, 606);
            this.Contenedor.TabIndex = 5;
            // 
            // Home
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.White;
            this.ClientSize = new System.Drawing.Size(1100, 650);
            this.Controls.Add(this.Contenedor);
            this.Controls.Add(this.MenuVertical);
            this.Controls.Add(this.header);
            this.Font = new System.Drawing.Font("Leelawadee UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Name = "Home";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "EuBank - Sistema Bancario";
            this.Load += new System.EventHandler(this.Home_Load);
            this.header.ResumeLayout(false);
            this.header.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox4)).EndInit();
            this.MenuVertical.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel header;
        private System.Windows.Forms.Panel MenuVertical;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.Panel Contenedor;
        private System.Windows.Forms.Button btn_exit;
        private System.Windows.Forms.PictureBox pictureBox4;
        private System.Windows.Forms.Button min_button;
        private System.Windows.Forms.Label lblUsuario;
        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.Panel panel2;
        private System.Windows.Forms.Panel panel3;
        private System.Windows.Forms.Panel panel_my_mov;
        private System.Windows.Forms.Button btn_clients;
        private System.Windows.Forms.Button btn_movements;
        private System.Windows.Forms.Button btn_my_movements;
        private System.Windows.Forms.Button btn_Ingresar;
    }
}
