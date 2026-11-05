package ec.edu.monster.datos;

import ec.edu.monster.modelos.Movimiento;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovimientoDAOJdbc implements MovimientoDAO {

    @Override
    public List<Movimiento> listar(UnidadTrabajo unidad, String numeroCuenta) throws SQLException {
        try (PreparedStatement sentencia = SentenciaSql.preparar(unidad, """
                select m.numero_cuenta, m.numero_movimiento, m.codigo_tipo, t.descripcion, t.accion,
                       m.codigo_empleado, m.cuenta_referencia, m.fecha_hora, m.importe
                from movimiento m
                join tipo_movimiento t on t.codigo_tipo = m.codigo_tipo
                where m.numero_cuenta = ?
                order by m.numero_movimiento
                """, numeroCuenta);
                ResultSet fila = sentencia.executeQuery()) {
            List<Movimiento> movimientos = new ArrayList<>();
            while (fila.next()) {
                Movimiento movimiento = new Movimiento();
                movimiento.setNumeroCuenta(fila.getString("numero_cuenta"));
                movimiento.setNumero(fila.getInt("numero_movimiento"));
                movimiento.setCodigoTipo(fila.getString("codigo_tipo"));
                movimiento.setDescripcionTipo(fila.getString("descripcion"));
                movimiento.setAccion(fila.getString("accion"));
                movimiento.setCodigoEmpleado(fila.getString("codigo_empleado"));
                movimiento.setCuentaReferencia(fila.getString("cuenta_referencia"));
                movimiento.setFechaHora(fila.getObject("fecha_hora", LocalDateTime.class));
                movimiento.setImporte(fila.getBigDecimal("importe"));
                movimientos.add(movimiento);
            }
            return movimientos;
        }
    }

    @Override
    public void registrar(UnidadTrabajo unidad, Movimiento movimiento) throws SQLException {
        try (PreparedStatement sentencia = SentenciaSql.preparar(unidad, """
                insert into movimiento (numero_cuenta, numero_movimiento, codigo_tipo, codigo_empleado,
                                        cuenta_referencia, fecha_hora, importe)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                movimiento.getNumeroCuenta(), movimiento.getNumero(), movimiento.getCodigoTipo(),
                movimiento.getCodigoEmpleado(), movimiento.getCuentaReferencia(), movimiento.getFechaHora(),
                movimiento.getImporte())) {
            sentencia.executeUpdate();
        }
    }
}
