using ec.edu.monster.Cliente.Dto;

namespace ec.edu.monster.Models;

/// <summary>Operaciones de la pantalla principal (pestañas).</summary>
public enum TipoOperacion
{
    Movimientos,
    Deposito,
    Retiro,
    Transferencia
}

public static class TipoOperacionExtensiones
{
    public static string NombreVisible(this TipoOperacion operacion) => operacion switch
    {
        TipoOperacion.Deposito => "Depósito",
        TipoOperacion.Retiro => "Retiro",
        TipoOperacion.Transferencia => "Transferencia",
        _ => "Movimientos"
    };
}

/// <summary>
/// Datos de la pantalla de operaciones: el formulario de la operación elegida, el comprobante y la cuenta
/// consultada (datos y movimientos).
/// </summary>
public class OperacionesModelo
{
    public TipoOperacion Operacion { get; set; }
    public string? Cuenta { get; set; }
    public string? CuentaDestino { get; set; }
    public string? Importe { get; set; }

    // Datos solo para mostrar en la vista
    public string? Error { get; set; }
    /// <summary>Campo que se resalta en rojo: "cuenta", "destino" o "importe".</summary>
    public string? CampoConError { get; set; }
    /// <summary>true si el servidor no respondió: se ofrece cerrar sesión y configurar el servidor.</summary>
    public bool SinConexion { get; set; }
    public string? ResultadoTitulo { get; set; }
    public string? ResultadoValor { get; set; }
    public RespuestaMovimientos? Consulta { get; set; }
    /// <summary>Cuentas de la lista desplegable de los campos de cuenta (vacía si el servidor no respondió).</summary>
    public IReadOnlyList<CuentaDto> Cuentas { get; set; } = [];
}
