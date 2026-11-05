package ec.edu.monster.modelo;

import java.util.List;

/**
 * Textos y formato de la consola: banner, títulos, tablas y mensajes de éxito, aviso y error con color.
 */
public class MensajesModelo {

    // Códigos ANSI de color (la consola de NetBeans y la de Windows 10+ los interpretan)
    private static final String RESET = "\u001B[0m";
    private static final String NEGRITA = "\u001B[1m";
    private static final String CIAN = "\u001B[36m";
    private static final String VERDE = "\u001B[32m";
    private static final String ROJO = "\u001B[31m";
    private static final String AMARILLO = "\u001B[33m";
    private static final String GRIS = "\u001B[90m";

    private static final int ANCHO = 66;

    // Arte ASCII ("EurekaBank"); las barras invertidas van escapadas
    private static final String[] LOGO = {
        " _____           _         ____              _    ",
        "| ____|   _ _ __| | ____ _| __ )  __ _ _ __ | | __",
        "|  _|| | | | '__| |/ / _` |  _ \\ / _` | '_ \\| |/ /",
        "| |__| |_| | |  |   < (_| | |_) | (_| | | | |   < ",
        "|_____\\__,_|_|  |_|\\_\\__,_|____/ \\__,_|_| |_|_|\\_\\"
    };

    public void mostrarBanner() {
        System.out.println(CIAN + borde('=') + RESET);
        for (String linea : LOGO) {
            System.out.println(CIAN + NEGRITA + lineaCentrada(linea) + RESET);
        }
        System.out.println(CIAN + borde('=') + RESET);
        System.out.println(CIAN + lineaCentrada("SISTEMA DE CUENTAS DE AHORRO  -  GR08") + RESET);
        System.out.println(CIAN + lineaCentrada("Cliente Consola  -  Servicio SOAP Java") + RESET);
        System.out.println(CIAN + lineaCentrada("Movimientos | Depósitos | Retiros | Transferencias") + RESET);
        System.out.println(CIAN + borde('=') + RESET);
    }

    public void mostrarTitulo(String texto) {
        System.out.println();
        System.out.println(CIAN + borde('-') + RESET);
        System.out.println(CIAN + NEGRITA + lineaCentrada(texto) + RESET);
        System.out.println(CIAN + borde('-') + RESET);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarDetalle(String mensaje) {
        System.out.println(GRIS + mensaje + RESET);
    }

    public void mostrarAviso(String mensaje) {
        System.out.println(AMARILLO + mensaje + RESET);
    }

    public void mostrarExito(String mensaje) {
        System.out.println(VERDE + NEGRITA + "  [OK] " + mensaje + RESET);
    }

    // Se usa System.out (no System.err) para que los errores salgan en orden con el resto del texto
    public void mostrarError(String mensaje) {
        System.out.println(ROJO + "  [ERROR] " + mensaje + RESET);
    }

    /**
     * Par etiqueta: valor alineado, por ejemplo "  Saldo             : $540.00".
     */
    public void mostrarDato(String etiqueta, String valor) {
        System.out.println(String.format("  %-18s: %s", etiqueta, valor));
    }

    /**
     * Tabla con columnas del ancho de su dato más largo. Las columnas indicadas en alinearDerecha
     * (por ejemplo, los importes) se alinean a la derecha.
     */
    public void mostrarTabla(String[] encabezados, List<String[]> filas, int... alinearDerecha) {
        int[] anchos = new int[encabezados.length];
        for (int columna = 0; columna < encabezados.length; columna++) {
            anchos[columna] = encabezados[columna].length();
            for (String[] fila : filas) {
                anchos[columna] = Math.max(anchos[columna], fila[columna].length());
            }
        }

        StringBuilder separador = new StringBuilder("  +");
        for (int ancho : anchos) {
            separador.append("-".repeat(ancho + 2)).append('+');
        }
        System.out.println(CIAN + separador + RESET);
        System.out.println(CIAN + fila(encabezados, anchos, alinearDerecha) + RESET);
        System.out.println(CIAN + separador + RESET);
        for (String[] fila : filas) {
            System.out.println(fila(fila, anchos, alinearDerecha));
        }
        System.out.println(CIAN + separador + RESET);
    }

    private static String fila(String[] celdas, int[] anchos, int[] alinearDerecha) {
        StringBuilder texto = new StringBuilder("  |");
        for (int columna = 0; columna < celdas.length; columna++) {
            String formato = contiene(alinearDerecha, columna) ? "%" + anchos[columna] + "s" : "%-" + anchos[columna] + "s";
            texto.append(' ').append(String.format(formato, celdas[columna])).append(" |");
        }
        return texto.toString();
    }

    private static boolean contiene(int[] columnas, int columna) {
        for (int valor : columnas) {
            if (valor == columna) {
                return true;
            }
        }
        return false;
    }

    private static String borde(char relleno) {
        return "+" + String.valueOf(relleno).repeat(ANCHO - 2) + "+";
    }

    private static String lineaCentrada(String texto) {
        int espacio = ANCHO - 4 - texto.length();
        int izquierda = Math.max(espacio / 2, 0);
        int derecha = Math.max(espacio - izquierda, 0);
        return "| " + " ".repeat(izquierda) + texto + " ".repeat(derecha) + " |";
    }
}
