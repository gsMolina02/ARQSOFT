package ec.edu.monster.datos;

import ec.edu.monster.modelos.Cuenta;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CuentaDAOJdbc implements CuentaDAO {

    private static final String SELECCION = """
            select c.numero_cuenta, c.saldo, c.fecha_apertura, c.estado, c.contador_movimientos,
                   cl.codigo_cliente, cl.cedula, concat(cl.nombres, ' ', cl.apellidos) as cliente,
                   m.codigo_moneda, m.nombre as moneda, m.simbolo,
                   s.codigo_sucursal, s.nombre as sucursal
            from cuenta c
            join cliente cl on cl.codigo_cliente = c.codigo_cliente
            join moneda m on m.codigo_moneda = c.codigo_moneda
            join sucursal s on s.codigo_sucursal = c.codigo_sucursal
            """;

    private static final String CONSULTA = SELECCION + "where c.numero_cuenta = ?\n";

    @Override
    public Cuenta buscar(UnidadTrabajo unidad, String numero) throws SQLException {
        return leer(unidad, CONSULTA, numero);
    }

    @Override
    public List<Cuenta> listar(UnidadTrabajo unidad) throws SQLException {
        try (PreparedStatement sentencia = SentenciaSql.preparar(unidad, SELECCION + "order by c.numero_cuenta");
                ResultSet fila = sentencia.executeQuery()) {
            List<Cuenta> lista = new ArrayList<>();
            while (fila.next()) {
                lista.add(mapear(fila));
            }
            return lista;
        }
    }

    // "of c" bloquea solo la fila de la cuenta: no la del cliente, la sucursal ni la moneda USD,
    // que comparten muchas cuentas y harían esperar a operaciones que no tienen relación entre sí
    @Override
    public Cuenta bloquear(UnidadTrabajo unidad, String numero) throws SQLException {
        return leer(unidad, CONSULTA + "for update of c", numero);
    }

    @Override
    public void actualizar(UnidadTrabajo unidad, Cuenta cuenta) throws SQLException {
        try (PreparedStatement sentencia = SentenciaSql.preparar(unidad,
                "update cuenta set saldo = ?, contador_movimientos = ? where numero_cuenta = ?",
                cuenta.getSaldo(), cuenta.getContadorMovimientos(), cuenta.getNumero())) {
            sentencia.executeUpdate();
        }
    }

    private static Cuenta leer(UnidadTrabajo unidad, String sql, String numero) throws SQLException {
        try (PreparedStatement sentencia = SentenciaSql.preparar(unidad, sql, numero);
                ResultSet fila = sentencia.executeQuery()) {
            return fila.next() ? mapear(fila) : null;
        }
    }

    private static Cuenta mapear(ResultSet fila) throws SQLException {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumero(fila.getString("numero_cuenta"));
        cuenta.setSaldo(fila.getBigDecimal("saldo"));
        cuenta.setFechaApertura(fila.getObject("fecha_apertura", LocalDate.class));
        cuenta.setEstado(fila.getString("estado"));
        cuenta.setContadorMovimientos(fila.getInt("contador_movimientos"));
        cuenta.setCodigoCliente(fila.getString("codigo_cliente"));
        cuenta.setCedulaCliente(fila.getString("cedula"));
        cuenta.setNombreCliente(fila.getString("cliente"));
        cuenta.setCodigoMoneda(fila.getString("codigo_moneda"));
        cuenta.setNombreMoneda(fila.getString("moneda"));
        cuenta.setSimboloMoneda(fila.getString("simbolo"));
        cuenta.setCodigoSucursal(fila.getString("codigo_sucursal"));
        cuenta.setNombreSucursal(fila.getString("sucursal"));
        return cuenta;
    }
}
