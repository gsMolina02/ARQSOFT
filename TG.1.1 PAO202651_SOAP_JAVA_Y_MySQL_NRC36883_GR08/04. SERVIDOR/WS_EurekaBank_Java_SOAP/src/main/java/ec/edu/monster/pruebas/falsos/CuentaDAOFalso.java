package ec.edu.monster.pruebas.falsos;

import ec.edu.monster.datos.CuentaDAO;
import ec.edu.monster.datos.UnidadTrabajo;
import ec.edu.monster.modelos.Cuenta;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CuentaDAOFalso implements CuentaDAO {

    public final Map<String, Cuenta> cuentas = new HashMap<>();
    public final List<String> bloqueadas = new ArrayList<>();
    public final List<String> actualizadas = new ArrayList<>();

    public CuentaDAOFalso con(Cuenta... lista) {
        for (Cuenta cuenta : lista) {
            cuentas.put(cuenta.getNumero(), cuenta);
        }
        return this;
    }

    // El servicio recibe una copia, como si la leyera de la base: sus cambios solo cuentan si llama a actualizar
    @Override
    public Cuenta buscar(UnidadTrabajo unidad, String numero) {
        Cuenta cuenta = cuentas.get(numero);
        return cuenta == null ? null : cuenta.copiar();
    }

    @Override
    public List<Cuenta> listar(UnidadTrabajo unidad) {
        return cuentas.values().stream()
                .sorted(Comparator.comparing(Cuenta::getNumero))
                .map(Cuenta::copiar)
                .toList();
    }

    @Override
    public Cuenta bloquear(UnidadTrabajo unidad, String numero) {
        bloqueadas.add(numero);
        return buscar(unidad, numero);
    }

    @Override
    public void actualizar(UnidadTrabajo unidad, Cuenta cuenta) {
        actualizadas.add(cuenta.getNumero());
        cuentas.put(cuenta.getNumero(), cuenta.copiar());
    }
}
