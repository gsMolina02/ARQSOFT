namespace ec.edu.monster.Modelos.Dto;

/// <summary>
/// Respuesta homogénea de todas las operaciones, de éxito o de error: {"codigo": 200, "mensaje": "..."}.
/// </summary>
public class Respuesta
{
    public int Codigo { get; init; }
    public string Mensaje { get; init; } = string.Empty;
}

/// <summary>
/// Respuesta del inicio de sesión: el token se envía después en "Authorization: Bearer &lt;token&gt;".
/// </summary>
public class RespuestaSesion : Respuesta
{
    public string Token { get; init; } = string.Empty;
    public string Usuario { get; init; } = string.Empty;
    public string Empleado { get; init; } = string.Empty;
    public int MinutosVigencia { get; init; }
}

/// <summary>
/// Resultado de un depósito, retiro o transferencia: saldo nuevo y número del movimiento registrado
/// (en la transferencia, de la cuenta de origen).
/// </summary>
public class RespuestaTransaccion : Respuesta
{
    public decimal Saldo { get; init; }
    public int NumeroMovimiento { get; init; }
}

/// <summary>
/// Datos de la cuenta y sus movimientos ordenados por número.
/// </summary>
public class RespuestaMovimientos : Respuesta
{
    public CuentaDto? Cuenta { get; init; }
    public IReadOnlyList<MovimientoDto> Movimientos { get; init; } = [];
}

/// <summary>
/// Cuentas que los clientes muestran en la lista desplegable del número de cuenta.
/// </summary>
public class RespuestaCuentas : Respuesta
{
    public IReadOnlyList<CuentaDto> Cuentas { get; init; } = [];
}

public class CuentaDto
{
    public string Numero { get; init; } = string.Empty;
    public string Cliente { get; init; } = string.Empty;
    public string Cedula { get; init; } = string.Empty;
    public string Moneda { get; init; } = string.Empty;
    public string SimboloMoneda { get; init; } = string.Empty;
    public string Sucursal { get; init; } = string.Empty;
    public string Estado { get; init; } = string.Empty;
    public decimal Saldo { get; init; }
    public DateOnly FechaApertura { get; init; }
    public int ContadorMovimientos { get; init; }
}

public class MovimientoDto
{
    public int Numero { get; init; }
    public DateTime FechaHora { get; init; }
    public string CodigoTipo { get; init; } = string.Empty;
    public string Tipo { get; init; } = string.Empty;
    public string Accion { get; init; } = string.Empty;
    public decimal Importe { get; init; }
    public string? CuentaReferencia { get; init; }
    public string Empleado { get; init; } = string.Empty;
}
