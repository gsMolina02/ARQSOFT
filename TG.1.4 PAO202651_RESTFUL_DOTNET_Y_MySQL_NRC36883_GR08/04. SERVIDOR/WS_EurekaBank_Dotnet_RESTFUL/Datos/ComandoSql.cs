using System.Data.Common;

namespace ec.edu.monster.Datos;

/// <summary>
/// Crea comandos parametrizados sobre la conexión y transacción de una unidad de trabajo.
/// Los valores siempre viajan como parámetros: nunca se concatenan en el SQL.
/// </summary>
internal static class ComandoSql
{
    public static DbCommand Crear(IUnidadTrabajo unidad, string sql, params (string Nombre, object? Valor)[] parametros)
    {
        DbCommand comando = unidad.Conexion.CreateCommand();
        comando.CommandText = sql;
        comando.Transaction = unidad.Transaccion;
        foreach ((string nombre, object? valor) in parametros)
        {
            DbParameter parametro = comando.CreateParameter();
            parametro.ParameterName = nombre;
            parametro.Value = valor ?? DBNull.Value;
            comando.Parameters.Add(parametro);
        }
        return comando;
    }

    public static string Texto(this DbDataReader lector, string columna) => lector.GetString(lector.GetOrdinal(columna));

    public static string? TextoONulo(this DbDataReader lector, string columna)
    {
        int posicion = lector.GetOrdinal(columna);
        return lector.IsDBNull(posicion) ? null : lector.GetString(posicion);
    }

    public static decimal Decimal(this DbDataReader lector, string columna) => lector.GetDecimal(lector.GetOrdinal(columna));

    public static int Entero(this DbDataReader lector, string columna) => lector.GetInt32(lector.GetOrdinal(columna));

    public static DateTime Fecha(this DbDataReader lector, string columna) => lector.GetDateTime(lector.GetOrdinal(columna));
}
