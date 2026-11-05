using ec.edu.monster.Cliente;
using ec.edu.monster.Cliente.Dto;
using ec.edu.monster.Controlador;
using ec.edu.monster.Util;

namespace ec.edu.monster.Vista;

/// <summary>
/// Pantalla principal: consulta de movimientos (CU-04), depósito (CU-05), retiro (CU-06) y
/// transferencia (CU-07). Abajo se muestran los datos y movimientos de la última cuenta usada.
/// La flecha de los campos de cuenta despliega todas las cuentas del servidor.
/// </summary>
public partial class OperacionesPagina : ContentPage
{
    private enum Operacion
    {
        Movimientos,
        Deposito,
        Retiro,
        Transferencia
    }

    private static readonly Color ColorPrimario = Color.FromArgb("#005493");
    private static readonly Color ColorSuave = Color.FromArgb("#E1EAF4");
    private static readonly Color ColorIngreso = Color.FromArgb("#198754");
    private static readonly Color ColorSalida = Color.FromArgb("#C00000");
    private static readonly Color ColorTenue = Color.FromArgb("#6C757D");

    private readonly LoginControlador loginControlador;
    private readonly CuentaControlador cuentaControlador;
    private readonly Dictionary<Button, Operacion> pestanias;
    private Operacion operacion;
    private string? cuentaMostrada;
    // Se piden al servidor la primera vez que se abre la lista y se renuevan con "Actualizar"
    private List<CuentaDto>? cuentasServidor;
    private bool enPeticion;
    private string textoCuentaAnterior = string.Empty;
    private string textoDestinoAnterior = string.Empty;
    private string textoImporteAnterior = string.Empty;

    public OperacionesPagina(LoginControlador loginControlador)
    {
        InitializeComponent();
        this.loginControlador = loginControlador;
        cuentaControlador = loginControlador.CrearControladorCuenta();

        // En el teléfono solo cabe el primer nombre; el nombre completo aparece en el menú
        btnUsuario.Text = $"{loginControlador.Empleado?.Split(' ')[0]}  ▾";
        pestanias = new Dictionary<Button, Operacion>
        {
            [btnMovimientos] = Operacion.Movimientos,
            [btnDeposito] = Operacion.Deposito,
            [btnRetiro] = Operacion.Retiro,
            [btnTransferencia] = Operacion.Transferencia
        };

        ResaltadoCampo.Aplicar(txtCuenta, bordeCuenta);
        ResaltadoCampo.Aplicar(txtDestino, bordeDestino);
        ResaltadoCampo.Aplicar(txtImporte, bordeImporte);
        SeleccionarOperacion(Operacion.Movimientos);
    }

    // Al volver de "Cambiar contraseña" se recarga la cuenta: puede haber movimientos desde otros clientes
    protected override async void OnAppearing()
    {
        base.OnAppearing();
        if (cuentaMostrada is not null && !enPeticion)
        {
            await EjecutarAsync(() => CargarCuentaAsync(cuentaMostrada, mostrarSaldo: false));
        }
    }

    private void BtnOperacion_Clicked(object? sender, EventArgs e)
    {
        if (!enPeticion && sender is Button boton && pestanias.TryGetValue(boton, out Operacion elegida))
        {
            SeleccionarOperacion(elegida);
        }
    }

    // Cada operación muestra solo los campos que necesita
    private void SeleccionarOperacion(Operacion elegida)
    {
        operacion = elegida;
        PintarPestanias();
        MostrarError(null);
        QuitarErrores();

        bool esTransferencia = elegida == Operacion.Transferencia;
        bool llevaImporte = elegida != Operacion.Movimientos;
        lblCuenta.Text = esTransferencia ? "Cuenta de origen" : "Número de cuenta";
        pnlDestino.IsVisible = esTransferencia;
        pnlImporte.IsVisible = llevaImporte;
        txtCuenta.ReturnType = llevaImporte ? ReturnType.Next : ReturnType.Done;

        (btnEjecutar.Text, lblAyuda.Text) = elegida switch
        {
            Operacion.Deposito => ("Depositar", "Registre un depósito en una cuenta activa."),
            Operacion.Retiro => ("Retirar", "Registre un retiro; la cuenta debe tener saldo suficiente."),
            Operacion.Transferencia => ("Transferir", "Transfiera entre dos cuentas activas distintas."),
            _ => ("Consultar", "Consulte los datos, el saldo y los movimientos de una cuenta.")
        };
        lblResultadoTitulo.Text = llevaImporte ? "Comprobante" : "Saldo disponible";
        lblResultado.Text = "—";
    }

    // La pestaña activa se pinta en azul; las demás en tono suave
    private void PintarPestanias()
    {
        foreach ((Button boton, Operacion opcion) in pestanias)
        {
            bool activa = opcion == operacion;
            boton.BackgroundColor = activa ? ColorPrimario : ColorSuave;
            boton.TextColor = activa ? Colors.White : ColorPrimario;
        }
    }

    // Bloquea lo que no es un número de cuenta o un importe: si deja de serlo, se restaura el texto anterior
    private void TxtCuenta_TextChanged(object? sender, TextChangedEventArgs e)
    {
        bool esDestino = sender == txtDestino;
        if (ValidacionUtil.EsCuentaParcial(e.NewTextValue))
        {
            if (esDestino)
            {
                textoDestinoAnterior = e.NewTextValue ?? string.Empty;
                ResaltadoCampo.QuitarError(bordeDestino);
            }
            else
            {
                textoCuentaAnterior = e.NewTextValue ?? string.Empty;
                ResaltadoCampo.QuitarError(bordeCuenta);
            }
            return;
        }
        if (esDestino)
        {
            txtDestino.Text = textoDestinoAnterior;
        }
        else
        {
            txtCuenta.Text = textoCuentaAnterior;
        }
    }

    private void TxtImporte_TextChanged(object? sender, TextChangedEventArgs e)
    {
        if (ValidacionUtil.EsImporteParcial(e.NewTextValue))
        {
            textoImporteAnterior = e.NewTextValue ?? string.Empty;
            ResaltadoCampo.QuitarError(bordeImporte);
            return;
        }
        txtImporte.Text = textoImporteAnterior;
    }

    private void TxtCuenta_Completed(object? sender, EventArgs e)
    {
        if (operacion == Operacion.Movimientos)
        {
            BtnEjecutar_Clicked(sender, e);
        }
        else
        {
            (operacion == Operacion.Transferencia ? txtDestino : txtImporte).Focus();
        }
    }

    private void TxtDestino_Completed(object? sender, EventArgs e) => txtImporte.Focus();

    private async void BtnEjecutar_Clicked(object? sender, EventArgs e)
    {
        if (enPeticion)
        {
            return;
        }
        MostrarError(null);
        QuitarErrores();

        if (!Leer(() => ValidacionUtil.ValidarCuenta(txtCuenta.Text), bordeCuenta, out string cuenta))
        {
            return;
        }
        if (operacion == Operacion.Movimientos)
        {
            await EjecutarAsync(() => CargarCuentaAsync(cuenta, mostrarSaldo: true));
            return;
        }

        string destino = string.Empty;
        if (operacion == Operacion.Transferencia)
        {
            if (!Leer(() => ValidacionUtil.ValidarCuenta(txtDestino.Text), bordeDestino, out destino))
            {
                return;
            }
            if (destino == cuenta)
            {
                MostrarErrorDeCampo(bordeDestino, ValidacionUtil.MensajeCuentasIguales);
                return;
            }
        }
        if (!Leer(() => ValidacionUtil.InterpretarImporte(txtImporte.Text), bordeImporte, out decimal importe))
        {
            return;
        }

        string monto = FormatoUtil.FormatearMonto(importe);
        (string titulo, string mensaje, string aceptar) = operacion switch
        {
            Operacion.Deposito => ("¿Registrar el depósito?", $"Se va a depositar {monto} en la cuenta {cuenta}.", "Sí, depositar"),
            Operacion.Retiro => ("¿Registrar el retiro?", $"Se va a retirar {monto} de la cuenta {cuenta}.", "Sí, retirar"),
            _ => ("¿Registrar la transferencia?",
                    $"Se van a transferir {monto} de la cuenta {cuenta} a la cuenta {destino}. Ambas cuentas se actualizan en una sola operación.",
                    "Sí, transferir")
        };
        if (!await DisplayAlertAsync(titulo, mensaje, aceptar, "Cancelar"))
        {
            return;
        }

        await EjecutarAsync(async () =>
        {
            RespuestaTransaccion respuesta = operacion switch
            {
                Operacion.Deposito => await cuentaControlador.DepositarAsync(cuenta, importe),
                Operacion.Retiro => await cuentaControlador.RetirarAsync(cuenta, importe),
                _ => await cuentaControlador.TransferirAsync(cuenta, destino, importe)
            };
            lblResultadoTitulo.Text = $"Movimiento Nro {respuesta.NumeroMovimiento} · nuevo saldo de la cuenta {cuenta}:";
            lblResultado.Text = FormatoUtil.FormatearMonto(respuesta.Saldo);
            txtImporte.Text = string.Empty;
            await CargarCuentaAsync(cuenta, mostrarSaldo: false);
            await DisplayAlertAsync("¡Operación registrada!",
                    $"{respuesta.Mensaje}. Nuevo saldo: {FormatoUtil.FormatearMonto(respuesta.Saldo)}.", "Aceptar");
        });
    }

    // Lista de cuentas en una hoja de opciones; las que no están activas muestran su estado
    // (se pueden consultar, pero el servidor rechaza depósitos, retiros y transferencias en ellas)
    private async void BtnListaCuenta_Clicked(object? sender, EventArgs e)
    {
        if (enPeticion)
        {
            return;
        }
        bool esDestino = sender == btnListaDestino;
        if (cuentasServidor is null)
        {
            await EjecutarAsync(async () => cuentasServidor = (await cuentaControlador.ListarCuentasAsync()).Cuentas);
            if (cuentasServidor is null)
            {
                return;
            }
        }
        if (cuentasServidor.Count == 0)
        {
            MostrarError("No hay cuentas registradas en el servidor.");
            return;
        }

        Dictionary<string, string> opciones = cuentasServidor.ToDictionary(FormatoUtil.FormatearOpcionCuenta, c => c.Numero);
        string? elegida = await DisplayActionSheetAsync(esDestino ? "Cuenta de destino" : "Elija la cuenta",
                "Cancelar", null, opciones.Keys.ToArray());
        if (elegida is not null && opciones.TryGetValue(elegida, out string? numero))
        {
            (esDestino ? txtDestino : txtCuenta).Text = numero;
        }
    }

    private async void BtnActualizar_Clicked(object? sender, EventArgs e)
    {
        if (!enPeticion && cuentaMostrada is not null)
        {
            cuentasServidor = null;
            string cuenta = cuentaMostrada;
            await EjecutarAsync(() => CargarCuentaAsync(cuenta, mostrarSaldo: false));
        }
    }

    // Los movimientos vienen del servidor: incluyen lo registrado desde la consola, el escritorio y la web
    private async Task CargarCuentaAsync(string numero, bool mostrarSaldo)
    {
        RespuestaMovimientos respuesta = await cuentaControlador.ConsultarAsync(numero);
        CuentaDto cuenta = respuesta.Cuenta!;
        string saldo = FormatoUtil.FormatearMonto(cuenta.Saldo, cuenta.SimboloMoneda);

        lblCuentaTitulo.Text = "Cuenta " + cuenta.Numero;
        lblCuentaDatos.Text = $"{cuenta.Cliente} · cédula {cuenta.Cedula}\n" +
                $"{cuenta.Sucursal} · {cuenta.Moneda} · estado {cuenta.Estado}\n" +
                $"Saldo {saldo} · {respuesta.Mensaje}";
        lblCuentaDatos.TextColor = cuenta.Estado == "ACTIVO" ? ColorTenue : ColorSalida;

        listaMovimientos.Children.Clear();
        // El más reciente arriba
        foreach (MovimientoDto movimiento in respuesta.Movimientos.OrderByDescending(m => m.Numero))
        {
            listaMovimientos.Children.Add(CrearFilaMovimiento(movimiento, cuenta.SimboloMoneda));
        }
        pnlSinMovimientos.IsVisible = respuesta.Movimientos.Count == 0;

        cuentaMostrada = cuenta.Numero;
        btnActualizar.IsVisible = true;

        if (mostrarSaldo)
        {
            lblResultadoTitulo.Text = $"Saldo disponible de la cuenta {cuenta.Numero} ({cuenta.Estado})";
            lblResultado.Text = saldo;
        }
    }

    private View CrearFilaMovimiento(MovimientoDto movimiento, string simbolo)
    {
        var fila = new Grid
        {
            ColumnDefinitions = [new ColumnDefinition(GridLength.Star), new ColumnDefinition(GridLength.Auto)],
            Padding = new Thickness(0, 10),
            ColumnSpacing = 8
        };

        var datos = new VerticalStackLayout { Spacing = 2 };
        datos.Children.Add(new Label
        {
            Text = $"N° {movimiento.Numero} · {FormatoUtil.FormatearFechaHora(movimiento.FechaHora)}",
            TextColor = ColorTenue,
            FontSize = 12
        });
        datos.Children.Add(new Label
        {
            Text = movimiento.Tipo,
            TextColor = Color.FromArgb("#212529"),
            FontSize = 15
        });
        if (movimiento.CuentaReferencia is not null)
        {
            datos.Children.Add(new Label
            {
                Text = $"{(movimiento.EsIngreso ? "desde" : "hacia")} la cuenta {movimiento.CuentaReferencia}",
                TextColor = ColorTenue,
                FontSize = 13
            });
        }

        var importe = new Label
        {
            Text = FormatoUtil.FormatearMovimiento(movimiento.Importe, movimiento.EsIngreso, simbolo),
            TextColor = movimiento.EsIngreso ? ColorIngreso : ColorSalida,
            FontSize = 16,
            FontAttributes = FontAttributes.Bold,
            VerticalOptions = LayoutOptions.Center
        };
        SemanticProperties.SetDescription(fila, $"{movimiento.Tipo}, {movimiento.Accion}, {importe.Text}");

        fila.Add(datos);
        fila.Add(importe, 1);

        var contenedor = new VerticalStackLayout();
        contenedor.Children.Add(fila);
        contenedor.Children.Add(new BoxView { HeightRequest = 1, Color = Color.FromArgb("#E9ECEF") });
        return contenedor;
    }

    // Las opciones de la sesión se agrupan en un menú para no ocupar espacio en la pantalla
    private async void BtnUsuario_Clicked(object? sender, EventArgs e)
    {
        const string cambiarContrasenia = "Cambiar contraseña";
        const string cerrarSesion = "Cerrar sesión";

        string opcion = await DisplayActionSheetAsync(loginControlador.Empleado, "Cancelar", null, cambiarContrasenia, cerrarSesion);

        if (opcion == cambiarContrasenia)
        {
            await Navigation.PushAsync(new CambiarContraseniaPagina(loginControlador));
        }
        else if (opcion == cerrarSesion)
        {
            bool confirmar = await DisplayAlertAsync("¿Cerrar sesión?",
                    "Tendrá que volver a ingresar su usuario y contraseña para seguir operando.",
                    "Sí, cerrar sesión", "Cancelar");
            if (confirmar)
            {
                await CerrarSesionAsync();
            }
        }
    }

    private async Task CerrarSesionAsync()
    {
        string aviso = "Sesión cerrada correctamente.";
        try
        {
            await loginControlador.CerrarSesionAsync();
        }
        catch (ExcepcionClienteWS ex) when (ex.EsErrorConexion)
        {
            aviso = "Sesión cerrada en este equipo; no se pudo avisar al servidor.";
        }
        catch (ExcepcionClienteWS)
        {
            // La sesión ya había vencido en el servidor: no hay nada más que cerrar
        }
        App.Actual.MostrarInicioSesion(aviso);
    }

    private async Task EjecutarAsync(Func<Task> accion)
    {
        CambiarEstadoOcupado(true);
        try
        {
            await accion();
        }
        catch (ExcepcionClienteWS ex)
        {
            await ManejarErrorServicioAsync(ex);
        }
        catch (ArgumentException ex)
        {
            MostrarError(ex.Message + ".");
        }
        finally
        {
            CambiarEstadoOcupado(false);
        }
    }

    private bool Leer<T>(Func<T> validacion, Border borde, out T valor)
    {
        try
        {
            valor = validacion();
            return true;
        }
        catch (ArgumentException ex)
        {
            valor = default!;
            MostrarErrorDeCampo(borde, ex.Message);
            return false;
        }
    }

    private void MostrarErrorDeCampo(Border borde, string mensaje)
    {
        ResaltadoCampo.MarcarError(borde);
        MostrarError(mensaje + ".");
    }

    private void QuitarErrores()
    {
        ResaltadoCampo.QuitarError(bordeCuenta);
        ResaltadoCampo.QuitarError(bordeDestino);
        ResaltadoCampo.QuitarError(bordeImporte);
    }

    private void MostrarError(string? mensaje)
    {
        lblError.Text = mensaje;
        pnlError.IsVisible = !string.IsNullOrEmpty(mensaje);
    }

    // 401: token inválido o sesión vencida (CU-10) → inicio de sesión.
    // Sin conexión: se ofrece cerrar sesión para cambiar la dirección del servidor.
    private async Task ManejarErrorServicioAsync(ExcepcionClienteWS ex)
    {
        if (ex.EsErrorAutenticacion)
        {
            await DisplayAlertAsync("Sesión no válida", ex.Message + "\n\nDebe iniciar sesión nuevamente.", "Entendido");
            App.Actual.MostrarInicioSesion("Inicie sesión para continuar.");
            return;
        }
        if (ex.EsErrorConexion)
        {
            if (await DisplayAlertAsync("Sin conexión con el servidor",
                    ex.Message + "\n\n¿Desea cerrar sesión para cambiar la dirección del servidor?", "Cambiar servidor", "Cancelar"))
            {
                await CerrarSesionAsync();
            }
            return;
        }
        if (ex.CodigoEstado == 404)
        {
            ResaltadoCampo.MarcarError(bordeCuenta);
        }
        MostrarError(ex.Message);
    }

    private void CambiarEstadoOcupado(bool ocupado)
    {
        enPeticion = ocupado;
        btnEjecutar.IsEnabled = !ocupado;
        btnUsuario.IsEnabled = !ocupado;
        btnActualizar.IsEnabled = !ocupado;
        btnListaCuenta.IsEnabled = btnListaDestino.IsEnabled = !ocupado;
        indicadorCarga.IsRunning = ocupado;
        indicadorCarga.IsVisible = ocupado;
    }
}
