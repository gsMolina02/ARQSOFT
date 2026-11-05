namespace ec.edu.monster.Cliente.Dto;

// Cuerpos JSON que se envían al servicio (se serializan en camelCase: "usuario", "claveActual"...).

public class SolicitudLogin
{
    public string Usuario { get; set; } = string.Empty;
    public string Clave { get; set; } = string.Empty;
}

public class SolicitudCambioClave
{
    public string ClaveActual { get; set; } = string.Empty;
    public string ClaveNueva { get; set; } = string.Empty;
}

public class SolicitudImporte
{
    public decimal Importe { get; set; }
}

public class SolicitudTransferencia
{
    public string CuentaOrigen { get; set; } = string.Empty;
    public string CuentaDestino { get; set; } = string.Empty;
    public decimal Importe { get; set; }
}
