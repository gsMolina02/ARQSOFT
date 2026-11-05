package ec.edu.monster.datos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Crea sentencias parametrizadas sobre la conexión de una unidad de trabajo.
 * Los valores siempre viajan como parámetros: nunca se concatenan en el SQL.
 */
final class SentenciaSql {

    private SentenciaSql() {
    }

    static PreparedStatement preparar(UnidadTrabajo unidad, String sql, Object... valores) throws SQLException {
        PreparedStatement sentencia = unidad.getConexion().prepareStatement(sql);
        try {
            for (int i = 0; i < valores.length; i++) {
                if (valores[i] == null) {
                    sentencia.setNull(i + 1, Types.NULL);
                } else {
                    sentencia.setObject(i + 1, valores[i]);
                }
            }
            return sentencia;
        } catch (SQLException ex) {
            sentencia.close();
            throw ex;
        }
    }
}
