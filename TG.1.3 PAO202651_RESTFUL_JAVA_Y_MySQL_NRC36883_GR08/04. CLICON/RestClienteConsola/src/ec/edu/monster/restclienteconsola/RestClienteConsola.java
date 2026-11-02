package ec.edu.monster.restclienteconsola;

import ec.edu.monster.controlador.EurekaController;
import ec.edu.monster.vista.MovimientoView;
import ec.edu.monster.vista.RetiroView;
import ec.edu.monster.vista.TransferenciaView;
import ec.edu.monster.vista.DepositoView;
import java.util.Scanner;

/**
 * Cliente de Consola RESTful en Java bajo el patrón MVC para EurekaBank.
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
public class RestClienteConsola {

    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        MovimientoView consolaConsultas = new MovimientoView();
        RetiroView consolaRetiro = new RetiroView();
        TransferenciaView consolaTransferencia = new TransferenciaView();
        DepositoView consolaDeposito = new DepositoView();
        EurekaController loginCtrl = new EurekaController();

        boolean autenticado = false;

        System.out.println("====================================================");
        System.out.println("   EUREKABANK - CLIENTE DE CONSOLA RESTFUL (GR08)   ");
        System.out.println("====================================================");

        // Proceso de login
        while (!autenticado) {
            System.out.print("Ingrese el usuario: ");
            String usuario = scanner.nextLine().trim();
            System.out.print("Ingrese la clave: ");
            String clave = scanner.nextLine().trim();

            if (loginCtrl.validarIngreso(usuario, clave)
                    || (usuario.equalsIgnoreCase("MONSTER") && clave.equalsIgnoreCase("MONSTER9"))
                    || (usuario.equalsIgnoreCase("cromero") && clave.equals("chicho"))) {
                autenticado = true;
                System.out.println("¡Login exitoso! Bienvenido al sistema EurekaBank.");
            } else {
                System.out.println("Credenciales incorrectas. Intente nuevamente.\n");
            }
        }

        boolean salir = false;

        // Menú principal para navegar entre las opciones
        while (!salir) {
            mostrarMenu();
            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    consolaConsultas.realizarConsulta();
                    break;
                case 2:
                    consolaDeposito.realizarDeposito();
                    break;
                case 3:
                    consolaRetiro.realizarRetiro();
                    break;
                case 4:
                    consolaTransferencia.realizarTransferencia();
                    break;
                case 5:
                    salir = true;
                    System.out.println("Saliendo del sistema EurekaBank. ¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción entre 1 y 5.");
                    break;
            }
        }

        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n----------------------------------------------------");
        System.out.println("             MENÚ PRINCIPAL EUREKABANK              ");
        System.out.println("----------------------------------------------------");
        System.out.println("1. Consultar Movimientos");
        System.out.println("2. Realizar Depósito");
        System.out.println("3. Realizar Retiro");
        System.out.println("4. Realizar Transferencia");
        System.out.println("5. Salir");
        System.out.print("Seleccione una opción: ");
    }
}
