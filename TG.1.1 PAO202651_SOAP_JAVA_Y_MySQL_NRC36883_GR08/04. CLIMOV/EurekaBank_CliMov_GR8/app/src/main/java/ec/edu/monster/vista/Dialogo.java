package ec.edu.monster.vista;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ec.edu.monster.R;

/**
 * Diálogo propio de la aplicación (equivale a DialogoConfirmacion del cliente de escritorio): ícono en un
 * círculo, título, mensaje y botones redondeados. Opcionalmente muestra un campo de texto, un mensaje de
 * estado y un tercer botón, que usa "Cambiar servidor".
 */
public final class Dialogo {

    private static final int ANCHO_MAXIMO_DP = 420;

    /** El ícono y su color indican el tipo de mensaje. */
    public enum Tipo {
        INFORMACION("i", "#005493", "#E1EAF4"),
        PREGUNTA("?", "#005493", "#E1EAF4"),
        EXITO("✓", "#198754", "#E6F4EA"),
        ERROR("!", "#C00000", "#FDECEA"),
        SERVIDOR("IP", "#005493", "#E1EAF4");

        private final String icono;
        private final int color;
        private final int fondo;

        Tipo(String icono, String color, String fondo) {
            this.icono = icono;
            this.color = Color.parseColor(color);
            this.fondo = Color.parseColor(fondo);
        }
    }

    private final Activity actividad;
    private final Dialog ventana;
    private final TextView lblEstado;
    private final EditText txtCampo;
    private final Button btnExtra;
    private final Button btnCancelar;
    private final Button btnAceptar;
    private Runnable alAceptar = () -> { };
    private Runnable alCancelar = () -> { };
    private boolean cerrarAlAceptar = true;

    public Dialogo(Activity actividad, Tipo tipo, String titulo, String mensaje) {
        this.actividad = actividad;
        ventana = new Dialog(actividad);
        ventana.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ventana.setContentView(R.layout.dialogo);
        ventana.setCanceledOnTouchOutside(false);

        TextView lblIcono = ventana.findViewById(R.id.lblIcono);
        lblIcono.setText(tipo.icono);
        lblIcono.setTextColor(tipo.color);
        lblIcono.setBackgroundTintList(ColorStateList.valueOf(tipo.fondo));
        if (tipo == Tipo.SERVIDOR) {
            lblIcono.setTextSize(20);
        }
        ((TextView) ventana.findViewById(R.id.lblTitulo)).setText(titulo);
        ((TextView) ventana.findViewById(R.id.lblMensaje)).setText(mensaje);

        lblEstado = ventana.findViewById(R.id.lblEstado);
        txtCampo = ventana.findViewById(R.id.txtCampo);
        btnExtra = ventana.findViewById(R.id.btnExtra);
        btnCancelar = ventana.findViewById(R.id.btnCancelar);
        btnAceptar = ventana.findViewById(R.id.btnAceptar);
        btnAceptar.setText("Aceptar");

        btnAceptar.setOnClickListener(v -> {
            if (cerrarAlAceptar) {
                ventana.dismiss();
            }
            alAceptar.run();
        });
        btnCancelar.setOnClickListener(v -> ventana.cancel());
        // Atrás equivale a Cancelar
        ventana.setOnCancelListener(d -> alCancelar.run());
    }

    /**
     * Aviso con un solo botón; alCerrar se ejecuta también si se cierra con Atrás.
     */
    public static void informar(Activity actividad, Tipo tipo, String titulo, String mensaje, Runnable alCerrar) {
        new Dialogo(actividad, tipo, titulo, mensaje)
                .botones("Aceptar", null)
                .alAceptar(alCerrar)
                .alCancelar(alCerrar)
                .mostrar();
    }

    public static void confirmar(Activity actividad, String titulo, String mensaje, String textoAceptar,
            Runnable alConfirmar) {
        new Dialogo(actividad, Tipo.PREGUNTA, titulo, mensaje)
                .botones(textoAceptar, "Cancelar")
                .alAceptar(alConfirmar)
                .mostrar();
    }

    /**
     * @param cancelar null para mostrar solo el botón de aceptar
     */
    public Dialogo botones(String aceptar, String cancelar) {
        btnAceptar.setText(aceptar);
        if (cancelar == null) {
            btnCancelar.setVisibility(View.GONE);
        } else {
            btnCancelar.setText(cancelar);
            btnCancelar.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public Dialogo alAceptar(Runnable accion) {
        alAceptar = accion == null ? () -> { } : accion;
        return this;
    }

    public Dialogo alCancelar(Runnable accion) {
        alCancelar = accion == null ? () -> { } : accion;
        return this;
    }

    /** El botón de aceptar no cierra el diálogo: lo cierra quien lo usa (por ejemplo, tras probar la conexión). */
    public Dialogo sinCerrarAlAceptar() {
        cerrarAlAceptar = false;
        return this;
    }

    public EditText mostrarCampo(String texto, String ejemplo) {
        txtCampo.setText(texto);
        txtCampo.setHint(ejemplo);
        txtCampo.setSelection(txtCampo.length());
        txtCampo.setVisibility(View.VISIBLE);
        return txtCampo;
    }

    public Button mostrarBotonExtra(String texto, View.OnClickListener accion) {
        btnExtra.setText(texto);
        btnExtra.setOnClickListener(accion);
        btnExtra.setVisibility(View.VISIBLE);
        return btnExtra;
    }

    public void mostrarEstado(String mensaje, int fondo, int color) {
        lblEstado.setText(mensaje);
        lblEstado.setBackgroundResource(fondo);
        lblEstado.setTextColor(actividad.getColor(color));
        lblEstado.setVisibility(View.VISIBLE);
    }

    public void ocultarEstado() {
        lblEstado.setVisibility(View.GONE);
    }

    /** Mientras se espera al servidor no se puede cerrar ni volver a pulsar. */
    public void cambiarEstadoOcupado(boolean ocupado) {
        ventana.setCancelable(!ocupado);
        txtCampo.setEnabled(!ocupado);
        btnExtra.setEnabled(!ocupado);
        btnCancelar.setEnabled(!ocupado);
        btnAceptar.setEnabled(!ocupado);
    }

    public void mostrar() {
        if (actividad.isFinishing() || actividad.isDestroyed()) {
            return;
        }
        ventana.show();
        Window ventanaSistema = ventana.getWindow();
        if (ventanaSistema != null) {
            DisplayMetrics medidas = actividad.getResources().getDisplayMetrics();
            int ancho = Math.min((int) (medidas.widthPixels * 0.9f), (int) (ANCHO_MAXIMO_DP * medidas.density));
            ventanaSistema.setLayout(ancho, WindowManager.LayoutParams.WRAP_CONTENT);
            ventanaSistema.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void cerrar() {
        ventana.dismiss();
    }
}
