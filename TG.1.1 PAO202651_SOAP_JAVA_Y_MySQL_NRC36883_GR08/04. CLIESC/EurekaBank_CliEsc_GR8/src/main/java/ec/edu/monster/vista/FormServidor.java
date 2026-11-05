package ec.edu.monster.vista;

import ec.edu.monster.controlador.ServidorControlador;
import ec.edu.monster.modelo.ResultadoConexion;
import ec.edu.monster.vista.controles.BotonRedondeado;
import ec.edu.monster.vista.controles.CampoTexto;
import ec.edu.monster.vista.controles.Estilo;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * Ventana para escribir la dirección del servidor SOAP EurekaBank cuando corre en otra computadora de la red.
 * Tiene el mismo estilo que DialogoConfirmacion. Devuelve true cuando se guardó una dirección nueva.
 */
public class FormServidor extends JDialog {

    private final ServidorControlador servidorControlador;
    private final CampoTexto txtServidor = new CampoTexto();
    private final JLabel lblEstado = Componentes.parrafo("", Estilo.fuente(9.5f), Estilo.TEXTO_SECUNDARIO);
    private final BotonRedondeado btnProbar = Componentes.botonSecundario("Probar conexión", 10.5f, Estilo.AZUL);
    private final BotonRedondeado btnCancelar = Componentes.botonSecundario("Cancelar", 10.5f, Estilo.TEXTO_ETIQUETA);
    private final BotonRedondeado btnGuardar = Componentes.botonPrincipal("Guardar", 10.5f);
    private boolean guardado;

    public FormServidor(Window duenio, ServidorControlador servidorControlador) {
        super(duenio, Dialog.ModalityType.APPLICATION_MODAL);
        this.servidorControlador = servidorControlador;
        setUndecorated(true);

        JPanel contenido = Componentes.panel(Color.WHITE);
        contenido.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        setContentPane(contenido);

        Componentes.ubicar(contenido, Componentes.icono("⚙"), 32, 32, 60, 60);
        Componentes.ubicar(contenido, Componentes.etiqueta("Servidor SOAP", Estilo.fuente(15f, Font.BOLD), Estilo.TEXTO),
                110, 30, 378, 36);
        Componentes.ubicar(contenido, Componentes.parrafo("Escriba la IP de la computadora donde corre el servidor "
                + "(ej.: 192.168.1.25) o la dirección completa. Para usar esta computadora escriba localhost.",
                Estilo.fuente(10f), Estilo.TEXTO_ETIQUETA), 112, 70, 376, 66);

        txtServidor.setFont(Estilo.fuente(11f));
        txtServidor.setText(servidorControlador.getUrlActual());
        txtServidor.addActionListener(e -> guardar());
        Componentes.ubicar(contenido, txtServidor, 32, 144, 456, 46);
        Componentes.ubicar(contenido, lblEstado, 32, 196, 456, 58);

        Componentes.ubicar(contenido, btnProbar, 32, 262, 160, 46).addActionListener(e -> probar(resultado -> { }));
        Componentes.ubicar(contenido, btnCancelar, 220, 262, 124, 46).addActionListener(e -> dispose());
        Componentes.ubicar(contenido, btnGuardar, 356, 262, 132, 46).addActionListener(e -> guardar());

        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        setSize(520, 336);
        setLocationRelativeTo(duenio);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                txtServidor.requestFocusInWindow();
                txtServidor.selectAll();
            }
        });
    }

    /**
     * Muestra la ventana y devuelve true si se guardó una dirección.
     */
    public boolean mostrar() {
        setVisible(true);
        return guardado;
    }

    private void guardar() {
        probar(resultado -> {
            if (!resultado.exitosa() && !DialogoConfirmacion.confirmar(this, "Sin conexión",
                    "No se pudo conectar con esa dirección. ¿Desea guardarla de todas formas?", "Guardar")) {
                return;
            }
            try {
                servidorControlador.guardar(txtServidor.getText());
                guardado = true;
                dispose();
            } catch (IllegalArgumentException ex) {
                mostrarEstado(ex.getMessage(), Estilo.ERROR);
            }
        });
    }

    // Completa la dirección, la prueba en segundo plano y entrega el resultado (no se llama si no es válida)
    private void probar(Consumer<ResultadoConexion> alTerminar) {
        String url;
        try {
            url = servidorControlador.completarUrl(txtServidor.getText());
        } catch (IllegalArgumentException ex) {
            mostrarEstado(ex.getMessage(), Estilo.ERROR);
            txtServidor.marcarError();
            txtServidor.requestFocusInWindow();
            return;
        }

        txtServidor.setText(url);
        cambiarEstadoOcupado(true);
        mostrarEstado("Probando la conexión con " + url + " ...", Estilo.TEXTO_SECUNDARIO);
        Tarea.ejecutar(() -> servidorControlador.probar(url), resultado -> {
            cambiarEstadoOcupado(false);
            mostrarEstado(resultado.mensaje(), resultado.exitosa() ? Estilo.EXITO : Estilo.ERROR);
            alTerminar.accept(resultado);
        }, ex -> {
            cambiarEstadoOcupado(false);
            mostrarEstado(ex.getMessage(), Estilo.ERROR);
        });
    }

    private void mostrarEstado(String mensaje, Color color) {
        lblEstado.setForeground(color);
        lblEstado.setText(Componentes.html(mensaje));
    }

    private void cambiarEstadoOcupado(boolean ocupado) {
        txtServidor.setEnabled(!ocupado);
        btnProbar.setEnabled(!ocupado);
        btnGuardar.setEnabled(!ocupado);
        btnCancelar.setEnabled(!ocupado);
        btnProbar.setText(ocupado ? "Probando..." : "Probar conexión");
        setCursor(ocupado ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}
