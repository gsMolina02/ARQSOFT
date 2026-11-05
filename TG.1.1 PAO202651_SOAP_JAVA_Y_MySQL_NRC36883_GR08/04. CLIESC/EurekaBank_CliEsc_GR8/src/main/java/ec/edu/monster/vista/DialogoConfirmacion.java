package ec.edu.monster.vista;

import ec.edu.monster.vista.controles.BotonRedondeado;
import ec.edu.monster.vista.controles.Estilo;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Ventana de confirmación con el estilo de la aplicación (reemplaza a JOptionPane):
 * ícono en un círculo, título, explicación y botones redondeados.
 */
public class DialogoConfirmacion extends JDialog {

    private static final int ANCHO = 472;
    private static final int ANCHO_MENSAJE = 328;

    private boolean aceptado;

    // Diálogo con dos botones (confirmar y cancelar) o con uno solo si textoCancelar es null
    private DialogoConfirmacion(Window duenio, String titulo, String mensaje, String textoAceptar,
            String textoCancelar, String icono) {
        super(duenio, Dialog.ModalityType.APPLICATION_MODAL);
        setUndecorated(true);

        JPanel contenido = Componentes.panel(java.awt.Color.WHITE);
        // Contorno fino gris para separar la ventana del fondo
        contenido.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        setContentPane(contenido);

        JComponent circulo = Componentes.icono(icono);
        Componentes.ubicar(contenido, circulo, 32, 32, 60, 60);
        Componentes.ubicar(contenido, Componentes.etiqueta(titulo, Estilo.fuente(15f, Font.BOLD), Estilo.TEXTO),
                110, 30, 330, 36);

        JLabel lblMensaje = Componentes.parrafo(mensaje, Estilo.fuente(10.5f), Estilo.TEXTO_ETIQUETA);
        int altoMensaje = Math.max(62, Componentes.altoNecesario(lblMensaje, ANCHO_MENSAJE));
        Componentes.ubicar(contenido, lblMensaje, 112, 70, ANCHO_MENSAJE, altoMensaje);

        int yBotones = Math.max(150, 70 + altoMensaje + 18);
        BotonRedondeado btnAceptar = Componentes.ubicar(contenido, Componentes.botonPrincipal(textoAceptar, 10.5f),
                288, yBotones, 152, 46);
        btnAceptar.addActionListener(e -> cerrar(true));
        getRootPane().setDefaultButton(btnAceptar);

        if (textoCancelar != null) {
            BotonRedondeado btnCancelar = Componentes.ubicar(contenido,
                    Componentes.botonSecundario(textoCancelar, 10.5f, Estilo.TEXTO_ETIQUETA), 132, yBotones, 144, 46);
            btnCancelar.addActionListener(e -> cerrar(false));
        }

        // Escape equivale a cancelar (o a "Entendido" si solo hay un botón)
        getRootPane().registerKeyboardAction(e -> cerrar(textoCancelar == null),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setSize(ANCHO, yBotones + 46 + 26);
        setLocationRelativeTo(duenio);
    }

    /**
     * Pregunta al usuario y devuelve true si confirmó.
     */
    public static boolean confirmar(Component duenio, String titulo, String mensaje, String textoAceptar) {
        DialogoConfirmacion dialogo = new DialogoConfirmacion(ventana(duenio), titulo, mensaje, textoAceptar,
                "Cancelar", "?");
        dialogo.setVisible(true);
        return dialogo.aceptado;
    }

    /**
     * Muestra un aviso con un solo botón "Entendido".
     */
    public static void informar(Component duenio, String titulo, String mensaje, String icono) {
        new DialogoConfirmacion(ventana(duenio), titulo, mensaje, "Entendido", null, icono).setVisible(true);
    }

    public static void informar(Component duenio, String titulo, String mensaje) {
        informar(duenio, titulo, mensaje, "✓");
    }

    private void cerrar(boolean aceptar) {
        aceptado = aceptar;
        dispose();
    }

    private static Window ventana(Component duenio) {
        if (duenio == null) {
            return null;
        }
        return duenio instanceof Window propia ? propia : SwingUtilities.getWindowAncestor(duenio);
    }
}
