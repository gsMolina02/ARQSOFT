package ec.edu.monster.modelos.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * Datos de la cuenta que se muestran al consultar sus movimientos. La fecha de apertura va como
 * texto yyyy-MM-dd para que cualquier cliente (ksoap2, JSP, Swing) la lea sin conversiones.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cuenta", propOrder = {"numero", "cliente", "cedula", "moneda", "simboloMoneda", "sucursal",
    "estado", "saldo", "fechaApertura", "contadorMovimientos"})
public class CuentaDTO {

    private String numero;
    private String cliente;
    private String cedula;
    private String moneda;
    private String simboloMoneda;
    private String sucursal;
    private String estado;
    private BigDecimal saldo;
    private String fechaApertura;
    private int contadorMovimientos;

    public CuentaDTO() {
    }

    public CuentaDTO(String numero, String cliente, String cedula, String moneda, String simboloMoneda,
            String sucursal, String estado, BigDecimal saldo, String fechaApertura, int contadorMovimientos) {
        this.numero = numero;
        this.cliente = cliente;
        this.cedula = cedula;
        this.moneda = moneda;
        this.simboloMoneda = simboloMoneda;
        this.sucursal = sucursal;
        this.estado = estado;
        this.saldo = saldo;
        this.fechaApertura = fechaApertura;
        this.contadorMovimientos = contadorMovimientos;
    }

    public String getNumero() {
        return numero;
    }

    public String getCliente() {
        return cliente;
    }

    public String getCedula() {
        return cedula;
    }

    public String getMoneda() {
        return moneda;
    }

    public String getSimboloMoneda() {
        return simboloMoneda;
    }

    public String getSucursal() {
        return sucursal;
    }

    public String getEstado() {
        return estado;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public String getFechaApertura() {
        return fechaApertura;
    }

    public int getContadorMovimientos() {
        return contadorMovimientos;
    }
}
