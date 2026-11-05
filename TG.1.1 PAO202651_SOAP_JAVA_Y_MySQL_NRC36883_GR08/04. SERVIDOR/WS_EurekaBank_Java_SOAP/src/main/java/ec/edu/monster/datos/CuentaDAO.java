package ec.edu.monster.datos;

import ec.edu.monster.modelos.Cuenta;
import java.sql.SQLException;
import java.util.List;

public interface CuentaDAO {

    /**
     * Cuenta con su cliente, moneda y sucursal; null si no existe.
     */
    Cuenta buscar(UnidadTrabajo unidad, String numero) throws SQLException;

    /**
     * Todas las cuentas, en cualquier estado, ordenadas por número.
     */
    List<Cuenta> listar(UnidadTrabajo unidad) throws SQLException;

    /**
     * Lee la cuenta y bloquea su fila hasta el fin de la transacción (SELECT ... FOR UPDATE).
     */
    Cuenta bloquear(UnidadTrabajo unidad, String numero) throws SQLException;

    void actualizar(UnidadTrabajo unidad, Cuenta cuenta) throws SQLException;
}
