package ec.edu.monster.vista.controles;

import java.awt.Color;
import java.awt.Font;

/**
 * Colores y fuentes de la aplicación (los mismos del cliente de escritorio de TG.1.4).
 */
public final class Estilo {

    public static final Color AZUL = new Color(0, 84, 147);
    public static final Color AZUL_HOVER = new Color(0, 99, 177);
    public static final Color AZUL_PRESIONADO = new Color(0, 62, 110);
    public static final Color AZUL_ENCABEZADO_BOTON = new Color(16, 105, 170);
    public static final Color AZUL_ENCABEZADO_HOVER = new Color(34, 124, 192);
    public static final Color AZUL_ENCABEZADO_PRESIONADO = new Color(0, 70, 122);
    public static final Color SUAVE = new Color(225, 234, 244);
    public static final Color SUAVE_HOVER = new Color(205, 222, 240);
    public static final Color SUAVE_PRESIONADO = new Color(185, 208, 232);
    public static final Color GRIS_BOTON = new Color(233, 236, 239);
    public static final Color GRIS_BOTON_HOVER = new Color(222, 226, 230);
    public static final Color GRIS_BOTON_PRESIONADO = new Color(206, 212, 218);
    public static final Color FONDO = new Color(244, 246, 249);
    public static final Color BORDE = new Color(206, 212, 218);
    public static final Color DESHABILITADO = new Color(206, 212, 218);
    public static final Color TEXTO = new Color(33, 37, 41);
    public static final Color TEXTO_ETIQUETA = new Color(73, 80, 87);
    public static final Color TEXTO_SECUNDARIO = new Color(108, 117, 125);
    public static final Color TEXTO_ENCABEZADO = new Color(214, 228, 240);
    public static final Color EXITO = new Color(25, 135, 84);
    public static final Color ERROR = new Color(192, 0, 0);

    private static final String FAMILIA = "Segoe UI";

    private Estilo() {
    }

    /**
     * Fuente con el tamaño en puntos de Windows Forms (96 ppp); Java mide en puntos de 72 ppp,
     * así que se convierte para que el texto se vea del mismo tamaño que en el cliente de .NET.
     */
    public static Font fuente(float puntos, int estilo) {
        return new Font(FAMILIA, estilo, Math.round(puntos * 96f / 72f));
    }

    public static Font fuente(float puntos) {
        return fuente(puntos, Font.PLAIN);
    }
}
