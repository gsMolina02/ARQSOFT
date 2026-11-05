package ec.edu.monster.modelos.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Respuesta común de todas las operaciones: los clientes siempre reciben el código
 * (200, 201, 400, 401, 404, 409 o 500) y un mensaje descriptivo, sea éxito o error.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuesta", propOrder = {"codigo", "mensaje"})
public class Respuesta {

    @XmlElement(required = true)
    private int codigo;

    @XmlElement(required = true)
    private String mensaje;

    public Respuesta() {
    }

    public Respuesta(int codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
