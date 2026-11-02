/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.monster.prueba;

import ec.edu.monster.controlador.EurekaController;
import ec.edu.monster.modelo.Movimiento;
import java.util.List;

/**
 *
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
public class PruebaMovimiento {
      public static void main(String[] args) {
          EurekaController controller = new EurekaController();
        List<Movimiento> movimientos = controller.traerMovimientos("00100001");

        for (Movimiento movimiento : movimientos) {
            System.out.println("Cuenta: " + movimiento.getCuenta() + 
                               ", NroMov: " + movimiento.getNromov() +
                               ", Tipo: " + movimiento.getTipo() + 
                               ", Acción: " + movimiento.getAccion() + 
                               ", Importe: " + movimiento.getImporte());
        }
    }
    
}
