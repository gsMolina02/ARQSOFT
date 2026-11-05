using ec.edu.monster.Datos;
using ec.edu.monster.Excepciones;
using ec.edu.monster.Modelos;
using ec.edu.monster.Modelos.Dto;

namespace ec.edu.monster.Servicios;

/// <summary>
/// CU-04: datos de la cuenta (cliente, moneda, sucursal, estado y saldo) y sus movimientos.
/// La consulta se permite en cualquier estado de la cuenta.
/// También entrega la lista de cuentas para que los clientes no tengan que memorizar los números.
/// </summary>
public class ServicioCuenta(IFabricaConexion fabrica, IRepositorioCuenta cuentas, IRepositorioMovimiento movimientos)
{
    public async Task<RespuestaMovimientos> ConsultarMovimientosAsync(string? numeroCuenta)
    {
        string numero = ValidadorDatos.ValidarCuenta(numeroCuenta);

        await using IUnidadTrabajo unidad = await fabrica.AbrirAsync();
        Cuenta cuenta = await cuentas.BuscarAsync(unidad, numero) ?? throw NoExiste(numero);
        IReadOnlyList<Movimiento> lista = await movimientos.ListarAsync(unidad, numero);

        return new RespuestaMovimientos
        {
            Codigo = StatusCodes.Status200OK,
            Mensaje = lista.Count == 0
                    ? "La cuenta no registra movimientos"
                    : $"La cuenta registra {lista.Count} movimiento(s)",
            Cuenta = Convertir(cuenta),
            Movimientos = lista.Select(movimiento => new MovimientoDto
            {
                Numero = movimiento.Numero,
                FechaHora = movimiento.FechaHora,
                CodigoTipo = movimiento.CodigoTipo,
                Tipo = movimiento.DescripcionTipo,
                Accion = movimiento.Accion,
                Importe = movimiento.Importe,
                CuentaReferencia = movimiento.CuentaReferencia,
                Empleado = movimiento.CodigoEmpleado
            }).ToList()
        };
    }

    /// <summary>Todas las cuentas (activas o no) para la lista desplegable de los clientes.</summary>
    public async Task<RespuestaCuentas> ListarCuentasAsync()
    {
        await using IUnidadTrabajo unidad = await fabrica.AbrirAsync();
        IReadOnlyList<Cuenta> lista = await cuentas.ListarAsync(unidad);

        return new RespuestaCuentas
        {
            Codigo = StatusCodes.Status200OK,
            Mensaje = $"Se encontraron {lista.Count} cuenta(s)",
            Cuentas = lista.Select(Convertir).ToList()
        };
    }

    private static CuentaDto Convertir(Cuenta cuenta) => new()
    {
        Numero = cuenta.Numero,
        Cliente = cuenta.NombreCliente,
        Cedula = cuenta.CedulaCliente,
        Moneda = cuenta.CodigoMoneda,
        SimboloMoneda = cuenta.SimboloMoneda,
        Sucursal = cuenta.NombreSucursal,
        Estado = cuenta.Estado,
        Saldo = cuenta.Saldo,
        FechaApertura = DateOnly.FromDateTime(cuenta.FechaApertura),
        ContadorMovimientos = cuenta.ContadorMovimientos
    };

    public static ExcepcionNoEncontrado NoExiste(string numero) => new($"La cuenta {numero} no existe");
}
