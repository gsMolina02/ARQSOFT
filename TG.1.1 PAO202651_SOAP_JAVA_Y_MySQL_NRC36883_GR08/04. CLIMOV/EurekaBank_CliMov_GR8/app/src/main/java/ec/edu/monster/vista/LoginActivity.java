package ec.edu.monster.vista;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.EurekaBankApp;
import ec.edu.monster.R;
import ec.edu.monster.cliente.ExcepcionClienteWS;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.modelo.LoginModelo;
import ec.edu.monster.modelo.ResultadoConexion;

/**
 * CU-01 Autenticarse. Desde aquí también se cambia la dirección del servidor SOAP.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String EXTRA_AVISO = "aviso";

    private final Tarea tarea = new Tarea();
    private EurekaBankApp aplicacion;
    private EditText txtUsuario;
    private EditText txtContrasenia;
    private Button btnVerContrasenia;
    private Button btnIngresar;
    private Button btnCambiarServidor;
    private ProgressBar indicadorCarga;
    private TextView lblAviso;
    private TextView lblError;
    private TextView lblServidor;

    /**
     * Vuelve al inicio de sesión desde cualquier pantalla, cerrando las demás (cierre de sesión o sesión vencida).
     */
    public static void abrir(Activity origen, String aviso) {
        Intent intencion = new Intent(origen, LoginActivity.class)
                .putExtra(EXTRA_AVISO, aviso)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        origen.startActivity(intencion);
        origen.finish();
    }

    @Override
    protected void onCreate(Bundle estadoGuardado) {
        super.onCreate(estadoGuardado);
        setContentView(R.layout.activity_login);
        aplicacion = (EurekaBankApp) getApplication();

        txtUsuario = findViewById(R.id.txtUsuario);
        txtContrasenia = findViewById(R.id.txtContrasenia);
        btnVerContrasenia = findViewById(R.id.btnVerContrasenia);
        btnIngresar = findViewById(R.id.btnIngresar);
        btnCambiarServidor = findViewById(R.id.btnCambiarServidor);
        indicadorCarga = findViewById(R.id.indicadorCarga);
        lblAviso = findViewById(R.id.lblAviso);
        lblError = findViewById(R.id.lblError);
        lblServidor = findViewById(R.id.lblServidor);

        View desplazamiento = findViewById(R.id.desplazamiento);
        AjusteBordes.aplicar(this, desplazamiento, null, desplazamiento);

        mostrarServidor();
        mostrarAviso(getIntent().getStringExtra(EXTRA_AVISO));

        btnVerContrasenia.setOnClickListener(v -> alternarContrasenia());
        btnIngresar.setOnClickListener(v -> iniciarSesion());
        btnCambiarServidor.setOnClickListener(v -> cambiarServidor());
        txtContrasenia.setOnEditorActionListener((vista, accion, evento) -> {
            if (accion == EditorInfo.IME_ACTION_DONE) {
                iniciarSesion();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        tarea.cancelar();
        super.onDestroy();
    }

    private void alternarContrasenia() {
        boolean oculta = txtContrasenia.getTransformationMethod() instanceof PasswordTransformationMethod;
        int cursor = txtContrasenia.getSelectionEnd();
        txtContrasenia.setTransformationMethod(oculta
                ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        txtContrasenia.setSelection(Math.max(cursor, 0));
        btnVerContrasenia.setText(oculta ? R.string.ocultar : R.string.ver);
    }

    private void iniciarSesion() {
        mostrarError(null);
        mostrarAviso(null);

        String usuario = txtUsuario.getText().toString().trim();
        String clave = txtContrasenia.getText().toString();
        if (usuario.isEmpty() || clave.trim().isEmpty()) {
            mostrarError("Ingrese el usuario y la contraseña.");
            return;
        }

        LoginControlador controlador = aplicacion.getLoginControlador();
        cambiarEstadoOcupado(true);
        tarea.ejecutar(() -> controlador.iniciarSesion(new LoginModelo(usuario, clave)), respuesta -> {
            cambiarEstadoOcupado(false);
            startActivity(new Intent(this, OperacionesActivity.class));
            finish();
        }, ex -> {
            cambiarEstadoOcupado(false);
            mostrarError(ex instanceof ExcepcionClienteWS || ex instanceof IllegalArgumentException
                    ? ex.getMessage() : "Ocurrió un error inesperado: " + ex.getMessage());
            txtContrasenia.setText("");
        });
    }

    // Permite usar la app desde un celular real: allí se escribe la IP de la PC donde corre Payara
    private void cambiarServidor() {
        new DialogoServidor(this, aplicacion.getServidorControlador(), tarea, this::alGuardarServidor).mostrar();
    }

    private void alGuardarServidor(ResultadoConexion resultado) {
        // El cliente SOAP se vuelve a crear con la nueva dirección
        aplicacion.conectar();
        mostrarServidor();
        mostrarError(null);
        mostrarAviso(resultado.exitosa()
                ? "Conexión correcta. Servidor actualizado, ya puede iniciar sesión."
                : "Servidor actualizado.");
    }

    private void mostrarServidor() {
        lblServidor.setText("Servidor SOAP: " + aplicacion.getLoginControlador().getUrlServidor());
    }

    private void mostrarAviso(String mensaje) {
        mostrarCaja(lblAviso, mensaje);
    }

    private void mostrarError(String mensaje) {
        mostrarCaja(lblError, mensaje);
    }

    private static void mostrarCaja(TextView caja, String mensaje) {
        caja.setText(mensaje);
        caja.setVisibility(mensaje == null || mensaje.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void cambiarEstadoOcupado(boolean ocupado) {
        btnIngresar.setEnabled(!ocupado);
        btnCambiarServidor.setEnabled(!ocupado);
        txtUsuario.setEnabled(!ocupado);
        txtContrasenia.setEnabled(!ocupado);
        btnIngresar.setText(ocupado ? R.string.conectando : R.string.iniciar_sesion);
        indicadorCarga.setVisibility(ocupado ? View.VISIBLE : View.GONE);
    }
}
