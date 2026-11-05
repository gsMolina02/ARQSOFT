namespace ec.edu.monster.Util;

public static class RelojUtil
{
    /// <summary>
    /// Fecha y hora local sin fracciones de segundo: es lo que guardan las columnas datetime de MySQL,
    /// así la hora devuelta al cliente coincide con la registrada.
    /// </summary>
    public static DateTime AhoraLocal(this TimeProvider reloj)
    {
        DateTime ahora = reloj.GetLocalNow().DateTime;
        return new DateTime(ahora.Ticks - ahora.Ticks % TimeSpan.TicksPerSecond, ahora.Kind);
    }
}
