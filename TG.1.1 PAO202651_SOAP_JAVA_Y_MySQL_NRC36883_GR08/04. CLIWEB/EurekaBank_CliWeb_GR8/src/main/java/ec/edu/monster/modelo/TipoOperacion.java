package ec.edu.monster.modelo;

import java.util.Locale;

/**
 * Operaciones de la pantalla principal (pestañas), con los textos de su formulario y de su confirmación.
 */
public enum TipoOperacion {

    MOVIMIENTOS("Movimientos", "Consultar", "Consulte los datos, el saldo y los movimientos de una cuenta.", null, null),
    DEPOSITO("Depósito", "Depositar", "Registre un depósito en una cuenta activa.",
            "Se va a depositar ${Importe} en la cuenta {Cuenta}.", "¿Registrar el depósito?"),
    RETIRO("Retiro", "Retirar", "Registre un retiro; la cuenta debe tener saldo suficiente.",
            "Se va a retirar ${Importe} de la cuenta {Cuenta}.", "¿Registrar el retiro?"),
    TRANSFERENCIA("Transferencia", "Transferir", "Transfiera entre dos cuentas activas distintas.",
            "Se van a transferir ${Importe} de la cuenta {Cuenta} a la cuenta {CuentaDestino}. "
            + "Ambas cuentas se actualizan en una sola operación.", "¿Registrar la transferencia?");

    private final String nombreVisible;
    private final String boton;
    private final String ayuda;
    private final String plantillaConfirmacion;
    private final String tituloConfirmacion;

    TipoOperacion(String nombreVisible, String boton, String ayuda, String plantillaConfirmacion,
            String tituloConfirmacion) {
        this.nombreVisible = nombreVisible;
        this.boton = boton;
        this.ayuda = ayuda;
        this.plantillaConfirmacion = plantillaConfirmacion;
        this.tituloConfirmacion = tituloConfirmacion;
    }

    /**
     * Operación escrita en la dirección (?operacion=deposito); cualquier otro valor es la consulta de movimientos.
     */
    public static TipoOperacion desde(String texto) {
        if (texto != null) {
            for (TipoOperacion operacion : values()) {
                if (operacion.getParametro().equalsIgnoreCase(texto.trim())) {
                    return operacion;
                }
            }
        }
        return MOVIMIENTOS;
    }

    /** Valor del parámetro "operacion" en la dirección de la página. */
    public String getParametro() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    public String getBoton() {
        return boton;
    }

    public String getAyuda() {
        return ayuda;
    }

    /**
     * Mensaje del diálogo de confirmación; site.js reemplaza {Cuenta}, {CuentaDestino} e {Importe} por lo escrito.
     */
    public String getPlantillaConfirmacion() {
        return plantillaConfirmacion;
    }

    public String getTituloConfirmacion() {
        return tituloConfirmacion;
    }

    public String getBotonConfirmacion() {
        return "Sí, " + boton.toLowerCase(Locale.ROOT);
    }

    public boolean isLlevaImporte() {
        return this != MOVIMIENTOS;
    }

    public boolean isTransferencia() {
        return this == TRANSFERENCIA;
    }
}
