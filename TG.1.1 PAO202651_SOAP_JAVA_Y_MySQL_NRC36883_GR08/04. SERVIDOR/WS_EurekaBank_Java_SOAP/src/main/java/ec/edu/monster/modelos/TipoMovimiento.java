package ec.edu.monster.modelos;

/**
 * Códigos del catálogo tipo_movimiento que genera el sistema.
 */
public final class TipoMovimiento {

    public static final String APERTURA = "001";
    public static final String DEPOSITO = "003";
    public static final String RETIRO = "004";
    /** Transferencia recibida (INGRESO en la cuenta de destino). */
    public static final String TRANSFERENCIA_RECIBIDA = "008";
    /** Transferencia enviada (SALIDA en la cuenta de origen). */
    public static final String TRANSFERENCIA_ENVIADA = "009";

    private TipoMovimiento() {
    }
}
