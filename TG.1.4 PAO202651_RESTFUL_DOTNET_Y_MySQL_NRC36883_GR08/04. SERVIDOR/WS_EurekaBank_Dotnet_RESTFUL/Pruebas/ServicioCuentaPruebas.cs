using ec.edu.monster.Excepciones;
using ec.edu.monster.Modelos;
using ec.edu.monster.Modelos.Dto;
using ec.edu.monster.Pruebas.Falsos;
using ec.edu.monster.Servicios;
using Xunit;

namespace ec.edu.monster.Pruebas;

public class ServicioCuentaPruebas
{
    private readonly RepositorioCuentaFalso cuentas = new();
    private readonly RepositorioMovimientoFalso movimientos = new();
    private readonly ServicioCuenta servicio;

    public ServicioCuentaPruebas()
    {
        servicio = new ServicioCuenta(new FabricaFalsa(), cuentas, movimientos);
        cuentas.Con(DatosPrueba.Cuenta("00100001", 540.00m, contador: 2),
                DatosPrueba.Cuenta("00300002", 0m, estado: "CANCELADO", contador: 0));
        movimientos.Existentes.AddRange([
            Movimiento("00100001", 2, TipoMovimiento.TransferenciaEnviada, "SALIDA", 40m, "00200001"),
            Movimiento("00100001", 1, TipoMovimiento.Apertura, "INGRESO", 580m, null)
        ]);
    }

    [Fact]
    public async Task DeberiaRetornarLaCuentaYSusMovimientosOrdenados()
    {
        RespuestaMovimientos respuesta = await servicio.ConsultarMovimientosAsync(" 00100001 ");

        Assert.Equal(200, respuesta.Codigo);
        CuentaDto cuenta = Assert.IsType<CuentaDto>(respuesta.Cuenta);
        Assert.Equal(("00100001", "Carlos Andrés Pérez Salazar", "USD", "Matriz Quito", 540.00m),
                (cuenta.Numero, cuenta.Cliente, cuenta.Moneda, cuenta.Sucursal, cuenta.Saldo));
        Assert.Equal(new DateOnly(2026, 1, 15), cuenta.FechaApertura);
        Assert.Equal([1, 2], respuesta.Movimientos.Select(m => m.Numero));
        Assert.Equal(("SALIDA", "00200001"), (respuesta.Movimientos[1].Accion, respuesta.Movimientos[1].CuentaReferencia));
    }

    [Fact]
    public async Task DeberiaPermitirConsultarCuentasNoActivasSinMovimientos()
    {
        RespuestaMovimientos respuesta = await servicio.ConsultarMovimientosAsync("00300002");

        Assert.Equal("CANCELADO", respuesta.Cuenta!.Estado);
        Assert.Empty(respuesta.Movimientos);
        Assert.Equal("La cuenta no registra movimientos", respuesta.Mensaje);
    }

    [Fact]
    public async Task DeberiaResponder404SiLaCuentaNoExiste()
    {
        var error = await Assert.ThrowsAsync<ExcepcionNoEncontrado>(() => servicio.ConsultarMovimientosAsync("99999999"));

        Assert.Equal("La cuenta 99999999 no existe", error.Message);
    }

    [Fact]
    public async Task DeberiaValidarElFormatoDeLaCuenta()
    {
        var error = await Assert.ThrowsAsync<ExcepcionValidacion>(() => servicio.ConsultarMovimientosAsync("12ab"));

        Assert.Equal(ValidadorDatos.MensajeCuenta, error.Message);
    }

    [Fact]
    public async Task DeberiaListarTodasLasCuentasOrdenadasConSuEstado()
    {
        cuentas.Con(DatosPrueba.Cuenta("00200001", 10m));

        RespuestaCuentas respuesta = await servicio.ListarCuentasAsync();

        Assert.Equal(200, respuesta.Codigo);
        Assert.Equal("Se encontraron 3 cuenta(s)", respuesta.Mensaje);
        Assert.Equal(["00100001", "00200001", "00300002"], respuesta.Cuentas.Select(c => c.Numero));
        Assert.Equal(("CANCELADO", "Carlos Andrés Pérez Salazar"), (respuesta.Cuentas[2].Estado, respuesta.Cuentas[2].Cliente));
    }

    private static Movimiento Movimiento(string cuenta, int numero, string tipo, string accion, decimal importe,
            string? referencia) => new()
    {
        NumeroCuenta = cuenta, Numero = numero, CodigoTipo = tipo, Accion = accion, Importe = importe,
        CuentaReferencia = referencia, CodigoEmpleado = "0001", FechaHora = DatosPrueba.Ahora
    };
}
