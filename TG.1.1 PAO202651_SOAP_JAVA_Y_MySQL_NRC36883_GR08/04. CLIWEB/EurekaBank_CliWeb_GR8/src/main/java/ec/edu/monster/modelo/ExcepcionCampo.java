package ec.edu.monster.modelo;

/**
 * Dato del formulario con formato incorrecto: indica qué campo resaltar ("cuenta", "destino" o "importe").
 */
public class ExcepcionCampo extends IllegalArgumentException {

    private final String campo;

    public ExcepcionCampo(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
