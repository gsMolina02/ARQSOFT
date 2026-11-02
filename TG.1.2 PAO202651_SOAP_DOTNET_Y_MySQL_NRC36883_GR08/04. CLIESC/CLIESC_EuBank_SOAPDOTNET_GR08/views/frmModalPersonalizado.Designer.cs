namespace CLIESC_EuBank_SOAPDOTNET_GR08.views
{
    partial class frmModalPersonalizado
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
            this.ldPanelRound1 = new CLIESC_ConUni_SOAPDOTNET_GR08.Controls.LDPanelRound();
            this.label1 = new System.Windows.Forms.Label();
            this.cbx_movimiento = new CLIESC_ConUni_SOAPDOTNET_GR08.Controls.LDComboBox();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.tbx_cuentaorigen = new System.Windows.Forms.TextBox();
            this.txt_cuentadestino = new System.Windows.Forms.TextBox();
            this.txt_monto = new System.Windows.Forms.TextBox();
            this.btn_cancelar = new System.Windows.Forms.Button();
            this.button1 = new System.Windows.Forms.Button();
            this.pbVisor = new System.Windows.Forms.PictureBox();
            this.ldPanelRound1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pbVisor)).BeginInit();
            this.SuspendLayout();
            // 
            // ldPanelRound1
            // 
            this.ldPanelRound1.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(133)))), ((int)(((byte)(80)))), ((int)(((byte)(241)))));
            this.ldPanelRound1.BorderColor = System.Drawing.Color.Transparent;
            this.ldPanelRound1.BorderThickness = 0;
            this.ldPanelRound1.Controls.Add(this.label1);
            this.ldPanelRound1.CornerRadius = 10;
            this.ldPanelRound1.Location = new System.Drawing.Point(4, 3);
            this.ldPanelRound1.Name = "ldPanelRound1";
            this.ldPanelRound1.Size = new System.Drawing.Size(792, 49);
            this.ldPanelRound1.TabIndex = 0;
            this.ldPanelRound1.MouseDown += new System.Windows.Forms.MouseEventHandler(this.ldPanelRound1_MouseDown);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("Leelawadee UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label1.ForeColor = System.Drawing.Color.White;
            this.label1.Location = new System.Drawing.Point(17, 15);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(227, 22);
            this.label1.TabIndex = 0;
            this.label1.Text = "Crear Nuevo Movimiento";
            // 
            // cbx_movimiento
            // 
            this.cbx_movimiento.BackColor = System.Drawing.Color.White;
            this.cbx_movimiento.BorderColor = System.Drawing.Color.MediumSlateBlue;
            this.cbx_movimiento.BorderSize = 1;
            this.cbx_movimiento.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDown;
            this.cbx_movimiento.Font = new System.Drawing.Font("Leelawadee UI", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.cbx_movimiento.ForeColor = System.Drawing.Color.Black;
            this.cbx_movimiento.IconColor = System.Drawing.Color.MediumSlateBlue;
            this.cbx_movimiento.ListBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(230)))), ((int)(((byte)(228)))), ((int)(((byte)(245)))));
            this.cbx_movimiento.ListTextColor = System.Drawing.Color.DimGray;
            this.cbx_movimiento.Location = new System.Drawing.Point(33, 109);
            this.cbx_movimiento.MinimumSize = new System.Drawing.Size(100, 40);
            this.cbx_movimiento.Name = "cbx_movimiento";
            this.cbx_movimiento.Padding = new System.Windows.Forms.Padding(1);
            this.cbx_movimiento.Size = new System.Drawing.Size(322, 40);
            this.cbx_movimiento.TabIndex = 1;
            this.cbx_movimiento.Texts = "";
            this.cbx_movimiento.OnSelectedIndexChanged += new System.EventHandler(this.cbx_movimiento_OnSelectedIndexChanged);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Font = new System.Drawing.Font("Leelawadee UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label2.ForeColor = System.Drawing.Color.Black;
            this.label2.Location = new System.Drawing.Point(29, 84);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(182, 22);
            this.label2.TabIndex = 2;
            this.label2.Text = "Tipo de Movimiento";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Font = new System.Drawing.Font("Leelawadee UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label3.ForeColor = System.Drawing.Color.Black;
            this.label3.Location = new System.Drawing.Point(29, 164);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(143, 22);
            this.label3.TabIndex = 3;
            this.label3.Text = "Cuenta Origen:";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Font = new System.Drawing.Font("Leelawadee UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label4.ForeColor = System.Drawing.Color.Black;
            this.label4.Location = new System.Drawing.Point(29, 256);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(150, 22);
            this.label4.TabIndex = 4;
            this.label4.Text = "Cuenta Destino:";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Font = new System.Drawing.Font("Leelawadee UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label5.ForeColor = System.Drawing.Color.Black;
            this.label5.Location = new System.Drawing.Point(29, 340);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(64, 22);
            this.label5.TabIndex = 5;
            this.label5.Text = "Monto";
            // 
            // tbx_cuentaorigen
            // 
            this.tbx_cuentaorigen.BackColor = System.Drawing.Color.White;
            this.tbx_cuentaorigen.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.tbx_cuentaorigen.Font = new System.Drawing.Font("Leelawadee UI", 16.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.tbx_cuentaorigen.Location = new System.Drawing.Point(33, 189);
            this.tbx_cuentaorigen.Name = "tbx_cuentaorigen";
            this.tbx_cuentaorigen.Size = new System.Drawing.Size(316, 43);
            this.tbx_cuentaorigen.TabIndex = 6;
            // 
            // txt_cuentadestino
            // 
            this.txt_cuentadestino.BackColor = System.Drawing.Color.White;
            this.txt_cuentadestino.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txt_cuentadestino.Font = new System.Drawing.Font("Leelawadee UI", 16.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txt_cuentadestino.Location = new System.Drawing.Point(33, 281);
            this.txt_cuentadestino.Name = "txt_cuentadestino";
            this.txt_cuentadestino.Size = new System.Drawing.Size(316, 43);
            this.txt_cuentadestino.TabIndex = 7;
            // 
            // txt_monto
            // 
            this.txt_monto.BackColor = System.Drawing.Color.White;
            this.txt_monto.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txt_monto.Font = new System.Drawing.Font("Leelawadee UI", 16.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txt_monto.Location = new System.Drawing.Point(33, 365);
            this.txt_monto.Name = "txt_monto";
            this.txt_monto.Size = new System.Drawing.Size(316, 43);
            this.txt_monto.TabIndex = 8;
            // 
            // btn_cancelar
            // 
            this.btn_cancelar.Anchor = System.Windows.Forms.AnchorStyles.None;
            this.btn_cancelar.BackColor = System.Drawing.SystemColors.ActiveBorder;
            this.btn_cancelar.Cursor = System.Windows.Forms.Cursors.Hand;
            this.btn_cancelar.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.btn_cancelar.FlatAppearance.BorderSize = 0;
            this.btn_cancelar.FlatAppearance.MouseOverBackColor = System.Drawing.Color.Gray;
            this.btn_cancelar.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_cancelar.Font = new System.Drawing.Font("Leelawadee UI", 10.8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btn_cancelar.ForeColor = System.Drawing.Color.Black;
            this.btn_cancelar.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.btn_cancelar.Location = new System.Drawing.Point(420, 366);
            this.btn_cancelar.Name = "btn_cancelar";
            this.btn_cancelar.Size = new System.Drawing.Size(101, 51);
            this.btn_cancelar.TabIndex = 14;
            this.btn_cancelar.Text = "Cancelar";
            this.btn_cancelar.UseVisualStyleBackColor = false;
            // 
            // button1
            // 
            this.button1.Anchor = System.Windows.Forms.AnchorStyles.None;
            this.button1.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(133)))), ((int)(((byte)(80)))), ((int)(((byte)(241)))));
            this.button1.Cursor = System.Windows.Forms.Cursors.Hand;
            this.button1.DialogResult = System.Windows.Forms.DialogResult.OK;
            this.button1.FlatAppearance.BorderSize = 0;
            this.button1.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(63)))), ((int)(((byte)(38)))), ((int)(((byte)(115)))));
            this.button1.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.button1.Font = new System.Drawing.Font("Leelawadee UI", 10.8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.button1.ForeColor = System.Drawing.Color.White;
            this.button1.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.button1.Location = new System.Drawing.Point(554, 365);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(210, 51);
            this.button1.TabIndex = 15;
            this.button1.Text = "Crear Movimiento";
            this.button1.UseVisualStyleBackColor = false;
            // 
            // pbVisor
            // 
            this.pbVisor.Location = new System.Drawing.Point(400, 101);
            this.pbVisor.Name = "pbVisor";
            this.pbVisor.Size = new System.Drawing.Size(363, 239);
            this.pbVisor.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pbVisor.TabIndex = 16;
            this.pbVisor.TabStop = false;
            // 
            // frmModalPersonalizado
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(250)))), ((int)(((byte)(247)))), ((int)(((byte)(252)))));
            this.ClientSize = new System.Drawing.Size(800, 470);
            this.Controls.Add(this.pbVisor);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.btn_cancelar);
            this.Controls.Add(this.txt_monto);
            this.Controls.Add(this.txt_cuentadestino);
            this.Controls.Add(this.tbx_cuentaorigen);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.cbx_movimiento);
            this.Controls.Add(this.ldPanelRound1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Name = "frmModalPersonalizado";
            this.Text = "frmModalPersonalizado";
            this.Load += new System.EventHandler(this.frmModalPersonalizado_Load);
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.ldPanelRound1.ResumeLayout(false);
            this.ldPanelRound1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pbVisor)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private CLIESC_ConUni_SOAPDOTNET_GR08.Controls.LDPanelRound ldPanelRound1;
        private System.Windows.Forms.Label label1;
        private CLIESC_ConUni_SOAPDOTNET_GR08.Controls.LDComboBox cbx_movimiento;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.TextBox tbx_cuentaorigen;
        private System.Windows.Forms.TextBox txt_cuentadestino;
        private System.Windows.Forms.TextBox txt_monto;
        private System.Windows.Forms.Button btn_cancelar;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.PictureBox pbVisor;
    }
}