package ec.edu.monster.datos;

import ec.edu.monster.modelos.Usuario;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAOJdbc implements UsuarioDAO {

    @Override
    public Usuario buscar(UnidadTrabajo unidad, String nombreUsuario) throws SQLException {
        try (PreparedStatement sentencia = SentenciaSql.preparar(unidad, """
                select u.nombre_usuario, u.codigo_empleado, u.hash_clave, u.estado,
                       concat(e.nombres, ' ', e.apellidos) as empleado
                from usuario u
                join empleado e on e.codigo_empleado = u.codigo_empleado
                where u.nombre_usuario = ?
                """, nombreUsuario);
                ResultSet fila = sentencia.executeQuery()) {
            if (!fila.next()) {
                return null;
            }
            return new Usuario(fila.getString("nombre_usuario"), fila.getString("codigo_empleado"),
                    fila.getString("empleado"), fila.getString("hash_clave"), fila.getString("estado"));
        }
    }

    @Override
    public void actualizarClave(UnidadTrabajo unidad, String nombreUsuario, String hashClave) throws SQLException {
        try (PreparedStatement sentencia = SentenciaSql.preparar(unidad,
                "update usuario set hash_clave = ? where nombre_usuario = ?", hashClave, nombreUsuario)) {
            sentencia.executeUpdate();
        }
    }
}
