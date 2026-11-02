using System;
using System.Drawing;
using System.Windows.Forms;
using CLIESC_EuBank_SOAPDOTNET_GR08.MovimientosService;

namespace CLIESC_EuBank_SOAPDOTNET_GR08.views
{
    public partial class AccountClient : Form
    {
        public string User { get; set; }

        public AccountClient()
        {
            InitializeComponent();
        }

        private async void AccountClient_Load_1(object sender, EventArgs e)
        {
            try
            {
                string u = (User ?? "").Trim().ToLower();
                string ctaPrincipal = "00200001";
                string nombre = User;

                if (u == "gustavo" || u == "gmolina") { nombre = "Gustavo Molina"; ctaPrincipal = "00200001"; }
                else if (u == "daniela" || u == "dtituana") { nombre = "Daniela Tituaña"; ctaPrincipal = "00200002"; }
                else if (u == "eduardo" || u == "ecampana" || u == "monster") { nombre = "Eduardo Campaña"; ctaPrincipal = "00300001"; }

                lblsubtitle.Text = $"👤 Sesión: {nombre} ({User})   |   ⭐ Tu Cuenta Principal: {ctaPrincipal}";

                var client = new MovimientosServiceClient();
                var cuentas = await client.GetDatosCuentasAsync();

                GW_Clients.CellFormatting -= GW_Clients_CellFormatting;
                GW_Clients.CellFormatting += GW_Clients_CellFormatting;

                GW_Clients.DataSource = cuentas;

                GW_Clients.Font = new Font("Leelawadee UI", 9.5F, FontStyle.Regular);
                GW_Clients.ColumnHeadersDefaultCellStyle.Font = new Font("Leelawadee UI", 10F, FontStyle.Bold);

                // Resaltar la fila de la cuenta del usuario en color distintivo
                foreach (DataGridViewRow row in GW_Clients.Rows)
                {
                    if (row.Cells["Codigo"]?.Value?.ToString() == ctaPrincipal)
                    {
                        row.DefaultCellStyle.BackColor = Color.FromArgb(235, 230, 255);
                        row.DefaultCellStyle.SelectionBackColor = Color.FromArgb(135, 83, 243);
                        row.DefaultCellStyle.Font = new Font("Leelawadee UI", 9.5F, FontStyle.Bold);
                    }
                    else
                    {
                        row.DefaultCellStyle.Font = new Font("Leelawadee UI", 9.5F, FontStyle.Regular);
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error al obtener cuentas: {ex.Message}", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void GW_Clients_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
        {
            if (e.RowIndex < 0 || e.ColumnIndex < 0) return;

            string colName = GW_Clients.Columns[e.ColumnIndex].Name;

            if (colName == "Moneda" && e.Value != null)
            {
                string val = e.Value.ToString().Trim();
                if (val == "01" || val == "Soles")
                {
                    e.Value = "Soles (S/.)";
                    e.FormattingApplied = true;
                }
                else if (val == "02" || val == "Dolares" || val == "Dólares")
                {
                    e.Value = "Dólares ($)";
                    e.FormattingApplied = true;
                }
            }
            else if (colName == "Saldo" && e.Value != null)
            {
                var row = GW_Clients.Rows[e.RowIndex];
                string mon = row.Cells["Moneda"]?.Value?.ToString()?.Trim();
                string prefijo = (mon == "01" || mon == "Soles" || mon == "Soles (S/.)") ? "S/ " : "$ ";
                if (decimal.TryParse(e.Value.ToString(), out decimal s))
                {
                    e.Value = prefijo + s.ToString("N2");
                    e.FormattingApplied = true;
                }
            }
        }
    }
}
