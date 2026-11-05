using System.Data.Common;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Datos;

public interface IRepositorioMovimiento
{
    /// <summary>Movimientos de la cuenta con la descripción y acción de su tipo, ordenados por número.</summary>
    Task<IReadOnlyList<Movimiento>> ListarAsync(IUnidadTrabajo unidad, string numeroCuenta);

    Task RegistrarAsync(IUnidadTrabajo unidad, Movimiento movimiento);
}

public class RepositorioMovimiento : IRepositorioMovimiento
{
    public async Task<IReadOnlyList<Movimiento>> ListarAsync(IUnidadTrabajo unidad, string numeroCuenta)
    {
        await using DbCommand comando = ComandoSql.Crear(unidad, """
            select m.numero_cuenta, m.numero_movimiento, m.codigo_tipo, t.descripcion, t.accion,
                   m.codigo_empleado, m.cuenta_referencia, m.fecha_hora, m.importe
            from movimiento m
            join tipo_movimiento t on t.codigo_tipo = m.codigo_tipo
            where m.numero_cuenta = @numero
            order by m.numero_movimiento
            """, ("@numero", numeroCuenta));
        await using DbDataReader lector = await comando.ExecuteReaderAsync();

        var movimientos = new List<Movimiento>();
        while (await lector.ReadAsync())
        {
            movimientos.Add(new Movimiento
            {
                NumeroCuenta = lector.Texto("numero_cuenta"),
                Numero = lector.Entero("numero_movimiento"),
                CodigoTipo = lector.Texto("codigo_tipo"),
                DescripcionTipo = lector.Texto("descripcion"),
                Accion = lector.Texto("accion"),
                CodigoEmpleado = lector.Texto("codigo_empleado"),
                CuentaReferencia = lector.TextoONulo("cuenta_referencia"),
                FechaHora = lector.Fecha("fecha_hora"),
                Importe = lector.Decimal("importe")
            });
        }
        return movimientos;
    }

    public async Task RegistrarAsync(IUnidadTrabajo unidad, Movimiento movimiento)
    {
        await using DbCommand comando = ComandoSql.Crear(unidad, """
            insert into movimiento (numero_cuenta, numero_movimiento, codigo_tipo, codigo_empleado,
                                    cuenta_referencia, fecha_hora, importe)
            values (@cuenta, @numero, @tipo, @empleado, @referencia, @fecha, @importe)
            """,
            ("@cuenta", movimiento.NumeroCuenta), ("@numero", movimiento.Numero), ("@tipo", movimiento.CodigoTipo),
            ("@empleado", movimiento.CodigoEmpleado), ("@referencia", movimiento.CuentaReferencia),
            ("@fecha", movimiento.FechaHora), ("@importe", movimiento.Importe));
        await comando.ExecuteNonQueryAsync();
    }
}
