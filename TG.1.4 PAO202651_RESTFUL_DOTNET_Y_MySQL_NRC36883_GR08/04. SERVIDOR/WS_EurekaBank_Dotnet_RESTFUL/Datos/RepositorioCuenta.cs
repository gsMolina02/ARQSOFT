using System.Data.Common;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Datos;

public interface IRepositorioCuenta
{
    Task<Cuenta?> BuscarAsync(IUnidadTrabajo unidad, string numero);

    /// <summary>Todas las cuentas, en cualquier estado, ordenadas por número.</summary>
    Task<IReadOnlyList<Cuenta>> ListarAsync(IUnidadTrabajo unidad);

    /// <summary>Lee la cuenta y bloquea su fila hasta el fin de la transacción (SELECT ... FOR UPDATE).</summary>
    Task<Cuenta?> BloquearAsync(IUnidadTrabajo unidad, string numero);

    Task ActualizarAsync(IUnidadTrabajo unidad, Cuenta cuenta);
}

public class RepositorioCuenta : IRepositorioCuenta
{
    private const string Seleccion = """
        select c.numero_cuenta, c.saldo, c.fecha_apertura, c.estado, c.contador_movimientos,
               cl.codigo_cliente, cl.cedula, concat(cl.nombres, ' ', cl.apellidos) as cliente,
               m.codigo_moneda, m.nombre as moneda, m.simbolo,
               s.codigo_sucursal, s.nombre as sucursal
        from cuenta c
        join cliente cl on cl.codigo_cliente = c.codigo_cliente
        join moneda m on m.codigo_moneda = c.codigo_moneda
        join sucursal s on s.codigo_sucursal = c.codigo_sucursal
        """;

    private const string Consulta = Seleccion + "\nwhere c.numero_cuenta = @numero";

    public Task<Cuenta?> BuscarAsync(IUnidadTrabajo unidad, string numero) => LeerAsync(unidad, Consulta, numero);

    public async Task<IReadOnlyList<Cuenta>> ListarAsync(IUnidadTrabajo unidad)
    {
        await using DbCommand comando = ComandoSql.Crear(unidad, Seleccion + "\norder by c.numero_cuenta");
        await using DbDataReader lector = await comando.ExecuteReaderAsync();
        List<Cuenta> lista = [];
        while (await lector.ReadAsync())
        {
            lista.Add(Mapear(lector));
        }
        return lista;
    }

    // "of c" bloquea solo la fila de la cuenta: no la del cliente, la sucursal ni la moneda USD,
    // que comparten muchas cuentas y harían esperar a operaciones que no tienen relación entre sí
    public Task<Cuenta?> BloquearAsync(IUnidadTrabajo unidad, string numero) =>
            LeerAsync(unidad, Consulta + "\nfor update of c", numero);

    public async Task ActualizarAsync(IUnidadTrabajo unidad, Cuenta cuenta)
    {
        await using DbCommand comando = ComandoSql.Crear(unidad,
                "update cuenta set saldo = @saldo, contador_movimientos = @contador where numero_cuenta = @numero",
                ("@saldo", cuenta.Saldo), ("@contador", cuenta.ContadorMovimientos), ("@numero", cuenta.Numero));
        await comando.ExecuteNonQueryAsync();
    }

    private static async Task<Cuenta?> LeerAsync(IUnidadTrabajo unidad, string sql, string numero)
    {
        await using DbCommand comando = ComandoSql.Crear(unidad, sql, ("@numero", numero));
        await using DbDataReader lector = await comando.ExecuteReaderAsync();
        return await lector.ReadAsync() ? Mapear(lector) : null;
    }

    private static Cuenta Mapear(DbDataReader lector) => new()
    {
        Numero = lector.Texto("numero_cuenta"),
        Saldo = lector.Decimal("saldo"),
        FechaApertura = lector.Fecha("fecha_apertura"),
        Estado = lector.Texto("estado"),
        ContadorMovimientos = lector.Entero("contador_movimientos"),
        CodigoCliente = lector.Texto("codigo_cliente"),
        CedulaCliente = lector.Texto("cedula"),
        NombreCliente = lector.Texto("cliente"),
        CodigoMoneda = lector.Texto("codigo_moneda"),
        NombreMoneda = lector.Texto("moneda"),
        SimboloMoneda = lector.Texto("simbolo"),
        CodigoSucursal = lector.Texto("codigo_sucursal"),
        NombreSucursal = lector.Texto("sucursal")
    };
}
