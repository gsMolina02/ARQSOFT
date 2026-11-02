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

namespace CLIESC_EuBank_SOAPDOTNET_GR08.views
{
    public partial class Home : Form
    {
        public string User { get; set; }

        public Home()
        {
            InitializeComponent();
        }

        [DllImport("user32.DLL", EntryPoint = "ReleaseCapture")]
        private extern static void ReleaseCapture();
        [DllImport("user32.DLL", EntryPoint = "SendMessage")]
        private extern static void SendMessage(System.IntPtr hWnd, int wMsg, int wParam, int IParam);

        private void header_MouseDown(object sender, MouseEventArgs e)
        {
            ReleaseCapture();
            SendMessage(this.Handle, 0x112, 0xf012, 0);
        }

        private void btn_exit_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void min_button_Click(object sender, EventArgs e)
        {
            this.WindowState = FormWindowState.Minimized;
        }

        private void btn_Ingresar_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void btn_my_movements_MouseEnter(object sender, EventArgs e)
        {
            btn_my_movements.ForeColor = Color.White;
        }

        private void btn_my_movements_MouseLeave(object sender, EventArgs e)
        {
            btn_my_movements.ForeColor = Color.FromArgb(135, 83, 243);
        }

        private void btn_clients_MouseEnter(object sender, EventArgs e)
        {
            btn_clients.ForeColor = Color.White;
        }

        private void btn_clients_MouseLeave(object sender, EventArgs e)
        {
            btn_clients.ForeColor = Color.FromArgb(135, 83, 243);
        }

        private void btn_movements_MouseEnter(object sender, EventArgs e)
        {
            btn_movements.ForeColor = Color.White;
        }

        private void btn_movements_MouseLeave(object sender, EventArgs e)
        {
            btn_movements.ForeColor = Color.FromArgb(135, 83, 243);
        }

        private void btn_Ingresar_MouseEnter(object sender, EventArgs e)
        {
            btn_Ingresar.ForeColor = Color.White;
        }

        private void btn_Ingresar_MouseLeave(object sender, EventArgs e)
        {
            btn_Ingresar.ForeColor = Color.FromArgb(135, 83, 243);
        }

        private void btn_my_movements_Click(object sender, EventArgs e)
        {
            AbrirFormHija(new Movements() { User = User, SoloMisMovimientos = true });
        }

        private void btn_movements_Click(object sender, EventArgs e)
        {
            AbrirFormHija(new Movements() { User = User, SoloMisMovimientos = false });
        }

        private void btn_clients_Click(object sender, EventArgs e)
        {
            AbrirFormHija(new AccountClient() { User = User });
        }

        private void AbrirFormHija(object formhija)
        {
            if (this.Contenedor.Controls.Count > 0)
                this.Contenedor.Controls.RemoveAt(0);
            Form fh = formhija as Form;
            fh.TopLevel = false;
            fh.Dock = DockStyle.Fill;
            this.Contenedor.Controls.Add(fh);
            this.Contenedor.Tag = fh;
            fh.Show();
        }

        private void Home_Load(object sender, EventArgs e)
        {
            string u = (User ?? "").Trim().ToLower();
            string nombreCompleto = User;
            string cuentaPrincipal = "00300001";

            if (u == "gustavo" || u == "gmolina")
            {
                nombreCompleto = "Gustavo Stiven Molina";
                cuentaPrincipal = "00200001";
            }
            else if (u == "daniela" || u == "dtituana")
            {
                nombreCompleto = "Daniela Lissette Tituaña";
                cuentaPrincipal = "00200002";
            }
            else if (u == "eduardo" || u == "ecampana" || u == "monster")
            {
                nombreCompleto = "Eduardo Mauricio Campaña Ortega";
                cuentaPrincipal = "00300001";
            }

            lblUsuario.Font = new Font("Leelawadee UI", 9.5F, FontStyle.Bold);
            lblUsuario.Text = $"👤 {nombreCompleto} ({User})   |   💳 Cuenta: {cuentaPrincipal}";

            // Iniciar por defecto en Mis Movimientos
            btn_my_movements_Click(null, e);
        }
    }
}
