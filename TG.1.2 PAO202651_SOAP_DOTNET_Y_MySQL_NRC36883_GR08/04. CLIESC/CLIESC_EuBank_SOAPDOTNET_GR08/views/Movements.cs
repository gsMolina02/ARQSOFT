using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using CLIESC_EuBank_SOAPDOTNET_GR08.MovimientosService;

namespace CLIESC_EuBank_SOAPDOTNET_GR08.views
{
    public partial class Movements : Form
    {
        public string User { get; set; }
        public bool SoloMisMovimientos { get; set; } = true;

        private Dictionary<string, string> mapaMonedas = new Dictionary<string, string>();

        public Movements()
        {
            InitializeComponent();
        }

        private async Task LoadMovements()
        {
            try
            {
                string u = (User ?? "").Trim().ToLower();
                string ctaPrincipal = "00300001";
                string nombre = "Eduardo Mauricio Campaña Ortega";
                var cuentasUsuario = new HashSet<string>(StringComparer.OrdinalIgnoreCase);

                if (u == "gustavo" || u == "gmolina")
                {
                    nombre = "Gustavo Stiven Molina";
                    ctaPrincipal = "00200001";
                    cuentasUsuario.Add("00200001");
                    cuentasUsuario.Add("00200003");
                }
                else if (u == "daniela" || u == "dtituana")
                {
                    nombre = "Daniela Lissette Tituaña";
                    ctaPrincipal = "00200002";
                    cuentasUsuario.Add("00200002");
                    cuentasUsuario.Add("00200004");
                }
                else
                {
                    nombre = "Eduardo Mauricio Campaña Ortega";
                    ctaPrincipal = "00300001";
                    cuentasUsuario.Add("00300001");
                    cuentasUsuario.Add("00300002");
                }

                if (SoloMisMovimientos)
                {
                    lblTitle.Text = "⭐ Mis Movimientos";
                    lblsubtitle.Text = $"👤 {nombre} ({User})   |   💳 Tus Cuentas: {string.Join(", ", cuentasUsuario)}";
                }
                else
                {
                    lblTitle.Text = "Movimientos Bancarios";
                    lblsubtitle.Text = $"Consulta general de todas las cuentas bancarias   |   👤 {nombre}";
                }

                var client = new MovimientosServiceClient();
                var cuentas = await client.GetDatosCuentasAsync();

                if (cuentas != null)
                {
                    mapaMonedas.Clear();
                    foreach (var c in cuentas)
                    {
                        mapaMonedas[c.Codigo] = c.Moneda;
                    }
                }

                var todosMovimientos = new List<MovimientoData>();

                foreach (var cuenta in cuentas)
                {
                    if (SoloMisMovimientos && !cuentasUsuario.Contains(cuenta.Codigo))
                    {
                        continue;
                    }

                    try
                    {
                        var movimientosCuenta = await client.GetMovimientosAsync(cuenta.Codigo);
                        if (movimientosCuenta != null)
                        {
                            todosMovimientos.AddRange(movimientosCuenta);
                        }
                    }
                    catch (Exception ex)
                    {
                        MessageBox.Show($"Error obteniendo movimientos de cuenta {cuenta.Codigo}: {ex.Message}", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    }
                }

                var movimientosOrdenados = todosMovimientos
                    .OrderByDescending(m => m.Fecha)
                    .ThenByDescending(m => m.Numero)
                    .ToArray();

                GW_Movements.CellFormatting -= GW_Movements_CellFormatting;
                GW_Movements.CellFormatting += GW_Movements_CellFormatting;

                GW_Movements.DataSource = movimientosOrdenados;
                GW_Movements.Refresh();

                // Formato y estilos uniformes
                GW_Movements.Font = new Font("Leelawadee UI", 9.5F, FontStyle.Regular);
                GW_Movements.ColumnHeadersDefaultCellStyle.Font = new Font("Leelawadee UI", 10F, FontStyle.Bold);

                foreach (DataGridViewRow row in GW_Movements.Rows)
                {
                    string cta = row.Cells["CodigoCuenta"]?.Value?.ToString();
                    if (!string.IsNullOrEmpty(cta) && cuentasUsuario.Contains(cta))
                    {
                        row.DefaultCellStyle.BackColor = Color.FromArgb(240, 235, 255);
                        row.DefaultCellStyle.SelectionBackColor = Color.FromArgb(135, 83, 243);
                        row.DefaultCellStyle.Font = new Font("Leelawadee UI", 9.5F, FontStyle.Bold);
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error al obtener movimientos: {ex.Message}", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void GW_Movements_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
        {
            if (e.RowIndex < 0 || e.ColumnIndex < 0) return;
            string col = GW_Movements.Columns[e.ColumnIndex].Name;

            if (col == "Fecha" && e.Value != null)
            {
                if (DateTime.TryParse(e.Value.ToString(), out DateTime dt))
                {
                    e.Value = dt.ToString("dd/MM/yyyy HH:mm:ss");
                    e.FormattingApplied = true;
                }
            }

            if (col == "Importe" || col == "SaldoActual")
            {
                if (e.Value != null && decimal.TryParse(e.Value.ToString(), out decimal val))
                {
                    var row = GW_Movements.Rows[e.RowIndex];
                    string cta = row.Cells["CodigoCuenta"]?.Value?.ToString();
                    string mon = "01";
                    if (!string.IsNullOrEmpty(cta) && mapaMonedas.TryGetValue(cta, out string m))
                    {
                        mon = m;
                    }
                    string prefijo = (mon == "01" || mon == "Soles") ? "S/ " : "$ ";
                    if (col == "Importe")
                    {
                        e.Value = (val < 0 ? "- " : "+ ") + prefijo + Math.Abs(val).ToString("N2");
                    }
                    else
                    {
                        e.Value = prefijo + val.ToString("N2");
                    }
                    e.FormattingApplied = true;
                }
            }
        }

        private void btn_Ingresar_Click(object sender, EventArgs e)
        {
            using (frmModalPersonalizado miModal = new frmModalPersonalizado() { User = User })
            {
                DialogResult resultado = miModal.ShowDialog();

                if (resultado == DialogResult.OK)
                {
                    CrearMovimiento(miModal.Tipo, miModal.CuentaOrigen, miModal.CuentaDestino, miModal.Importe);
                }
            }
        }

        private async void CrearMovimiento(string tipo, string cuentaOrigen, string cuentaDestino, decimal importe)
        {
            try
            {
                var client = new MovimientosServiceClient();
                var resultado = await client.ProcesarMovimientoAsync(tipo, cuentaOrigen, cuentaDestino ?? "", importe);

                if (resultado.Codigo == 1)
                {
                    MessageBox.Show(resultado.Mensaje ?? "Movimiento realizado exitosamente.", "Éxito", MessageBoxButtons.OK, MessageBoxIcon.Information);
                    await LoadMovements();
                }
                else
                {
                    MessageBox.Show(resultado.Mensaje, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error al crear movimiento: {ex.Message}", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private async void Movements_Load_1(object sender, EventArgs e)
        {
            await LoadMovements();
        }
    }
}
