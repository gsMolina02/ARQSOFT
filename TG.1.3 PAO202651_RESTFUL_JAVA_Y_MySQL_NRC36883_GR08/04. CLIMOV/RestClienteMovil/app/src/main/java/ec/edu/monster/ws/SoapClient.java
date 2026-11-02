package ec.edu.monster.ws;

import java.util.List;
import ec.edu.monster.modelo.CuentaItem;
import ec.edu.monster.modelo.Movimiento;

/**
 * Fachada para el cliente REST de EurekaBank en Android.
 * Delega todas las operaciones al servicio RESTful WS_EurekaBank.
 */
public class SoapClient {

    public static boolean validarIngreso(String usuario, String clave) {
        return RestApiClient.validarIngreso(usuario, clave);
    }

    public static List<CuentaItem> listarCuentas() {
        return RestApiClient.listarCuentas();
    }

    public static List<Movimiento> traerMovimientos(String cuenta) {
        return RestApiClient.traerMovimientos(cuenta);
    }

    public static boolean regDeposito(String cuenta, double importe) {
        return RestApiClient.registrarDeposito(cuenta, importe) > 0;
    }

    public static int registrarDeposito(String cuenta, double importe, String codEmp) {
        return RestApiClient.registrarDeposito(cuenta, importe);
    }

    public static boolean regRetiro(String cuenta, double importe) {
        return RestApiClient.registrarRetiro(cuenta, importe) > 0;
    }

    public static int registrarRetiro(String cuenta, double importe, String codEmp) {
        return RestApiClient.registrarRetiro(cuenta, importe);
    }

    public static boolean regTransferencia(String cuentaOrigen, String cuentaDestino, double importe) {
        return RestApiClient.registrarTransferencia(cuentaOrigen, cuentaDestino, importe) > 0;
    }

    public static int registrarTransferencia(String cuentaOrigen, String cuentaDestino, double importe, String codEmp) {
        return RestApiClient.registrarTransferencia(cuentaOrigen, cuentaDestino, importe);
    }
}
