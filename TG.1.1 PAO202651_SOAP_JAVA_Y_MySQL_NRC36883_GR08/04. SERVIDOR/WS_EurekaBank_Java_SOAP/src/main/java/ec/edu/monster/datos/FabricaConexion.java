package ec.edu.monster.datos;

import java.sql.SQLException;

/**
 * Abre conexiones con la base de datos eurekabank.
 */
public interface FabricaConexion {

    /**
     * @param transaccional true inicia una transacción que se confirma con {@link UnidadTrabajo#confirmar()}.
     */
    UnidadTrabajo abrir(boolean transaccional) throws SQLException;

    default UnidadTrabajo abrir() throws SQLException {
        return abrir(false);
    }
}
