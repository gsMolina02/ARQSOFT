namespace ec.edu.monster.Modelos;

/// <summary>
/// Códigos del catálogo tipo_movimiento que genera el sistema.
/// </summary>
public static class TipoMovimiento
{
    public const string Apertura = "001";
    public const string Deposito = "003";
    public const string Retiro = "004";
    /// <summary>Transferencia recibida (INGRESO en la cuenta de destino).</summary>
    public const string TransferenciaRecibida = "008";
    /// <summary>Transferencia enviada (SALIDA en la cuenta de origen).</summary>
    public const string TransferenciaEnviada = "009";
}
