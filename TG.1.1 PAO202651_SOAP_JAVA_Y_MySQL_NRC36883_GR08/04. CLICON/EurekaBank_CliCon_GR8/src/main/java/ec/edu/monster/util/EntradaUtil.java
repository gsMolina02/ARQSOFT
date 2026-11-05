package ec.edu.monster.util;

import java.io.Console;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Lectura de datos desde el teclado. Cada método repite la pregunta hasta recibir un dato válido.
 */
public final class EntradaUtil {

    private static final Console CONSOLA = System.console();
    // Se lee con la codificación de la consola de Windows para que la ñ y las tildes lleguen bien
    private static final Scanner ENTRADA =
            new Scanner(System.in, CONSOLA != null ? CONSOLA.charset() : Charset.defaultCharset());
    private static final Pattern PATRON_ENTERO = Pattern.compile("[0-9]{1,9}");

    private static final String ROJO = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private EntradaUtil() {
    }

    public static String leerTextoNoVacio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = leerLinea().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            mostrarError("Entrada inválida. El valor no puede quedar vacío.");
        }
    }

    public static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = leerLinea().trim();
            if (PATRON_ENTERO.matcher(valor).matches()) {
                return Integer.parseInt(valor);
            }
            mostrarError("Entrada inválida. Debe ingresar un número entero.");
        }
    }

    public static String leerCuenta(String mensaje) {
        while (true) {
            try {
                return ValidacionUtil.validarCuenta(leerTextoNoVacio(mensaje));
            } catch (IllegalArgumentException ex) {
                mostrarError(ex.getMessage() + ".");
            }
        }
    }

    public static BigDecimal leerImporte(String mensaje) {
        while (true) {
            try {
                return ValidacionUtil.interpretarImporte(leerTextoNoVacio(mensaje));
            } catch (IllegalArgumentException ex) {
                mostrarError(ex.getMessage() + ", por ejemplo 100 o 25.50.");
            }
        }
    }

    /**
     * Lee la contraseña sin mostrarla. Si no hay una consola real (Output de NetBeans o entrada redirigida)
     * no se puede ocultar y se lee la línea completa.
     */
    public static String leerClave(String mensaje) {
        while (true) {
            String clave;
            if (CONSOLA != null) {
                char[] caracteres = CONSOLA.readPassword(mensaje);
                if (caracteres == null) {
                    throw new NoSuchElementException("Se cerró la entrada de la consola.");
                }
                clave = new String(caracteres);
            } else {
                System.out.print(mensaje);
                clave = leerLinea();
            }
            if (!clave.isEmpty()) {
                return clave;
            }
            mostrarError("Entrada inválida. La contraseña no puede quedar vacía.");
        }
    }

    public static boolean leerSiNo(String mensaje) {
        while (true) {
            String respuesta = leerTextoNoVacio(mensaje + " (S/N): ").toUpperCase();
            if (respuesta.equals("S")) {
                return true;
            }
            if (respuesta.equals("N")) {
                return false;
            }
            mostrarError("Responda S o N.");
        }
    }

    // nextLine lanza NoSuchElementException si se cierra la entrada (Ctrl+Z): el programa termina
    private static String leerLinea() {
        return ENTRADA.nextLine();
    }

    private static void mostrarError(String mensaje) {
        System.out.println(ROJO + "  [ERROR] " + mensaje + RESET);
    }
}
