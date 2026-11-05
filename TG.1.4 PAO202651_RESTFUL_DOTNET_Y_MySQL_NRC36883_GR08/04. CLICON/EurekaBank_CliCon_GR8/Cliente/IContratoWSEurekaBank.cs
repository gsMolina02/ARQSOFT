using ec.edu.monster.Cliente.Dto;

namespace ec.edu.monster.Cliente;

/// <summary>
/// Operaciones que expone el servicio RESTful EurekaBank.
/// Los controladores dependen de esta interfaz y no de la implementación HTTP.
/// </summary>
public interface IContratoWSEurekaBank
{
    /// <summary>Nombre del empleado de la sesión actual (null sin sesión).</summary>
    string? Empleado { get; }

    bool HaySesionActiva { get; }

    // POST /api/sesion/login
    Task<RespuestaSesion> IniciarSesionAsync(string usuario, string clave);

    // PUT /api/sesion/clave
    Task<string> CambiarClaveAsync(string claveActual, string claveNueva);

    // POST /api/sesion/logout
    Task<string> CerrarSesionAsync();

    // GET /api/cuentas
    Task<RespuestaCuentas> ListarCuentasAsync();

    // GET /api/cuentas/{cuenta}/movimientos
    Task<RespuestaMovimientos> ConsultarMovimientosAsync(string cuenta);

    // POST /api/cuentas/{cuenta}/depositos
    Task<RespuestaTransaccion> RegistrarDepositoAsync(string cuenta, decimal importe);

    // POST /api/cuentas/{cuenta}/retiros
    Task<RespuestaTransaccion> RegistrarRetiroAsync(string cuenta, decimal importe);

    // POST /api/transferencias
    Task<RespuestaTransaccion> RegistrarTransferenciaAsync(string cuentaOrigen, string cuentaDestino, decimal importe);
}
