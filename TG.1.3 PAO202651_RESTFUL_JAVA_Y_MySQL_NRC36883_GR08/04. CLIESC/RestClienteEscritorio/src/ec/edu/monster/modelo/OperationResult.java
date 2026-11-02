package ec.edu.monster.modelo;

import java.io.Serializable;

/**
 * Resultado detallado de una operación bancaria para el cliente de escritorio.
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
public class OperationResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private String tipoOperacion;
    private String cuenta;
    private String cuentaDestino;
    private double importe;
    private double nuevoSaldo;
    private int nromov;
    private String titular;

    public OperationResult() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getNuevoSaldo() {
        return nuevoSaldo;
    }

    public void setNuevoSaldo(double nuevoSaldo) {
        this.nuevoSaldo = nuevoSaldo;
    }

    public int getNromov() {
        return nromov;
    }

    public void setNromov(int nromov) {
        this.nromov = nromov;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }
}
