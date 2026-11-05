using ec.edu.monster.Cliente;
using ec.edu.monster.Cliente.Dto;
using ec.edu.monster.Util;

namespace ec.edu.monster.Controlador;

/// <summary>
/// CU-04 Consultar movimientos, CU-05 Depósito, CU-06 Retiro y CU-07 Transferencia.
/// Valida el formato antes de llamar al servicio; las reglas de negocio (cuenta activa, saldo,
/// moneda) las decide el servidor.
/// </summary>
public class CuentaControlador
{
    private readonly IContratoWSEurekaBank clienteWs;

    public CuentaControlador(IContratoWSEurekaBank clienteWs)
    {
        this.clienteWs = clienteWs;
    }

    /// <summary>Cuentas que se ofrecen en la lista desplegable (todas, con su estado).</summary>
    public Task<RespuestaCuentas> ListarCuentasAsync() => clienteWs.ListarCuentasAsync();

    public Task<RespuestaMovimientos> ConsultarAsync(string? cuenta) =>
            clienteWs.ConsultarMovimientosAsync(ValidacionUtil.ValidarCuenta(cuenta));

    public Task<RespuestaTransaccion> DepositarAsync(string? cuenta, decimal importe) =>
            clienteWs.RegistrarDepositoAsync(ValidacionUtil.ValidarCuenta(cuenta), ValidacionUtil.ValidarImporte(importe));

    public Task<RespuestaTransaccion> RetirarAsync(string? cuenta, decimal importe) =>
            clienteWs.RegistrarRetiroAsync(ValidacionUtil.ValidarCuenta(cuenta), ValidacionUtil.ValidarImporte(importe));

    public Task<RespuestaTransaccion> TransferirAsync(string? cuentaOrigen, string? cuentaDestino, decimal importe)
    {
        string origen = ValidacionUtil.ValidarCuenta(cuentaOrigen);
        string destino = ValidacionUtil.ValidarCuenta(cuentaDestino);
        ValidacionUtil.ValidarCuentasDistintas(origen, destino);
        return clienteWs.RegistrarTransferenciaAsync(origen, destino, ValidacionUtil.ValidarImporte(importe));
    }
}
