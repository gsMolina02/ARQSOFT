package ec.edu.monster.controlador;

import com.google.gson.JsonObject;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.ws.CoreBancario;
import ec.edu.monster.ws.CoreBancario.Response;
import java.util.List;

/**
 * Controlador de EurekaBank para el cliente de Consola y Escritorio.
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
public class EurekaController {

    public boolean validarIngreso(String usuario, String clave) {
        CoreBancario core = new CoreBancario();
        boolean res = core.validarIngreso(usuario, clave);
        core.close();
        return res;
    }

    public List<Movimiento> traerMovimientos(String cuenta) {
        CoreBancario core = new CoreBancario();
        List<Movimiento> movimientos = core.getMovimientos(cuenta);
        core.close();
        return movimientos;
    }

    public Response realizarDeposito(String cuenta, double importe) {
        CoreBancario core = new CoreBancario();
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("cuenta", cuenta);
        requestBody.addProperty("importe", importe);
        Response response = core.registrarDeposito(requestBody.toString());
        core.close();
        return response;
    }

    public Response realizarRetiro(String cuenta, double importe) {
        CoreBancario core = new CoreBancario();
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("cuenta", cuenta);
        requestBody.addProperty("importe", importe);
        Response response = core.registrarRetiro(requestBody.toString());
        core.close();
        return response;
    }

    public Response realizarTransferencia(String cuentaOrigen, String cuentaDestino, double importe) {
        CoreBancario core = new CoreBancario();
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("cuentaOrigen", cuentaOrigen);
        requestBody.addProperty("cuentaDestino", cuentaDestino);
        requestBody.addProperty("importe", importe);
        Response response = core.registrarTransferencia(requestBody.toString());
        core.close();
        return response;
    }
}
