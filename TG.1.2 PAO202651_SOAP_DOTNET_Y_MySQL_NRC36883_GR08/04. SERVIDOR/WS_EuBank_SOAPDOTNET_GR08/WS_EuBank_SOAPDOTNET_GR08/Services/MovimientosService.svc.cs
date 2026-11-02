using System;
using System.Collections.Generic;
using System.Linq;
using MySql.Data.MySqlClient;
using WS_EuBank_SOAPDOTNET_GR08.DB;

namespace WS_EuBank_SOAPDOTNET_GR08
{
    public class MovimientosService : IMovimientosService
    {
        private const string EmpleadoDefault = "0001";
        private const string TipoDeposito = "003";
        private const string TipoRetiro = "004";
        private const string TipoTransferenciaSalida = "009";
        private const string TipoTransferenciaIngreso = "008";

        public List<CuentaData> GetDatosCuentas()
        {
            var lista = new List<CuentaData>();
            const string sql = @"
                SELECT c.chr_cuencodigo,
                       c.chr_monecodigo,
                       c.dec_cuensaldo,
                       c.vch_cuenestado,
                       CONCAT(cl.vch_clienombre, ' ', cl.vch_cliepaterno, ' ', cl.vch_cliematerno) AS nombre_cliente,
                       cl.vch_clieemail,
                       cl.vch_clietelefono
                FROM cuenta c
                INNER JOIN cliente cl ON c.chr_cliecodigo = cl.chr_cliecodigo
                ORDER BY c.chr_cuencodigo";

            using (var cn = ConexionBD.ObtenerConexion())
            using (var cmd = new MySqlCommand(sql, cn))
            using (var rd = cmd.ExecuteReader())
            {
                while (rd.Read())
                {
                    lista.Add(new CuentaData
                    {
                        Codigo = rd.GetString("chr_cuencodigo"),
                        Moneda = rd.GetString("chr_monecodigo"),
                        Saldo = rd.GetDecimal("dec_cuensaldo"),
                        Estado = rd.GetString("vch_cuenestado"),
                        NombreCliente = rd.GetString("nombre_cliente"),
                        EmailCliente = rd.IsDBNull(rd.GetOrdinal("vch_clieemail")) ? "" : rd.GetString("vch_clieemail"),
                        TelefonoCliente = rd.IsDBNull(rd.GetOrdinal("vch_clietelefono")) ? "" : rd.GetString("vch_clietelefono")
                    });
                }
            }
            return lista;
        }

        public List<MovimientoData> GetMovimientos(string cuenta = null)
        {
            var rawItems = new List<Tuple<MovimientoData, decimal>>();
            string sql = @"
                SELECT m.int_movinumero,
                       m.dtt_movifecha,
                       tm.chr_tipocodigo,
                       tm.vch_tipodescripcion,
                       tm.vch_tipoaccion,
                       m.dec_moviimporte,
                       m.chr_cuenreferencia,
                       m.chr_cuencodigo,
                       c.dec_cuensaldo,
                       CONCAT(cl.vch_clienombre, ' ', cl.vch_cliepaterno, ' ', cl.vch_cliematerno) AS nombre_cliente
                FROM movimiento m
                INNER JOIN cuenta c ON m.chr_cuencodigo = c.chr_cuencodigo
                INNER JOIN cliente cl ON c.chr_cliecodigo = cl.chr_cliecodigo
                INNER JOIN tipomovimiento tm ON m.chr_tipocodigo = tm.chr_tipocodigo";

            bool filtrar = !string.IsNullOrWhiteSpace(cuenta) && cuenta != "?";
            if (filtrar)
            {
                sql += " WHERE m.chr_cuencodigo = @cuenta";
            }
            sql += " ORDER BY m.dtt_movifecha ASC, m.int_movinumero ASC";

            using (var cn = ConexionBD.ObtenerConexion())
            using (var cmd = new MySqlCommand(sql, cn))
            {
                if (filtrar)
                {
                    cmd.Parameters.AddWithValue("@cuenta", cuenta.Trim());
                }
                using (var rd = cmd.ExecuteReader())
                {
                    while (rd.Read())
                    {
                        string tipoCod = rd.GetString("chr_tipocodigo");
                        string tipoDesc = rd.GetString("vch_tipodescripcion") +
                            (tipoCod == "008" ? " Entrada" : tipoCod == "009" ? " Salida" : "");
                        string accion = rd.GetString("vch_tipoaccion");
                        decimal imp = rd.GetDecimal("dec_moviimporte") * (accion == "INGRESO" ? 1m : -1m);

                        var md = new MovimientoData
                        {
                            Numero = rd.GetInt32("int_movinumero"),
                            Fecha = rd.GetDateTime("dtt_movifecha"),
                            Tipo = tipoDesc,
                            Importe = imp,
                            Referencia = rd.IsDBNull(rd.GetOrdinal("chr_cuenreferencia")) ? null : rd.GetString("chr_cuenreferencia"),
                            CodigoCuenta = rd.GetString("chr_cuencodigo"),
                            NombreCliente = rd.GetString("nombre_cliente")
                        };
                        rawItems.Add(Tuple.Create(md, rd.GetDecimal("dec_cuensaldo")));
                    }
                }
            }

            foreach (var grp in rawItems.GroupBy(x => x.Item1.CodigoCuenta))
            {
                var ordered = grp.OrderBy(x => x.Item1.Fecha).ThenBy(x => x.Item1.Numero).ToList();
                decimal saldoActual = ordered.Last().Item2 - ordered.Sum(x => x.Item1.Importe);
                foreach (var item in ordered)
                {
                    saldoActual += item.Item1.Importe;
                    item.Item1.SaldoActual = saldoActual;
                }
            }

            return rawItems
                .Select(x => x.Item1)
                .OrderByDescending(m => m.Fecha)
                .ThenByDescending(m => m.Numero)
                .ToList();
        }

        public ResultadoOperacion ProcesarMovimiento(string tipo, string cuentaOrigen, string cuentaDestino, decimal importe)
        {
            if (importe <= 0)
                return new ResultadoOperacion(-1, "El monto debe ser mayor a cero.");

            switch ((tipo ?? "").Trim().ToUpperInvariant())
            {
                case "DEPOSITO":
                    return Deposito(cuentaOrigen, importe)
                        ? new ResultadoOperacion(1, "Depósito realizado exitosamente.")
                        : new ResultadoOperacion(-1, "Error al realizar el depósito.");
                case "RETIRO":
                    return Retiro(cuentaOrigen, importe)
                        ? new ResultadoOperacion(1, "Retiro realizado exitosamente.")
                        : new ResultadoOperacion(-1, "Error al realizar el retiro. Verifique saldo suficiente.");
                case "TRANSFERENCIA":
                    if (string.IsNullOrWhiteSpace(cuentaDestino))
                        return new ResultadoOperacion(-1, "Cuenta destino requerida para transferencia.");
                    string mensajeTransferencia;
                    return Transferencia(cuentaOrigen, cuentaDestino, importe, out mensajeTransferencia)
                        ? new ResultadoOperacion(1, mensajeTransferencia)
                        : new ResultadoOperacion(-1, mensajeTransferencia ?? "Error al realizar la transferencia. Verifique cuentas y saldo.");
                default:
                    return new ResultadoOperacion(-1, "Tipo de movimiento no válido.");
            }
        }

        private bool Deposito(string cuenta, decimal monto)
        {
            using (var cn = ConexionBD.ObtenerConexion())
            using (var tx = cn.BeginTransaction())
            {
                try
                {
                    decimal saldo;
                    int cont;
                    const string qLock = "SELECT dec_cuensaldo, int_cuencontmov FROM cuenta WHERE chr_cuencodigo = @c AND vch_cuenestado = 'ACTIVO' FOR UPDATE";
                    using (var cmd = new MySqlCommand(qLock, cn, tx))
                    {
                        cmd.Parameters.AddWithValue("@c", cuenta);
                        using (var rd = cmd.ExecuteReader())
                        {
                            if (!rd.Read()) return false;
                            saldo = rd.GetDecimal("dec_cuensaldo");
                            cont = rd.GetInt32("int_cuencontmov");
                        }
                    }

                    int nextNum = cont + 1;
                    decimal nuevoSaldo = saldo + monto;

                    const string qUpd = "UPDATE cuenta SET dec_cuensaldo = @s, int_cuencontmov = @n WHERE chr_cuencodigo = @c";
                    using (var cmd = new MySqlCommand(qUpd, cn, tx))
                    {
                        cmd.Parameters.AddWithValue("@s", nuevoSaldo);
                        cmd.Parameters.AddWithValue("@n", nextNum);
                        cmd.Parameters.AddWithValue("@c", cuenta);
                        cmd.ExecuteNonQuery();
                    }

                    const string qIns = @"INSERT INTO movimiento(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte, chr_cuenreferencia)
                                          VALUES (@c, @n, NOW(), @e, @t, @m, NULL)";
                    using (var cmd = new MySqlCommand(qIns, cn, tx))
                    {
                        cmd.Parameters.AddWithValue("@c", cuenta);
                        cmd.Parameters.AddWithValue("@n", nextNum);
                        cmd.Parameters.AddWithValue("@e", EmpleadoDefault);
                        cmd.Parameters.AddWithValue("@t", TipoDeposito);
                        cmd.Parameters.AddWithValue("@m", monto);
                        cmd.ExecuteNonQuery();
                    }

                    tx.Commit();
                    return true;
                }
                catch
                {
                    tx.Rollback();
                    return false;
                }
            }
        }

        private bool Retiro(string cuenta, decimal monto)
        {
            using (var cn = ConexionBD.ObtenerConexion())
            using (var tx = cn.BeginTransaction())
            {
                try
                {
                    decimal saldo;
                    int cont;
                    const string qLock = "SELECT dec_cuensaldo, int_cuencontmov FROM cuenta WHERE chr_cuencodigo = @c AND vch_cuenestado = 'ACTIVO' FOR UPDATE";
                    using (var cmd = new MySqlCommand(qLock, cn, tx))
                    {
                        cmd.Parameters.AddWithValue("@c", cuenta);
                        using (var rd = cmd.ExecuteReader())
                        {
                            if (!rd.Read()) return false;
                            saldo = rd.GetDecimal("dec_cuensaldo");
                            cont = rd.GetInt32("int_cuencontmov");
                        }
                    }

                    if (saldo < monto) return false;

                    int nextNum = cont + 1;
                    decimal nuevoSaldo = saldo - monto;

                    const string qUpd = "UPDATE cuenta SET dec_cuensaldo = @s, int_cuencontmov = @n WHERE chr_cuencodigo = @c";
                    using (var cmd = new MySqlCommand(qUpd, cn, tx))
                    {
                        cmd.Parameters.AddWithValue("@s", nuevoSaldo);
                        cmd.Parameters.AddWithValue("@n", nextNum);
                        cmd.Parameters.AddWithValue("@c", cuenta);
                        cmd.ExecuteNonQuery();
                    }

                    const string qIns = @"INSERT INTO movimiento(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte, chr_cuenreferencia)
                                          VALUES (@c, @n, NOW(), @e, @t, @m, NULL)";
                    using (var cmd = new MySqlCommand(qIns, cn, tx))
                    {
                        cmd.Parameters.AddWithValue("@c", cuenta);
                        cmd.Parameters.AddWithValue("@n", nextNum);
                        cmd.Parameters.AddWithValue("@e", EmpleadoDefault);
                        cmd.Parameters.AddWithValue("@t", TipoRetiro);
                        cmd.Parameters.AddWithValue("@m", monto);
                        cmd.ExecuteNonQuery();
                    }

                    tx.Commit();
                    return true;
                }
                catch
                {
                    tx.Rollback();
                    return false;
                }
            }
        }

        private bool Transferencia(string cuentaOrigen, string cuentaDestino, decimal monto, out string mensaje)
        {
            mensaje = null;
            if (cuentaOrigen == cuentaDestino)
            {
                mensaje = "La cuenta de origen y destino no pueden ser iguales.";
                return false;
            }

            using (var cn = ConexionBD.ObtenerConexion())
            using (var tx = cn.BeginTransaction())
            {
                try
                {
                    decimal saldoOri, saldoDest;
                    int contOri, contDest;
                    string moneOri, moneDest;
                    const string qLock = "SELECT dec_cuensaldo, int_cuencontmov, chr_monecodigo FROM cuenta WHERE chr_cuencodigo = @c AND vch_cuenestado = 'ACTIVO' FOR UPDATE";

                    using (var cmd = new MySqlCommand(qLock, cn, tx))
                    {
                        cmd.Parameters.AddWithValue("@c", cuentaOrigen);
                        using (var rd = cmd.ExecuteReader())
                        {
                            if (!rd.Read())
                            {
                                mensaje = "Cuenta origen no encontrada o inactiva.";
                                return false;
                            }
                            saldoOri = rd.GetDecimal("dec_cuensaldo");
                            contOri = rd.GetInt32("int_cuencontmov");
                            moneOri = rd.GetString("chr_monecodigo");
                        }
                    }
                    if (saldoOri < monto)
                    {
                        mensaje = "Saldo insuficiente en la cuenta de origen.";
                        return false;
                    }

                    using (var cmd = new MySqlCommand(qLock, cn, tx))
                    {
                        cmd.Parameters.AddWithValue("@c", cuentaDestino);
                        using (var rd = cmd.ExecuteReader())
                        {
                            if (!rd.Read())
                            {
                                mensaje = "Cuenta destino no encontrada o inactiva.";
                                return false;
                            }
                            saldoDest = rd.GetDecimal("dec_cuensaldo");
                            contDest = rd.GetInt32("int_cuencontmov");
                            moneDest = rd.GetString("chr_monecodigo");
                        }
                    }

                    // Tasa de cambio simulada: 1 USD = 3.75 Soles
                    const decimal TipoCambioUSD_PEN = 3.75m;
                    decimal montoDestino = monto;
                    string detalleConversion = "";

                    if (moneOri == "01" && moneDest == "02")
                    {
                        // De Soles (01) a Dólares (02)
                        montoDestino = Math.Round(monto / TipoCambioUSD_PEN, 2);
                        detalleConversion = $" (Conversión: S/ {monto:F2} a $ {montoDestino:F2} | T.C: {TipoCambioUSD_PEN})";
                    }
                    else if (moneOri == "02" && moneDest == "01")
                    {
                        // De Dólares (02) a Soles (01)
                        montoDestino = Math.Round(monto * TipoCambioUSD_PEN, 2);
                        detalleConversion = $" (Conversión: $ {monto:F2} a S/ {montoDestino:F2} | T.C: {TipoCambioUSD_PEN})";
                    }

                    int nextOri = contOri + 1;
                    int nextDest = contDest + 1;

                    const string qUpd = "UPDATE cuenta SET dec_cuensaldo = @s, int_cuencontmov = @n WHERE chr_cuencodigo = @c";
                    using (var cmd = new MySqlCommand(qUpd, cn, tx))
                    {
                        cmd.Parameters.AddWithValue("@s", saldoOri - monto);
                        cmd.Parameters.AddWithValue("@n", nextOri);
                        cmd.Parameters.AddWithValue("@c", cuentaOrigen);
                        cmd.ExecuteNonQuery();
                    }
                    using (var cmd = new MySqlCommand(qUpd, cn, tx))
                    {
                        cmd.Parameters.AddWithValue("@s", saldoDest + montoDestino);
                        cmd.Parameters.AddWithValue("@n", nextDest);
                        cmd.Parameters.AddWithValue("@c", cuentaDestino);
                        cmd.ExecuteNonQuery();
                    }

                    const string qIns = @"INSERT INTO movimiento(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte, chr_cuenreferencia)
                                          VALUES (@c, @n, NOW(), @e, @t, @m, @r)";
                    using (var cmd = new MySqlCommand(qIns, cn, tx))
                    {
                        cmd.Parameters.AddWithValue("@c", cuentaOrigen);
                        cmd.Parameters.AddWithValue("@n", nextOri);
                        cmd.Parameters.AddWithValue("@e", EmpleadoDefault);
                        cmd.Parameters.AddWithValue("@t", TipoTransferenciaSalida);
                        cmd.Parameters.AddWithValue("@m", monto);
                        cmd.Parameters.AddWithValue("@r", cuentaDestino);
                        cmd.ExecuteNonQuery();
                    }
                    using (var cmd = new MySqlCommand(qIns, cn, tx))
                    {
                        cmd.Parameters.AddWithValue("@c", cuentaDestino);
                        cmd.Parameters.AddWithValue("@n", nextDest);
                        cmd.Parameters.AddWithValue("@e", EmpleadoDefault);
                        cmd.Parameters.AddWithValue("@t", TipoTransferenciaIngreso);
                        cmd.Parameters.AddWithValue("@m", montoDestino);
                        cmd.Parameters.AddWithValue("@r", cuentaOrigen);
                        cmd.ExecuteNonQuery();
                    }

                    tx.Commit();
                    mensaje = $"Transferencia realizada exitosamente.{detalleConversion}";
                    return true;
                }
                catch (Exception ex)
                {
                    tx.Rollback();
                    mensaje = $"Error en la transacción: {ex.Message}";
                    return false;
                }
            }
        }

        public List<string> GetTiposMovimiento()
        {
            return new List<string> { "DEPOSITO", "RETIRO", "TRANSFERENCIA" };
        }
    }
}
