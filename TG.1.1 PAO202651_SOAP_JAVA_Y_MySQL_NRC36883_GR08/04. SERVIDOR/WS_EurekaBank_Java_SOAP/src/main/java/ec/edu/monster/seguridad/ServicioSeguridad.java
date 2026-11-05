package ec.edu.monster.seguridad;

import ec.edu.monster.datos.FabricaConexion;
import ec.edu.monster.datos.SesionDAO;
import ec.edu.monster.datos.UnidadTrabajo;
import ec.edu.monster.datos.UsuarioDAO;
import ec.edu.monster.excepciones.ExcepcionAutenticacion;
import ec.edu.monster.excepciones.ExcepcionValidacion;
import ec.edu.monster.modelos.Sesion;
import ec.edu.monster.modelos.SesionActiva;
import ec.edu.monster.modelos.Usuario;
import ec.edu.monster.modelos.dto.Respuesta;
import ec.edu.monster.modelos.dto.RespuestaSesion;
import ec.edu.monster.util.RelojUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Autenticación, sesiones y contraseñas (CU-01, CU-02, CU-03, CU-08 y CU-10).
 * Las sesiones viven en la base de datos: el cambio de contraseña y el cierre de sesión se aplican
 * de inmediato en todos los clientes y sobreviven a un reinicio del servidor.
 */
public class ServicioSeguridad {

    public static final int MINUTOS_VIGENCIA = 30;
    public static final int LONGITUD_MINIMA_CLAVE = 6;

    public static final String MENSAJE_CREDENCIALES_INCORRECTAS = "Usuario o contraseña incorrectos";
    public static final String MENSAJE_CREDENCIALES_VACIAS = "El usuario y la contraseña son obligatorios";
    public static final String MENSAJE_SIN_TOKEN = "Acceso denegado: no se encontró el token. Inicie sesión primero.";
    public static final String MENSAJE_TOKEN_INVALIDO = "Acceso denegado: token inválido.";
    public static final String MENSAJE_SESION_EXPIRADA = "La sesión ha expirado. Inicie sesión nuevamente.";
    public static final String MENSAJE_CLAVE_ACTUAL_INCORRECTA = "La contraseña actual es incorrecta";
    public static final String MENSAJE_CLAVE_NUEVA_INVALIDA =
            "La nueva contraseña debe tener al menos 6 caracteres y ser distinta de la actual";

    private final FabricaConexion fabrica;
    private final UsuarioDAO usuarios;
    private final SesionDAO sesiones;
    private final Clock reloj;

    public ServicioSeguridad(FabricaConexion fabrica, UsuarioDAO usuarios, SesionDAO sesiones, Clock reloj) {
        this.fabrica = fabrica;
        this.usuarios = usuarios;
        this.sesiones = sesiones;
        this.reloj = reloj;
    }

    public RespuestaSesion iniciarSesion(String nombreUsuario, String clave, String ip) throws SQLException {
        if (nombreUsuario == null || nombreUsuario.isBlank() || clave == null || clave.isEmpty()) {
            throw new ExcepcionValidacion(MENSAJE_CREDENCIALES_VACIAS);
        }

        try (UnidadTrabajo unidad = fabrica.abrir()) {
            Usuario usuario = usuarios.buscar(unidad, nombreUsuario.trim());
            // El mismo mensaje para usuario inexistente, inactivo o clave errada: no revela cuál falló
            if (usuario == null || !usuario.estaActivo() || !usuario.verificarClave(clave)) {
                throw new ExcepcionAutenticacion(MENSAJE_CREDENCIALES_INCORRECTAS);
            }

            LocalDateTime ahora = RelojUtil.ahoraLocal(reloj);
            Sesion sesion = new Sesion(UUID.randomUUID().toString(), usuario.getNombreUsuario(), ahora, ahora, null, ip);
            sesiones.registrar(unidad, sesion);

            return new RespuestaSesion(HttpServletResponse.SC_OK, "Bienvenido, " + usuario.getNombreEmpleado(),
                    sesion.getToken(), usuario.getNombreUsuario(), usuario.getNombreEmpleado(), MINUTOS_VIGENCIA);
        }
    }

    /**
     * CU-08: el token debe pertenecer a una sesión abierta con menos de 30 minutos sin uso.
     * Si venció, la sesión queda cerrada (CU-10). Si es válido, se renueva la última actividad.
     */
    public SesionActiva validarToken(String token) throws SQLException {
        if (token == null || token.isBlank()) {
            throw new ExcepcionAutenticacion(MENSAJE_SIN_TOKEN);
        }

        try (UnidadTrabajo unidad = fabrica.abrir()) {
            SesionActiva sesion = sesiones.buscarAbierta(unidad, token.trim());
            if (sesion == null) {
                throw new ExcepcionAutenticacion(MENSAJE_TOKEN_INVALIDO);
            }

            LocalDateTime ahora = RelojUtil.ahoraLocal(reloj);
            if (Duration.between(sesion.getUltimaActividad(), ahora).compareTo(Duration.ofMinutes(MINUTOS_VIGENCIA)) > 0) {
                sesiones.cerrar(unidad, sesion.getToken(), ahora);
                throw new ExcepcionAutenticacion(MENSAJE_SESION_EXPIRADA);
            }
            sesiones.actualizarActividad(unidad, sesion.getToken(), ahora);
            return sesion;
        }
    }

    public Respuesta cambiarClave(SesionActiva sesion, String claveActual, String claveNueva) throws SQLException {
        try (UnidadTrabajo unidad = fabrica.abrir()) {
            Usuario usuario = usuarios.buscar(unidad, sesion.getNombreUsuario());
            if (usuario == null) {
                throw new ExcepcionAutenticacion(MENSAJE_TOKEN_INVALIDO);
            }

            if (claveActual == null || claveActual.isEmpty() || !usuario.verificarClave(claveActual)) {
                throw new ExcepcionValidacion(MENSAJE_CLAVE_ACTUAL_INCORRECTA);
            }
            if (claveNueva == null || claveNueva.length() < LONGITUD_MINIMA_CLAVE || claveNueva.equals(claveActual)) {
                throw new ExcepcionValidacion(MENSAJE_CLAVE_NUEVA_INVALIDA);
            }

            usuarios.actualizarClave(unidad, usuario.getNombreUsuario(), Usuario.calcularHash(claveNueva));
            return new Respuesta(HttpServletResponse.SC_OK, "Contraseña actualizada correctamente");
        }
    }

    public Respuesta cerrarSesion(SesionActiva sesion) throws SQLException {
        try (UnidadTrabajo unidad = fabrica.abrir()) {
            sesiones.cerrar(unidad, sesion.getToken(), RelojUtil.ahoraLocal(reloj));
            return new Respuesta(HttpServletResponse.SC_OK, "Sesión cerrada correctamente");
        }
    }
}
