package ec.edu.monster.datos;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Conexión abierta y, si la operación lo requiere, su transacción. Al cerrarse sin confirmar()
 * la transacción se revierte (rollback): ningún saldo ni movimiento queda a medias.
 */
public interface UnidadTrabajo extends AutoCloseable {

    Connection getConexion();

    void confirmar() throws SQLException;

    @Override
    void close() throws SQLException;
}
