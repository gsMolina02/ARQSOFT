using ec.edu.monster.Excepciones;
using ec.edu.monster.Modelos;
using ec.edu.monster.Modelos.Dto;
using ec.edu.monster.Pruebas.Falsos;
using ec.edu.monster.Servicios;
using Xunit;

namespace ec.edu.monster.Pruebas;

public class ServicioTransaccionPruebas
{
    private readonly FabricaFalsa fabrica = new();
    private readonly RepositorioCuentaFalso cuentas = new();
    private readonly RepositorioMovimientoFalso movimientos = new();
    private readonly ServicioTransaccion servicio;
    private readonly SesionActiva sesion = DatosPrueba.SesionMonster();

    public ServicioTransaccionPruebas()
    {
        servicio = new ServicioTransaccion(fabrica, cuentas, movimientos, new RelojFalso(DatosPrueba.Ahora));
        cuentas.Con(DatosPrueba.Cuenta("00100001", 540.00m, contador: 4),
                DatosPrueba.Cuenta("00200001", 530.25m, contador: 3),
                DatosPrueba.Cuenta("00100003", 20.00m, estado: "ANULADO"),
                DatosPrueba.Cuenta("00900001", 100.00m, moneda: "EUR"));
    }

    [Fact]
    public async Task DeberiaRegistrarElDepositoActualizandoSaldoYContador()
    {
        RespuestaTransaccion respuesta = await servicio.RegistrarDepositoAsync("00100001", 100.50m, sesion);

        Assert.Equal(201, respuesta.Codigo);
        Assert.Equal(640.50m, respuesta.Saldo);
        Assert.Equal(5, respuesta.NumeroMovimiento);
        Assert.Equal(640.50m, cuentas.Cuentas["00100001"].Saldo);
        Assert.Equal(5, cuentas.Cuentas["00100001"].ContadorMovimientos);

        Movimiento movimiento = Assert.Single(movimientos.Registrados);
        Assert.Equal((TipoMovimiento.Deposito, 5, "0001", 100.50m, DatosPrueba.Ahora),
                (movimiento.CodigoTipo, movimiento.Numero, movimiento.CodigoEmpleado, movimiento.Importe, movimiento.FechaHora));
        Assert.Null(movimiento.CuentaReferencia);
        AssertTransaccionConfirmada();
    }

    [Fact]
    public async Task DeberiaRegistrarElRetiro()
    {
        RespuestaTransaccion respuesta = await servicio.RegistrarRetiroAsync("00100001", 540.00m, sesion);

        Assert.Equal(0m, respuesta.Saldo);
        Assert.Equal(TipoMovimiento.Retiro, Assert.Single(movimientos.Registrados).CodigoTipo);
        AssertTransaccionConfirmada();
    }

    [Fact]
    public async Task DeberiaRechazarElRetiroConSaldoInsuficienteSinModificarNada()
    {
        var error = await Assert.ThrowsAsync<ExcepcionReglaNegocio>(() =>
                servicio.RegistrarRetiroAsync("00100001", 540.01m, sesion));

        Assert.Equal(409, error.Codigo);
        Assert.Equal("Saldo insuficiente. Saldo disponible: $540.00", error.Message);
        AssertSinCambios();
    }

    [Theory]
    [InlineData("0")]
    [InlineData("-5")]
    [InlineData("10.555")]
    public async Task DeberiaRechazarImportesNoValidos(string importe)
    {
        var error = await Assert.ThrowsAsync<ExcepcionValidacion>(() =>
                servicio.RegistrarDepositoAsync("00100001", decimal.Parse(importe, System.Globalization.CultureInfo.InvariantCulture), sesion));

        Assert.Equal(400, error.Codigo);
        Assert.Equal(ValidadorDatos.MensajeImporte, error.Message);
        Assert.Empty(fabrica.Unidades);
    }

    [Fact]
    public async Task DeberiaRechazarUnImporteAusente()
    {
        var error = await Assert.ThrowsAsync<ExcepcionValidacion>(() => servicio.RegistrarDepositoAsync("00100001", null, sesion));

        Assert.Equal(ValidadorDatos.MensajeImporte, error.Message);
    }

    [Fact]
    public async Task DeberiaAceptarImportesConCerosALaDerecha()
    {
        RespuestaTransaccion respuesta = await servicio.RegistrarDepositoAsync("00100001", 10.500m, sesion);

        Assert.Equal(550.50m, respuesta.Saldo);
    }

    [Theory]
    [InlineData("123")]
    [InlineData("abcdefgh")]
    [InlineData("001000011")]
    [InlineData("")]
    [InlineData(null)]
    public async Task DeberiaRechazarNumerosDeCuentaSinOchoDigitos(string? cuenta)
    {
        var error = await Assert.ThrowsAsync<ExcepcionValidacion>(() => servicio.RegistrarDepositoAsync(cuenta, 10m, sesion));

        Assert.Equal(ValidadorDatos.MensajeCuenta, error.Message);
    }

    [Fact]
    public async Task DeberiaResponder404SiLaCuentaNoExiste()
    {
        var error = await Assert.ThrowsAsync<ExcepcionNoEncontrado>(() => servicio.RegistrarDepositoAsync("99999999", 10m, sesion));

        Assert.Equal(404, error.Codigo);
        Assert.Equal("La cuenta 99999999 no existe", error.Message);
        AssertSinCambios();
    }

    [Fact]
    public async Task DeberiaRechazarOperacionesSobreCuentasNoActivas()
    {
        var error = await Assert.ThrowsAsync<ExcepcionReglaNegocio>(() => servicio.RegistrarDepositoAsync("00100003", 10m, sesion));

        Assert.Equal("La cuenta 00100003 no está activa", error.Message);
        AssertSinCambios();
    }

    [Fact]
    public async Task DeberiaRechazarUnDepositoQueSupereElSaldoMaximo()
    {
        await Assert.ThrowsAsync<ExcepcionReglaNegocio>(() =>
                servicio.RegistrarDepositoAsync("00100001", ValidadorDatos.ImporteMaximo, sesion));

        AssertSinCambios();
    }

    [Fact]
    public async Task DeberiaTransferirRegistrandoLosDosMovimientosEnUnaTransaccion()
    {
        RespuestaTransaccion respuesta = await servicio.RegistrarTransferenciaAsync("00100001", "00200001", 150.00m, sesion);

        Assert.Equal(201, respuesta.Codigo);
        Assert.Equal(390.00m, respuesta.Saldo);
        Assert.Equal(5, respuesta.NumeroMovimiento);
        Assert.Equal(390.00m, cuentas.Cuentas["00100001"].Saldo);
        Assert.Equal(680.25m, cuentas.Cuentas["00200001"].Saldo);

        Assert.Collection(movimientos.Registrados,
                salida => Assert.Equal(("00100001", 5, TipoMovimiento.TransferenciaEnviada, "00200001"),
                        (salida.NumeroCuenta, salida.Numero, salida.CodigoTipo, salida.CuentaReferencia)),
                ingreso => Assert.Equal(("00200001", 4, TipoMovimiento.TransferenciaRecibida, "00100001"),
                        (ingreso.NumeroCuenta, ingreso.Numero, ingreso.CodigoTipo, ingreso.CuentaReferencia)));
        Assert.All(movimientos.Registrados, m => Assert.Equal((150.00m, "0001"), (m.Importe, m.CodigoEmpleado)));
        AssertTransaccionConfirmada();
    }

    [Fact]
    public async Task DeberiaBloquearLasCuentasEnOrdenDeNumero()
    {
        await servicio.RegistrarTransferenciaAsync("00200001", "00100001", 10m, sesion);

        Assert.Equal(["00100001", "00200001"], cuentas.Bloqueadas);
    }

    [Fact]
    public async Task DeberiaRechazarTransferenciasALaMismaCuenta()
    {
        var error = await Assert.ThrowsAsync<ExcepcionValidacion>(() =>
                servicio.RegistrarTransferenciaAsync("00100001", "00100001", 10m, sesion));

        Assert.Equal(ServicioTransaccion.MensajeCuentasIguales, error.Message);
        Assert.Empty(fabrica.Unidades);
    }

    [Fact]
    public async Task DeberiaRechazarTransferenciasEntreMonedasDistintas()
    {
        var error = await Assert.ThrowsAsync<ExcepcionReglaNegocio>(() =>
                servicio.RegistrarTransferenciaAsync("00100001", "00900001", 10m, sesion));

        Assert.Equal(ServicioTransaccion.MensajeMonedasDistintas, error.Message);
        AssertSinCambios();
    }

    [Fact]
    public async Task DeberiaRechazarTransferenciasHaciaCuentasInexistentes()
    {
        var error = await Assert.ThrowsAsync<ExcepcionNoEncontrado>(() =>
                servicio.RegistrarTransferenciaAsync("00100001", "99999999", 10m, sesion));

        Assert.Equal("La cuenta 99999999 no existe", error.Message);
        AssertSinCambios();
    }

    [Fact]
    public async Task DeberiaRechazarTransferenciasConSaldoInsuficiente()
    {
        var error = await Assert.ThrowsAsync<ExcepcionReglaNegocio>(() =>
                servicio.RegistrarTransferenciaAsync("00200001", "00100001", 1000m, sesion));

        Assert.Equal("Saldo insuficiente. Saldo disponible: $530.25", error.Message);
        AssertSinCambios();
    }

    private void AssertTransaccionConfirmada()
    {
        UnidadFalsa unidad = Assert.Single(fabrica.Unidades);
        Assert.True(unidad.Transaccional);
        Assert.True(unidad.Confirmada);
        Assert.True(unidad.Liberada);
    }

    // Sin confirmar: al liberarse la unidad de trabajo, MySQL revierte la transacción
    private void AssertSinCambios()
    {
        Assert.Empty(movimientos.Registrados);
        Assert.Empty(cuentas.Actualizadas);
        Assert.All(fabrica.Unidades, unidad => Assert.False(unidad.Confirmada));
        Assert.All(fabrica.Unidades, unidad => Assert.True(unidad.Liberada));
    }
}
