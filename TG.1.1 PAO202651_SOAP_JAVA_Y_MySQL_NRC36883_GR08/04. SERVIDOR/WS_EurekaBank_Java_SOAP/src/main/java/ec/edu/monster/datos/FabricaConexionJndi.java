package ec.edu.monster.datos;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Conexiones MySQL del origen de datos que Payara crea a partir de @DataSourceDefinition (usuario Monster,
 * ver ec.edu.monster.ws.InicioAplicacion). Solo el servidor se conecta a MySQL; los clientes nunca lo hacen.
 */
public class FabricaConexionJndi implements FabricaConexion {

    private final String nombreJndi;
    private volatile DataSource origenDatos;

    public FabricaConexionJndi(String nombreJndi) {
        this.nombreJndi = nombreJndi;
    }

    @Override
    public UnidadTrabajo abrir(boolean transaccional) throws SQLException {
        Connection conexion = obtenerOrigenDatos().getConnection();
        try {
            conexion.setAutoCommit(!transaccional);
            return new UnidadTrabajoJdbc(conexion, transaccional);
        } catch (SQLException ex) {
            conexion.close();
            throw ex;
        }
    }

    // Se busca al primer uso: el origen de datos existe recién cuando la aplicación terminó de desplegarse
    private DataSource obtenerOrigenDatos() throws SQLException {
        DataSource actual = origenDatos;
        if (actual == null) {
            try {
                actual = (DataSource) new InitialContext().lookup(nombreJndi);
                origenDatos = actual;
            } catch (NamingException ex) {
                // Se trata como base de datos no disponible: el cliente recibe el mismo mensaje (500)
                throw new SQLException("No se encontró el origen de datos " + nombreJndi, ex);
            }
        }
        return actual;
    }

    private static final class UnidadTrabajoJdbc implements UnidadTrabajo {

        private final Connection conexion;
        private final boolean transaccional;
        private boolean confirmada;

        private UnidadTrabajoJdbc(Connection conexion, boolean transaccional) {
            this.conexion = conexion;
            this.transaccional = transaccional;
        }

        @Override
        public Connection getConexion() {
            return conexion;
        }

        @Override
        public void confirmar() throws SQLException {
            if (transaccional) {
                conexion.commit();
                confirmada = true;
            }
        }

        @Override
        public void close() throws SQLException {
            try {
                if (transaccional) {
                    if (!confirmada) {
                        try {
                            conexion.rollback();
                        } catch (SQLException ex) {
                            // La conexión ya se perdió: MySQL descarta la transacción por su cuenta
                        }
                    }
                    // La conexión vuelve al pool de Payara tal como la entregó
                    conexion.setAutoCommit(true);
                }
            } finally {
                conexion.close();
            }
        }
    }
}
