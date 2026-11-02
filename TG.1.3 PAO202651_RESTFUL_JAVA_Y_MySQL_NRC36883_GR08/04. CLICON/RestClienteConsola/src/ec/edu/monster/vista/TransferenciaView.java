/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.monster.vista;

/**
 *
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
import ec.edu.monster.controlador.EurekaController;
import java.util.Scanner;
import ec.edu.monster.ws.CoreBancario.Response;

public class TransferenciaView {

    public void realizarTransferencia() {
        Scanner scanner = ec.edu.monster.restclienteconsola.RestClienteConsola.scanner;

        System.out.println("Bienvenido a la consola de transferencias bancarias");
        System.out.print("Por favor ingrese su número de cuenta de origen: ");
        String cuentaOrigen = scanner.nextLine().trim();
        System.out.print("Por favor ingrese el número de cuenta de destino: ");
        String cuentaDestino = scanner.nextLine().trim();
        System.out.print("Por favor ingrese el importe para la transferencia: ");
        double importe;

        try {
            importe = Double.parseDouble(scanner.nextLine().trim());
            EurekaController controller = new EurekaController();
            Response response = controller.realizarTransferencia(cuentaOrigen, cuentaDestino, importe);

            if (response.getStatus() == 200) {
                System.out.println("Transferencia realizada exitosamente.");
            } else {
                System.out.println("No se pudo realizar la transferencia. Código de estado: " + response.getStatus());
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor ingrese un importe válido.");
        } catch (Exception e) {
            System.out.println("Ocurrió un error al intentar realizar la transferencia.");
            e.printStackTrace();
        }
    }
}

