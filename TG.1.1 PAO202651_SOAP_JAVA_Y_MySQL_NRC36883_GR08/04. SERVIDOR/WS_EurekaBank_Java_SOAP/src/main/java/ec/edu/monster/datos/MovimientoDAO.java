package ec.edu.monster.datos;

import ec.edu.monster.modelos.Movimiento;
import java.sql.SQLException;
import java.util.List;

public interface MovimientoDAO {

    /**
     * Movimientos de la cuenta con la descripción y acción de su tipo, ordenados por número.
     */
    List<Movimiento> listar(UnidadTrabajo unidad, String numeroCuenta) throws SQLException;

    void registrar(UnidadTrabajo unidad, Movimiento movimiento) throws SQLException;
}
