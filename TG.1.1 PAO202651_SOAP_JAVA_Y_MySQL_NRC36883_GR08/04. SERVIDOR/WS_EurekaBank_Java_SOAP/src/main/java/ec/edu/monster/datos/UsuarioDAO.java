package ec.edu.monster.datos;

import ec.edu.monster.modelos.Usuario;
import java.sql.SQLException;

public interface UsuarioDAO {

    /**
     * Usuario con el nombre de su empleado; null si no existe.
     */
    Usuario buscar(UnidadTrabajo unidad, String nombreUsuario) throws SQLException;

    void actualizarClave(UnidadTrabajo unidad, String nombreUsuario, String hashClave) throws SQLException;
}
