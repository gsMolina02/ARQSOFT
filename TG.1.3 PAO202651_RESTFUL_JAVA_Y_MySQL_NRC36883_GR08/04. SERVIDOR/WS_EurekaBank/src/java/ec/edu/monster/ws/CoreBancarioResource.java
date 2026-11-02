package ec.edu.monster.ws;

import ec.edu.monster.modelo.CuentaDTO;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.OperationResult;
import ec.edu.monster.servicio.EurekaService;
import java.util.ArrayList;
import java.util.List;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Recurso RESTful JAX-RS CoreBancarioResource (/webresources/coreBancario).
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
@Path("coreBancario")
public class CoreBancarioResource {

    @Context
    private UriInfo context;
    private final Gson gson = new Gson();

    public CoreBancarioResource() {
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarIngreso(String jsonBody) {
        JsonObject responseJson = new JsonObject();
        boolean exito = false;
        try {
            JsonObject jsonObject = gson.fromJson(jsonBody, JsonObject.class);
            String usuario = jsonObject.has("usuario") ? jsonObject.get("usuario").getAsString() : "";
            String clave = jsonObject.has("clave") ? jsonObject.get("clave").getAsString()
                         : (jsonObject.has("password") ? jsonObject.get("password").getAsString() : "");
            EurekaService service = new EurekaService();
            exito = service.validarIngreso(usuario, clave);
        } catch (Exception e) {
            exito = false;
        }
        responseJson.addProperty("success", exito);
        responseJson.addProperty("message", exito ? "Inicio de sesión correcto" : "Credenciales inválidas");
        return Response.ok(responseJson.toString()).build();
    }

    @GET
    @Path("/cuentas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCuentas() {
        try {
            EurekaService service = new EurekaService();
            List<CuentaDTO> lista = service.listarCuentas();
            return Response.ok(gson.toJson(lista)).build();
        } catch (Exception e) {
            return Response.ok("[]").build();
        }
    }

    @GET
    @Path("/cuenta/{cuenta}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCuenta(@PathParam("cuenta") String cuenta) {
        try {
            EurekaService service = new EurekaService();
            CuentaDTO dto = service.obtenerCuenta(cuenta);
            if (dto != null) {
                return Response.ok(gson.toJson(dto)).build();
            }
            JsonObject error = new JsonObject();
            error.addProperty("error", "Cuenta no encontrada");
            return Response.status(Response.Status.NOT_FOUND).entity(error.toString()).build();
        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error.toString()).build();
        }
    }

    @GET
    @Path("/movimientos/{cuenta}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovimientos(@PathParam("cuenta") String cuenta) {
        List<Movimiento> lista;
        try {
            EurekaService service = new EurekaService();
            lista = service.leerMovimientos(cuenta);
        } catch (Exception e) {
            lista = new ArrayList<>();
        }
        String jsonLista = gson.toJson(lista);
        return Response.ok(jsonLista).build();
    }

    @POST
    @Path("/deposito")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarDeposito(String jsonBody) {
        try {
            JsonObject jsonObject = gson.fromJson(jsonBody, JsonObject.class);
            String cuenta = jsonObject.get("cuenta").getAsString();
            double importe = jsonObject.get("importe").getAsDouble();
            String codEmp = jsonObject.has("codEmp") ? jsonObject.get("codEmp").getAsString() : "0001";

            EurekaService service = new EurekaService();
            OperationResult res = service.registrarDepositoConResultado(cuenta, importe, codEmp);
            return Response.ok(gson.toJson(res)).build();
        } catch (Exception e) {
            OperationResult err = new OperationResult();
            err.setSuccess(false);
            err.setMessage("Error en depósito: " + e.getMessage());
            return Response.ok(gson.toJson(err)).build();
        }
    }

    @POST
    @Path("/retiro")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarRetiro(String jsonBody) {
        try {
            JsonObject jsonObject = gson.fromJson(jsonBody, JsonObject.class);
            String cuenta = jsonObject.get("cuenta").getAsString();
            double importe = jsonObject.get("importe").getAsDouble();
            String codEmp = jsonObject.has("codEmp") ? jsonObject.get("codEmp").getAsString() : "0001";

            EurekaService service = new EurekaService();
            OperationResult res = service.registrarRetiroConResultado(cuenta, importe, codEmp);
            return Response.ok(gson.toJson(res)).build();
        } catch (Exception e) {
            OperationResult err = new OperationResult();
            err.setSuccess(false);
            err.setMessage("Error en retiro: " + e.getMessage());
            return Response.ok(gson.toJson(err)).build();
        }
    }

    @POST
    @Path("/transferencia")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarTransferencia(String jsonBody) {
        try {
            JsonObject jsonObject = gson.fromJson(jsonBody, JsonObject.class);
            String cuentaOrigen = jsonObject.get("cuentaOrigen").getAsString();
            String cuentaDestino = jsonObject.get("cuentaDestino").getAsString();
            double importe = jsonObject.get("importe").getAsDouble();
            String codEmp = jsonObject.has("codEmp") ? jsonObject.get("codEmp").getAsString() : "0001";

            EurekaService service = new EurekaService();
            OperationResult res = service.registrarTransferenciaConResultado(cuentaOrigen, cuentaDestino, importe, codEmp);
            return Response.ok(gson.toJson(res)).build();
        } catch (Exception e) {
            OperationResult err = new OperationResult();
            err.setSuccess(false);
            err.setMessage("Error en transferencia: " + e.getMessage());
            return Response.ok(gson.toJson(err)).build();
        }
    }
}
