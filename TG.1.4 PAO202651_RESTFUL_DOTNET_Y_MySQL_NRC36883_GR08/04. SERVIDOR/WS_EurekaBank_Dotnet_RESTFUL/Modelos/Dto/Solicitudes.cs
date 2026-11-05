namespace ec.edu.monster.Modelos.Dto;

// Las propiedades son anulables a propósito: si faltan, la validación la hace el servicio
// con los mensajes del ECUD en lugar de la validación automática de ASP.NET Core.

/// <summary>POST /api/sesion/login</summary>
public class SolicitudLogin
{
    public string? Usuario { get; init; }
    public string? Clave { get; init; }
}

/// <summary>PUT /api/sesion/clave</summary>
public class SolicitudCambioClave
{
    public string? ClaveActual { get; init; }
    public string? ClaveNueva { get; init; }
}

/// <summary>POST /api/cuentas/{cuenta}/depositos y /retiros</summary>
public class SolicitudImporte
{
    public decimal? Importe { get; init; }
}

/// <summary>POST /api/transferencias</summary>
public class SolicitudTransferencia
{
    public string? CuentaOrigen { get; init; }
    public string? CuentaDestino { get; init; }
    public decimal? Importe { get; init; }
}
