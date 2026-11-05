package ec.edu.monster.pruebas.falsos;

import ec.edu.monster.datos.SesionDAO;
import ec.edu.monster.datos.UnidadTrabajo;
import ec.edu.monster.modelos.Sesion;
import ec.edu.monster.modelos.SesionActiva;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SesionDAOFalso implements SesionDAO {

    public final List<Sesion> registradas = new ArrayList<>();
    public final Map<String, SesionActiva> abiertas = new HashMap<>();
    public final Map<String, LocalDateTime> actividad = new HashMap<>();
    public final Map<String, LocalDateTime> cerradas = new HashMap<>();

    public SesionDAOFalso con(SesionActiva sesion) {
        abiertas.put(sesion.getToken(), sesion);
        return this;
    }

    @Override
    public void registrar(UnidadTrabajo unidad, Sesion sesion) {
        registradas.add(sesion);
    }

    @Override
    public SesionActiva buscarAbierta(UnidadTrabajo unidad, String token) {
        return cerradas.containsKey(token) ? null : abiertas.get(token);
    }

    @Override
    public void actualizarActividad(UnidadTrabajo unidad, String token, LocalDateTime fecha) {
        actividad.put(token, fecha);
    }

    @Override
    public void cerrar(UnidadTrabajo unidad, String token, LocalDateTime fecha) {
        cerradas.put(token, fecha);
    }
}
