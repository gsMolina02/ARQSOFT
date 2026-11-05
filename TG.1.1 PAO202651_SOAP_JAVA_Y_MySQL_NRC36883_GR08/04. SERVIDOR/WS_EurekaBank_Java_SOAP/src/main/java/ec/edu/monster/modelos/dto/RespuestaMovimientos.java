package ec.edu.monster.modelos.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de consultarMovimientos (CU-04): datos de la cuenta y sus movimientos ordenados por número.
 * Los movimientos viajan dentro de &lt;movimientos&gt;, uno por cada &lt;movimiento&gt;.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaMovimientos", propOrder = {"cuenta", "movimientos"})
public class RespuestaMovimientos extends Respuesta {

    private CuentaDTO cuenta;

    @XmlElementWrapper(name = "movimientos")
    @XmlElement(name = "movimiento")
    private List<MovimientoDTO> movimientos = new ArrayList<>();

    public RespuestaMovimientos() {
    }

    public RespuestaMovimientos(int codigo, String mensaje, CuentaDTO cuenta, List<MovimientoDTO> movimientos) {
        super(codigo, mensaje);
        this.cuenta = cuenta;
        this.movimientos = movimientos;
    }

    public CuentaDTO getCuenta() {
        return cuenta;
    }

    public List<MovimientoDTO> getMovimientos() {
        return movimientos;
    }
}
