/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.monster.prueba;

import ec.edu.monster.controlador.EurekaController;
import ec.edu.monster.modelo.Movimiento;
import java.util.List;
import ec.edu.monster.ws.CoreBancario.Response;

/**
 *
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
public class PruebaDeposito {
    public static void main(String[] args) {
        EurekaController controller = new EurekaController();
        
        // Realizar un depósito de prueba
        String cuenta = "00100001"; // Cuenta a la que se hará el depósito
        double importe = 100.00; // Importe del depósito
        
        Response response = controller.realizarDeposito(cuenta, importe);
        
        if (response.getStatus() == 200) {
            System.out.println("Depósito realizado exitosamente.");
        } else {
            System.out.println("No se pudo realizar el depósito. Código de estado: " + response.getStatus());
        }
        
        // Obtener y mostrar los movimientos después del depósito
        List<Movimiento> movimientos = controller.traerMovimientos(cuenta);

        for (Movimiento movimiento : movimientos) {
            System.out.println("Cuenta: " + movimiento.getCuenta() + 
                               ", NroMov: " + movimiento.getNromov() +
                               ", Tipo: " + movimiento.getTipo() + 
                               ", Acción: " + movimiento.getAccion() + 
                               ", Importe: " + movimiento.getImporte());
        }
    }
    
}

