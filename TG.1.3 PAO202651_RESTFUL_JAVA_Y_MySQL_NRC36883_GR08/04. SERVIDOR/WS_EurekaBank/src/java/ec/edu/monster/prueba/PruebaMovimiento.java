/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.monster.prueba;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.servicio.EurekaService;
import java.util.List;

/**
 *
 * @author Steven
 */
public class PruebaMovimiento {
    public static void main(String[] args) {
        try {
            //dato de la prueba
            String cuenta = "00100001";
            
            //proceso
            EurekaService service =new EurekaService();
            List<Movimiento> lista = service.leerMovimientos(cuenta);
            
            //reporte
            for(Movimiento r : lista){
                System.out.println(r.getNromov()+ " - " + r.getAccion() + " - " + r.getImporte());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
