/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.monster.restclienteescritorio;

/**
 *
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
import ec.edu.monster.vista.LoginView;

/**
 *
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
public class RestClienteEscritorio {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Crear y mostrar la ventana de LoginVista
        java.awt.EventQueue.invokeLater(() -> {
            LoginView login = new LoginView();
            login.setVisible(true);
        });
    }
}