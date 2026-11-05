package ec.edu.monster.vista;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.EurekaBankApp;
import ec.edu.monster.R;
import ec.edu.monster.cliente.ExcepcionClienteWS;
import ec.edu.monster.controlador.LoginControlador;
import java.util.Map;

/**
 * CU-02 Cambiar contraseña del empleado que inició sesión.
 */
public class CambiarContraseniaActivity extends AppCompatActivity {

    private final Tarea tarea = new Tarea();
    private LoginControlador loginControlador;
    private EditText txtActual;
    private EditText txtNueva;
    private EditText txtConfirmacion;
    private TextView lblError;
    private Button btnGuardar;
    private Button btnCancelar;
    private Button btnVolver;

    @Override
    protected void onCreate(Bundle estadoGuardado) {
        super.onCreate(estadoGuardado);
        loginControlador = ((EurekaBankApp) getApplication()).getLoginControlador();
        if (!loginControlador.haySesionActiva()) {
            LoginActivity.abrir(this, "Inicie sesión para continuar.");
            return;
        }
        setContentView(R.layout.activity_cambiar_contrasenia);

        txtActual = findViewById(R.id.txtActual);
        txtNueva = findViewById(R.id.txtNueva);
        txtConfirmacion = findViewById(R.id.txtConfirmacion);
        lblError = findViewById(R.id.lblError);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnVolver = findViewById(R.id.btnVolver);

        AjusteBordes.aplicar(this, findViewById(android.R.id.content), findViewById(R.id.encabezado),
                findViewById(R.id.desplazamiento));

        // Cada campo tiene su propio botón para mostrar u ocultar lo escrito
        Map<Integer, EditText> botonesVer = Map.of(
                R.id.btnVerActual, txtActual,
                R.id.btnVerNueva, txtNueva,
                R.id.btnVerConfirmacion, txtConfirmacion);
        for (Map.Entry<Integer, EditText> entrada : botonesVer.entrySet()) {
            Button boton = findViewById(entrada.getKey());
            boton.setOnClickListener(v -> alternar(entrada.getValue(), boton));
        }
        ResaltadoCampo.aplicar(txtActual, txtNueva, txtConfirmacion);

        txtConfirmacion.setOnEditorActionListener((vista, accion, evento) -> {
            if (accion == EditorInfo.IME_ACTION_DONE) {
                guardar();
                return true;
            }
            return false;
        });
        btnGuardar.setOnClickListener(v -> guardar());
        btnCancelar.setOnClickListener(v -> finish());
        btnVolver.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        tarea.cancelar();
        super.onDestroy();
    }

    private static void alternar(EditText campo, Button boton) {
        boolean oculta = campo.getTransformationMethod() instanceof PasswordTransformationMethod;
        int cursor = campo.getSelectionEnd();
        campo.setTransformationMethod(oculta
                ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        campo.setSelection(Math.max(cursor, 0));
        boton.setText(oculta ? R.string.ocultar : R.string.ver);
    }

    private void guardar() {
        mostrarError(null);
        ResaltadoCampo.quitarError(txtActual, txtNueva, txtConfirmacion);

        String actual = txtActual.getText().toString();
        String nueva = txtNueva.getText().toString();
        String confirmacion = txtConfirmacion.getText().toString();
        if (actual.trim().isEmpty() || nueva.trim().isEmpty() || confirmacion.trim().isEmpty()) {
            mostrarError("Complete los tres campos.");
            return;
        }
        if (!nueva.equals(confirmacion)) {
            mostrarError("La confirmación no coincide con la nueva contraseña.");
            ResaltadoCampo.marcarError(txtConfirmacion);
            return;
        }

        cambiarEstadoOcupado(true);
        tarea.ejecutar(() -> loginControlador.cambiarClave(actual, nueva, confirmacion), mensaje -> {
            cambiarEstadoOcupado(false);
            Dialogo.informar(this, Dialogo.Tipo.EXITO, "Contraseña actualizada", mensaje, this::finish);
        }, ex -> {
            cambiarEstadoOcupado(false);
            if (ex instanceof ExcepcionClienteWS error && error.esErrorAutenticacion()) {
                Dialogo.informar(this, Dialogo.Tipo.ERROR, "Sesión no válida",
                        error.getMessage() + "\n\nDebe iniciar sesión nuevamente.",
                        () -> LoginActivity.abrir(this, "Inicie sesión para continuar."));
                return;
            }
            mostrarError(ex instanceof ExcepcionClienteWS || ex instanceof IllegalArgumentException
                    ? ex.getMessage() : "Ocurrió un error inesperado: " + ex.getMessage());
        });
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisibility(mensaje == null || mensaje.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void cambiarEstadoOcupado(boolean ocupado) {
        txtActual.setEnabled(!ocupado);
        txtNueva.setEnabled(!ocupado);
        txtConfirmacion.setEnabled(!ocupado);
        btnGuardar.setEnabled(!ocupado);
        btnCancelar.setEnabled(!ocupado);
        btnVolver.setEnabled(!ocupado);
        btnGuardar.setText(ocupado ? "Guardando..." : getString(R.string.guardar));
    }
}
