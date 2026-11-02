// Archivo: CLIESC_ConUni_SOAPDOTNET_GR08/Controls/LDComboBox.cs

using System;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.Drawing.Design;
using System.Drawing.Drawing2D;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Windows.Forms.Design;

namespace CLIESC_ConUni_SOAPDOTNET_GR08.Controls
{
    [DefaultEvent("OnSelectedIndexChanged")]
    class LDComboBox : UserControl
    {
        //Fields
        private Color backColor = Color.WhiteSmoke;
        private Color iconColor = Color.MediumSlateBlue;
        private Color listBackColor = Color.FromArgb(230, 228, 245);
        private Color listTextColor = Color.DimGray;
        private Color borderColor = Color.MediumSlateBlue;
        private int borderSize = 1;

        //Items
        private ComboBox cmbList;
        private Label lblText;
        private Button btnIcon;

        //Properties
        //-> Appearance
        [Category("LD Code - Appearance")]
        public new Color BackColor
        {
            get { return backColor; }
            set
            {
                backColor = value;
                lblText.BackColor = backColor;
                btnIcon.BackColor = backColor;
            }
        }

        [Category("LD Code - Appearance")]
        public Color IconColor
        {
            get { return iconColor; }
            set
            {
                iconColor = value;
                btnIcon.Invalidate();//Redraw icon
            }
        }

        [Category("LD Code - Appearance")]
        public Color ListBackColor
        {
            get { return listBackColor; }
            set
            {
                listBackColor = value;
                cmbList.BackColor = listBackColor;
            }
        }

        [Category("LD Code - Appearance")]
        public Color ListTextColor
        {
            get { return listTextColor; }
            set
            {
                listTextColor = value;
                cmbList.ForeColor = listTextColor;
            }
        }

        [Category("LD Code - Appearance")]
        public Color BorderColor
        {
            get { return borderColor; }
            set
            {
                borderColor = value;
                base.BackColor = borderColor; //Border Color
            }
        }

        [Category("LD Code - Appearance")]
        public int BorderSize
        {
            get { return borderSize; }
            set
            {
                borderSize = value;
                this.Padding = new Padding(borderSize);//Border Size
                AdjustComboBoxDimensions();
            }
        }

        [Category("LD Code - Appearance")]
        public override Color ForeColor
        {
            get { return base.ForeColor; }
            set
            {
                base.ForeColor = value;
                lblText.ForeColor = value;
            }
        }

        [Category("LD Code - Appearance")]
        public override Font Font
        {
            get { return base.Font; }
            set
            {
                base.Font = value;
                lblText.Font = value;
                cmbList.Font = value;//Optional
            }
        }

        [Category("LD Code - Appearance")]
        public string Texts
        {
            get { return lblText.Text; }
            set { lblText.Text = value; }
        }

        [Category("LD Code - Appearance")]
        public ComboBoxStyle DropDownStyle
        {
            get { return cmbList.DropDownStyle; }
            set
            {
                if (cmbList.DropDownStyle != ComboBoxStyle.Simple)
                    cmbList.DropDownStyle = value;
            }
        }

        //Properties
        //-> Data
        [Category("LD Code - Data")]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Content)]
        [Editor("System.Windows.Forms.Design.ListControlStringCollectionEditor, System.Design, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b03f5f7f11d50a3a", typeof(UITypeEditor))]
        [Localizable(true)]
        [MergableProperty(false)]
        public ComboBox.ObjectCollection Items
        {
            get { return cmbList.Items; }
        }

        [Category("LD Code - Data")]
        [AttributeProvider(typeof(IListSource))]
        [DefaultValue(null)]
        [RefreshProperties(RefreshProperties.Repaint)]
        public object DataSource
        {
            get { return cmbList.DataSource; }
            set
            {
                if (this.InvokeRequired)
                {
                    this.BeginInvoke(new Action(() => DataSource = value));
                    return;
                }

                cmbList.DataSource = value;

                // Selección por defecto y sincronización del label
                if (cmbList.Items.Count > 0 && cmbList.SelectedIndex == -1)
                    cmbList.SelectedIndex = 0;

                lblText.Text = cmbList.Text;
            }
        }

        [Category("LD Code - Data")]
        [Browsable(true)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Content)]
        [Editor("System.Windows.Forms.Design.ListControlStringCollectionEditor, System.Design, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b03f5f7f11d50a3a", typeof(UITypeEditor))]
        [EditorBrowsable(EditorBrowsableState.Always)]
        [Localizable(true)]
        public AutoCompleteStringCollection AutoCompleteCustomSource
        {
            get { return cmbList.AutoCompleteCustomSource; }
            set { cmbList.AutoCompleteCustomSource = value; }
        }

        [Category("LD Code - Data")]
        [Browsable(true)]
        [DefaultValue(AutoCompleteSource.None)]
        [EditorBrowsable(EditorBrowsableState.Always)]
        public AutoCompleteSource AutoCompleteSource
        {
            get { return cmbList.AutoCompleteSource; }
            set { cmbList.AutoCompleteSource = value; }
        }

        [Category("LD Code - Data")]
        [Browsable(true)]
        [DefaultValue(AutoCompleteMode.None)]
        [EditorBrowsable(EditorBrowsableState.Always)]
        public AutoCompleteMode AutoCompleteMode
        {
            get { return cmbList.AutoCompleteMode; }
            set { cmbList.AutoCompleteMode = value; }
        }

        [Category("LD Code - Data")]
        [Bindable(true)]
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public object SelectedItem
        {
            get { return cmbList.SelectedItem; }
            set
            {
                cmbList.SelectedItem = value;
                lblText.Text = cmbList.Text;
            }
        }

        [Category("LD Code - Data")]
        [Browsable(false)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public int SelectedIndex
        {
            get { return cmbList.SelectedIndex; }
            set
            {
                cmbList.SelectedIndex = value;
                lblText.Text = cmbList.Text;
            }
        }


        //Events
        public event EventHandler OnSelectedIndexChanged;//Default event

        //Constructor
        public LDComboBox()
        {
            cmbList = new ComboBox();
            lblText = new Label();
            btnIcon = new Button();
            this.SuspendLayout();

            //ComboBox: Dropdown list
            cmbList.BackColor = listBackColor;
            cmbList.Font = new Font(this.Font.Name, 10F);
            cmbList.ForeColor = listTextColor;
            cmbList.SelectedIndexChanged += new EventHandler(ComboBox_SelectedIndexChanged);//Default event
            cmbList.TextChanged += new EventHandler(ComboBox_TextChanged);//Refresh text
            cmbList.DropDown += new EventHandler(ComboBox_DropDown);
            cmbList.DropDownClosed += new EventHandler(ComboBox_DropDownClosed);

            //Button: Icon
            btnIcon.Dock = DockStyle.Right;
            btnIcon.FlatStyle = FlatStyle.Flat;
            btnIcon.FlatAppearance.BorderSize = 0;
            btnIcon.BackColor = backColor;
            btnIcon.Size = new Size(30, 30);
            btnIcon.Cursor = Cursors.Hand;
            btnIcon.Click += new EventHandler(Icon_Click);//Open dropdown list
            btnIcon.Paint += new PaintEventHandler(Icon_Paint);//Draw icon

            //Label: Text
            lblText.Dock = DockStyle.Fill;
            lblText.AutoSize = false;
            lblText.BackColor = backColor;
            lblText.TextAlign = ContentAlignment.MiddleLeft;
            lblText.Padding = new Padding(8, 0, 0, 0);
            lblText.Font = new Font(this.Font.Name, 10F);
            //->Attach label events to user control event
            lblText.Click += new EventHandler(Surface_Click);//Select combo box
            lblText.MouseEnter += new EventHandler(Surface_MouseEnter);
            lblText.MouseLeave += new EventHandler(Surface_MouseLeave);

            //User Control
            this.Controls.Add(lblText);//2
            this.Controls.Add(btnIcon);//1
            this.Controls.Add(cmbList);//0
            this.MinimumSize = new Size(100, 40);
            this.Size = new Size(100, 40);
            this.ForeColor = Color.DimGray;
            this.Padding = new Padding(borderSize);//Border Size
            this.Font = new Font(this.Font.Name, 10F);
            base.BackColor = borderColor; //Border Color
            this.ResumeLayout();
            AdjustComboBoxDimensions();

            // Ocultar cmbList al inicio para que el Label y el Botón lo cubran
            cmbList.Visible = false;
        }


        private void AdjustComboBoxDimensions()
        {
            // Ajustar cmbList para que ocupe todo el espacio a la izquierda del botón de icono
            cmbList.Width = this.Width - btnIcon.Width - this.Padding.Horizontal;
            cmbList.Location = new Point()
            {
                X = this.Padding.Left,
                Y = this.Height - cmbList.Height // Alinear con el fondo
            };
        }


        // **CORRECCIÓN: Ocultar cmbList de forma segura después de que el dropdown se cierra**
        private void ComboBox_DropDownClosed(object sender, EventArgs e)
        {
            lblText.Text = cmbList.Text;

            // Usar BeginInvoke para ocultar cmbList después de que Windows Forms haya terminado
            // de procesar la selección y el cierre del dropdown.
            this.BeginInvoke((MethodInvoker)delegate
            {
                if (cmbList.DropDownStyle == ComboBoxStyle.DropDownList)
                {
                    cmbList.Visible = false;
                }
            });
        }


        private void ComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            lblText.Text = cmbList.Text;
            OnSelectedIndexChanged?.Invoke(this, EventArgs.Empty);
        }

        private void ComboBox_TextChanged(object sender, EventArgs e)
        {
            lblText.Text = cmbList.Text;
        }


        private void ComboBox_DropDown(object sender, EventArgs e)
        {
            // Este evento se dispara ANTES de abrir el desplegable, no es el mejor lugar para mostrar/ocultar
        }

        private void Icon_Click(object sender, EventArgs e)
        {
            cmbList.Select();
            cmbList.Visible = true; // Mostrar antes de abrir
            cmbList.DroppedDown = true;
        }

        private void Surface_Click(object sender, EventArgs e)
        {
            this.OnClick(e);
            cmbList.Select();
            if (cmbList.DropDownStyle == ComboBoxStyle.DropDownList)
            {
                cmbList.Visible = true; // Mostrar antes de abrir
                cmbList.DroppedDown = true;
            }
        }

        // ... (El resto de métodos como Icon_Paint, Surface_MouseLeave, etc. se mantienen)
        private void Icon_Paint(object sender, PaintEventArgs e)
        {
            int iconWidht = 14;
            int iconHeight = 6;
            var rectIcon = new Rectangle((btnIcon.Width - iconWidht) / 2, (btnIcon.Height - iconHeight) / 2, iconWidht, iconHeight);
            Graphics graph = e.Graphics;
            using (GraphicsPath path = new GraphicsPath())
            using (Pen pen = new Pen(iconColor, 2))
            {
                graph.SmoothingMode = SmoothingMode.AntiAlias;
                path.AddLine(rectIcon.X, rectIcon.Y, rectIcon.X + (iconWidht / 2), rectIcon.Bottom);
                path.AddLine(rectIcon.X + (iconWidht / 2), rectIcon.Bottom, rectIcon.Right, rectIcon.Y);
                graph.DrawPath(pen, path);
            }
        }

        private void Surface_MouseLeave(object sender, EventArgs e)
        {
            this.OnMouseLeave(e);
        }

        private void Surface_MouseEnter(object sender, EventArgs e)
        {
            this.OnMouseEnter(e);
        }


        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);
            using (Pen pen = new Pen(borderColor, borderSize))
            {
                e.Graphics.DrawRectangle(pen, 0, 0, this.Width - 1, this.Height - 1);
            }
        }

        protected override void OnResize(EventArgs e)
        {
            base.OnResize(e);
            AdjustComboBoxDimensions();
        }
    }
}