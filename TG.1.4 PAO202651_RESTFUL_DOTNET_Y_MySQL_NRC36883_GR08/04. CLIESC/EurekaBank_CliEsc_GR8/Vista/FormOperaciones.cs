using ec.edu.monster.Cliente;
using ec.edu.monster.Cliente.Dto;
using ec.edu.monster.Controlador;
using ec.edu.monster.Util;
using ec.edu.monster.Vista.Controles;

namespace ec.edu.monster.Vista
{
    /// <summary>
    /// Pantalla principal: consulta de movimientos (CU-04), depósito (CU-05), retiro (CU-06) y
    /// transferencia (CU-07). A la derecha se muestran los datos y movimientos de la última cuenta usada.
    /// Los campos de cuenta tienen una flecha que despliega todas las cuentas del servidor.
    /// </summary>
    public partial class FormOperaciones : Form
    {
        /// <summary>Cómo se cerró la pantalla.</summary>
        public enum Salida
        {
            SalirDeLaAplicacion,
            VolverAlInicioDeSesion,
            ConfigurarServidor
        }

        private enum Operacion
        {
            Movimientos,
            Deposito,
            Retiro,
            Transferencia
        }

        private static readonly Color ColorPrimario = Color.FromArgb(0, 84, 147);
        private static readonly Color ColorPrimarioHover = Color.FromArgb(0, 99, 177);
        private static readonly Color ColorSuave = Color.FromArgb(225, 234, 244);
        private static readonly Color ColorSuaveHover = Color.FromArgb(205, 222, 240);
        private static readonly Color ColorSuavePresionado = Color.FromArgb(185, 208, 232);
        private static readonly Color ColorIngreso = Color.FromArgb(25, 135, 84);
        private static readonly Color ColorSalida = Color.FromArgb(192, 0, 0);
        private static readonly Color ColorTexto = Color.FromArgb(33, 37, 41);

        private readonly LoginControlador loginControlador;
        private readonly CuentaControlador cuentaControlador;
        private readonly Dictionary<BotonRedondeado, Operacion> pestanias;
        private readonly ContextMenuStrip mnuCuentas = new()
        {
            Font = new Font("Segoe UI", 11F),
            ShowImageMargin = false,
            ShowCheckMargin = false
        };
        private readonly string textoEstado;
        private Operacion operacion;
        private string? cuentaMostrada;
        // Se piden al servidor la primera vez que se abre la lista y se renuevan con "Actualizar"
        private List<CuentaDto>? cuentasServidor;
        private bool enPeticion;

        public Salida ResultadoSalida { get; private set; } = Salida.SalirDeLaAplicacion;

        public FormOperaciones(LoginControlador loginControlador)
        {
            InitializeComponent();
            this.loginControlador = loginControlador;
            cuentaControlador = loginControlador.CrearControladorCuenta();

            btnUsuario.Text = $"{loginControlador.Empleado}  ▼";
            textoEstado = $"Conectado a {loginControlador.UrlServidor}";
            lblEstado.Text = textoEstado;

            pestanias = new Dictionary<BotonRedondeado, Operacion>
            {
                [btnMovimientos] = Operacion.Movimientos,
                [btnDeposito] = Operacion.Deposito,
                [btnRetiro] = Operacion.Retiro,
                [btnTransferencia] = Operacion.Transferencia
            };
            SeleccionarOperacion(Operacion.Movimientos);

            txtCuenta.ConLista = txtDestino.ConLista = true;
            txtCuenta.ListaSolicitada += campoCuenta_ListaSolicitada;
            txtDestino.ListaSolicitada += campoCuenta_ListaSolicitada;
            toolTip.SetToolTip(txtCuenta, "Escriba el número o pulse la flecha (Alt+↓) para elegir la cuenta");
            toolTip.SetToolTip(txtDestino, "Escriba el número o pulse la flecha (Alt+↓) para elegir la cuenta");
        }

        private void FormOperaciones_Shown(object? sender, EventArgs e)
        {
            txtCuenta.Focus();
        }

        private void btnOperacion_Click(object? sender, EventArgs e)
        {
            if (!enPeticion && sender is BotonRedondeado boton && pestanias.TryGetValue(boton, out Operacion elegida))
            {
                SeleccionarOperacion(elegida);
            }
        }

        // Cada operación muestra solo los campos que necesita
        private void SeleccionarOperacion(Operacion elegida)
        {
            operacion = elegida;
            PintarPestanias();
            errorProvider.Clear();

            bool esTransferencia = elegida == Operacion.Transferencia;
            bool llevaImporte = elegida != Operacion.Movimientos;

            lblCuenta.Text = esTransferencia ? "Cuenta de origen" : "Número de cuenta";
            txtCuenta.Width = esTransferencia ? 244 : 512;
            lblDestino.Visible = txtDestino.Visible = esTransferencia;
            lblImporte.Visible = txtImporte.Visible = llevaImporte;
            btnEjecutar.Top = llevaImporte ? 194 : 106;

            (btnEjecutar.Text, lblSubtitulo.Text) = elegida switch
            {
                Operacion.Deposito => ("Depositar", "Registre un depósito en una cuenta activa"),
                Operacion.Retiro => ("Retirar", "Registre un retiro; la cuenta debe tener saldo suficiente"),
                Operacion.Transferencia => ("Transferir", "Transfiera entre dos cuentas activas distintas"),
                _ => ("Consultar", "Consulte los datos, el saldo y los movimientos de una cuenta")
            };

            if (!llevaImporte)
            {
                txtImporte.Clear();
            }
            LimpiarResultado();
            txtCuenta.Focus();
        }

        // La pestaña activa se pinta en azul; las demás en tono suave
        private void PintarPestanias()
        {
            foreach ((BotonRedondeado boton, Operacion opcion) in pestanias)
            {
                bool activa = opcion == operacion;
                boton.BackColor = activa ? ColorPrimario : ColorSuave;
                boton.ForeColor = activa ? Color.White : ColorPrimario;
                boton.ColorHover = activa ? ColorPrimarioHover : ColorSuaveHover;
                boton.ColorPresionado = activa ? ColorPrimario : ColorSuavePresionado;
            }
        }

        private async void btnEjecutar_Click(object? sender, EventArgs e)
        {
            if (enPeticion)
            {
                return;
            }
            errorProvider.Clear();

            switch (operacion)
            {
                case Operacion.Movimientos:
                    await ConsultarAsync();
                    break;
                case Operacion.Deposito:
                case Operacion.Retiro:
                    await RegistrarEnCuentaAsync(operacion == Operacion.Deposito);
                    break;
                case Operacion.Transferencia:
                    await TransferirAsync();
                    break;
            }
        }

        private async Task ConsultarAsync()
        {
            if (!LeerCuenta(txtCuenta, out string cuenta))
            {
                return;
            }
            await EjecutarAsync("Consultando la cuenta " + cuenta + "...", () => CargarCuentaAsync(cuenta, mostrarSaldo: true));
        }

        private async Task RegistrarEnCuentaAsync(bool esDeposito)
        {
            if (!LeerCuenta(txtCuenta, out string cuenta) || !LeerImporte(out decimal importe))
            {
                return;
            }

            string accion = esDeposito ? "depositar" : "retirar";
            if (!DialogoConfirmacion.Confirmar(this, esDeposito ? "¿Registrar el depósito?" : "¿Registrar el retiro?",
                    $"Se va a {accion} {FormatoUtil.FormatearMonto(importe)} en la cuenta {cuenta}.",
                    esDeposito ? "Sí, depositar" : "Sí, retirar"))
            {
                return;
            }

            await EjecutarAsync(esDeposito ? "Registrando el depósito..." : "Registrando el retiro...", async () =>
            {
                RespuestaTransaccion respuesta = esDeposito
                        ? await cuentaControlador.DepositarAsync(cuenta, importe)
                        : await cuentaControlador.RetirarAsync(cuenta, importe);
                MostrarComprobante(respuesta, cuenta);
                txtImporte.Clear();
                await CargarCuentaAsync(cuenta, mostrarSaldo: false);
            });
        }

        private async Task TransferirAsync()
        {
            if (!LeerCuenta(txtCuenta, out string origen) || !LeerCuenta(txtDestino, out string destino))
            {
                return;
            }
            if (origen == destino)
            {
                MostrarErrorDeCampo(txtDestino, ValidacionUtil.MensajeCuentasIguales);
                return;
            }
            if (!LeerImporte(out decimal importe))
            {
                return;
            }

            if (!DialogoConfirmacion.Confirmar(this, "¿Registrar la transferencia?",
                    $"Se van a transferir {FormatoUtil.FormatearMonto(importe)} de la cuenta {origen} a la cuenta {destino}. " +
                    "Ambas cuentas se actualizan en una sola operación.", "Sí, transferir"))
            {
                return;
            }

            await EjecutarAsync("Registrando la transferencia...", async () =>
            {
                RespuestaTransaccion respuesta = await cuentaControlador.TransferirAsync(origen, destino, importe);
                MostrarComprobante(respuesta, origen);
                txtImporte.Clear();
                await CargarCuentaAsync(origen, mostrarSaldo: false);
            });
        }

        // Despliega todas las cuentas debajo del campo; las que no están activas se muestran en rojo
        // (se pueden consultar, pero el servidor rechaza depósitos, retiros y transferencias en ellas)
        private async void campoCuenta_ListaSolicitada(object? sender, EventArgs e)
        {
            if (enPeticion || sender is not CampoTexto campo)
            {
                return;
            }
            if (cuentasServidor is null)
            {
                await EjecutarAsync("Cargando la lista de cuentas...", async () =>
                        cuentasServidor = (await cuentaControlador.ListarCuentasAsync()).Cuentas);
                if (cuentasServidor is null)
                {
                    return;
                }
            }

            mnuCuentas.Items.Clear();
            if (cuentasServidor.Count == 0)
            {
                mnuCuentas.Items.Add(new ToolStripMenuItem("No hay cuentas registradas") { Enabled = false });
            }
            foreach (CuentaDto cuenta in cuentasServidor)
            {
                var opcion = new ToolStripMenuItem(FormatoUtil.FormatearOpcionCuenta(cuenta))
                {
                    ForeColor = cuenta.EstaActiva ? ColorTexto : ColorSalida,
                    Font = cuenta.Numero == campo.Text ? new Font(mnuCuentas.Font, FontStyle.Bold) : mnuCuentas.Font,
                    Padding = new Padding(4, 6, 4, 6)
                };
                opcion.Click += (_, _) =>
                {
                    campo.Text = cuenta.Numero;
                    campo.Focus();
                    campo.SelectionStart = campo.Text.Length;
                };
                mnuCuentas.Items.Add(opcion);
            }
            mnuCuentas.MinimumSize = new Size(campo.Width, 0);
            mnuCuentas.Show(campo, new Point(0, campo.Height + 2));
        }

        private async void btnActualizar_Click(object? sender, EventArgs e)
        {
            if (!enPeticion && cuentaMostrada is not null)
            {
                cuentasServidor = null;
                string cuenta = cuentaMostrada;
                await EjecutarAsync("Actualizando la cuenta " + cuenta + "...", () => CargarCuentaAsync(cuenta, mostrarSaldo: false));
            }
        }

        // Los movimientos vienen del servidor: incluyen lo registrado desde la consola, la web y el móvil
        private async Task CargarCuentaAsync(string numero, bool mostrarSaldo)
        {
            RespuestaMovimientos respuesta = await cuentaControlador.ConsultarAsync(numero);
            CuentaDto cuenta = respuesta.Cuenta!;
            string saldo = FormatoUtil.FormatearMonto(cuenta.Saldo, cuenta.SimboloMoneda);

            lblCuentaTitulo.Text = "Cuenta " + cuenta.Numero;
            lblCuentaDatos.Text = $"{cuenta.Cliente} · cédula {cuenta.Cedula}\n" +
                    $"{cuenta.Sucursal} · {cuenta.Moneda} · estado {cuenta.Estado}\n" +
                    $"Saldo {saldo} · {respuesta.Mensaje}";
            lblCuentaDatos.ForeColor = cuenta.Estado == "ACTIVO" ? Color.FromArgb(108, 117, 125) : ColorSalida;

            dgvMovimientos.SuspendLayout();
            dgvMovimientos.Rows.Clear();
            // El más reciente arriba
            foreach (MovimientoDto movimiento in respuesta.Movimientos.OrderByDescending(m => m.Numero))
            {
                string tipo = movimiento.CuentaReferencia is null
                        ? movimiento.Tipo
                        : $"{movimiento.Tipo}\n{(movimiento.EsIngreso ? "desde" : "hacia")} {movimiento.CuentaReferencia}";
                int indice = dgvMovimientos.Rows.Add(movimiento.Numero, movimiento.FechaHora.ToString("dd/MM/yyyy\nHH:mm:ss"),
                        tipo, FormatoUtil.FormatearMovimiento(movimiento.Importe, movimiento.EsIngreso, cuenta.SimboloMoneda));

                DataGridViewRow fila = dgvMovimientos.Rows[indice];
                fila.Cells[colImporte.Index].Style.ForeColor = movimiento.EsIngreso ? ColorIngreso : ColorSalida;
                fila.Cells[colImporte.Index].Style.SelectionForeColor = movimiento.EsIngreso ? ColorIngreso : ColorSalida;
                fila.Cells[colTipo.Index].ToolTipText =
                        $"{movimiento.Accion} · registrado por el empleado {movimiento.Empleado} el {FormatoUtil.FormatearFechaHora(movimiento.FechaHora)}";
            }
            dgvMovimientos.ResumeLayout();
            dgvMovimientos.ClearSelection();
            dgvMovimientos.CurrentCell = null;

            cuentaMostrada = cuenta.Numero;
            btnActualizar.Enabled = true;

            if (mostrarSaldo)
            {
                lblResultadoTitulo.Text = $"Saldo disponible de la cuenta {cuenta.Numero} ({cuenta.Estado})";
                lblResultado.Text = saldo;
                toolTip.SetToolTip(lblResultado, $"{cuenta.Cliente} · {cuenta.Sucursal}");
            }
        }

        private void MostrarComprobante(RespuestaTransaccion respuesta, string cuenta)
        {
            lblResultadoTitulo.Text = $"Movimiento Nro {respuesta.NumeroMovimiento} · nuevo saldo de la cuenta {cuenta}:";
            lblResultado.Text = FormatoUtil.FormatearMonto(respuesta.Saldo);
            toolTip.SetToolTip(lblResultado, respuesta.Mensaje);
            DialogoConfirmacion.Informar(this, "¡Operación registrada!",
                    $"{respuesta.Mensaje}. Nuevo saldo: {FormatoUtil.FormatearMonto(respuesta.Saldo)}.");
        }

        private void LimpiarResultado()
        {
            lblResultadoTitulo.Text = operacion == Operacion.Movimientos ? "Saldo disponible" : "Comprobante";
            lblResultado.Text = "—";
            toolTip.SetToolTip(lblResultado, string.Empty);
        }

        private bool LeerCuenta(CampoTexto campo, out string cuenta)
        {
            try
            {
                cuenta = ValidacionUtil.ValidarCuenta(campo.Text);
                return true;
            }
            catch (ArgumentException ex)
            {
                cuenta = string.Empty;
                MostrarErrorDeCampo(campo, ex.Message);
                return false;
            }
        }

        private bool LeerImporte(out decimal importe)
        {
            try
            {
                importe = ValidacionUtil.InterpretarImporte(txtImporte.Text);
                return true;
            }
            catch (ArgumentException ex)
            {
                importe = 0;
                MostrarErrorDeCampo(txtImporte, ex.Message);
                return false;
            }
        }

        // Bloquea letras y símbolos mientras se escribe; lo pegado se valida al enviar
        private void txtCuenta_KeyPress(object? sender, KeyPressEventArgs e)
        {
            if (!ValidacionUtil.EsCaracterCuentaPermitido(e.KeyChar))
            {
                e.Handled = true;
            }
        }

        private void txtImporte_KeyPress(object? sender, KeyPressEventArgs e)
        {
            string textoSinSeleccion = txtImporte.Text.Remove(txtImporte.SelectionStart, txtImporte.SelectionLength);
            if (!ValidacionUtil.EsCaracterImportePermitido(e.KeyChar, textoSinSeleccion, txtImporte.SelectionStart))
            {
                e.Handled = true;
            }
        }

        private void txtCampo_TextChanged(object? sender, EventArgs e)
        {
            if (sender is Control campo)
            {
                errorProvider.SetError(campo, string.Empty);
            }
        }

        // Las opciones de la sesión se agrupan en un menú para no ocupar espacio en la ventana
        private void btnUsuario_Click(object? sender, EventArgs e)
        {
            mnuUsuario.Show(btnUsuario, new Point(btnUsuario.Width - mnuUsuario.Width, btnUsuario.Height + 4));
        }

        // Cambiar contraseña se abre como otra pantalla: mismo tamaño y misma posición que esta,
        // así la cubre por completo en lugar de verse como una ventana flotante
        private void mnuCambiarContrasenia_Click(object? sender, EventArgs e)
        {
            using var pantalla = new FormCambiarContrasenia(loginControlador)
            {
                StartPosition = FormStartPosition.Manual,
                Bounds = Bounds
            };
            pantalla.ShowDialog(this);

            // Si el usuario movió la pantalla, esta reaparece en ese mismo lugar
            Location = pantalla.Location;

            if (pantalla.SesionExpirada)
            {
                Cerrar(Salida.VolverAlInicioDeSesion);
            }
        }

        private async void mnuCerrarSesion_Click(object? sender, EventArgs e)
        {
            if (!DialogoConfirmacion.Confirmar(this, "¿Cerrar sesión?",
                    "Tendrá que volver a ingresar su usuario y contraseña para seguir operando.", "Sí, cerrar sesión"))
            {
                return;
            }

            try
            {
                await loginControlador.CerrarSesionAsync();
            }
            catch (ExcepcionClienteWS ex) when (ex.EsErrorConexion)
            {
                DialogoConfirmacion.Informar(this, "Sesión cerrada en este equipo",
                        "No se pudo avisar al servidor; la sesión vencerá sola tras 30 minutos sin uso.", "!");
            }
            catch (ExcepcionClienteWS)
            {
                // La sesión ya había vencido en el servidor: no hay nada más que cerrar
            }
            Cerrar(Salida.VolverAlInicioDeSesion);
        }

        private async Task EjecutarAsync(string mensaje, Func<Task> accion)
        {
            CambiarEstadoOcupado(true, mensaje);
            try
            {
                await accion();
            }
            catch (ExcepcionClienteWS ex)
            {
                ManejarErrorServicio(ex);
            }
            catch (ArgumentException ex)
            {
                DialogoConfirmacion.Informar(this, "Datos inválidos", ex.Message + ".", "!");
            }
            finally
            {
                CambiarEstadoOcupado(false);
            }
        }

        private void MostrarErrorDeCampo(Control campo, string mensaje)
        {
            errorProvider.SetError(campo, mensaje);
            DialogoConfirmacion.Informar(this, "Datos inválidos", mensaje + ".", "!");
            campo.Focus();
        }

        // 401: token inválido o sesión vencida (CU-10) → inicio de sesión.
        // Sin conexión: se ofrece cambiar la dirección del servidor.
        private void ManejarErrorServicio(ExcepcionClienteWS ex)
        {
            if (ex.EsErrorAutenticacion)
            {
                DialogoConfirmacion.Informar(this, "Sesión no válida", ex.Message + " Debe iniciar sesión nuevamente.", "!");
                Cerrar(Salida.VolverAlInicioDeSesion);
                return;
            }
            if (ex.EsErrorConexion)
            {
                if (DialogoConfirmacion.Confirmar(this, "Sin conexión con el servidor",
                        ex.Message + " ¿Desea cambiar la dirección del servidor?", "Configurar servidor"))
                {
                    Cerrar(Salida.ConfigurarServidor);
                }
                return;
            }
            DialogoConfirmacion.Informar(this, "No se pudo completar la operación", ex.Message, "!");
        }

        private void Cerrar(Salida salida)
        {
            ResultadoSalida = salida;
            Close();
        }

        private void CambiarEstadoOcupado(bool ocupado, string? mensaje = null)
        {
            // Las pestañas no se deshabilitan (se verían grises); se ignoran sus clics mientras hay una petición
            enPeticion = ocupado;
            pnlFormulario.Enabled = !ocupado;
            btnUsuario.Enabled = !ocupado;
            btnActualizar.Enabled = !ocupado && cuentaMostrada is not null;
            UseWaitCursor = ocupado;
            lblEstado.Text = ocupado && mensaje is not null ? mensaje : textoEstado;
        }
    }
}
