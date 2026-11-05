package ec.edu.monster.vista;

import android.app.Activity;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import ec.edu.monster.R;
import ec.edu.monster.controlador.ServidorControlador;
import ec.edu.monster.modelo.ResultadoConexion;
import java.util.function.Consumer;

/**
 * "Cambiar servidor": permite usar la aplicación desde un celular real escribiendo la IP de la PC donde corre
 * Payara (se completan el puerto 8080 y la ruta del servicio). Se puede probar la conexión, y al guardar se
 * prueba de nuevo: si falla, se pregunta si se guarda de todas formas.
 */
public final class DialogoServidor {

    private final Activity actividad;
    private final ServidorControlador controlador;
    private final Tarea tarea;
    private final Consumer<ResultadoConexion> alGuardar;
    private final Dialogo dialogo;
    private final EditText txtDireccion;
    private final Button btnProbar;

    /**
     * @param alGuardar recibe el resultado de la prueba hecha antes de guardar la dirección
     */
    public DialogoServidor(Activity actividad, ServidorControlador controlador, Tarea tarea,
            Consumer<ResultadoConexion> alGuardar) {
        this.actividad = actividad;
        this.controlador = controlador;
        this.tarea = tarea;
        this.alGuardar = alGuardar;

        dialogo = new Dialogo(actividad, Dialogo.Tipo.SERVIDOR, "Servidor SOAP",
                "Escriba la IP de la PC donde corre el servidor (por ejemplo 192.168.1.25) o la dirección completa. "
                + "En el emulador de Android, 10.0.2.2 es la PC donde corre el emulador.")
                .botones("Guardar", "Cancelar")
                .sinCerrarAlAceptar()
                .alAceptar(this::guardar);
        txtDireccion = dialogo.mostrarCampo(controlador.getUrlActual(), "Ej.: 192.168.1.25");
        txtDireccion.setOnEditorActionListener((vista, accion, evento) -> {
            if (accion == EditorInfo.IME_ACTION_DONE) {
                guardar();
                return true;
            }
            return false;
        });
        btnProbar = dialogo.mostrarBotonExtra("Probar conexión", v -> probar());
    }

    public void mostrar() {
        dialogo.mostrar();
    }

    private void probar() {
        String url = completar();
        if (url == null) {
            return;
        }
        dialogo.mostrarEstado("Probando la conexión con " + url + " ...", R.drawable.caja_suave, R.color.texto_secundario);
        cambiarEstadoOcupado(true, "Probando...");
        tarea.ejecutar(() -> controlador.probar(url), resultado -> {
            cambiarEstadoOcupado(false, null);
            mostrarResultado(resultado);
        }, this::mostrarFallo);
    }

    // Se prueba antes de guardar: así se sabe en el momento si falla la red, el firewall o la dirección
    private void guardar() {
        String url = completar();
        if (url == null) {
            return;
        }
        dialogo.mostrarEstado("Probando la conexión con " + url + " ...", R.drawable.caja_suave, R.color.texto_secundario);
        cambiarEstadoOcupado(true, null);
        tarea.ejecutar(() -> controlador.probar(url), resultado -> {
            cambiarEstadoOcupado(false, null);
            mostrarResultado(resultado);
            if (resultado.exitosa()) {
                guardarDireccion(url, resultado);
                return;
            }
            new Dialogo(actividad, Dialogo.Tipo.ERROR, "Sin conexión",
                    "No se pudo conectar con esa dirección. ¿Desea guardarla de todas formas?")
                    .botones("Guardar", "Cancelar")
                    .alAceptar(() -> guardarDireccion(url, resultado))
                    .mostrar();
        }, this::mostrarFallo);
    }

    private void guardarDireccion(String url, ResultadoConexion resultado) {
        try {
            controlador.guardar(url);
        } catch (IllegalArgumentException ex) {
            dialogo.mostrarEstado(ex.getMessage(), R.drawable.caja_error, R.color.peligro);
            return;
        }
        dialogo.cerrar();
        alGuardar.accept(resultado);
    }

    // Muestra la dirección completa en el campo (192.168.1.25 → http://192.168.1.25:8080/...) o el error de formato
    private String completar() {
        try {
            String url = controlador.completarUrl(txtDireccion.getText().toString());
            txtDireccion.setText(url);
            txtDireccion.setSelection(url.length());
            ResaltadoCampo.quitarError(txtDireccion);
            return url;
        } catch (IllegalArgumentException ex) {
            ResaltadoCampo.marcarError(txtDireccion);
            dialogo.mostrarEstado(ex.getMessage(), R.drawable.caja_error, R.color.peligro);
            return null;
        }
    }

    private void mostrarResultado(ResultadoConexion resultado) {
        if (resultado.exitosa()) {
            dialogo.mostrarEstado(resultado.mensaje(), R.drawable.caja_exito, R.color.exito);
        } else {
            dialogo.mostrarEstado(resultado.mensaje(), R.drawable.caja_error, R.color.peligro);
        }
    }

    private void mostrarFallo(RuntimeException ex) {
        cambiarEstadoOcupado(false, null);
        dialogo.mostrarEstado("No fue posible probar la conexión: " + ex.getMessage(), R.drawable.caja_error,
                R.color.peligro);
    }

    private void cambiarEstadoOcupado(boolean ocupado, String textoProbar) {
        dialogo.cambiarEstadoOcupado(ocupado);
        btnProbar.setText(ocupado && textoProbar != null ? textoProbar : "Probar conexión");
    }
}
