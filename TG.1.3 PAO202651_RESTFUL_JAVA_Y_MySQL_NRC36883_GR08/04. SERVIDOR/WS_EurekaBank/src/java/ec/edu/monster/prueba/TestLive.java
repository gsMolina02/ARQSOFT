
package ec.edu.monster.prueba;

import ec.edu.monster.db.AccesoDB;
import ec.edu.monster.servicio.EurekaService;
import ec.edu.monster.modelo.Movimiento;
import java.sql.Connection;
import java.util.List;

public class TestLive {
    public static void main(String[] args) {
        System.out.println("--- 4.1 Test Conexion JDBC ---");
        try (Connection cn = AccesoDB.getConnection()) {
            if (cn != null && !cn.isClosed()) {
                System.out.println("[PASS] AccesoDB conecto exitosamente a MySQL eurekabank.");
            }
        } catch (Exception e) {
            System.out.println("[FAIL] AccesoDB error: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        EurekaService svc = new EurekaService();
        System.out.println("--- 4.2 Test Login ---");
        boolean loginOk = svc.validarIngreso("MONSTER", "MONSTER9");
        System.out.println("Login MONSTER/MONSTER9: " + (loginOk ? "[PASS] EXITOSO" : "[FAIL] RECHAZADO"));

        boolean loginFail = svc.validarIngreso("INVALIDO", "123");
        System.out.println("Login Invalido: " + (!loginFail ? "[PASS] RECHAZADO CORRECTAMENTE" : "[FAIL] ACEPTADO"));

        System.out.println("--- 4.3 Test Lectura de Movimientos ---");
        List<Movimiento> movs = svc.leerMovimientos("00100001");
        System.out.println("Cuenta 00100001 tiene " + movs.size() + " movimientos.");
        if (!movs.isEmpty()) {
            Movimiento m = movs.get(movs.size() - 1);
            System.out.println("Ultimo movimiento: Nro " + m.getNromov() + " | " + m.getTipo() + " | $" + m.getImporte());
            System.out.println("[PASS] Lectura de movimientos correcta.");
        }
    }
}
