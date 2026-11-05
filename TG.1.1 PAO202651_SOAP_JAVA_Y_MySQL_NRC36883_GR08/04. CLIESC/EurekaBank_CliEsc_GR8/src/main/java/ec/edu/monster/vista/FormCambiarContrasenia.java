package ec.edu.monster.vista;

import ec.edu.monster.cliente.ExcepcionClienteWS;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.vista.controles.BotonRedondeado;
import ec.edu.monster.vista.controles.CampoTexto;
import ec.edu.monster.vista.controles.Estilo;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Pantalla de cambio de contraseña (CU-02). Tiene el mismo tamaño que la pantalla de operaciones y se abre en su
 * misma posición, de modo que se ve como otra pantalla de la aplicación y no como una ventana flotante.
 */
public class FormCambiarContrasenia extends JDialog {

    private final LoginControlador loginControlador;
    private final String textoEstado;
    private final CampoTexto txtActual = campoClave();
    private final CampoTexto txtNueva = campoClave();
    private final CampoTexto txtConfirmacion = campoClave();
    private final JLabel lblError = Componentes.parrafo("", Estilo.fuente(10f), Estilo.ERROR);
    private final BotonRedondeado btnCancelar = Componentes.botonSecundario("Cancelar", 11f, Estilo.TEXTO_ETIQUETA);
    private final BotonRedondeado btnGuardar = Componentes.botonPrincipal("Guardar cambios", 11f);
    private final BotonRedondeado btnVolver = Componentes.botonEncabezado("←  Volver a las operaciones");
    private final JLabel lblEstado = Componentes.etiqueta("", Estilo.fuente(9f), Color.GRAY);

    // Si el servidor responde 401, la ventana principal debe volver al inicio de sesión
    private boolean sesionExpirada;

    public FormCambiarContrasenia(Window duenio, LoginControlador loginControlador) {
        super(duenio, "EurekaBank SOAP GR08 - Cambiar contraseña", Dialog.ModalityType.APPLICATION_MODAL);
        this.loginControlador = loginControlador;
        this.textoEstado = "Conectado a " + loginControlador.getUrlServidor();
        setResizable(false);

        JPanel contenido = Componentes.panel(Estilo.FONDO);
        contenido.setPreferredSize(new Dimension(1060, 640));
        setContentPane(contenido);

        JPanel encabezado = Componentes.ubicar(contenido, Componentes.panel(Estilo.AZUL), 0, 0, 1060, 92);
        Componentes.ubicar(encabezado, Componentes.etiqueta("Cambiar contraseña", Estilo.fuente(19f, Font.BOLD), Color.WHITE),
                30, 10, 500, 45);
        Componentes.ubicar(encabezado, Componentes.etiqueta("EurekaBank SOAP GR08 · Seguridad de la cuenta",
                Estilo.fuente(10f), Estilo.TEXTO_ENCABEZADO), 34, 56, 500, 23);
        btnVolver.addActionListener(e -> dispose());
        Componentes.ubicar(encabezado, btnVolver, 808, 26, 220, 40);

        JPanel tarjeta = Componentes.ubicar(contenido, Componentes.panel(Color.WHITE), 250, 112, 560, 482);
        Componentes.ubicar(tarjeta, Componentes.etiqueta("Actualice su contraseña", Estilo.fuente(14f, Font.BOLD),
                Estilo.TEXTO), 38, 24, 400, 32);
        Componentes.ubicar(tarjeta, Componentes.etiqueta("Mínimo 6 caracteres y diferente a la contraseña actual.",
                Estilo.fuente(10f), Estilo.TEXTO_SECUNDARIO), 40, 58, 480, 24);

        Componentes.ubicar(tarjeta, etiquetaCampo("Contraseña actual"), 40, 98, 300, 25);
        Componentes.ubicar(tarjeta, txtActual, 40, 126, 480, 48);
        Componentes.ubicar(tarjeta, etiquetaCampo("Nueva contraseña"), 40, 188, 300, 25);
        Componentes.ubicar(tarjeta, txtNueva, 40, 216, 480, 48);
        Componentes.ubicar(tarjeta, etiquetaCampo("Confirmar nueva contraseña"), 40, 278, 300, 25);
        Componentes.ubicar(tarjeta, txtConfirmacion, 40, 306, 480, 48);
        Componentes.ubicar(tarjeta, lblError, 40, 360, 480, 42);

        btnCancelar.addActionListener(e -> dispose());
        Componentes.ubicar(tarjeta, btnCancelar, 40, 406, 232, 52);
        btnGuardar.addActionListener(e -> guardar());
        Componentes.ubicar(tarjeta, btnGuardar, 288, 406, 232, 52);

        txtActual.addActionListener(e -> txtNueva.requestFocusInWindow());
        txtNueva.addActionListener(e -> txtConfirmacion.requestFocusInWindow());
        txtConfirmacion.addActionListener(e -> guardar());

        JPanel barra = Componentes.ubicar(contenido, Componentes.panel(Color.WHITE), 0, 614, 1060, 26);
        lblEstado.setText(textoEstado);
        lblEstado.setVerticalAlignment(SwingConstants.CENTER);
        Componentes.ubicar(barra, lblEstado, 12, 0, 1030, 26);

        pack();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                txtActual.requestFocusInWindow();
            }
        });
    }

    public boolean isSesionExpirada() {
        return sesionExpirada;
    }

    private void guardar() {
        if (!btnGuardar.isEnabled()) {
            return;
        }
        lblError.setText("");

        if (txtActual.getText().isBlank() || txtNueva.getText().isBlank() || txtConfirmacion.getText().isBlank()) {
            lblError.setText("Complete los tres campos.");
            primerCampoVacio().requestFocusInWindow();
            return;
        }
        if (!txtNueva.getText().equals(txtConfirmacion.getText())) {
            lblError.setText("La confirmación no coincide con la nueva contraseña.");
            txtConfirmacion.marcarError();
            txtConfirmacion.selectAll();
            txtConfirmacion.requestFocusInWindow();
            return;
        }

        String actual = txtActual.getText();
        String nueva = txtNueva.getText();
        String confirmacion = txtConfirmacion.getText();
        cambiarEstadoOcupado(true);
        Tarea.ejecutar(() -> loginControlador.cambiarClave(actual, nueva, confirmacion), mensaje -> {
            cambiarEstadoOcupado(false);
            DialogoConfirmacion.informar(this, "¡Contraseña actualizada!",
                    mensaje + ". Use la nueva contraseña la próxima vez que inicie sesión.");
            dispose();
        }, ex -> {
            cambiarEstadoOcupado(false);
            if (ex instanceof ExcepcionClienteWS servicio && servicio.esErrorAutenticacion()) {
                DialogoConfirmacion.informar(this, "Sesión no válida", ex.getMessage() + " Debe iniciar sesión nuevamente.", "!");
                sesionExpirada = true;
                dispose();
                return;
            }
            lblError.setText(Componentes.html(ex.getMessage()));
        });
    }

    private CampoTexto primerCampoVacio() {
        if (txtActual.getText().isBlank()) {
            return txtActual;
        }
        return txtNueva.getText().isBlank() ? txtNueva : txtConfirmacion;
    }

    private void cambiarEstadoOcupado(boolean ocupado) {
        txtActual.setEnabled(!ocupado);
        txtNueva.setEnabled(!ocupado);
        txtConfirmacion.setEnabled(!ocupado);
        btnGuardar.setEnabled(!ocupado);
        btnCancelar.setEnabled(!ocupado);
        btnVolver.setEnabled(!ocupado);
        btnGuardar.setText(ocupado ? "Guardando..." : "Guardar cambios");
        lblEstado.setText(ocupado ? "Guardando la nueva contraseña..." : textoEstado);
        setCursor(ocupado ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private static CampoTexto campoClave() {
        CampoTexto campo = new CampoTexto(true);
        campo.setFont(Estilo.fuente(12f));
        return campo;
    }

    private static JLabel etiquetaCampo(String texto) {
        return Componentes.etiqueta(texto, Estilo.fuente(10.5f), Estilo.TEXTO_ETIQUETA);
    }
}
