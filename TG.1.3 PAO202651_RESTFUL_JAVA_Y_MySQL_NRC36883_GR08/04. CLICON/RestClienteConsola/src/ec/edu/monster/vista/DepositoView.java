/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.monster.vista;

import ec.edu.monster.controlador.EurekaController;
import ec.edu.monster.modelo.Movimiento;
import java.util.List;
import java.util.Scanner;
import ec.edu.monster.ws.CoreBancario.Response;

/**
 *
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
public class DepositoView {
      public void realizarDeposito() {
        Scanner scanner = ec.edu.monster.restclienteconsola.RestClienteConsola.scanner;

        System.out.println("Bienvenido a la consola de depósitos bancarios");
        System.out.print("Por favor ingrese su número de cuenta: ");
        String cuenta = scanner.nextLine().trim();

        System.out.print("Ingrese el importe a depositar: ");
        double importe = Double.parseDouble(scanner.nextLine().trim());

        EurekaController controller = new EurekaController();
          Response response = controller.realizarDeposito(cuenta, importe);

        if (response.getStatus() == 200) {
            System.out.println("Depósito realizado exitosamente para la cuenta " + cuenta);
        } else {
            System.out.println("Error al realizar el depósito para la cuenta " + cuenta);
        }
    }
}

