package ec.edu.monster.vista.controles;

import ec.edu.monster.util.ValidacionUtil;
import java.awt.Toolkit;
import java.util.function.Predicate;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Valida mientras se escribe: un cambio solo se acepta si el texto resultante sigue siendo válido a medio
 * escribir (por ejemplo, solo dígitos en la cuenta). Lo rechazado no se escribe y suena un aviso.
 */
public class FiltroTexto extends DocumentFilter {

    private final Predicate<String> esValido;

    public FiltroTexto(Predicate<String> esValido) {
        this.esValido = esValido;
    }

    /** Número de cuenta: hasta 8 dígitos. */
    public static FiltroTexto cuenta() {
        return new FiltroTexto(ValidacionUtil::esCuentaParcial);
    }

    /** Importe: dígitos y un solo punto o coma decimal, con hasta dos decimales. */
    public static FiltroTexto importe() {
        return new FiltroTexto(ValidacionUtil::esImporteParcial);
    }

    @Override
    public void insertString(FilterBypass paso, int posicion, String texto, AttributeSet atributos)
            throws BadLocationException {
        replace(paso, posicion, 0, texto, atributos);
    }

    @Override
    public void remove(FilterBypass paso, int posicion, int longitud) throws BadLocationException {
        replace(paso, posicion, longitud, "", null);
    }

    @Override
    public void replace(FilterBypass paso, int posicion, int longitud, String texto, AttributeSet atributos)
            throws BadLocationException {
        String actual = paso.getDocument().getText(0, paso.getDocument().getLength());
        String nuevo = actual.substring(0, posicion) + (texto == null ? "" : texto)
                + actual.substring(posicion + longitud);
        if (esValido.test(nuevo)) {
            super.replace(paso, posicion, longitud, texto, atributos);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
