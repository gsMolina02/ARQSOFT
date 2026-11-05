package ec.edu.monster.vista;

import android.text.InputFilter;
import android.text.Spanned;
import ec.edu.monster.util.ValidacionUtil;
import java.util.function.Predicate;

/**
 * Bloquea mientras se escribe lo que no puede ser un número de cuenta o un importe: si el texto
 * resultante deja de serlo, la tecla o el texto pegado no se aplican.
 */
public final class FiltroTexto implements InputFilter {

    private final Predicate<String> esValido;

    private FiltroTexto(Predicate<String> esValido) {
        this.esValido = esValido;
    }

    /** Hasta 8 dígitos. */
    public static FiltroTexto cuenta() {
        return new FiltroTexto(ValidacionUtil::esCuentaParcial);
    }

    /** Dígitos con punto o coma decimal y hasta dos decimales. */
    public static FiltroTexto importe() {
        return new FiltroTexto(ValidacionUtil::esImporteParcial);
    }

    @Override
    public CharSequence filter(CharSequence origen, int inicio, int fin, Spanned destino, int inicioDestino,
            int finDestino) {
        String resultado = destino.subSequence(0, inicioDestino).toString()
                + origen.subSequence(inicio, fin)
                + destino.subSequence(finDestino, destino.length());
        if (esValido.test(resultado)) {
            // null: se acepta el cambio tal cual
            return null;
        }
        // Se conserva lo que había en el tramo reemplazado
        return destino.subSequence(inicioDestino, finDestino);
    }
}
