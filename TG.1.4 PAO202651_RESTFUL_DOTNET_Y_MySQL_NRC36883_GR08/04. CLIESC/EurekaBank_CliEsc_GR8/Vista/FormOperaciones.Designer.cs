namespace ec.edu.monster.Vista
{
    partial class FormOperaciones
    {
        /// <summary>
        ///  Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        ///  Clean up any resources being used.
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
        ///  Required method for Designer support - do not modify
        ///  the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            components = new System.ComponentModel.Container();
            DataGridViewCellStyle estiloEncabezadoMovimientos = new DataGridViewCellStyle();
            DataGridViewCellStyle estiloCeldasMovimientos = new DataGridViewCellStyle();
            DataGridViewCellStyle estiloImporte = new DataGridViewCellStyle();
            pnlEncabezado = new Panel();
            btnUsuario = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            lblSubtitulo = new Label();
            lblTitulo = new Label();
            mnuUsuario = new ContextMenuStrip(components);
            mnuCambiarContrasenia = new ToolStripMenuItem();
            mnuSeparador = new ToolStripSeparator();
            mnuCerrarSesion = new ToolStripMenuItem();
            btnMovimientos = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            btnDeposito = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            btnRetiro = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            btnTransferencia = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            pnlFormulario = new Panel();
            lblCuenta = new Label();
            txtCuenta = new ec.edu.monster.Vista.Controles.CampoTexto();
            lblDestino = new Label();
            txtDestino = new ec.edu.monster.Vista.Controles.CampoTexto();
            lblImporte = new Label();
            txtImporte = new ec.edu.monster.Vista.Controles.CampoTexto();
            btnEjecutar = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            pnlResultado = new Panel();
            lblResultado = new Label();
            lblResultadoTitulo = new Label();
            pnlCuenta = new Panel();
            lblCuentaTitulo = new Label();
            lblCuentaDatos = new Label();
            btnActualizar = new ec.edu.monster.Vista.Controles.BotonRedondeado();
            dgvMovimientos = new DataGridView();
            colNumero = new DataGridViewTextBoxColumn();
            colFecha = new DataGridViewTextBoxColumn();
            colTipo = new DataGridViewTextBoxColumn();
            colImporte = new DataGridViewTextBoxColumn();
            barraEstado = new StatusStrip();
            lblEstado = new ToolStripStatusLabel();
            errorProvider = new ErrorProvider(components);
            toolTip = new ToolTip(components);
            pnlEncabezado.SuspendLayout();
            mnuUsuario.SuspendLayout();
            pnlFormulario.SuspendLayout();
            pnlResultado.SuspendLayout();
            pnlCuenta.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)dgvMovimientos).BeginInit();
            barraEstado.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)errorProvider).BeginInit();
            SuspendLayout();
            //
            // pnlEncabezado
            //
            pnlEncabezado.BackColor = Color.FromArgb(0, 84, 147);
            pnlEncabezado.Controls.Add(btnUsuario);
            pnlEncabezado.Controls.Add(lblSubtitulo);
            pnlEncabezado.Controls.Add(lblTitulo);
            pnlEncabezado.Dock = DockStyle.Top;
            pnlEncabezado.Location = new Point(0, 0);
            pnlEncabezado.Name = "pnlEncabezado";
            pnlEncabezado.Size = new Size(1060, 92);
            pnlEncabezado.TabIndex = 0;
            //
            // btnUsuario
            //
            btnUsuario.Anchor = AnchorStyles.Top | AnchorStyles.Right;
            btnUsuario.BackColor = Color.FromArgb(16, 105, 170);
            btnUsuario.ColorHover = Color.FromArgb(34, 124, 192);
            btnUsuario.ColorPresionado = Color.FromArgb(0, 70, 122);
            btnUsuario.Font = new Font("Segoe UI", 10.5F);
            btnUsuario.Location = new Point(808, 26);
            btnUsuario.Name = "btnUsuario";
            btnUsuario.RadioBorde = 20;
            btnUsuario.Size = new Size(220, 40);
            btnUsuario.TabIndex = 2;
            btnUsuario.Text = "Empleado  ▼";
            toolTip.SetToolTip(btnUsuario, "Opciones de la sesión");
            btnUsuario.Click += btnUsuario_Click;
            //
            // lblSubtitulo
            //
            lblSubtitulo.AutoSize = true;
            lblSubtitulo.ForeColor = Color.FromArgb(214, 228, 240);
            lblSubtitulo.Location = new Point(34, 54);
            lblSubtitulo.Name = "lblSubtitulo";
            lblSubtitulo.Size = new Size(358, 23);
            lblSubtitulo.TabIndex = 1;
            lblSubtitulo.Text = "Elija la operación, la cuenta y el importe en USD";
            //
            // lblTitulo
            //
            lblTitulo.AutoSize = true;
            lblTitulo.Font = new Font("Segoe UI", 19F, FontStyle.Bold);
            lblTitulo.ForeColor = Color.White;
            lblTitulo.Location = new Point(30, 10);
            lblTitulo.Name = "lblTitulo";
            lblTitulo.Size = new Size(372, 45);
            lblTitulo.TabIndex = 0;
            lblTitulo.Text = "EurekaBank · Operaciones";
            //
            // mnuUsuario
            //
            mnuUsuario.Font = new Font("Segoe UI", 10.5F);
            mnuUsuario.ImageScalingSize = new Size(20, 20);
            mnuUsuario.Items.AddRange(new ToolStripItem[] { mnuCambiarContrasenia, mnuSeparador, mnuCerrarSesion });
            mnuUsuario.Name = "mnuUsuario";
            mnuUsuario.Size = new Size(225, 74);
            //
            // mnuCambiarContrasenia
            //
            mnuCambiarContrasenia.Name = "mnuCambiarContrasenia";
            mnuCambiarContrasenia.Size = new Size(224, 32);
            mnuCambiarContrasenia.Text = "Cambiar contraseña";
            mnuCambiarContrasenia.Click += mnuCambiarContrasenia_Click;
            //
            // mnuSeparador
            //
            mnuSeparador.Name = "mnuSeparador";
            mnuSeparador.Size = new Size(221, 6);
            //
            // mnuCerrarSesion
            //
            mnuCerrarSesion.ForeColor = Color.FromArgb(192, 0, 0);
            mnuCerrarSesion.Name = "mnuCerrarSesion";
            mnuCerrarSesion.Size = new Size(224, 32);
            mnuCerrarSesion.Text = "Cerrar sesión";
            mnuCerrarSesion.Click += mnuCerrarSesion_Click;
            //
            // btnMovimientos
            //
            btnMovimientos.Font = new Font("Segoe UI", 10.5F, FontStyle.Bold);
            btnMovimientos.Location = new Point(32, 114);
            btnMovimientos.Name = "btnMovimientos";
            btnMovimientos.RadioBorde = 22;
            btnMovimientos.Size = new Size(131, 48);
            btnMovimientos.TabIndex = 1;
            btnMovimientos.Text = "Movimientos";
            btnMovimientos.Click += btnOperacion_Click;
            //
            // btnDeposito
            //
            btnDeposito.Font = new Font("Segoe UI", 10.5F, FontStyle.Bold);
            btnDeposito.Location = new Point(174, 114);
            btnDeposito.Name = "btnDeposito";
            btnDeposito.RadioBorde = 22;
            btnDeposito.Size = new Size(131, 48);
            btnDeposito.TabIndex = 2;
            btnDeposito.Text = "Depósito";
            btnDeposito.Click += btnOperacion_Click;
            //
            // btnRetiro
            //
            btnRetiro.Font = new Font("Segoe UI", 10.5F, FontStyle.Bold);
            btnRetiro.Location = new Point(316, 114);
            btnRetiro.Name = "btnRetiro";
            btnRetiro.RadioBorde = 22;
            btnRetiro.Size = new Size(131, 48);
            btnRetiro.TabIndex = 3;
            btnRetiro.Text = "Retiro";
            btnRetiro.Click += btnOperacion_Click;
            //
            // btnTransferencia
            //
            btnTransferencia.Font = new Font("Segoe UI", 10.5F, FontStyle.Bold);
            btnTransferencia.Location = new Point(458, 114);
            btnTransferencia.Name = "btnTransferencia";
            btnTransferencia.RadioBorde = 22;
            btnTransferencia.Size = new Size(134, 48);
            btnTransferencia.TabIndex = 4;
            btnTransferencia.Text = "Transferencia";
            btnTransferencia.Click += btnOperacion_Click;
            //
            // pnlFormulario
            //
            pnlFormulario.BackColor = Color.White;
            pnlFormulario.Controls.Add(lblCuenta);
            pnlFormulario.Controls.Add(txtCuenta);
            pnlFormulario.Controls.Add(lblDestino);
            pnlFormulario.Controls.Add(txtDestino);
            pnlFormulario.Controls.Add(lblImporte);
            pnlFormulario.Controls.Add(txtImporte);
            pnlFormulario.Controls.Add(btnEjecutar);
            pnlFormulario.Location = new Point(32, 178);
            pnlFormulario.Name = "pnlFormulario";
            pnlFormulario.Size = new Size(560, 270);
            pnlFormulario.TabIndex = 5;
            //
            // lblCuenta
            //
            lblCuenta.AutoSize = true;
            lblCuenta.Font = new Font("Segoe UI", 10.5F);
            lblCuenta.ForeColor = Color.FromArgb(73, 80, 87);
            lblCuenta.Location = new Point(24, 18);
            lblCuenta.Name = "lblCuenta";
            lblCuenta.Size = new Size(150, 25);
            lblCuenta.TabIndex = 0;
            lblCuenta.Text = "Número de cuenta";
            //
            // txtCuenta
            //
            txtCuenta.Font = new Font("Segoe UI", 13F);
            txtCuenta.Location = new Point(24, 44);
            txtCuenta.MaxLength = 8;
            txtCuenta.Name = "txtCuenta";
            txtCuenta.PlaceholderText = "Ejemplo: 00100001";
            txtCuenta.Size = new Size(512, 48);
            txtCuenta.TabIndex = 1;
            txtCuenta.TextChanged += txtCampo_TextChanged;
            txtCuenta.KeyPress += txtCuenta_KeyPress;
            //
            // lblDestino
            //
            lblDestino.AutoSize = true;
            lblDestino.Font = new Font("Segoe UI", 10.5F);
            lblDestino.ForeColor = Color.FromArgb(73, 80, 87);
            lblDestino.Location = new Point(292, 18);
            lblDestino.Name = "lblDestino";
            lblDestino.Size = new Size(147, 25);
            lblDestino.TabIndex = 2;
            lblDestino.Text = "Cuenta de destino";
            //
            // txtDestino
            //
            txtDestino.Font = new Font("Segoe UI", 13F);
            txtDestino.Location = new Point(292, 44);
            txtDestino.MaxLength = 8;
            txtDestino.Name = "txtDestino";
            txtDestino.PlaceholderText = "Ejemplo: 00200001";
            txtDestino.Size = new Size(244, 48);
            txtDestino.TabIndex = 3;
            txtDestino.TextChanged += txtCampo_TextChanged;
            txtDestino.KeyPress += txtCuenta_KeyPress;
            //
            // lblImporte
            //
            lblImporte.AutoSize = true;
            lblImporte.Font = new Font("Segoe UI", 10.5F);
            lblImporte.ForeColor = Color.FromArgb(73, 80, 87);
            lblImporte.Location = new Point(24, 106);
            lblImporte.Name = "lblImporte";
            lblImporte.Size = new Size(139, 25);
            lblImporte.TabIndex = 4;
            lblImporte.Text = "Importe en USD";
            //
            // txtImporte
            //
            txtImporte.Font = new Font("Segoe UI", 13F);
            txtImporte.Location = new Point(24, 132);
            txtImporte.MaxLength = 16;
            txtImporte.Name = "txtImporte";
            txtImporte.PlaceholderText = "Ejemplo: 100 o 25.50";
            txtImporte.Size = new Size(512, 48);
            txtImporte.TabIndex = 5;
            txtImporte.TextChanged += txtCampo_TextChanged;
            txtImporte.KeyPress += txtImporte_KeyPress;
            //
            // btnEjecutar
            //
            btnEjecutar.Font = new Font("Segoe UI", 12.5F, FontStyle.Bold);
            btnEjecutar.Location = new Point(24, 194);
            btnEjecutar.Name = "btnEjecutar";
            btnEjecutar.RadioBorde = 24;
            btnEjecutar.Size = new Size(512, 54);
            btnEjecutar.TabIndex = 6;
            btnEjecutar.Text = "Consultar";
            btnEjecutar.Click += btnEjecutar_Click;
            //
            // pnlResultado
            //
            pnlResultado.BackColor = Color.FromArgb(225, 234, 244);
            pnlResultado.Controls.Add(lblResultado);
            pnlResultado.Controls.Add(lblResultadoTitulo);
            pnlResultado.Location = new Point(32, 464);
            pnlResultado.Name = "pnlResultado";
            pnlResultado.Size = new Size(560, 120);
            pnlResultado.TabIndex = 6;
            //
            // lblResultado
            //
            lblResultado.AutoEllipsis = true;
            lblResultado.Font = new Font("Segoe UI", 22F, FontStyle.Bold);
            lblResultado.ForeColor = Color.FromArgb(0, 84, 147);
            lblResultado.Location = new Point(22, 50);
            lblResultado.Name = "lblResultado";
            lblResultado.Size = new Size(516, 56);
            lblResultado.TabIndex = 1;
            lblResultado.Text = "—";
            //
            // lblResultadoTitulo
            //
            lblResultadoTitulo.AutoEllipsis = true;
            lblResultadoTitulo.Font = new Font("Segoe UI", 10.5F);
            lblResultadoTitulo.ForeColor = Color.FromArgb(73, 80, 87);
            lblResultadoTitulo.Location = new Point(22, 14);
            lblResultadoTitulo.Name = "lblResultadoTitulo";
            lblResultadoTitulo.Size = new Size(516, 36);
            lblResultadoTitulo.TabIndex = 0;
            lblResultadoTitulo.Text = "Comprobante";
            //
            // pnlCuenta
            //
            pnlCuenta.BackColor = Color.White;
            pnlCuenta.Controls.Add(lblCuentaTitulo);
            pnlCuenta.Controls.Add(lblCuentaDatos);
            pnlCuenta.Controls.Add(btnActualizar);
            pnlCuenta.Controls.Add(dgvMovimientos);
            pnlCuenta.Location = new Point(612, 114);
            pnlCuenta.Name = "pnlCuenta";
            pnlCuenta.Size = new Size(416, 470);
            pnlCuenta.TabIndex = 7;
            //
            // lblCuentaTitulo
            //
            lblCuentaTitulo.AutoSize = true;
            lblCuentaTitulo.Font = new Font("Segoe UI", 13F, FontStyle.Bold);
            lblCuentaTitulo.ForeColor = Color.FromArgb(33, 37, 41);
            lblCuentaTitulo.Location = new Point(18, 14);
            lblCuentaTitulo.Name = "lblCuentaTitulo";
            lblCuentaTitulo.Size = new Size(254, 30);
            lblCuentaTitulo.TabIndex = 0;
            lblCuentaTitulo.Text = "Movimientos de la cuenta";
            //
            // lblCuentaDatos
            //
            lblCuentaDatos.AutoEllipsis = true;
            lblCuentaDatos.Font = new Font("Segoe UI", 9.5F);
            lblCuentaDatos.ForeColor = Color.FromArgb(108, 117, 125);
            lblCuentaDatos.Location = new Point(20, 48);
            lblCuentaDatos.Name = "lblCuentaDatos";
            lblCuentaDatos.Size = new Size(378, 66);
            lblCuentaDatos.TabIndex = 1;
            lblCuentaDatos.Text = "Consulte una cuenta para ver su saldo y sus movimientos.";
            //
            // btnActualizar
            //
            btnActualizar.BackColor = Color.FromArgb(225, 234, 244);
            btnActualizar.ColorHover = Color.FromArgb(205, 222, 240);
            btnActualizar.ColorPresionado = Color.FromArgb(185, 208, 232);
            btnActualizar.Enabled = false;
            btnActualizar.Font = new Font("Segoe UI", 9.5F, FontStyle.Bold);
            btnActualizar.ForeColor = Color.FromArgb(0, 84, 147);
            btnActualizar.Location = new Point(294, 14);
            btnActualizar.Name = "btnActualizar";
            btnActualizar.RadioBorde = 16;
            btnActualizar.Size = new Size(104, 32);
            btnActualizar.TabIndex = 2;
            btnActualizar.Text = "Actualizar";
            toolTip.SetToolTip(btnActualizar, "Ver los movimientos registrados desde los otros clientes");
            btnActualizar.Click += btnActualizar_Click;
            //
            // dgvMovimientos
            //
            dgvMovimientos.AllowUserToAddRows = false;
            dgvMovimientos.AllowUserToDeleteRows = false;
            dgvMovimientos.AllowUserToResizeColumns = false;
            dgvMovimientos.AllowUserToResizeRows = false;
            dgvMovimientos.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
            dgvMovimientos.BackgroundColor = Color.White;
            dgvMovimientos.BorderStyle = BorderStyle.None;
            dgvMovimientos.CellBorderStyle = DataGridViewCellBorderStyle.SingleHorizontal;
            dgvMovimientos.ColumnHeadersBorderStyle = DataGridViewHeaderBorderStyle.None;
            estiloEncabezadoMovimientos.Alignment = DataGridViewContentAlignment.MiddleLeft;
            estiloEncabezadoMovimientos.BackColor = Color.FromArgb(244, 246, 249);
            estiloEncabezadoMovimientos.Font = new Font("Segoe UI", 9.5F, FontStyle.Bold);
            estiloEncabezadoMovimientos.ForeColor = Color.FromArgb(73, 80, 87);
            estiloEncabezadoMovimientos.Padding = new Padding(6, 0, 0, 0);
            estiloEncabezadoMovimientos.SelectionBackColor = Color.FromArgb(244, 246, 249);
            estiloEncabezadoMovimientos.SelectionForeColor = Color.FromArgb(73, 80, 87);
            estiloEncabezadoMovimientos.WrapMode = DataGridViewTriState.False;
            dgvMovimientos.ColumnHeadersDefaultCellStyle = estiloEncabezadoMovimientos;
            dgvMovimientos.ColumnHeadersHeight = 38;
            dgvMovimientos.ColumnHeadersHeightSizeMode = DataGridViewColumnHeadersHeightSizeMode.DisableResizing;
            dgvMovimientos.Columns.AddRange(new DataGridViewColumn[] { colNumero, colFecha, colTipo, colImporte });
            estiloCeldasMovimientos.Alignment = DataGridViewContentAlignment.MiddleLeft;
            estiloCeldasMovimientos.BackColor = Color.White;
            estiloCeldasMovimientos.Font = new Font("Segoe UI", 9.5F);
            estiloCeldasMovimientos.ForeColor = Color.FromArgb(33, 37, 41);
            estiloCeldasMovimientos.Padding = new Padding(6, 2, 4, 2);
            estiloCeldasMovimientos.SelectionBackColor = Color.FromArgb(225, 234, 244);
            estiloCeldasMovimientos.SelectionForeColor = Color.FromArgb(0, 84, 147);
            estiloCeldasMovimientos.WrapMode = DataGridViewTriState.True;
            dgvMovimientos.DefaultCellStyle = estiloCeldasMovimientos;
            dgvMovimientos.EnableHeadersVisualStyles = false;
            dgvMovimientos.GridColor = Color.FromArgb(233, 236, 239);
            dgvMovimientos.Location = new Point(18, 120);
            dgvMovimientos.MultiSelect = false;
            dgvMovimientos.Name = "dgvMovimientos";
            dgvMovimientos.ReadOnly = true;
            dgvMovimientos.RowHeadersVisible = false;
            dgvMovimientos.RowTemplate.Height = 54;
            dgvMovimientos.ScrollBars = ScrollBars.Vertical;
            dgvMovimientos.SelectionMode = DataGridViewSelectionMode.FullRowSelect;
            dgvMovimientos.Size = new Size(380, 332);
            dgvMovimientos.TabIndex = 3;
            //
            // colNumero
            //
            colNumero.FillWeight = 13F;
            colNumero.HeaderText = "N°";
            colNumero.Name = "colNumero";
            colNumero.ReadOnly = true;
            colNumero.SortMode = DataGridViewColumnSortMode.NotSortable;
            //
            // colFecha
            //
            colFecha.FillWeight = 34F;
            colFecha.HeaderText = "Fecha";
            colFecha.Name = "colFecha";
            colFecha.ReadOnly = true;
            colFecha.SortMode = DataGridViewColumnSortMode.NotSortable;
            //
            // colTipo
            //
            colTipo.FillWeight = 54F;
            colTipo.HeaderText = "Tipo";
            colTipo.Name = "colTipo";
            colTipo.ReadOnly = true;
            colTipo.SortMode = DataGridViewColumnSortMode.NotSortable;
            //
            // colImporte
            //
            estiloImporte.Alignment = DataGridViewContentAlignment.MiddleRight;
            estiloImporte.Font = new Font("Segoe UI", 9.5F, FontStyle.Bold);
            colImporte.DefaultCellStyle = estiloImporte;
            colImporte.FillWeight = 31F;
            colImporte.HeaderText = "Importe";
            colImporte.Name = "colImporte";
            colImporte.ReadOnly = true;
            colImporte.SortMode = DataGridViewColumnSortMode.NotSortable;
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
            barraEstado.TabIndex = 8;
            //
            // lblEstado
            //
            lblEstado.ForeColor = Color.DimGray;
            lblEstado.Name = "lblEstado";
            lblEstado.Size = new Size(41, 20);
            lblEstado.Text = "Listo";
            //
            // errorProvider
            //
            errorProvider.BlinkStyle = ErrorBlinkStyle.NeverBlink;
            errorProvider.ContainerControl = this;
            //
            // FormOperaciones
            //
            AcceptButton = btnEjecutar;
            AutoScaleDimensions = new SizeF(96F, 96F);
            AutoScaleMode = AutoScaleMode.Dpi;
            BackColor = Color.FromArgb(244, 246, 249);
            ClientSize = new Size(1060, 640);
            Controls.Add(barraEstado);
            Controls.Add(pnlCuenta);
            Controls.Add(pnlResultado);
            Controls.Add(pnlFormulario);
            Controls.Add(btnTransferencia);
            Controls.Add(btnRetiro);
            Controls.Add(btnDeposito);
            Controls.Add(btnMovimientos);
            Controls.Add(pnlEncabezado);
            Font = new Font("Segoe UI", 10F);
            FormBorderStyle = FormBorderStyle.FixedSingle;
            MaximizeBox = false;
            Name = "FormOperaciones";
            StartPosition = FormStartPosition.CenterScreen;
            Text = "EurekaBank REST GR08 - Operaciones";
            Shown += FormOperaciones_Shown;
            pnlEncabezado.ResumeLayout(false);
            pnlEncabezado.PerformLayout();
            mnuUsuario.ResumeLayout(false);
            pnlFormulario.ResumeLayout(false);
            pnlFormulario.PerformLayout();
            pnlResultado.ResumeLayout(false);
            pnlCuenta.ResumeLayout(false);
            pnlCuenta.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)dgvMovimientos).EndInit();
            barraEstado.ResumeLayout(false);
            barraEstado.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)errorProvider).EndInit();
            ResumeLayout(false);
            PerformLayout();
        }

        #endregion

        private Panel pnlEncabezado;
        private ec.edu.monster.Vista.Controles.BotonRedondeado btnUsuario;
        private Label lblSubtitulo;
        private Label lblTitulo;
        private ContextMenuStrip mnuUsuario;
        private ToolStripMenuItem mnuCambiarContrasenia;
        private ToolStripSeparator mnuSeparador;
        private ToolStripMenuItem mnuCerrarSesion;
        private ec.edu.monster.Vista.Controles.BotonRedondeado btnMovimientos;
        private ec.edu.monster.Vista.Controles.BotonRedondeado btnDeposito;
        private ec.edu.monster.Vista.Controles.BotonRedondeado btnRetiro;
        private ec.edu.monster.Vista.Controles.BotonRedondeado btnTransferencia;
        private Panel pnlFormulario;
        private Label lblCuenta;
        private ec.edu.monster.Vista.Controles.CampoTexto txtCuenta;
        private Label lblDestino;
        private ec.edu.monster.Vista.Controles.CampoTexto txtDestino;
        private Label lblImporte;
        private ec.edu.monster.Vista.Controles.CampoTexto txtImporte;
        private ec.edu.monster.Vista.Controles.BotonRedondeado btnEjecutar;
        private Panel pnlResultado;
        private Label lblResultado;
        private Label lblResultadoTitulo;
        private Panel pnlCuenta;
        private Label lblCuentaTitulo;
        private Label lblCuentaDatos;
        private ec.edu.monster.Vista.Controles.BotonRedondeado btnActualizar;
        private DataGridView dgvMovimientos;
        private DataGridViewTextBoxColumn colNumero;
        private DataGridViewTextBoxColumn colFecha;
        private DataGridViewTextBoxColumn colTipo;
        private DataGridViewTextBoxColumn colImporte;
        private StatusStrip barraEstado;
        private ToolStripStatusLabel lblEstado;
        private ErrorProvider errorProvider;
        private ToolTip toolTip;
    }
}
