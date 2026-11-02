using System.Windows.Forms;

namespace CLIESC_EuBank_SOAPDOTNET_GR08.views
{
    partial class Login : Form
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Login));
            this.header = new System.Windows.Forms.Panel();
            this.min_button = new System.Windows.Forms.Button();
            this.btn_exit = new System.Windows.Forms.Button();
            this.pictureBox4 = new System.Windows.Forms.PictureBox();
            this.lblHeader = new System.Windows.Forms.Label();
            this.lbl_tech = new System.Windows.Forms.Label();
            this.lblnames = new System.Windows.Forms.Label();
            this.dotnet_image = new System.Windows.Forms.PictureBox();
            this.image_back = new System.Windows.Forms.PictureBox();
            this.pnl_credenciales = new CLIESC_ConUni_SOAPDOTNET_GR08.Controls.LDPanelRound();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.btn_Ingresar = new System.Windows.Forms.Button();
            this.txb_password = new System.Windows.Forms.TextBox();
            this.tbx_user = new System.Windows.Forms.TextBox();
            this.lblPass = new System.Windows.Forms.Label();
            this.lblUser = new System.Windows.Forms.Label();
            this.header.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox4)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.dotnet_image)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.image_back)).BeginInit();
            this.pnl_credenciales.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.SuspendLayout();
            // 
            // header
            // 
            this.header.BackColor = System.Drawing.Color.DarkOrchid;
            this.header.Controls.Add(this.min_button);
            this.header.Controls.Add(this.btn_exit);
            this.header.Controls.Add(this.pictureBox4);
            this.header.Controls.Add(this.lblHeader);
            this.header.Dock = System.Windows.Forms.DockStyle.Top;
            this.header.Location = new System.Drawing.Point(0, 0);
            this.header.Margin = new System.Windows.Forms.Padding(2, 2, 2, 2);
            this.header.Name = "header";
            this.header.Size = new System.Drawing.Size(705, 37);
            this.header.TabIndex = 2;
            this.header.MouseDown += new System.Windows.Forms.MouseEventHandler(this.header_MouseDown);
            // 
            // min_button
            // 
            this.min_button.BackColor = System.Drawing.Color.Transparent;
            this.min_button.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("min_button.BackgroundImage")));
            this.min_button.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Zoom;
            this.min_button.Cursor = System.Windows.Forms.Cursors.Hand;
            this.min_button.FlatAppearance.BorderSize = 0;
            this.min_button.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(91)))), ((int)(((byte)(44)))), ((int)(((byte)(99)))));
            this.min_button.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.min_button.ForeColor = System.Drawing.Color.Transparent;
            this.min_button.Location = new System.Drawing.Point(644, 8);
            this.min_button.Margin = new System.Windows.Forms.Padding(2, 2, 2, 2);
            this.min_button.Name = "min_button";
            this.min_button.Size = new System.Drawing.Size(19, 20);
            this.min_button.TabIndex = 10;
            this.min_button.UseVisualStyleBackColor = true;
            this.min_button.Click += new System.EventHandler(this.min_button_Click);
            // 
            // btn_exit
            // 
            this.btn_exit.BackColor = System.Drawing.Color.Transparent;
            this.btn_exit.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("btn_exit.BackgroundImage")));
            this.btn_exit.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Zoom;
            this.btn_exit.Cursor = System.Windows.Forms.Cursors.Hand;
            this.btn_exit.FlatAppearance.BorderSize = 0;
            this.btn_exit.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(91)))), ((int)(((byte)(44)))), ((int)(((byte)(99)))));
            this.btn_exit.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_exit.ForeColor = System.Drawing.Color.Transparent;
            this.btn_exit.Location = new System.Drawing.Point(677, 8);
            this.btn_exit.Margin = new System.Windows.Forms.Padding(2, 2, 2, 2);
            this.btn_exit.Name = "btn_exit";
            this.btn_exit.Size = new System.Drawing.Size(19, 20);
            this.btn_exit.TabIndex = 9;
            this.btn_exit.UseVisualStyleBackColor = true;
            this.btn_exit.Click += new System.EventHandler(this.btn_exit_Click);
            // 
            // pictureBox4
            // 
            this.pictureBox4.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox4.Image")));
            this.pictureBox4.Location = new System.Drawing.Point(2, 0);
            this.pictureBox4.Margin = new System.Windows.Forms.Padding(2, 2, 2, 2);
            this.pictureBox4.Name = "pictureBox4";
            this.pictureBox4.Size = new System.Drawing.Size(31, 31);
            this.pictureBox4.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox4.TabIndex = 8;
            this.pictureBox4.TabStop = false;
            // 
            // lblHeader
            // 
            this.lblHeader.AutoSize = true;
            this.lblHeader.Font = new System.Drawing.Font("Leelawadee UI", 13.8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblHeader.ForeColor = System.Drawing.Color.White;
            this.lblHeader.Location = new System.Drawing.Point(38, 3);
            this.lblHeader.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.lblHeader.Name = "lblHeader";
            this.lblHeader.Size = new System.Drawing.Size(151, 25);
            this.lblHeader.TabIndex = 5;
            this.lblHeader.Text = "Inicio de Sesión";
            // 
            // lbl_tech
            // 
            this.lbl_tech.AutoSize = true;
            this.lbl_tech.Font = new System.Drawing.Font("Leelawadee UI", 16.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lbl_tech.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(81)))), ((int)(((byte)(43)))), ((int)(((byte)(212)))));
            this.lbl_tech.Location = new System.Drawing.Point(150, 72);
            this.lbl_tech.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.lbl_tech.Name = "lbl_tech";
            this.lbl_tech.Size = new System.Drawing.Size(93, 26);
            this.lbl_tech.TabIndex = 18;
            this.lbl_tech.Text = "+ SOAP";
            // 
            // lblnames
            // 
            this.lblnames.AutoSize = true;
            this.lblnames.Font = new System.Drawing.Font("Leelawadee UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblnames.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(91)))), ((int)(((byte)(44)))), ((int)(((byte)(99)))));
            this.lblnames.Location = new System.Drawing.Point(95, 125);
            this.lblnames.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.lblnames.Name = "lblnames";
            this.lblnames.Size = new System.Drawing.Size(152, 60);
            this.lblnames.TabIndex = 19;
            this.lblnames.Text = "Daniela Tituaña\r\nGustavo Molina";
            this.lblnames.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // dotnet_image
            // 
            this.dotnet_image.Image = global::CLIESC_EuBank_SOAPDOTNET_GR08.Properties.Resources.dotnet;
            this.dotnet_image.Location = new System.Drawing.Point(103, 65);
            this.dotnet_image.Margin = new System.Windows.Forms.Padding(2, 2, 2, 2);
            this.dotnet_image.Name = "dotnet_image";
            this.dotnet_image.Size = new System.Drawing.Size(43, 40);
            this.dotnet_image.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.dotnet_image.TabIndex = 17;
            this.dotnet_image.TabStop = false;
            // 
            // image_back
            // 
            this.image_back.Image = global::CLIESC_EuBank_SOAPDOTNET_GR08.Properties.Resources.sulliLogo;
            this.image_back.Location = new System.Drawing.Point(42, 185);
            this.image_back.Margin = new System.Windows.Forms.Padding(2, 2, 2, 2);
            this.image_back.Name = "image_back";
            this.image_back.Size = new System.Drawing.Size(250, 277);
            this.image_back.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.image_back.TabIndex = 16;
            this.image_back.TabStop = false;
            // 
            // pnl_credenciales
            // 
            this.pnl_credenciales.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(250)))), ((int)(((byte)(247)))), ((int)(((byte)(252)))));
            this.pnl_credenciales.BorderColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(128)))), ((int)(((byte)(255)))));
            this.pnl_credenciales.BorderThickness = 1;
            this.pnl_credenciales.Controls.Add(this.pictureBox1);
            this.pnl_credenciales.Controls.Add(this.btn_Ingresar);
            this.pnl_credenciales.Controls.Add(this.txb_password);
            this.pnl_credenciales.Controls.Add(this.tbx_user);
            this.pnl_credenciales.Controls.Add(this.lblPass);
            this.pnl_credenciales.Controls.Add(this.lblUser);
            this.pnl_credenciales.CornerRadius = 10;
            this.pnl_credenciales.Location = new System.Drawing.Point(343, 60);
            this.pnl_credenciales.Margin = new System.Windows.Forms.Padding(2, 2, 2, 2);
            this.pnl_credenciales.Name = "pnl_credenciales";
            this.pnl_credenciales.Size = new System.Drawing.Size(304, 401);
            this.pnl_credenciales.TabIndex = 15;
            // 
            // pictureBox1
            // 
            this.pictureBox1.Image = global::CLIESC_EuBank_SOAPDOTNET_GR08.Properties.Resources.EB_SOAP_DOTNET2;
            this.pictureBox1.Location = new System.Drawing.Point(60, 5);
            this.pictureBox1.Margin = new System.Windows.Forms.Padding(2, 2, 2, 2);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(190, 71);
            this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox1.TabIndex = 13;
            this.pictureBox1.TabStop = false;
            // 
            // btn_Ingresar
            // 
            this.btn_Ingresar.Anchor = System.Windows.Forms.AnchorStyles.None;
            this.btn_Ingresar.BackColor = System.Drawing.Color.DarkOrchid;
            this.btn_Ingresar.Cursor = System.Windows.Forms.Cursors.Hand;
            this.btn_Ingresar.FlatAppearance.BorderSize = 0;
            this.btn_Ingresar.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(91)))), ((int)(((byte)(44)))), ((int)(((byte)(99)))));
            this.btn_Ingresar.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_Ingresar.Font = new System.Drawing.Font("Leelawadee UI", 13.8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btn_Ingresar.ForeColor = System.Drawing.Color.White;
            this.btn_Ingresar.Image = global::CLIESC_EuBank_SOAPDOTNET_GR08.Properties.Resources.ingresar;
            this.btn_Ingresar.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.btn_Ingresar.Location = new System.Drawing.Point(52, 318);
            this.btn_Ingresar.Margin = new System.Windows.Forms.Padding(2, 2, 2, 2);
            this.btn_Ingresar.Name = "btn_Ingresar";
            this.btn_Ingresar.Size = new System.Drawing.Size(199, 49);
            this.btn_Ingresar.TabIndex = 12;
            this.btn_Ingresar.Text = "Iniciar Sesión";
            this.btn_Ingresar.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.btn_Ingresar.UseVisualStyleBackColor = false;
            this.btn_Ingresar.Click += async (sender, e) => await this.btn_Ingresar_Click_1Async(sender, e);
            // 
            // txb_password
            // 
            this.txb_password.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txb_password.Font = new System.Drawing.Font("Leelawadee UI", 13.8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txb_password.Location = new System.Drawing.Point(35, 249);
            this.txb_password.Margin = new System.Windows.Forms.Padding(2, 2, 2, 2);
            this.txb_password.Name = "txb_password";
            this.txb_password.Size = new System.Drawing.Size(237, 32);
            this.txb_password.TabIndex = 5;
            this.txb_password.UseSystemPasswordChar = true;
            this.txb_password.WordWrap = false;
            // 
            // tbx_user
            // 
            this.tbx_user.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.tbx_user.Font = new System.Drawing.Font("Leelawadee UI", 16.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.tbx_user.Location = new System.Drawing.Point(34, 149);
            this.tbx_user.Margin = new System.Windows.Forms.Padding(2, 2, 2, 2);
            this.tbx_user.Name = "tbx_user";
            this.tbx_user.Size = new System.Drawing.Size(238, 36);
            this.tbx_user.TabIndex = 4;
            // 
            // lblPass
            // 
            this.lblPass.AutoSize = true;
            this.lblPass.Font = new System.Drawing.Font("Leelawadee UI", 19.8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblPass.ForeColor = System.Drawing.Color.DarkOrchid;
            this.lblPass.Location = new System.Drawing.Point(65, 207);
            this.lblPass.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.lblPass.Name = "lblPass";
            this.lblPass.Size = new System.Drawing.Size(165, 31);
            this.lblPass.TabIndex = 3;
            this.lblPass.Text = "Contraseña";
            // 
            // lblUser
            // 
            this.lblUser.AutoSize = true;
            this.lblUser.Font = new System.Drawing.Font("Leelawadee UI", 19.8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblUser.ForeColor = System.Drawing.Color.DarkOrchid;
            this.lblUser.Location = new System.Drawing.Point(87, 106);
            this.lblUser.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.lblUser.Name = "lblUser";
            this.lblUser.Size = new System.Drawing.Size(115, 31);
            this.lblUser.TabIndex = 2;
            this.lblUser.Text = "Usuario";
            this.lblUser.Click += new System.EventHandler(this.lblUser_Click);
            // 
            // Login
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.White;
            this.ClientSize = new System.Drawing.Size(705, 488);
            this.Controls.Add(this.lbl_tech);
            this.Controls.Add(this.dotnet_image);
            this.Controls.Add(this.lblnames);
            this.Controls.Add(this.image_back);
            this.Controls.Add(this.pnl_credenciales);
            this.Controls.Add(this.header);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Margin = new System.Windows.Forms.Padding(2, 2, 2, 2);
            this.Name = "Login";
            this.Text = "Form1";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.header.ResumeLayout(false);
            this.header.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox4)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.dotnet_image)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.image_back)).EndInit();
            this.pnl_credenciales.ResumeLayout(false);
            this.pnl_credenciales.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Panel header;
        private System.Windows.Forms.Button min_button;
        private System.Windows.Forms.Button btn_exit;
        private System.Windows.Forms.PictureBox pictureBox4;
        private System.Windows.Forms.Label lblHeader;
        private CLIESC_ConUni_SOAPDOTNET_GR08.Controls.LDPanelRound pnl_credenciales;
        private System.Windows.Forms.Button btn_Ingresar;
        private System.Windows.Forms.TextBox txb_password;
        private System.Windows.Forms.TextBox tbx_user;
        private System.Windows.Forms.Label lblPass;
        private System.Windows.Forms.Label lblUser;
        private System.Windows.Forms.PictureBox image_back;
        private System.Windows.Forms.PictureBox dotnet_image;
        private System.Windows.Forms.Label lbl_tech;
        private System.Windows.Forms.Label lblnames;
        private System.Windows.Forms.PictureBox pictureBox1;
    }
}


