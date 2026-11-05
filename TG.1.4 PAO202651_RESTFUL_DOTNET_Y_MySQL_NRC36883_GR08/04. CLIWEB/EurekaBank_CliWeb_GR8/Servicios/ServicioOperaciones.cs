using ec.edu.monster.Cliente;
using ec.edu.monster.Cliente.Dto;
using ec.edu.monster.Models;
using ec.edu.monster.Util;

namespace ec.edu.monster.Servicios;

/// <summary>
/// CU-04 a CU-07: valida el formulario de la operación elegida y llama al servicio RESTful.
/// Las reglas de negocio (cuenta activa, saldo, moneda) las decide el servidor.
/// </summary>
public class ServicioOperaciones
{
    private readonly IContratoWSEurekaBank clienteWs;

    public ServicioOperaciones(IContratoWSEurekaBank clienteWs)
    {
        this.clienteWs = clienteWs;
    }

    /// <summary>Todas las cuentas (con su estado) para la lista desplegable del número de cuenta.</summary>
    public Task<RespuestaCuentas> ListarCuentasAsync() => clienteWs.ListarCuentasAsync();

    public Task<RespuestaMovimientos> ConsultarAsync(string? cuenta) =>
            clienteWs.ConsultarMovimientosAsync(ValidacionUtil.ValidarCuenta(cuenta));

    /// <summary>Depósito, retiro o transferencia según la pestaña del formulario.</summary>
    /// <exception cref="ExcepcionCampo">Un dato del formulario no tiene el formato correcto.</exception>
    public Task<RespuestaTransaccion> RegistrarAsync(OperacionesModelo modelo)
    {
        string cuenta = Validar("cuenta", () => ValidacionUtil.ValidarCuenta(modelo.Cuenta));

        if (modelo.Operacion == TipoOperacion.Transferencia)
        {
            string destino = Validar("destino", () => ValidacionUtil.ValidarCuenta(modelo.CuentaDestino));
            Validar("destino", () =>
            {
                ValidacionUtil.ValidarCuentasDistintas(cuenta, destino);
                return destino;
            });
            decimal importeTransferencia = Validar("importe", () => ValidacionUtil.InterpretarImporte(modelo.Importe));
            return clienteWs.RegistrarTransferenciaAsync(cuenta, destino, importeTransferencia);
        }

        decimal importe = Validar("importe", () => ValidacionUtil.InterpretarImporte(modelo.Importe));
        return modelo.Operacion switch
        {
            TipoOperacion.Deposito => clienteWs.RegistrarDepositoAsync(cuenta, importe),
            TipoOperacion.Retiro => clienteWs.RegistrarRetiroAsync(cuenta, importe),
            _ => throw new ArgumentException("La consulta de movimientos no registra ninguna operación")
        };
    }

    private static T Validar<T>(string campo, Func<T> validacion)
    {
        try
        {
            return validacion();
        }
        catch (ArgumentException ex)
        {
            throw new ExcepcionCampo(campo, ex.Message);
        }
    }
}

/// <summary>Dato del formulario con formato incorrecto: indica qué campo resaltar.</summary>
public class ExcepcionCampo(string campo, string mensaje) : ArgumentException(mensaje)
{
    public string Campo { get; } = campo;
}
