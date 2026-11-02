/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.monster.modelo;

/**
 *
 * @author diego
 */
public class Cuenta {
    private int id_cuenta;
    private int id_cliente;
    private String nro_cuenta;
    private String tipo_cuenta;
    private Double saldo_cuenta;

    public Cuenta() {
    }

    public Cuenta(int id_cuenta, int id_cliente, String nro_cuenta, String tipo_cuenta, Double saldo_cuenta) {
        this.id_cuenta = id_cuenta;
        this.id_cliente = id_cliente;
        this.nro_cuenta = nro_cuenta;
        this.tipo_cuenta = tipo_cuenta;
        this.saldo_cuenta = saldo_cuenta;
    }

    public int getId_cuenta() {
        return id_cuenta;
    }

    public void setId_cuenta(int id_cuenta) {
        this.id_cuenta = id_cuenta;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getNro_cuenta() {
        return nro_cuenta;
    }

    public void setNro_cuenta(String nro_cuenta) {
        this.nro_cuenta = nro_cuenta;
    }

    public String getTipo_cuenta() {
        return tipo_cuenta;
    }

    public void setTipo_cuenta(String tipo_cuenta) {
        this.tipo_cuenta = tipo_cuenta;
    }

    public Double getSaldo_cuenta() {
        return saldo_cuenta;
    }

    public void setSaldo_cuenta(Double saldo_cuenta) {
        this.saldo_cuenta = saldo_cuenta;
    }

    @Override
    public String toString() {
        return "Cuenta{" + "id_cuenta=" + id_cuenta + ", id_cliente=" + id_cliente + ", nro_cuenta=" + nro_cuenta + ", tipo_cuenta=" + tipo_cuenta + ", saldo_cuenta=" + saldo_cuenta + '}';
    }
    
    
}
