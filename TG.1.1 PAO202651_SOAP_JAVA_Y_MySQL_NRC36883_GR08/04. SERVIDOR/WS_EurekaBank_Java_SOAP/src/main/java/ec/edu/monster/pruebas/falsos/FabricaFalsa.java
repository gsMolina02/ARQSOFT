package ec.edu.monster.pruebas.falsos;

import ec.edu.monster.datos.FabricaConexion;
import ec.edu.monster.datos.UnidadTrabajo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Dobles de prueba: reemplazan MySQL por colecciones en memoria para probar las reglas de negocio.

public class FabricaFalsa implements FabricaConexion {

    public final List<UnidadFalsa> unidades = new ArrayList<>();

    /** Si no es null, abrir() falla con este error, como cuando MySQL está apagado. */
    public SQLException error;

    @Override
    public UnidadTrabajo abrir(boolean transaccional) throws SQLException {
        if (error != null) {
            throw error;
        }
        UnidadFalsa unidad = new UnidadFalsa(transaccional);
        unidades.add(unidad);
        return unidad;
    }

    public static class UnidadFalsa implements UnidadTrabajo {

        public final boolean transaccional;
        public boolean confirmada;
        public boolean cerrada;

        UnidadFalsa(boolean transaccional) {
            this.transaccional = transaccional;
        }

        @Override
        public Connection getConexion() {
            throw new UnsupportedOperationException("Los DAO falsos no usan la conexión.");
        }

        @Override
        public void confirmar() {
            confirmada = true;
        }

        @Override
        public void close() {
            cerrada = true;
        }
    }
}
