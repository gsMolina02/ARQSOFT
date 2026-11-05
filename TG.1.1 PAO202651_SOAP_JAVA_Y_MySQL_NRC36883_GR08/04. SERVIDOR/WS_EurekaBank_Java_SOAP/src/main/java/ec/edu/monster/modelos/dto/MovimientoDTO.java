package ec.edu.monster.modelos.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * Un movimiento de la cuenta. La fecha y hora va como texto yyyy-MM-dd HH:mm:ss;
 * cuentaReferencia solo aparece en las transferencias (la otra cuenta).
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "movimiento", propOrder = {"numero", "fechaHora", "codigoTipo", "tipo", "accion", "importe",
    "cuentaReferencia", "empleado"})
public class MovimientoDTO {

    private int numero;
    private String fechaHora;
    private String codigoTipo;
    private String tipo;
    private String accion;
    private BigDecimal importe;
    private String cuentaReferencia;
    private String empleado;

    public MovimientoDTO() {
    }

    public MovimientoDTO(int numero, String fechaHora, String codigoTipo, String tipo, String accion,
            BigDecimal importe, String cuentaReferencia, String empleado) {
        this.numero = numero;
        this.fechaHora = fechaHora;
        this.codigoTipo = codigoTipo;
        this.tipo = tipo;
        this.accion = accion;
        this.importe = importe;
        this.cuentaReferencia = cuentaReferencia;
        this.empleado = empleado;
    }

    public int getNumero() {
        return numero;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public String getCodigoTipo() {
        return codigoTipo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getAccion() {
        return accion;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public String getCuentaReferencia() {
        return cuentaReferencia;
    }

    public String getEmpleado() {
        return empleado;
    }
}
