package ec.edu.monster.vista;

import ec.edu.monster.vista.controles.BotonRedondeado;
import ec.edu.monster.vista.controles.Estilo;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

/**
 * Piezas comunes de las ventanas: etiquetas, botones con los colores de la aplicación, el ícono
 * en un círculo y la ubicación de los controles con las mismas coordenadas del diseño de Windows Forms.
 */
final class Componentes {

    private Componentes() {
    }

    static <T extends Component> T ubicar(Container contenedor, T componente, int x, int y, int ancho, int alto) {
        componente.setBounds(x, y, ancho, alto);
        contenedor.add(componente);
        return componente;
    }

    static JLabel etiqueta(String texto, Font fuente, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente);
        etiqueta.setForeground(color);
        etiqueta.setVerticalAlignment(SwingConstants.TOP);
        return etiqueta;
    }

    /**
     * Etiqueta de varias líneas: el texto se ajusta al ancho del control.
     */
    static JLabel parrafo(String texto, Font fuente, Color color) {
        return etiqueta(html(texto), fuente, color);
    }

    static String html(String texto) {
        String seguro = texto == null ? "" : texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return "<html>" + seguro.replace("\n", "<br>") + "</html>";
    }

    /**
     * Alto que necesita una etiqueta HTML para mostrar todo su texto en el ancho indicado.
     */
    static int altoNecesario(JLabel etiqueta, int ancho) {
        View vista = (View) etiqueta.getClientProperty(BasicHTML.propertyKey);
        if (vista == null) {
            return etiqueta.getPreferredSize().height;
        }
        vista.setSize(ancho, 0);
        return (int) Math.ceil(vista.getPreferredSpan(View.Y_AXIS));
    }

    static BotonRedondeado botonPrincipal(String texto, float puntos) {
        BotonRedondeado boton = new BotonRedondeado(texto);
        boton.setFont(Estilo.fuente(puntos, Font.BOLD));
        boton.setRadioBorde(12);
        return boton;
    }

    static BotonRedondeado botonSecundario(String texto, float puntos, Color colorTexto) {
        BotonRedondeado boton = new BotonRedondeado(texto);
        boton.setFont(Estilo.fuente(puntos));
        boton.setColores(Estilo.GRIS_BOTON, colorTexto, Estilo.GRIS_BOTON_HOVER, Estilo.GRIS_BOTON_PRESIONADO);
        boton.setRadioBorde(12);
        return boton;
    }

    /**
     * Botón del encabezado azul (menú del empleado, volver).
     */
    static BotonRedondeado botonEncabezado(String texto) {
        BotonRedondeado boton = new BotonRedondeado(texto);
        boton.setFont(Estilo.fuente(10.5f));
        boton.setColores(Estilo.AZUL_ENCABEZADO_BOTON, Color.WHITE, Estilo.AZUL_ENCABEZADO_HOVER,
                Estilo.AZUL_ENCABEZADO_PRESIONADO);
        boton.setRadioBorde(20);
        return boton;
    }

    static JPanel panel(Color fondo) {
        JPanel panel = new JPanel(null);
        panel.setBackground(fondo);
        return panel;
    }

    /**
     * Ícono de los diálogos: un símbolo azul dentro de un círculo celeste.
     */
    static JComponent icono(String simbolo) {
        return new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(Estilo.SUAVE);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Estilo.AZUL);
                g2.setFont(new Font("Segoe UI Symbol", Font.BOLD, 29));
                int x = (getWidth() - g2.getFontMetrics().stringWidth(simbolo)) / 2;
                int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
                g2.drawString(simbolo, x, y);
                g2.dispose();
            }
        };
    }
}
