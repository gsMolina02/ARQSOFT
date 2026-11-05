package ec.edu.monster.pruebas.falsos;

import ec.edu.monster.datos.MovimientoDAO;
import ec.edu.monster.datos.UnidadTrabajo;
import ec.edu.monster.modelos.Movimiento;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MovimientoDAOFalso implements MovimientoDAO {

    public final List<Movimiento> existentes = new ArrayList<>();
    public final List<Movimiento> registrados = new ArrayList<>();

    @Override
    public List<Movimiento> listar(UnidadTrabajo unidad, String numeroCuenta) {
        return existentes.stream()
                .filter(movimiento -> movimiento.getNumeroCuenta().equals(numeroCuenta))
                .sorted(Comparator.comparingInt(Movimiento::getNumero))
                .toList();
    }

    @Override
    public void registrar(UnidadTrabajo unidad, Movimiento movimiento) {
        registrados.add(movimiento);
    }
}
