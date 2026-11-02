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
public class Usuario {
    private int id_usuario;
    private int id_cliente;
    private String cedula;
    private String nombre;
    private String password;
    private Integer cambio_usuario;

    public Usuario() {
    }

    public Usuario(int id_usuario, int id_cliente, String cedula, String nombre, String password, Integer cambio_usuario) {
        this.id_usuario = id_usuario;
        this.id_cliente = id_cliente;
        this.cedula = cedula;
        this.nombre = nombre;
        this.password = password;
        this.cambio_usuario = cambio_usuario;
    }

    
    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Integer getCambio_usuario() {
        return cambio_usuario;
    }

    public void setCambio_usuario(Integer cambio_usuario) {
        this.cambio_usuario = cambio_usuario;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id_usuario=" + id_usuario + ", id_cliente=" + id_cliente + ", cedula=" + cedula + ", nombre=" + nombre + ", password=" + password + ", cambio_usuario=" + cambio_usuario + '}';
    }

    

    
}
