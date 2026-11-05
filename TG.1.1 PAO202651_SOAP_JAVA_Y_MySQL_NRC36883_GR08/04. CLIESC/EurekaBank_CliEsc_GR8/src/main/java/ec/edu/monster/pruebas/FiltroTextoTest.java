package ec.edu.monster.pruebas;

import ec.edu.monster.vista.controles.FiltroTexto;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * El filtro de los campos numéricos se prueba sobre un documento de texto, sin abrir ventanas.
 */
public class FiltroTextoTest {

    private static PlainDocument documento(FiltroTexto filtro) {
        PlainDocument documento = new PlainDocument();
        documento.setDocumentFilter(filtro);
        return documento;
    }

    private static String texto(PlainDocument documento) throws BadLocationException {
        return documento.getText(0, documento.getLength());
    }

    @Test
    void deberiaAceptarSoloDigitosEnLaCuentaHastaOcho() throws Exception {
        PlainDocument cuenta = documento(FiltroTexto.cuenta());

        cuenta.insertString(0, "0010", null);
        cuenta.insertString(4, "a", null);
        cuenta.insertString(4, "00019", null);
        cuenta.insertString(4, "0001", null);

        assertEquals("00100001", texto(cuenta));
    }

    @Test
    void deberiaAceptarUnSoloSeparadorYDosDecimalesEnElImporte() throws Exception {
        PlainDocument importe = documento(FiltroTexto.importe());

        importe.insertString(0, "25", null);
        importe.insertString(2, ",", null);
        importe.insertString(3, ".", null);
        importe.insertString(3, "5", null);
        importe.insertString(4, "0", null);
        importe.insertString(5, "1", null);
        importe.insertString(0, "-", null);

        assertEquals("25,50", texto(importe));
    }

    @Test
    void deberiaRechazarTextoPegadoQueNoEsUnNumero() throws Exception {
        PlainDocument importe = documento(FiltroTexto.importe());

        importe.insertString(0, "1e5", null);

        assertEquals("", texto(importe));
    }

    @Test
    void deberiaPermitirBorrar() throws Exception {
        PlainDocument cuenta = documento(FiltroTexto.cuenta());
        cuenta.insertString(0, "00100001", null);

        cuenta.remove(4, 4);

        assertEquals("0010", texto(cuenta));
    }
}
