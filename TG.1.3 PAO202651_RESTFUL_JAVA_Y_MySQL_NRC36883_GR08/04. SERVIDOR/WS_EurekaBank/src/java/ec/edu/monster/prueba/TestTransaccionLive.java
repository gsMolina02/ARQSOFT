
package ec.edu.monster.prueba;

import ec.edu.monster.servicio.EurekaService;
import ec.edu.monster.modelo.Movimiento;
import java.util.List;

public class TestTransaccionLive {
    public static void main(String[] args) {
        EurekaService svc = new EurekaService();
        System.out.println("=== TEST TRANSACCIONAL ACID EN VIVO ===");
        
        String cta = "00100001";
        List<Movimiento> antes = svc.leerMovimientos(cta);
        System.out.println("Movimientos ANTES del deposito: " + antes.size());
        
        System.out.println("Ejecutando registrarDeposito(cuenta='00100001', importe=200.0, empleado='0001')...");
        try {
            svc.registrarDeposito(cta, 200.0, "0001");
            System.out.println("Resultado de registrarDeposito: [PASS] EXITO (Commit en MySQL)");
        } catch (Exception e) {
            System.out.println("Resultado de registrarDeposito: [FAIL] " + e.getMessage());
        }
        
        List<Movimiento> despues = svc.leerMovimientos(cta);
        System.out.println("Movimientos DESPUES del deposito: " + despues.size());
        if (despues.size() == antes.size() + 1) {
            Movimiento nuevo = despues.get(despues.size() - 1);
            System.out.println("[PASS] Nuevo movimiento creado:");
            System.out.println("  - Nro Movimiento : " + nuevo.getNromov());
            System.out.println("  - Fecha          : " + nuevo.getFecha());
            System.out.println("  - Tipo           : " + nuevo.getTipo());
            System.out.println("  - Accion         : " + nuevo.getAccion());
            System.out.println("  - Importe        : $" + nuevo.getImporte());
        }
    }
}
