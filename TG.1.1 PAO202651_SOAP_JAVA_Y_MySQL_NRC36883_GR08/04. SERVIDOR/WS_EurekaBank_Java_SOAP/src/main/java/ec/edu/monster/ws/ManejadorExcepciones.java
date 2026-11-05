package ec.edu.monster.ws;

import ec.edu.monster.excepciones.ExcepcionServicio;
import ec.edu.monster.modelos.dto.Respuesta;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Traduce cualquier excepción a una respuesta SOAP normal con {codigo, mensaje}: los clientes siempre
 * reciben el mismo formato, sea éxito o error, y nunca un SOAP Fault.
 */
public final class ManejadorExcepciones {

    public static final String MENSAJE_BASE_DATOS = "El servicio no está disponible, intente más tarde";
    public static final String MENSAJE_INTERNO = "Error interno del servidor";

    private static final Logger REGISTRO = Logger.getLogger(ManejadorExcepciones.class.getName());

    private ManejadorExcepciones() {
    }

    /**
     * Operación del servicio que puede fallar con una regla de negocio o con un error de la base de datos.
     */
    @FunctionalInterface
    public interface Operacion<T extends Respuesta> {

        T ejecutar() throws Exception;
    }

    /**
     * Ejecuta la operación; si falla, devuelve una respuesta vacía del tipo esperado con el código y el mensaje del error.
     */
    public static <T extends Respuesta> T atender(String nombreOperacion, Supplier<T> respuestaVacia, Operacion<T> operacion) {
        try {
            return operacion.ejecutar();
        } catch (Exception ex) {
            Respuesta error = traducir(ex);
            if (error.getCodigo() >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                REGISTRO.log(Level.SEVERE, "Error al atender " + nombreOperacion, ex);
            }
            T respuesta = respuestaVacia.get();
            respuesta.setCodigo(error.getCodigo());
            respuesta.setMensaje(error.getMensaje());
            return respuesta;
        }
    }

    public static Respuesta traducir(Throwable excepcion) {
        if (excepcion instanceof ExcepcionServicio propia) {
            return new Respuesta(propia.getCodigo(), propia.getMessage());
        }
        // MySQL apagado, credenciales de la base incorrectas o caída de la conexión
        for (Throwable causa = excepcion; causa != null; causa = causa.getCause()) {
            if (causa instanceof SQLException) {
                return new Respuesta(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, MENSAJE_BASE_DATOS);
            }
        }
        return new Respuesta(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, MENSAJE_INTERNO);
    }
}
