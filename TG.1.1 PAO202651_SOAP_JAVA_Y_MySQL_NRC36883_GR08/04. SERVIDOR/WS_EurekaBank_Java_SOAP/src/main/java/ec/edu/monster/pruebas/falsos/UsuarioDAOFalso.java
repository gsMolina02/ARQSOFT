package ec.edu.monster.pruebas.falsos;

import ec.edu.monster.datos.UnidadTrabajo;
import ec.edu.monster.datos.UsuarioDAO;
import ec.edu.monster.modelos.Usuario;
import java.util.HashMap;
import java.util.Map;

public class UsuarioDAOFalso implements UsuarioDAO {

    public final Map<String, Usuario> usuarios = new HashMap<>();

    public UsuarioDAOFalso con(Usuario usuario) {
        usuarios.put(usuario.getNombreUsuario(), usuario);
        return this;
    }

    @Override
    public Usuario buscar(UnidadTrabajo unidad, String nombreUsuario) {
        return usuarios.get(nombreUsuario);
    }

    @Override
    public void actualizarClave(UnidadTrabajo unidad, String nombreUsuario, String hashClave) {
        Usuario actual = usuarios.get(nombreUsuario);
        usuarios.put(nombreUsuario, new Usuario(actual.getNombreUsuario(), actual.getCodigoEmpleado(),
                actual.getNombreEmpleado(), hashClave, actual.getEstado()));
    }
}
