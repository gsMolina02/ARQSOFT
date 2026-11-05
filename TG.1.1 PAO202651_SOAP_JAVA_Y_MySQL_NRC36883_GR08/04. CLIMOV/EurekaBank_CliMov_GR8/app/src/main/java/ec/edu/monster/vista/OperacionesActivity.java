package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.EurekaBankApp;
import ec.edu.monster.R;
import ec.edu.monster.cliente.ExcepcionClienteWS;
import ec.edu.monster.cliente.dto.CuentaDTO;
import ec.edu.monster.cliente.dto.MovimientoDTO;
import ec.edu.monster.cliente.dto.RespuestaMovimientos;
import ec.edu.monster.cliente.dto.RespuestaTransaccion;
import ec.edu.monster.controlador.CuentaControlador;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.util.FormatoUtil;
import ec.edu.monster.util.ValidacionUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Pantalla principal: consulta de movimientos (CU-04), depósito (CU-05), retiro (CU-06) y
 * transferencia (CU-07). Abajo se muestran los datos y movimientos de la última cuenta usada.
 */
public class OperacionesActivity extends AppCompatActivity {

    private enum Operacion {
        MOVIMIENTOS,
        DEPOSITO,
        RETIRO,
        TRANSFERENCIA
    }

    private static final int OPCION_CAMBIAR_CONTRASENIA = 1;
    private static final int OPCION_CERRAR_SESION = 2;

    private final Tarea tarea = new Tarea();
    private final Map<Button, Operacion> pestanias = new LinkedHashMap<>();
    private LoginControlador loginControlador;
    private CuentaControlador cuentaControlador;
    private Operacion operacion;
    private String cuentaMostrada;
    // Se piden al servidor la primera vez que se abre la lista y se renuevan con "Actualizar"
    private List<CuentaDTO> cuentasServidor;
    private boolean enPeticion;

    private Button btnUsuario;
    private TextView lblAyuda;
    private TextView lblCuenta;
    private EditText txtCuenta;
    private EditText txtDestino;
    private EditText txtImporte;
    private Button btnListaCuenta;
    private Button btnListaDestino;
    private View pnlDestino;
    private View pnlImporte;
    private TextView lblError;
    private Button btnEjecutar;
    private TextView lblResultadoTitulo;
    private TextView lblResultado;
    private TextView lblCuentaTitulo;
    private Button btnActualizar;
    private TextView lblCuentaDatos;
    private View pnlSinMovimientos;
    private LinearLayout listaMovimientos;
    private ProgressBar indicadorCarga;

    @Override
    protected void onCreate(Bundle estadoGuardado) {
        super.onCreate(estadoGuardado);
        loginControlador = ((EurekaBankApp) getApplication()).getLoginControlador();
        // Si Android cerró la aplicación en segundo plano, el token se perdió: se vuelve a iniciar sesión
        if (!loginControlador.haySesionActiva()) {
            LoginActivity.abrir(this, "Inicie sesión para continuar.");
            return;
        }
        cuentaControlador = loginControlador.crearControladorCuenta();
        setContentView(R.layout.activity_operaciones);

        btnUsuario = findViewById(R.id.btnUsuario);
        lblAyuda = findViewById(R.id.lblAyuda);
        lblCuenta = findViewById(R.id.lblCuenta);
        txtCuenta = findViewById(R.id.txtCuenta);
        txtDestino = findViewById(R.id.txtDestino);
        btnListaCuenta = findViewById(R.id.btnListaCuenta);
        btnListaDestino = findViewById(R.id.btnListaDestino);
        txtImporte = findViewById(R.id.txtImporte);
        pnlDestino = findViewById(R.id.pnlDestino);
        pnlImporte = findViewById(R.id.pnlImporte);
        lblError = findViewById(R.id.lblError);
        btnEjecutar = findViewById(R.id.btnEjecutar);
        lblResultadoTitulo = findViewById(R.id.lblResultadoTitulo);
        lblResultado = findViewById(R.id.lblResultado);
        lblCuentaTitulo = findViewById(R.id.lblCuentaTitulo);
        btnActualizar = findViewById(R.id.btnActualizar);
        lblCuentaDatos = findViewById(R.id.lblCuentaDatos);
        pnlSinMovimientos = findViewById(R.id.pnlSinMovimientos);
        listaMovimientos = findViewById(R.id.listaMovimientos);
        indicadorCarga = findViewById(R.id.indicadorCarga);

        AjusteBordes.aplicar(this, findViewById(android.R.id.content), findViewById(R.id.encabezado),
                findViewById(R.id.desplazamiento));

        // En el celular solo cabe el primer nombre; el nombre completo aparece en el menú
        String empleado = loginControlador.getEmpleado() == null ? "Empleado" : loginControlador.getEmpleado();
        btnUsuario.setText(empleado.split(" ")[0] + "  ▾");
        btnUsuario.setOnClickListener(v -> mostrarMenuUsuario());

        pestanias.put(findViewById(R.id.btnMovimientos), Operacion.MOVIMIENTOS);
        pestanias.put(findViewById(R.id.btnDeposito), Operacion.DEPOSITO);
        pestanias.put(findViewById(R.id.btnRetiro), Operacion.RETIRO);
        pestanias.put(findViewById(R.id.btnTransferencia), Operacion.TRANSFERENCIA);
        for (Map.Entry<Button, Operacion> pestania : pestanias.entrySet()) {
            pestania.getKey().setOnClickListener(v -> {
                if (!enPeticion) {
                    seleccionarOperacion(pestania.getValue());
                }
            });
        }

        // Validación mientras se escribe: solo dígitos en las cuentas y hasta dos decimales en el importe
        txtCuenta.setFilters(new FiltroTexto[] {FiltroTexto.cuenta()});
        txtDestino.setFilters(new FiltroTexto[] {FiltroTexto.cuenta()});
        txtImporte.setFilters(new FiltroTexto[] {FiltroTexto.importe()});
        ResaltadoCampo.aplicar(txtCuenta, txtDestino, txtImporte);

        txtCuenta.setOnEditorActionListener((vista, accion, evento) -> {
            if (accion == EditorInfo.IME_ACTION_DONE) {
                ejecutarOperacion();
                return true;
            }
            return false;
        });
        txtImporte.setOnEditorActionListener((vista, accion, evento) -> {
            if (accion == EditorInfo.IME_ACTION_DONE) {
                ejecutarOperacion();
                return true;
            }
            return false;
        });
        btnEjecutar.setOnClickListener(v -> ejecutarOperacion());
        btnListaCuenta.setOnClickListener(v -> mostrarListaCuentas(txtCuenta, btnListaCuenta));
        btnListaDestino.setOnClickListener(v -> mostrarListaCuentas(txtDestino, btnListaDestino));
        btnActualizar.setOnClickListener(v -> {
            if (!enPeticion && cuentaMostrada != null) {
                cuentasServidor = null;
                cargarCuenta(cuentaMostrada, false);
            }
        });

        // Atrás no vuelve al inicio de sesión sin avisar: se pide confirmar el cierre de sesión
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!enPeticion) {
                    confirmarCierreSesion();
                }
            }
        });

        seleccionarOperacion(Operacion.MOVIMIENTOS);
    }

    // Al volver de "Cambiar contraseña" se recarga la cuenta: puede haber movimientos desde otros clientes
    @Override
    protected void onRestart() {
        super.onRestart();
        if (cuentaMostrada != null && !enPeticion) {
            cargarCuenta(cuentaMostrada, false);
        }
    }

    @Override
    protected void onDestroy() {
        tarea.cancelar();
        super.onDestroy();
    }

    // Cada operación muestra solo los campos que necesita
    private void seleccionarOperacion(Operacion elegida) {
        operacion = elegida;
        pintarPestanias();
        mostrarError(null);
        quitarErrores();

        boolean esTransferencia = elegida == Operacion.TRANSFERENCIA;
        boolean llevaImporte = elegida != Operacion.MOVIMIENTOS;
        lblCuenta.setText(esTransferencia ? R.string.cuenta_origen : R.string.numero_cuenta);
        pnlDestino.setVisibility(esTransferencia ? View.VISIBLE : View.GONE);
        pnlImporte.setVisibility(llevaImporte ? View.VISIBLE : View.GONE);
        txtCuenta.setImeOptions(llevaImporte ? EditorInfo.IME_ACTION_NEXT : EditorInfo.IME_ACTION_DONE);

        switch (elegida) {
            case DEPOSITO -> mostrarAyuda("Depositar", "Registre un depósito en una cuenta activa.");
            case RETIRO -> mostrarAyuda("Retirar", "Registre un retiro; la cuenta debe tener saldo suficiente.");
            case TRANSFERENCIA -> mostrarAyuda("Transferir", "Transfiera entre dos cuentas activas distintas.");
            default -> mostrarAyuda("Consultar", "Consulte los datos, el saldo y los movimientos de una cuenta.");
        }
        lblResultadoTitulo.setText(llevaImporte ? "Comprobante" : "Saldo disponible");
        lblResultado.setText(R.string.sin_resultado);
    }

    private void mostrarAyuda(String textoBoton, String ayuda) {
        btnEjecutar.setText(textoBoton);
        lblAyuda.setText(ayuda);
    }

    // La pestaña activa se pinta en azul; las demás en tono suave
    private void pintarPestanias() {
        for (Map.Entry<Button, Operacion> pestania : pestanias.entrySet()) {
            boolean activa = pestania.getValue() == operacion;
            pestania.getKey().setBackgroundResource(activa ? R.drawable.pestania_activa : R.drawable.pestania);
            pestania.getKey().setTextColor(activa ? 0xFFFFFFFF : getColor(R.color.primario));
        }
    }

    private void ejecutarOperacion() {
        if (enPeticion) {
            return;
        }
        mostrarError(null);
        quitarErrores();

        String cuenta = leer(() -> ValidacionUtil.validarCuenta(txtCuenta.getText().toString()), txtCuenta);
        if (cuenta == null) {
            return;
        }
        if (operacion == Operacion.MOVIMIENTOS) {
            cargarCuenta(cuenta, true);
            return;
        }

        String destino = "";
        if (operacion == Operacion.TRANSFERENCIA) {
            destino = leer(() -> ValidacionUtil.validarCuenta(txtDestino.getText().toString()), txtDestino);
            if (destino == null) {
                return;
            }
            if (destino.equals(cuenta)) {
                mostrarErrorDeCampo(txtDestino, ValidacionUtil.MENSAJE_CUENTAS_IGUALES);
                return;
            }
        }
        BigDecimal importe = leer(() -> ValidacionUtil.interpretarImporte(txtImporte.getText().toString()), txtImporte);
        if (importe == null) {
            return;
        }

        String monto = FormatoUtil.formatearMonto(importe);
        String cuentaDestino = destino;
        switch (operacion) {
            case DEPOSITO -> Dialogo.confirmar(this, "¿Registrar el depósito?",
                    "Se va a depositar " + monto + " en la cuenta " + cuenta + ".", "Sí, depositar",
                    () -> registrar(cuenta, () -> cuentaControlador.depositar(cuenta, importe)));
            case RETIRO -> Dialogo.confirmar(this, "¿Registrar el retiro?",
                    "Se va a retirar " + monto + " de la cuenta " + cuenta + ".", "Sí, retirar",
                    () -> registrar(cuenta, () -> cuentaControlador.retirar(cuenta, importe)));
            default -> Dialogo.confirmar(this, "¿Registrar la transferencia?",
                    "Se van a transferir " + monto + " de la cuenta " + cuenta + " a la cuenta " + cuentaDestino
                    + ". Ambas cuentas se actualizan en una sola operación.", "Sí, transferir",
                    () -> registrar(cuenta, () -> cuentaControlador.transferir(cuenta, cuentaDestino, importe)));
        }
    }

    private void registrar(String cuenta, Supplier<RespuestaTransaccion> transaccion) {
        ejecutar(transaccion, respuesta -> {
            String saldo = FormatoUtil.formatearMonto(respuesta.saldo());
            lblResultadoTitulo.setText("Movimiento Nro " + respuesta.numeroMovimiento()
                    + " · nuevo saldo de la cuenta " + cuenta + ":");
            lblResultado.setText(saldo);
            txtImporte.setText("");
            Dialogo.informar(this, Dialogo.Tipo.EXITO, "¡Operación registrada!",
                    respuesta.mensaje() + ". Nuevo saldo: " + saldo + ".", null);
            cargarCuenta(cuenta, false);
        });
    }

    // Los movimientos vienen del servidor: incluyen lo registrado desde la consola, el escritorio y la web
    private void cargarCuenta(String numero, boolean mostrarSaldo) {
        ejecutar(() -> cuentaControlador.consultar(numero), respuesta -> mostrarCuenta(respuesta, mostrarSaldo));
    }

    private void mostrarCuenta(RespuestaMovimientos respuesta, boolean mostrarSaldo) {
        CuentaDTO cuenta = respuesta.cuenta();
        String saldo = FormatoUtil.formatearMonto(cuenta.saldo(), cuenta.simboloMoneda());

        lblCuentaTitulo.setText("Cuenta " + cuenta.numero());
        lblCuentaDatos.setText(cuenta.cliente() + " · cédula " + cuenta.cedula() + "\n"
                + cuenta.sucursal() + " · " + cuenta.moneda() + " · estado " + cuenta.estado() + "\n"
                + "Saldo " + saldo + " · " + respuesta.mensaje());
        lblCuentaDatos.setTextColor(getColor("ACTIVO".equals(cuenta.estado()) ? R.color.texto_tenue : R.color.peligro));

        listaMovimientos.removeAllViews();
        // El más reciente arriba
        List<MovimientoDTO> movimientos = new ArrayList<>(respuesta.movimientos());
        movimientos.sort(Comparator.comparingInt(MovimientoDTO::numero).reversed());
        LayoutInflater inflador = getLayoutInflater();
        for (MovimientoDTO movimiento : movimientos) {
            listaMovimientos.addView(crearFilaMovimiento(inflador, movimiento, cuenta.simboloMoneda()));
        }
        pnlSinMovimientos.setVisibility(movimientos.isEmpty() ? View.VISIBLE : View.GONE);

        cuentaMostrada = cuenta.numero();
        btnActualizar.setVisibility(View.VISIBLE);

        if (mostrarSaldo) {
            lblResultadoTitulo.setText("Saldo disponible de la cuenta " + cuenta.numero() + " (" + cuenta.estado() + ")");
            lblResultado.setText(saldo);
        }
    }

    private View crearFilaMovimiento(LayoutInflater inflador, MovimientoDTO movimiento, String simbolo) {
        View fila = inflador.inflate(R.layout.fila_movimiento, listaMovimientos, false);
        ((TextView) fila.findViewById(R.id.lblNumeroFecha)).setText("N° " + movimiento.numero() + " · "
                + FormatoUtil.formatearFechaHora(movimiento.fechaHora()));
        ((TextView) fila.findViewById(R.id.lblTipo)).setText(movimiento.tipo());
        if (movimiento.cuentaReferencia() != null && !movimiento.cuentaReferencia().isEmpty()) {
            TextView lblReferencia = fila.findViewById(R.id.lblReferencia);
            lblReferencia.setText((movimiento.esIngreso() ? "desde" : "hacia") + " la cuenta "
                    + movimiento.cuentaReferencia());
            lblReferencia.setVisibility(View.VISIBLE);
        }
        TextView lblImporte = fila.findViewById(R.id.lblImporte);
        lblImporte.setText(FormatoUtil.formatearMovimiento(movimiento.importe(), movimiento.esIngreso(), simbolo));
        lblImporte.setTextColor(getColor(movimiento.esIngreso() ? R.color.exito : R.color.peligro));
        fila.setContentDescription(movimiento.tipo() + ", " + movimiento.accion() + ", " + lblImporte.getText());
        return fila;
    }

    // Las opciones de la sesión se agrupan en un menú para no ocupar espacio en la pantalla
    // Lista de cuentas bajo el campo; las que no están activas se muestran en rojo con su estado
    // (se pueden consultar, pero el servidor rechaza depósitos, retiros y transferencias en ellas)
    private void mostrarListaCuentas(EditText campo, View ancla) {
        if (enPeticion) {
            return;
        }
        if (cuentasServidor == null) {
            ejecutar(cuentaControlador::listarCuentas, respuesta -> {
                cuentasServidor = respuesta.cuentas();
                desplegarCuentas(campo, ancla);
            });
            return;
        }
        desplegarCuentas(campo, ancla);
    }

    private void desplegarCuentas(EditText campo, View ancla) {
        if (cuentasServidor.isEmpty()) {
            mostrarError("No hay cuentas registradas en el servidor.");
            return;
        }
        PopupMenu menu = new PopupMenu(this, ancla);
        for (int i = 0; i < cuentasServidor.size(); i++) {
            CuentaDTO cuenta = cuentasServidor.get(i);
            SpannableString texto = new SpannableString(FormatoUtil.formatearOpcionCuenta(cuenta));
            if (!cuenta.estaActiva()) {
                texto.setSpan(new ForegroundColorSpan(getColor(R.color.peligro)), 0, texto.length(), 0);
            }
            menu.getMenu().add(Menu.NONE, i, i, texto);
        }
        menu.setOnMenuItemClickListener(opcion -> {
            campo.setText(cuentasServidor.get(opcion.getItemId()).numero());
            campo.setSelection(campo.length());
            campo.requestFocus();
            return true;
        });
        menu.show();
    }

    private void mostrarMenuUsuario() {
        PopupMenu menu = new PopupMenu(this, btnUsuario);
        menu.getMenu().add(Menu.NONE, Menu.NONE, 0, loginControlador.getEmpleado()).setEnabled(false);
        menu.getMenu().add(Menu.NONE, OPCION_CAMBIAR_CONTRASENIA, 1, R.string.cambiar_contrasenia);
        menu.getMenu().add(Menu.NONE, OPCION_CERRAR_SESION, 2, "Cerrar sesión");
        menu.setOnMenuItemClickListener(opcion -> {
            if (opcion.getItemId() == OPCION_CAMBIAR_CONTRASENIA) {
                startActivity(new Intent(this, CambiarContraseniaActivity.class));
            } else if (opcion.getItemId() == OPCION_CERRAR_SESION) {
                confirmarCierreSesion();
            }
            return true;
        });
        menu.show();
    }

    private void confirmarCierreSesion() {
        Dialogo.confirmar(this, "¿Cerrar sesión?",
                "Tendrá que volver a ingresar su usuario y contraseña para seguir operando.",
                "Sí, cerrar sesión", this::cerrarSesion);
    }

    private void cerrarSesion() {
        cambiarEstadoOcupado(true);
        tarea.ejecutar(loginControlador::cerrarSesion,
                mensaje -> LoginActivity.abrir(this, "Sesión cerrada correctamente."),
                ex -> LoginActivity.abrir(this, ex instanceof ExcepcionClienteWS error && error.esErrorConexion()
                        ? "Sesión cerrada en este equipo; no se pudo avisar al servidor."
                        // La sesión ya había vencido en el servidor: no hay nada más que cerrar
                        : "Sesión cerrada correctamente."));
    }

    private <T> void ejecutar(Supplier<T> trabajo, Consumer<T> alTerminar) {
        cambiarEstadoOcupado(true);
        tarea.ejecutar(trabajo, resultado -> {
            cambiarEstadoOcupado(false);
            alTerminar.accept(resultado);
        }, ex -> {
            cambiarEstadoOcupado(false);
            manejarError(ex);
        });
    }

    // Valida un campo: si no es válido lo marca en rojo, muestra el mensaje y devuelve null
    private <T> T leer(Supplier<T> validacion, EditText campo) {
        try {
            return validacion.get();
        } catch (IllegalArgumentException ex) {
            mostrarErrorDeCampo(campo, ex.getMessage());
            return null;
        }
    }

    private void mostrarErrorDeCampo(EditText campo, String mensaje) {
        ResaltadoCampo.marcarError(campo);
        campo.requestFocus();
        mostrarError(mensaje + ".");
    }

    private void quitarErrores() {
        ResaltadoCampo.quitarError(txtCuenta, txtDestino, txtImporte);
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisibility(mensaje == null || mensaje.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // 401: token inválido o sesión vencida (CU-10) → inicio de sesión.
    // Sin conexión: se ofrece cerrar sesión para cambiar la dirección del servidor.
    private void manejarError(RuntimeException ex) {
        if (ex instanceof ExcepcionClienteWS error) {
            if (error.esErrorAutenticacion()) {
                Dialogo.informar(this, Dialogo.Tipo.ERROR, "Sesión no válida",
                        error.getMessage() + "\n\nDebe iniciar sesión nuevamente.",
                        () -> LoginActivity.abrir(this, "Inicie sesión para continuar."));
                return;
            }
            if (error.esErrorConexion()) {
                new Dialogo(this, Dialogo.Tipo.ERROR, "Sin conexión con el servidor",
                        error.getMessage() + "\n\n¿Desea cerrar sesión para cambiar la dirección del servidor?")
                        .botones("Cambiar servidor", "Cancelar")
                        .alAceptar(this::cerrarSesion)
                        .mostrar();
                return;
            }
            if (error.getCodigoEstado() == 404) {
                ResaltadoCampo.marcarError(txtCuenta);
            }
            mostrarError(error.getMessage());
        } else if (ex instanceof IllegalArgumentException) {
            mostrarError(ex.getMessage() + ".");
        } else {
            mostrarError("Ocurrió un error inesperado: " + ex.getMessage());
        }
    }

    private void cambiarEstadoOcupado(boolean ocupado) {
        enPeticion = ocupado;
        btnEjecutar.setEnabled(!ocupado);
        btnUsuario.setEnabled(!ocupado);
        btnActualizar.setEnabled(!ocupado);
        btnListaCuenta.setEnabled(!ocupado);
        btnListaDestino.setEnabled(!ocupado);
        indicadorCarga.setVisibility(ocupado ? View.VISIBLE : View.GONE);
    }
}
