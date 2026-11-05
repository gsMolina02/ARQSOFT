package ec.edu.monster.modelos.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de listarCuentas: todas las cuentas (con su estado) para que los clientes las muestren
 * en la lista desplegable del número de cuenta. Viajan dentro de &lt;cuentas&gt;, una por cada &lt;cuenta&gt;.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaCuentas", propOrder = {"cuentas"})
public class RespuestaCuentas extends Respuesta {

    @XmlElementWrapper(name = "cuentas")
    @XmlElement(name = "cuenta")
    private List<CuentaDTO> cuentas = new ArrayList<>();

    public RespuestaCuentas() {
    }

    public RespuestaCuentas(int codigo, String mensaje, List<CuentaDTO> cuentas) {
        super(codigo, mensaje);
        this.cuentas = cuentas;
    }

    public List<CuentaDTO> getCuentas() {
        return cuentas;
    }
}
