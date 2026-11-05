package ec.edu.monster.vista.controles;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/**
 * Muestra una imagen cubriendo todo el control (como "object-fit: cover"):
 * la agranda hasta llenar el ancho y el alto sin deformarla y recorta lo que sobra.
 * El punto de enfoque indica qué parte de la imagen se conserva al recortar.
 */
public class ImagenRecortada extends JComponent {

    private final Image imagen;
    private final float focoHorizontal;
    private final float focoVertical;

    public ImagenRecortada(Image imagen, float focoHorizontal, float focoVertical) {
        this.imagen = imagen;
        this.focoHorizontal = Math.max(0f, Math.min(1f, focoHorizontal));
        this.focoVertical = Math.max(0f, Math.min(1f, focoVertical));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        int anchoImagen = imagen == null ? -1 : imagen.getWidth(this);
        int altoImagen = imagen == null ? -1 : imagen.getHeight(this);
        if (anchoImagen <= 0 || altoImagen <= 0 || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        // Escala mínima que cubre el ancho y el alto; el sobrante se recorta según el foco
        float escala = Math.max((float) getWidth() / anchoImagen, (float) getHeight() / altoImagen);
        int anchoVisible = Math.round(getWidth() / escala);
        int altoVisible = Math.round(getHeight() / escala);
        int x = Math.round((anchoImagen - anchoVisible) * focoHorizontal);
        int y = Math.round((altoImagen - altoVisible) * focoVertical);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(imagen, 0, 0, getWidth(), getHeight(), x, y, x + anchoVisible, y + altoVisible, this);
        g2.dispose();
    }
}
