package ec.edu.monster.modelos;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Cuenta de ahorro con los datos de su cliente, moneda y sucursal.
 */
public class Cuenta {

    public static final String ESTADO_ACTIVO = "ACTIVO";

    private String numero;
    private BigDecimal saldo;
    private LocalDate fechaApertura;
    private String estado;
    private int contadorMovimientos;

    private String codigoCliente;
    private String cedulaCliente;
    private String nombreCliente;
    private String codigoMoneda;
    private String nombreMoneda;
    private String simboloMoneda;
    private String codigoSucursal;
    private String nombreSucursal;

    public boolean estaActiva() {
        return ESTADO_ACTIVO.equals(estado);
    }

    public boolean tieneSaldo(BigDecimal importe) {
        return saldo.compareTo(importe) >= 0;
    }

    /**
     * Suma el importe y avanza el contador: el nuevo valor es el número del movimiento.
     */
    public void acreditar(BigDecimal importe) {
        saldo = saldo.add(importe);
        contadorMovimientos++;
    }

    /**
     * Resta el importe y avanza el contador: el nuevo valor es el número del movimiento.
     */
    public void debitar(BigDecimal importe) {
        saldo = saldo.subtract(importe);
        contadorMovimientos++;
    }

    /**
     * Copia independiente: los cambios sobre ella no afectan a la original.
     */
    public Cuenta copiar() {
        Cuenta copia = new Cuenta();
        copia.numero = numero;
        copia.saldo = saldo;
        copia.fechaApertura = fechaApertura;
        copia.estado = estado;
        copia.contadorMovimientos = contadorMovimientos;
        copia.codigoCliente = codigoCliente;
        copia.cedulaCliente = cedulaCliente;
        copia.nombreCliente = nombreCliente;
        copia.codigoMoneda = codigoMoneda;
        copia.nombreMoneda = nombreMoneda;
        copia.simboloMoneda = simboloMoneda;
        copia.codigoSucursal = codigoSucursal;
        copia.nombreSucursal = nombreSucursal;
        return copia;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDate fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getContadorMovimientos() {
        return contadorMovimientos;
    }

    public void setContadorMovimientos(int contadorMovimientos) {
        this.contadorMovimientos = contadorMovimientos;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getCedulaCliente() {
        return cedulaCliente;
    }

    public void setCedulaCliente(String cedulaCliente) {
        this.cedulaCliente = cedulaCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getCodigoMoneda() {
        return codigoMoneda;
    }

    public void setCodigoMoneda(String codigoMoneda) {
        this.codigoMoneda = codigoMoneda;
    }

    public String getNombreMoneda() {
        return nombreMoneda;
    }

    public void setNombreMoneda(String nombreMoneda) {
        this.nombreMoneda = nombreMoneda;
    }

    public String getSimboloMoneda() {
        return simboloMoneda;
    }

    public void setSimboloMoneda(String simboloMoneda) {
        this.simboloMoneda = simboloMoneda;
    }

    public String getCodigoSucursal() {
        return codigoSucursal;
    }

    public void setCodigoSucursal(String codigoSucursal) {
        this.codigoSucursal = codigoSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }
}
