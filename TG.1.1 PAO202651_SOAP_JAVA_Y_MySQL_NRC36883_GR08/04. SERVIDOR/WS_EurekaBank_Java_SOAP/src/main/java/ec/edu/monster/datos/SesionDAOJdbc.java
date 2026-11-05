package ec.edu.monster.datos;

import ec.edu.monster.modelos.Sesion;
import ec.edu.monster.modelos.SesionActiva;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SesionDAOJdbc implements SesionDAO {

    @Override
    public void registrar(UnidadTrabajo unidad, Sesion sesion) throws SQLException {
        try (PreparedStatement sentencia = SentenciaSql.preparar(unidad, """
                insert into sesion (token, nombre_usuario, fecha_ingreso, ultima_actividad, fecha_salida, ip)
                values (?, ?, ?, ?, ?, ?)
                """,
                sesion.getToken(), sesion.getNombreUsuario(), sesion.getFechaIngreso(), sesion.getUltimaActividad(),
                sesion.getFechaSalida(), sesion.getIp())) {
            sentencia.executeUpdate();
        }
    }

    @Override
    public SesionActiva buscarAbierta(UnidadTrabajo unidad, String token) throws SQLException {
        try (PreparedStatement sentencia = SentenciaSql.preparar(unidad, """
                select s.token, s.ultima_actividad, u.nombre_usuario, u.codigo_empleado,
                       concat(e.nombres, ' ', e.apellidos) as empleado
                from sesion s
                join usuario u on u.nombre_usuario = s.nombre_usuario
                join empleado e on e.codigo_empleado = u.codigo_empleado
                where s.token = ? and s.fecha_salida is null and u.estado = 'ACTIVO'
                """, token);
                ResultSet fila = sentencia.executeQuery()) {
            if (!fila.next()) {
                return null;
            }
            return new SesionActiva(fila.getString("token"), fila.getString("nombre_usuario"),
                    fila.getString("codigo_empleado"), fila.getString("empleado"),
                    fila.getObject("ultima_actividad", LocalDateTime.class));
        }
    }

    @Override
    public void actualizarActividad(UnidadTrabajo unidad, String token, LocalDateTime fecha) throws SQLException {
        try (PreparedStatement sentencia = SentenciaSql.preparar(unidad,
                "update sesion set ultima_actividad = ? where token = ?", fecha, token)) {
            sentencia.executeUpdate();
        }
    }

    @Override
    public void cerrar(UnidadTrabajo unidad, String token, LocalDateTime fecha) throws SQLException {
        try (PreparedStatement sentencia = SentenciaSql.preparar(unidad,
                "update sesion set fecha_salida = ? where token = ? and fecha_salida is null", fecha, token)) {
            sentencia.executeUpdate();
        }
    }
}
