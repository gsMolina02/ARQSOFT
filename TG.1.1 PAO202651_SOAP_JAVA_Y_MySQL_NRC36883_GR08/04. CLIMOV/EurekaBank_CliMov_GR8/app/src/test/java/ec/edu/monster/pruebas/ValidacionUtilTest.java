package ec.edu.monster.pruebas;

import ec.edu.monster.util.ValidacionUtil;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ValidacionUtilTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"123", "abcdefgh", "001000011", "0010 001"})
    void deberiaRechazarCuentasSinOchoDigitos(String cuenta) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> ValidacionUtil.validarCuenta(cuenta));

        assertEquals(ValidacionUtil.MENSAJE_CUENTA, error.getMessage());
    }

    @Test
    void deberiaAceptarUnaCuentaConEspaciosAlrededor() {
        assertEquals("00100001", ValidacionUtil.validarCuenta(" 00100001 "));
    }

    @ParameterizedTest
    @CsvSource({"100, 100", "25.5, 25.5", "'25,50', 25.50", "9999999999.99, 9999999999.99"})
    void deberiaInterpretarImportesValidos(String texto, String esperado) {
        assertEquals(new BigDecimal(esperado), ValidacionUtil.interpretarImporte(texto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.00", "-5", "10.555", "abc", "1,000.50", "1e5", "", "  "})
    void deberiaRechazarImportesNoValidos(String texto) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> ValidacionUtil.interpretarImporte(texto));

        assertEquals(ValidacionUtil.MENSAJE_IMPORTE, error.getMessage());
    }

    @Test
    void deberiaRechazarImportesMayoresAlMaximo() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> ValidacionUtil.interpretarImporte("10000000000"));

        assertEquals(ValidacionUtil.MENSAJE_IMPORTE_MAXIMO, error.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {"'', true", "0010, true", "00100001, true", "001000011, false", "0a, false"})
    void deberiaReconocerUnaCuentaAMedioEscribir(String texto, boolean esperado) {
        assertEquals(esperado, ValidacionUtil.esCuentaParcial(texto));
    }

    @ParameterizedTest
    @CsvSource(value = {"'', true", "25, true", "25., true", "'25,5', true", "25.50, true", "25.555, false",
        "-1, false", "2.5.5, false"})
    void deberiaReconocerUnImporteAMedioEscribir(String texto, boolean esperado) {
        assertEquals(esperado, ValidacionUtil.esImporteParcial(texto));
    }

    @Test
    void deberiaValidarElCambioDeContrasenia() {
        assertThrows(IllegalArgumentException.class, () -> ValidacionUtil.validarCambioClave("", "Nueva123", "Nueva123"));
        assertThrows(IllegalArgumentException.class, () -> ValidacionUtil.validarCambioClave("Monster9", "corta", "corta"));
        assertThrows(IllegalArgumentException.class,
                () -> ValidacionUtil.validarCambioClave("Monster9", "Monster9", "Monster9"));
        assertDoesNotThrow(() -> ValidacionUtil.validarCambioClave("Monster9", "Nueva123", "Nueva123"));
    }
}
