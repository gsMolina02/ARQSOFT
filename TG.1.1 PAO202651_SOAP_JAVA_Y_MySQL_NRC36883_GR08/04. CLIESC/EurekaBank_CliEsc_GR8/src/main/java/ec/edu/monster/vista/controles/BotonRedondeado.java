package ec.edu.monster.vista.controles;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.ButtonModel;
import javax.swing.JButton;

/**
 * Botón con esquinas redondeadas y colores para los estados normal, mouse encima y presionado.
 */
public class BotonRedondeado extends JButton {

    private int radioBorde = 10;
    private Color colorHover = Estilo.AZUL_HOVER;
    private Color colorPresionado = Estilo.AZUL_PRESIONADO;
    private Color colorBorde;

    public BotonRedondeado(String texto) {
        super(texto);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBackground(Estilo.AZUL);
        setForeground(Color.WHITE);
        setFont(Estilo.fuente(10.5f));
    }

    public void setRadioBorde(int radioBorde) {
        this.radioBorde = Math.max(0, radioBorde);
        repaint();
    }

    public void setColorHover(Color colorHover) {
        this.colorHover = colorHover;
        repaint();
    }

    public void setColorPresionado(Color colorPresionado) {
        this.colorPresionado = colorPresionado;
        repaint();
    }

    /**
     * Color del contorno; null para no dibujar contorno.
     */
    public void setColorBorde(Color colorBorde) {
        this.colorBorde = colorBorde;
        repaint();
    }

    /**
     * Colores de una vez: fondo, texto, mouse encima y presionado.
     */
    public void setColores(Color fondo, Color texto, Color hover, Color presionado) {
        setBackground(fondo);
        setForeground(texto);
        colorHover = hover;
        colorPresionado = presionado;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        ButtonModel modelo = getModel();
        Color fondo = !isEnabled() ? Estilo.DESHABILITADO
                : modelo.isPressed() ? colorPresionado
                : modelo.isRollover() ? colorHover
                : getBackground();

        float diametro = Math.min(radioBorde * 2f, Math.min(getWidth(), getHeight()));
        RoundRectangle2D forma = new RoundRectangle2D.Float(0, 0, getWidth() - 1f, getHeight() - 1f, diametro, diametro);
        g2.setColor(fondo);
        g2.fill(forma);
        if (colorBorde != null) {
            g2.setColor(isEnabled() ? colorBorde : Estilo.DESHABILITADO);
            g2.draw(forma);
        }

        g2.setFont(getFont());
        g2.setColor(isEnabled() ? getForeground() : Color.WHITE);
        FontMetrics medidas = g2.getFontMetrics();
        String texto = getText() == null ? "" : getText();
        int x = (getWidth() - medidas.stringWidth(texto)) / 2;
        int y = (getHeight() - medidas.getHeight()) / 2 + medidas.getAscent();
        g2.drawString(texto, x, y);

        if (isFocusOwner()) {
            g2.setColor(new Color(255, 255, 255, 120));
            g2.draw(new RoundRectangle2D.Float(3, 3, getWidth() - 7f, getHeight() - 7f, diametro, diametro));
        }
        g2.dispose();
    }
}
