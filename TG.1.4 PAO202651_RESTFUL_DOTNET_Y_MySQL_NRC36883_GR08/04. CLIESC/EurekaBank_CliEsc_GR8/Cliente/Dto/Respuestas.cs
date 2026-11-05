namespace ec.edu.monster.Cliente.Dto;

// Respuestas JSON del servicio RESTful EurekaBank: todas traen {"codigo": ..., "mensaje": "..."}.

public class Respuesta
{
    public int Codigo { get; set; }
    public string? Mensaje { get; set; }
}

/// <summary>POST /api/sesion/login</summary>
public class RespuestaSesion : Respuesta
{
    public string Token { get; set; } = string.Empty;
    public string Usuario { get; set; } = string.Empty;
    public string Empleado { get; set; } = string.Empty;
    public int MinutosVigencia { get; set; }
}

/// <summary>Depósito, retiro o transferencia: saldo nuevo y número del movimiento registrado.</summary>
public class RespuestaTransaccion : Respuesta
{
    public decimal Saldo { get; set; }
    public int NumeroMovimiento { get; set; }
}

/// <summary>GET /api/cuentas/{cuenta}/movimientos</summary>
public class RespuestaMovimientos : Respuesta
{
    public CuentaDto? Cuenta { get; set; }
    public List<MovimientoDto> Movimientos { get; set; } = [];
}

/// <summary>GET /api/cuentas: todas las cuentas, para elegir el número en una lista desplegable.</summary>
public class RespuestaCuentas : Respuesta
{
    public List<CuentaDto> Cuentas { get; set; } = [];
}

public class CuentaDto
{
    public string Numero { get; set; } = string.Empty;
    public string Cliente { get; set; } = string.Empty;
    public string Cedula { get; set; } = string.Empty;
    public string Moneda { get; set; } = string.Empty;
    public string SimboloMoneda { get; set; } = "$";
    public string Sucursal { get; set; } = string.Empty;
    public string Estado { get; set; } = string.Empty;
    public decimal Saldo { get; set; }
    public DateOnly FechaApertura { get; set; }
    public int ContadorMovimientos { get; set; }

    public bool EstaActiva => Estado == "ACTIVO";
}

public class MovimientoDto
{
    public int Numero { get; set; }
    public DateTime FechaHora { get; set; }
    public string CodigoTipo { get; set; } = string.Empty;
    public string Tipo { get; set; } = string.Empty;
    public string Accion { get; set; } = string.Empty;
    public decimal Importe { get; set; }
    public string? CuentaReferencia { get; set; }
    public string Empleado { get; set; } = string.Empty;

    public bool EsIngreso => Accion == "INGRESO";
}
