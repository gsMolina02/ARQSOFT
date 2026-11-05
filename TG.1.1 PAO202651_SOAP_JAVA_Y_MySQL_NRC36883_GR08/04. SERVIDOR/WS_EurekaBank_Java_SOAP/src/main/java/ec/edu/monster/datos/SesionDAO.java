package ec.edu.monster.datos;

import ec.edu.monster.modelos.Sesion;
import ec.edu.monster.modelos.SesionActiva;
import java.sql.SQLException;
import java.time.LocalDateTime;

public interface SesionDAO {

    void registrar(UnidadTrabajo unidad, Sesion sesion) throws SQLException;

    /**
     * Sesión sin fecha de salida de un usuario ACTIVO; null si el token no existe o ya se cerró.
     */
    SesionActiva buscarAbierta(UnidadTrabajo unidad, String token) throws SQLException;

    void actualizarActividad(UnidadTrabajo unidad, String token, LocalDateTime fecha) throws SQLException;

    void cerrar(UnidadTrabajo unidad, String token, LocalDateTime fecha) throws SQLException;
}
