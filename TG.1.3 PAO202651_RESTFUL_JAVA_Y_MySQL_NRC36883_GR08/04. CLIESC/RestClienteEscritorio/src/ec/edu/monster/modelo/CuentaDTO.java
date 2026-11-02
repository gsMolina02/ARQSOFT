package ec.edu.monster.modelo;

import java.io.Serializable;

/**
 * DTO que representa una cuenta bancaria para el cliente de escritorio.
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
public class CuentaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String cuenta;
    private String cliente;
    private double saldo;
    private String moneda;
    private String estado;
    private int contMov;

    public CuentaDTO() {
    }

    public CuentaDTO(String cuenta, String cliente, double saldo, String moneda, String estado, int contMov) {
        this.cuenta = cuenta;
        this.cliente = cliente;
        this.saldo = saldo;
        this.moneda = moneda;
        this.estado = estado;
        this.contMov = contMov;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getContMov() {
        return contMov;
    }

    public void setContMov(int contMov) {
        this.contMov = contMov;
    }

    @Override
    public String toString() {
        return cuenta + " - " + cliente + " (" + moneda + " - Saldo: $" + String.format("%.2f", saldo) + ")";
    }
}
