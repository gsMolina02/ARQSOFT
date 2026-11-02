using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using CLIESC_EuBank_SOAPDOTNET_GR08.MovimientosService;

namespace CLIESC_EuBank_SOAPDOTNET_GR08.views
{
    public partial class frmModalPersonalizado : Form
    {
        public string User { get; set; }

        private Dictionary<string, Image> misImagenes = new Dictionary<string, Image>();
        private ComboBox cmb_cuentaorigen;
        private ComboBox cmb_cuentadestino;
        private CuentaData[] listaCuentas;
        private Label lblConversionInfo;

        public frmModalPersonalizado()
        {
            InitializeComponent();
        }

        [DllImport("user32.DLL", EntryPoint = "ReleaseCapture")]
        private extern static void ReleaseCapture();
        [DllImport("user32.DLL", EntryPoint = "SendMessage")]
        private extern static void SendMessage(System.IntPtr hWnd, int wMsg, int wParam, int IParam);

        private void ldPanelRound1_MouseDown(object sender, MouseEventArgs e)
        {
            ReleaseCapture();
            SendMessage(this.Handle, 0x112, 0xf012, 0);
        }

        private async void frmModalPersonalizado_Load(object sender, EventArgs e)
        {
            pbVisor.SizeMode = PictureBoxSizeMode.Zoom;

            misImagenes.Add("retiro", Properties.Resources.Retiro);
            misImagenes.Add("deposito", Properties.Resources.Depósito);
            misImagenes.Add("transferencia", Properties.Resources.Transferencia);

            cbx_movimiento.Items.Clear();
            foreach (string tipoMovimiento in misImagenes.Keys)
            {
                cbx_movimiento.Items.Add(tipoMovimiento);
            }

            // Crear y configurar ComboBox para Cuenta Origen
            cmb_cuentaorigen = new ComboBox
            {
                Location = tbx_cuentaorigen.Location,
                Size = new Size(tbx_cuentaorigen.Width, 36),
                DropDownStyle = ComboBoxStyle.DropDownList,
                Font = new Font("Leelawadee UI", 10.5f, FontStyle.Regular),
                BackColor = Color.White,
                DropDownWidth = 560
            };
            tbx_cuentaorigen.Visible = false;

            // Crear y configurar ComboBox para Cuenta Destino
            cmb_cuentadestino = new ComboBox
            {
                Location = txt_cuentadestino.Location,
                Size = new Size(txt_cuentadestino.Width, 36),
                DropDownStyle = ComboBoxStyle.DropDownList,
                Font = new Font("Leelawadee UI", 10.5f, FontStyle.Regular),
                BackColor = Color.White,
                Visible = false,
                DropDownWidth = 560
            };
            txt_cuentadestino.Visible = false;

            // Etiqueta para previsualizar el cambio de moneda en tiempo real
            lblConversionInfo = new Label
            {
                Location = new Point(33, 415),
                Size = new Size(740, 32),
                Font = new Font("Leelawadee UI", 9.5f, FontStyle.Bold),
                ForeColor = Color.FromArgb(90, 40, 180),
                BackColor = Color.FromArgb(243, 238, 255),
                TextAlign = ContentAlignment.MiddleLeft,
                BorderStyle = BorderStyle.FixedSingle,
                Visible = false
            };

            this.Controls.Add(cmb_cuentaorigen);
            this.Controls.Add(cmb_cuentadestino);
            this.Controls.Add(lblConversionInfo);
            cmb_cuentaorigen.BringToFront();
            cmb_cuentadestino.BringToFront();
            lblConversionInfo.BringToFront();

            cbx_movimiento.OnSelectedIndexChanged += cbx_movimiento_OnSelectedIndexChanged;
            txt_monto.TextChanged += (s, ev) => ActualizarInfoConversion();
            cmb_cuentaorigen.SelectedIndexChanged += (s, ev) => ActualizarInfoConversion();
            cmb_cuentadestino.SelectedIndexChanged += (s, ev) => ActualizarInfoConversion();

            if (cbx_movimiento.Items.Count > 0)
            {
                cbx_movimiento.SelectedIndex = 0;
                label4.Visible = false;
                cmb_cuentadestino.Visible = false;
            }

            // Cargar cuentas disponibles desde el servicio SOAP
            await CargarCuentasDisponibles();
        }

        private async Task CargarCuentasDisponibles()
        {
            try
            {
                var client = new MovimientosServiceClient();
                var cuentas = await client.GetDatosCuentasAsync();

                if (cuentas != null && cuentas.Length > 0)
                {
                    listaCuentas = cuentas;
                    cmb_cuentaorigen.Items.Clear();
                    cmb_cuentadestino.Items.Clear();

                    string ctaPrincipal = "00200001";
                    string u = (User ?? "").Trim().ToLower();
                    if (u == "monster" || u == "eduardo" || u == "ecampana") ctaPrincipal = "00300001";
                    else if (u == "daniela" || u == "dtituana") ctaPrincipal = "00200002";
                    else if (u == "gustavo" || u == "gmolina") ctaPrincipal = "00200001";

                    int indexOrigen = 0;
                    int count = 0;
                    foreach (var c in cuentas)
                    {
                        string descMoneda = (c.Moneda == "01" || c.Moneda == "Soles") ? "[Soles S/.]" : "[Dólares $]";
                        string item = $"{c.Codigo} {descMoneda} - {c.NombreCliente} (Saldo: {c.Saldo:N2})";
                        cmb_cuentaorigen.Items.Add(item);
                        cmb_cuentadestino.Items.Add(item);

                        if (c.Codigo == ctaPrincipal)
                        {
                            indexOrigen = count;
                        }
                        count++;
                    }

                    if (cmb_cuentaorigen.Items.Count > 0)
                    {
                        cmb_cuentaorigen.SelectedIndex = indexOrigen;
                    }
                    if (cmb_cuentadestino.Items.Count > 1)
                    {
                        cmb_cuentadestino.SelectedIndex = (indexOrigen + 1) % cmb_cuentadestino.Items.Count;
                    }

                    ActualizarInfoConversion();
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error cargando cuentas: " + ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            }
        }

        private void ActualizarInfoConversion()
        {
            if (lblConversionInfo == null) return;
            string selTipo = cbx_movimiento.SelectedItem != null ? cbx_movimiento.SelectedItem.ToString().ToLower() : "";
            decimal monto = Importe;

            string ctaOriCod = CuentaOrigen;
            var ctaOri = listaCuentas?.FirstOrDefault(c => c.Codigo == ctaOriCod);

            if (selTipo == "transferencia")
            {
                string ctaDestCod = CuentaDestino;
                var ctaDest = listaCuentas?.FirstOrDefault(c => c.Codigo == ctaDestCod);

                if (ctaOri != null && ctaDest != null)
                {
                    string moneOri = ctaOri.Moneda;
                    string moneDest = ctaDest.Moneda;

                    if (moneOri == "01" && moneDest == "02")
                    {
                        decimal conv = Math.Round(monto / 3.75m, 2);
                        lblConversionInfo.Text = $"  💱 Conversión de Divisa (T.C: 1 USD = 3.75 Soles)  |  Envías: S/ {monto:N2}  ➔  Recibe: $ {conv:N2} USD";
                        lblConversionInfo.Visible = true;
                    }
                    else if (moneOri == "02" && moneDest == "01")
                    {
                        decimal conv = Math.Round(monto * 3.75m, 2);
                        lblConversionInfo.Text = $"  💱 Conversión de Divisa (T.C: 1 USD = 3.75 Soles)  |  Envías: $ {monto:N2}  ➔  Recibe: S/ {conv:N2} PEN";
                        lblConversionInfo.Visible = true;
                    }
                    else
                    {
                        string desc = (moneOri == "01" || moneOri == "Soles") ? "Soles (S/.)" : "Dólares ($)";
                        lblConversionInfo.Text = $"  ✓ Transferencia en {desc}  |  Envías: {monto:N2}  ➔  Recibe: {monto:N2} (Misma divisa)";
                        lblConversionInfo.Visible = true;
                    }
                }
                else
                {
                    lblConversionInfo.Visible = false;
                }
            }
            else if (selTipo == "deposito")
            {
                if (ctaOri != null)
                {
                    string desc = (ctaOri.Moneda == "01" || ctaOri.Moneda == "Soles") ? "Soles (S/.)" : "Dólares ($)";
                    lblConversionInfo.Text = $"  💰 Depósito en efectivo a cuenta en {desc}  |  Saldo actual: {ctaOri.Saldo:N2}";
                    lblConversionInfo.Visible = true;
                }
                else
                {
                    lblConversionInfo.Visible = false;
                }
            }
            else if (selTipo == "retiro")
            {
                if (ctaOri != null)
                {
                    string desc = (ctaOri.Moneda == "01" || ctaOri.Moneda == "Soles") ? "Soles (S/.)" : "Dólares ($)";
                    lblConversionInfo.Text = $"  💸 Retiro de cuenta en {desc}  |  Saldo disponible: {ctaOri.Saldo:N2}";
                    lblConversionInfo.Visible = true;
                }
                else
                {
                    lblConversionInfo.Visible = false;
                }
            }
            else
            {
                lblConversionInfo.Visible = false;
            }
        }

        private void cbx_movimiento_OnSelectedIndexChanged(object sender, EventArgs e)
        {
            string seleccion = cbx_movimiento.SelectedItem != null ? cbx_movimiento.SelectedItem.ToString() : "";

            if (misImagenes.ContainsKey(seleccion))
            {
                pbVisor.Image = misImagenes[seleccion];
            }
            else
            {
                pbVisor.Image = null;
            }

            if (seleccion == "transferencia")
            {
                label3.Text = "Cuenta Origen (Envía):";
                label4.Text = "Cuenta Destino (Recibe):";
                label4.Visible = true;
                if (cmb_cuentadestino != null) cmb_cuentadestino.Visible = true;
            }
            else if (seleccion == "deposito")
            {
                label3.Text = "Cuenta a Depositar (Recibe):";
                label4.Visible = false;
                if (cmb_cuentadestino != null) cmb_cuentadestino.Visible = false;
            }
            else if (seleccion == "retiro")
            {
                label3.Text = "Cuenta a Retirar (Debitar):";
                label4.Visible = false;
                if (cmb_cuentadestino != null) cmb_cuentadestino.Visible = false;
            }
            else
            {
                label3.Text = "Cuenta a Operar:";
                label4.Visible = false;
                if (cmb_cuentadestino != null) cmb_cuentadestino.Visible = false;
            }

            ActualizarInfoConversion();
        }

        public string Tipo
        {
            get
            {
                var sel = cbx_movimiento.SelectedItem?.ToString();
                switch (sel)
                {
                    case "retiro":
                        return "RETIRO";
                    case "deposito":
                        return "DEPOSITO";
                    case "transferencia":
                        return "TRANSFERENCIA";
                    default:
                        return "";
                }
            }
        }

        public string CuentaOrigen
        {
            get
            {
                if (cmb_cuentaorigen != null && cmb_cuentaorigen.SelectedItem != null)
                {
                    string sel = cmb_cuentaorigen.SelectedItem.ToString();
                    return sel.Length >= 8 ? sel.Substring(0, 8).Trim() : sel.Trim();
                }
                return tbx_cuentaorigen.Text.Trim();
            }
        }

        public string CuentaDestino
        {
            get
            {
                if (cmb_cuentadestino != null && cmb_cuentadestino.SelectedItem != null)
                {
                    string sel = cmb_cuentadestino.SelectedItem.ToString();
                    return sel.Length >= 8 ? sel.Substring(0, 8).Trim() : sel.Trim();
                }
                return txt_cuentadestino.Text.Trim();
            }
        }

        public decimal Importe => decimal.TryParse(txt_monto.Text, out var importe) ? importe : 0;

        private void button1_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.OK;
            this.Close();
        }

        private void btn_cancelar_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }
    }
}
