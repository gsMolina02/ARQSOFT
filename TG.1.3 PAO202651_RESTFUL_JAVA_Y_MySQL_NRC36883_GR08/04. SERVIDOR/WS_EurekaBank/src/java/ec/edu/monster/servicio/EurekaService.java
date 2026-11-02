package ec.edu.monster.servicio;

import ec.edu.monster.db.AccesoDB;
import ec.edu.monster.modelo.CuentaDTO;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.OperationResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio transaccional del Core Bancario EurekaBank con control ACID en MySQL.
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
public class EurekaService {

    public boolean validarIngreso(String usuario, String password) {
        Connection cn = null;
        boolean valido = false;
        String sql = "SELECT COUNT(*) AS total FROM usuario "
                   + "WHERE vch_emplusuario = ? AND vch_emplclave = ? AND vch_emplestado = 'ACTIVO'";
        try {
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, usuario);
            pstm.setString(2, password);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                valido = rs.getInt("total") > 0;
            }
            rs.close();
            pstm.close();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
            }
        }
        return valido;
    }

    public List<CuentaDTO> listarCuentas() {
        Connection cn = null;
        List<CuentaDTO> lista = new ArrayList<>();
        String sql = "SELECT c.chr_cuencodigo, c.dec_cuensaldo, c.vch_cuenestado, c.int_cuencontmov, "
                   + "TRIM(CONCAT(cl.vch_clienombre, ' ', cl.vch_cliepaterno, ' ', cl.vch_cliematerno)) AS titular, "
                   + "m.vch_monedescripcion "
                   + "FROM cuenta c "
                   + "JOIN cliente cl ON c.chr_cliecodigo = cl.chr_cliecodigo "
                   + "JOIN moneda m ON c.chr_monecodigo = m.chr_monecodigo "
                   + "ORDER BY c.chr_cuencodigo";
        try {
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                CuentaDTO dto = new CuentaDTO();
                dto.setCuenta(rs.getString("chr_cuencodigo"));
                dto.setSaldo(rs.getDouble("dec_cuensaldo"));
                dto.setEstado(rs.getString("vch_cuenestado"));
                dto.setContMov(rs.getInt("int_cuencontmov"));
                dto.setCliente(rs.getString("titular"));
                dto.setMoneda(rs.getString("vch_monedescripcion"));
                lista.add(dto);
            }
            rs.close();
            pstm.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar cuentas: " + e.getMessage());
        } finally {
            try { if (cn != null) cn.close(); } catch (Exception e) {}
        }
        return lista;
    }

    public CuentaDTO obtenerCuenta(String cuenta) {
        Connection cn = null;
        CuentaDTO dto = null;
        String sql = "SELECT c.chr_cuencodigo, c.dec_cuensaldo, c.vch_cuenestado, c.int_cuencontmov, "
                   + "TRIM(CONCAT(cl.vch_clienombre, ' ', cl.vch_cliepaterno, ' ', cl.vch_cliematerno)) AS titular, "
                   + "m.vch_monedescripcion "
                   + "FROM cuenta c "
                   + "JOIN cliente cl ON c.chr_cliecodigo = cl.chr_cliecodigo "
                   + "JOIN moneda m ON c.chr_monecodigo = m.chr_monecodigo "
                   + "WHERE c.chr_cuencodigo = ?";
        try {
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                dto = new CuentaDTO();
                dto.setCuenta(rs.getString("chr_cuencodigo"));
                dto.setSaldo(rs.getDouble("dec_cuensaldo"));
                dto.setEstado(rs.getString("vch_cuenestado"));
                dto.setContMov(rs.getInt("int_cuencontmov"));
                dto.setCliente(rs.getString("titular"));
                dto.setMoneda(rs.getString("vch_monedescripcion"));
            }
            rs.close();
            pstm.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener cuenta: " + e.getMessage());
        } finally {
            try { if (cn != null) cn.close(); } catch (Exception e) {}
        }
        return dto;
    }

    public List<Movimiento> leerMovimientos(String cuenta) {
        Connection cn = null;
        List<Movimiento> lista = new ArrayList<Movimiento>();
        String sql = "SELECT "
                + " m.chr_cuencodigo cuenta, "
                + " m.int_movinumero nromov, "
                + " m.dtt_movifecha fecha, "
                + " t.vch_tipodescripcion tipo, "
                + " t.vch_tipoaccion accion, "
                + " m.dec_moviimporte importe "
                + "FROM tipomovimiento t INNER JOIN movimiento m "
                + "ON t.chr_tipocodigo = m.chr_tipocodigo "
                + "WHERE m.chr_cuencodigo = ? "
                + "ORDER BY 2";

        try {
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Movimiento rec = new Movimiento();
                rec.setCuenta(rs.getString("cuenta"));
                rec.setNromov(rs.getInt("nromov"));
                rec.setFecha(rs.getDate("fecha"));
                rec.setTipo(rs.getString("tipo"));
                rec.setAccion(rs.getString("accion"));
                rec.setImporte(rs.getDouble("importe"));

                lista.add(rec);
            }
            rs.close();
            pstm.close();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
            }
        }
        return lista;
    }

    public OperationResult registrarDepositoConResultado(String cuenta, double importe, String codEmp) {
        Connection cn = null;
        OperationResult result = new OperationResult();
        result.setTipoOperacion("DEPOSITO");
        result.setCuenta(cuenta);
        result.setImporte(importe);
        try {
            cn = AccesoDB.getConnection();
            cn.setAutoCommit(false);

            String sql = "SELECT c.dec_cuensaldo, c.int_cuencontmov, "
                       + "TRIM(CONCAT(cl.vch_clienombre, ' ', cl.vch_cliepaterno)) AS titular "
                       + "FROM cuenta c JOIN cliente cl ON c.chr_cliecodigo = cl.chr_cliecodigo "
                       + "WHERE c.chr_cuencodigo = ? AND c.vch_cuenestado = 'ACTIVO' FOR UPDATE";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            ResultSet rs = pstm.executeQuery();
            if (!rs.next()) {
                throw new SQLException("La cuenta " + cuenta + " no existe o no está activa.");
            }
            double saldo = rs.getDouble("dec_cuensaldo");
            int cont = rs.getInt("int_cuencontmov");
            String titular = rs.getString("titular");
            rs.close();
            pstm.close();

            saldo += importe;
            cont++;

            sql = "UPDATE cuenta SET dec_cuensaldo = ?, int_cuencontmov = ? WHERE chr_cuencodigo = ?";
            pstm = cn.prepareStatement(sql);
            pstm.setDouble(1, saldo);
            pstm.setInt(2, cont);
            pstm.setString(3, cuenta);
            pstm.executeUpdate();
            pstm.close();

            sql = "INSERT INTO movimiento(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte) "
                + "VALUES(?, ?, SYSDATE(), ?, '003', ?)";
            pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            pstm.setInt(2, cont);
            pstm.setString(3, codEmp);
            pstm.setDouble(4, importe);
            pstm.executeUpdate();
            pstm.close();

            cn.commit();

            result.setSuccess(true);
            result.setMessage("Depósito realizado exitosamente.");
            result.setNromov(cont);
            result.setNuevoSaldo(saldo);
            result.setTitular(titular);
            return result;
        } catch (Exception e) {
            try { if (cn != null) cn.rollback(); } catch (Exception ex) {}
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        } finally {
            try { if (cn != null) cn.close(); } catch (Exception e) {}
        }
    }

    public void registrarDeposito(String cuenta, double importe, String codEmp) {
        OperationResult res = registrarDepositoConResultado(cuenta, importe, codEmp);
        if (!res.isSuccess()) {
            throw new RuntimeException(res.getMessage());
        }
    }

    public OperationResult registrarRetiroConResultado(String cuenta, double importe, String codEmp) {
        Connection cn = null;
        OperationResult result = new OperationResult();
        result.setTipoOperacion("RETIRO");
        result.setCuenta(cuenta);
        result.setImporte(importe);
        try {
            cn = AccesoDB.getConnection();
            cn.setAutoCommit(false);

            String sql = "SELECT c.dec_cuensaldo, c.int_cuencontmov, "
                       + "TRIM(CONCAT(cl.vch_clienombre, ' ', cl.vch_cliepaterno)) AS titular "
                       + "FROM cuenta c JOIN cliente cl ON c.chr_cliecodigo = cl.chr_cliecodigo "
                       + "WHERE c.chr_cuencodigo = ? AND c.vch_cuenestado = 'ACTIVO' FOR UPDATE";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            ResultSet rs = pstm.executeQuery();
            if (!rs.next()) {
                throw new SQLException("La cuenta " + cuenta + " no existe o no está activa.");
            }
            double saldo = rs.getDouble("dec_cuensaldo");
            int cont = rs.getInt("int_cuencontmov");
            String titular = rs.getString("titular");
            rs.close();
            pstm.close();

            if (saldo < importe) {
                throw new SQLException("Saldo insuficiente. Saldo disponible: $" + String.format("%.2f", saldo));
            }

            saldo -= importe;
            cont++;

            sql = "UPDATE cuenta SET dec_cuensaldo = ?, int_cuencontmov = ? WHERE chr_cuencodigo = ?";
            pstm = cn.prepareStatement(sql);
            pstm.setDouble(1, saldo);
            pstm.setInt(2, cont);
            pstm.setString(3, cuenta);
            pstm.executeUpdate();
            pstm.close();

            sql = "INSERT INTO movimiento(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte) "
                + "VALUES (?, ?, SYSDATE(), ?, '004', ?)";
            pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            pstm.setInt(2, cont);
            pstm.setString(3, codEmp);
            pstm.setDouble(4, importe);
            pstm.executeUpdate();
            pstm.close();

            cn.commit();

            result.setSuccess(true);
            result.setMessage("Retiro realizado exitosamente.");
            result.setNromov(cont);
            result.setNuevoSaldo(saldo);
            result.setTitular(titular);
            return result;
        } catch (Exception e) {
            try { if (cn != null) cn.rollback(); } catch (Exception ex) {}
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        } finally {
            try { if (cn != null) cn.close(); } catch (Exception e) {}
        }
    }

    public void registrarRetiro(String cuenta, double importe, String codEmp) {
        OperationResult res = registrarRetiroConResultado(cuenta, importe, codEmp);
        if (!res.isSuccess()) {
            throw new RuntimeException(res.getMessage());
        }
    }

    public OperationResult registrarTransferenciaConResultado(String cuentaOrigen, String cuentaDestino, double importe, String codEmp) {
        Connection cn = null;
        OperationResult result = new OperationResult();
        result.setTipoOperacion("TRANSFERENCIA");
        result.setCuenta(cuentaOrigen);
        result.setCuentaDestino(cuentaDestino);
        result.setImporte(importe);
        try {
            cn = AccesoDB.getConnection();
            cn.setAutoCommit(false);

            // Validar cuenta de origen
            String sql = "SELECT c.dec_cuensaldo, c.int_cuencontmov, c.chr_monecodigo, "
                       + "TRIM(CONCAT(cl.vch_clienombre, ' ', cl.vch_cliepaterno)) AS titular "
                       + "FROM cuenta c JOIN cliente cl ON c.chr_cliecodigo = cl.chr_cliecodigo "
                       + "WHERE c.chr_cuencodigo = ? AND c.vch_cuenestado = 'ACTIVO' FOR UPDATE";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuentaOrigen);
            ResultSet rs = pstm.executeQuery();
            if (!rs.next()) {
                throw new SQLException("La cuenta de origen " + cuentaOrigen + " no existe o no está activa.");
            }
            double saldoOrigen = rs.getDouble("dec_cuensaldo");
            int contOrigen = rs.getInt("int_cuencontmov");
            String moneOrigen = rs.getString("chr_monecodigo");
            String titularOrigen = rs.getString("titular");
            rs.close();
            pstm.close();

            if (saldoOrigen < importe) {
                throw new SQLException("Saldo insuficiente en cuenta origen. Saldo disponible: $" + String.format("%.2f", saldoOrigen));
            }

            // Validar cuenta de destino
            sql = "SELECT c.int_cuencontmov, c.chr_monecodigo, "
                + "TRIM(CONCAT(cl.vch_clienombre, ' ', cl.vch_cliepaterno)) AS titular "
                + "FROM cuenta c JOIN cliente cl ON c.chr_cliecodigo = cl.chr_cliecodigo "
                + "WHERE c.chr_cuencodigo = ? AND c.vch_cuenestado = 'ACTIVO' FOR UPDATE";
            pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuentaDestino);
            rs = pstm.executeQuery();
            if (!rs.next()) {
                throw new SQLException("La cuenta de destino " + cuentaDestino + " no existe o no está activa.");
            }
            int contDestino = rs.getInt("int_cuencontmov");
            String moneDestino = rs.getString("chr_monecodigo");
            String titularDestino = rs.getString("titular");
            rs.close();
            pstm.close();

            // Tasa de cambio simulada: 1 USD = 3.75 Soles
            double tipoCambioUSD_PEN = 3.75;
            double importeDestino = importe;

            if ("01".equals(moneOrigen) && "02".equals(moneDestino)) {
                // De Soles (01) a Dolares (02)
                importeDestino = Math.round((importe / tipoCambioUSD_PEN) * 100.0) / 100.0;
            } else if ("02".equals(moneOrigen) && "01".equals(moneDestino)) {
                // De Dolares (02) a Soles (01)
                importeDestino = Math.round((importe * tipoCambioUSD_PEN) * 100.0) / 100.0;
            }

            saldoOrigen -= importe;
            contOrigen++;
            sql = "UPDATE cuenta SET dec_cuensaldo = ?, int_cuencontmov = ? WHERE chr_cuencodigo = ?";
            pstm = cn.prepareStatement(sql);
            pstm.setDouble(1, saldoOrigen);
            pstm.setInt(2, contOrigen);
            pstm.setString(3, cuentaOrigen);
            pstm.executeUpdate();
            pstm.close();

            contDestino++;
            sql = "UPDATE cuenta SET dec_cuensaldo = dec_cuensaldo + ?, int_cuencontmov = ? WHERE chr_cuencodigo = ?";
            pstm = cn.prepareStatement(sql);
            pstm.setDouble(1, importeDestino);
            pstm.setInt(2, contDestino);
            pstm.setString(3, cuentaDestino);
            pstm.executeUpdate();
            pstm.close();

            // Registrar movimiento origen (009 = Transferencia Salida)
            sql = "INSERT INTO movimiento(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte, chr_cuenreferencia) "
                + "VALUES (?, ?, SYSDATE(), ?, '009', ?, ?)";
            pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuentaOrigen);
            pstm.setInt(2, contOrigen);
            pstm.setString(3, codEmp);
            pstm.setDouble(4, importe);
            pstm.setString(5, cuentaDestino);
            pstm.executeUpdate();
            pstm.close();

            // Registrar movimiento destino (008 = Transferencia Ingreso)
            sql = "INSERT INTO movimiento(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte, chr_cuenreferencia) "
                + "VALUES (?, ?, SYSDATE(), ?, '008', ?, ?)";
            pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuentaDestino);
            pstm.setInt(2, contDestino);
            pstm.setString(3, codEmp);
            pstm.setDouble(4, importeDestino);
            pstm.setString(5, cuentaOrigen);
            pstm.executeUpdate();
            pstm.close();

            cn.commit();

            result.setSuccess(true);
            result.setMessage("Transferencia de $" + String.format("%.2f", importe) + " a " + titularDestino + " (" + cuentaDestino + ") realizada exitosamente.");
            result.setNromov(contOrigen);
            result.setNuevoSaldo(saldoOrigen);
            result.setTitular(titularOrigen);
            return result;
        } catch (Exception e) {
            try { if (cn != null) cn.rollback(); } catch (Exception ex) {}
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        } finally {
            try { if (cn != null) cn.close(); } catch (Exception e) {}
        }
    }

    public void registrarTransferencia(String cuentaOrigen, String cuentaDestino, double importe, String codEmp) {
        OperationResult res = registrarTransferenciaConResultado(cuentaOrigen, cuentaDestino, importe, codEmp);
        if (!res.isSuccess()) {
            throw new RuntimeException(res.getMessage());
        }
    }
}
