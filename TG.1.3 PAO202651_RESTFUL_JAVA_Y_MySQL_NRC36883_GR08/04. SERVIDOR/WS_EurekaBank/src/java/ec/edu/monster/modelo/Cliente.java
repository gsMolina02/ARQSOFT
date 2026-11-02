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
public class Cliente {
    private int id_cliente;
    private String nombres_cliente;
    private String apellidos_cliente;
    private String cedula_cliente;
    private String direccion_cliente;
    private String telefono_cliente;
    private String correo_cliente;

    public Cliente() {
    }

    public Cliente(int id_cliente, String nombres_cliente, String apellidos_cliente, String cedula_cliente, String direccion_cliente, String telefono_cliente, String correo_cliente) {
        this.id_cliente = id_cliente;
        this.nombres_cliente = nombres_cliente;
        this.apellidos_cliente = apellidos_cliente;
        this.cedula_cliente = cedula_cliente;
        this.direccion_cliente = direccion_cliente;
        this.telefono_cliente = telefono_cliente;
        this.correo_cliente = correo_cliente;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getNombres_cliente() {
        return nombres_cliente;
    }

    public void setNombres_cliente(String nombres_cliente) {
        this.nombres_cliente = nombres_cliente;
    }

    public String getApellidos_cliente() {
        return apellidos_cliente;
    }

    public void setApellidos_cliente(String apellidos_cliente) {
        this.apellidos_cliente = apellidos_cliente;
    }

    public String getCedula_cliente() {
        return cedula_cliente;
    }

    public void setCedula_cliente(String cedula_cliente) {
        this.cedula_cliente = cedula_cliente;
    }

    public String getDireccion_cliente() {
        return direccion_cliente;
    }

    public void setDireccion_cliente(String direccion_cliente) {
        this.direccion_cliente = direccion_cliente;
    }

    public String getTelefono_cliente() {
        return telefono_cliente;
    }

    public void setTelefono_cliente(String telefono_cliente) {
        this.telefono_cliente = telefono_cliente;
    }

    public String getCorreo_cliente() {
        return correo_cliente;
    }

    public void setCorreo_cliente(String correo_cliente) {
        this.correo_cliente = correo_cliente;
    }

    @Override
    public String toString() {
        return "Cliente{" + "id_cliente=" + id_cliente + ", nombres_cliente=" + nombres_cliente + ", apellidos_cliente=" + apellidos_cliente + ", cedula_cliente=" + cedula_cliente + ", direccion_cliente=" + direccion_cliente + ", telefono_cliente=" + telefono_cliente + ", correo_cliente=" + correo_cliente + '}';
    }
    
    
}
