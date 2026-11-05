using System.Globalization;
using ec.edu.monster.Datos;
using ec.edu.monster.Excepciones;
using ec.edu.monster.Modelos;
using ec.edu.monster.Modelos.Dto;
using ec.edu.monster.Util;

namespace ec.edu.monster.Servicios;

/// <summary>
/// CU-05, CU-06 y CU-07: depósitos, retiros y transferencias. Cada operación es una transacción:
/// se bloquean las cuentas (SELECT ... FOR UPDATE), se actualiza el saldo y el contador, se registra el
/// movimiento con el empleado de la sesión y se confirma. Ante cualquier error se revierte (rollback).
/// </summary>
public class ServicioTransaccion(IFabricaConexion fabrica, IRepositorioCuenta cuentas, IRepositorioMovimiento movimientos,
        TimeProvider reloj)
{
    public const string MensajeCuentasIguales = "La cuenta de origen y la de destino deben ser distintas";
    public const string MensajeMonedasDistintas = "Las cuentas tienen monedas distintas";

    public Task<RespuestaTransaccion> RegistrarDepositoAsync(string? numeroCuenta, decimal? importe, SesionActiva sesion) =>
            RegistrarEnCuentaAsync(numeroCuenta, importe, sesion, TipoMovimiento.Deposito);

    public Task<RespuestaTransaccion> RegistrarRetiroAsync(string? numeroCuenta, decimal? importe, SesionActiva sesion) =>
            RegistrarEnCuentaAsync(numeroCuenta, importe, sesion, TipoMovimiento.Retiro);

    public async Task<RespuestaTransaccion> RegistrarTransferenciaAsync(string? cuentaOrigen, string? cuentaDestino,
            decimal? importe, SesionActiva sesion)
    {
        string numeroOrigen = ValidadorDatos.ValidarCuenta(cuentaOrigen);
        string numeroDestino = ValidadorDatos.ValidarCuenta(cuentaDestino);
        decimal valor = ValidadorDatos.ValidarImporte(importe);
        if (numeroOrigen == numeroDestino)
        {
            throw new ExcepcionValidacion(MensajeCuentasIguales);
        }

        await using IUnidadTrabajo unidad = await fabrica.AbrirAsync(transaccional: true);

        // Se bloquean siempre en orden de número: dos transferencias cruzadas (A→B y B→A) no se
        // esperan mutuamente para siempre (interbloqueo)
        bool origenPrimero = string.CompareOrdinal(numeroOrigen, numeroDestino) < 0;
        Cuenta? primera = await cuentas.BloquearAsync(unidad, origenPrimero ? numeroOrigen : numeroDestino);
        Cuenta? segunda = await cuentas.BloquearAsync(unidad, origenPrimero ? numeroDestino : numeroOrigen);
        Cuenta origen = (origenPrimero ? primera : segunda) ?? throw ServicioCuenta.NoExiste(numeroOrigen);
        Cuenta destino = (origenPrimero ? segunda : primera) ?? throw ServicioCuenta.NoExiste(numeroDestino);

        ExigirActiva(origen);
        ExigirActiva(destino);
        if (origen.CodigoMoneda != destino.CodigoMoneda)
        {
            throw new ExcepcionReglaNegocio(MensajeMonedasDistintas);
        }
        ExigirSaldo(origen, valor);
        ExigirCapacidad(destino, valor);

        DateTime ahora = reloj.AhoraLocal();
        origen.Debitar(valor);
        destino.Acreditar(valor);
        await cuentas.ActualizarAsync(unidad, origen);
        await cuentas.ActualizarAsync(unidad, destino);
        await movimientos.RegistrarAsync(unidad,
                CrearMovimiento(origen, TipoMovimiento.TransferenciaEnviada, valor, destino.Numero, sesion, ahora));
        await movimientos.RegistrarAsync(unidad,
                CrearMovimiento(destino, TipoMovimiento.TransferenciaRecibida, valor, origen.Numero, sesion, ahora));
        await unidad.ConfirmarAsync();

        return new RespuestaTransaccion
        {
            Codigo = StatusCodes.Status201Created,
            Mensaje = $"Transferencia de {Monto(origen, valor)} de la cuenta {origen.Numero} a la cuenta {destino.Numero} registrada correctamente",
            Saldo = origen.Saldo,
            NumeroMovimiento = origen.ContadorMovimientos
        };
    }

    private async Task<RespuestaTransaccion> RegistrarEnCuentaAsync(string? numeroCuenta, decimal? importe,
            SesionActiva sesion, string tipo)
    {
        string numero = ValidadorDatos.ValidarCuenta(numeroCuenta);
        decimal valor = ValidadorDatos.ValidarImporte(importe);

        await using IUnidadTrabajo unidad = await fabrica.AbrirAsync(transaccional: true);
        Cuenta cuenta = await cuentas.BloquearAsync(unidad, numero) ?? throw ServicioCuenta.NoExiste(numero);
        ExigirActiva(cuenta);

        bool esRetiro = tipo == TipoMovimiento.Retiro;
        if (esRetiro)
        {
            ExigirSaldo(cuenta, valor);
            cuenta.Debitar(valor);
        }
        else
        {
            ExigirCapacidad(cuenta, valor);
            cuenta.Acreditar(valor);
        }
        await cuentas.ActualizarAsync(unidad, cuenta);
        await movimientos.RegistrarAsync(unidad, CrearMovimiento(cuenta, tipo, valor, null, sesion, reloj.AhoraLocal()));
        await unidad.ConfirmarAsync();

        string operacion = esRetiro ? "Retiro" : "Depósito";
        return new RespuestaTransaccion
        {
            Codigo = StatusCodes.Status201Created,
            Mensaje = $"{operacion} de {Monto(cuenta, valor)} en la cuenta {cuenta.Numero} registrado correctamente",
            Saldo = cuenta.Saldo,
            NumeroMovimiento = cuenta.ContadorMovimientos
        };
    }

    private static void ExigirActiva(Cuenta cuenta)
    {
        if (!cuenta.EstaActiva())
        {
            throw new ExcepcionReglaNegocio($"La cuenta {cuenta.Numero} no está activa");
        }
    }

    private static void ExigirSaldo(Cuenta cuenta, decimal importe)
    {
        if (!cuenta.TieneSaldo(importe))
        {
            throw new ExcepcionReglaNegocio($"Saldo insuficiente. Saldo disponible: {Monto(cuenta, cuenta.Saldo)}");
        }
    }

    // El saldo resultante debe caber en la columna decimal(12,2)
    private static void ExigirCapacidad(Cuenta cuenta, decimal importe)
    {
        if (cuenta.Saldo + importe > ValidadorDatos.ImporteMaximo)
        {
            throw new ExcepcionReglaNegocio($"El saldo de la cuenta {cuenta.Numero} superaría el máximo permitido");
        }
    }

    // El número del movimiento es el contador de la cuenta ya incrementado por Acreditar/Debitar
    private static Movimiento CrearMovimiento(Cuenta cuenta, string tipo, decimal importe, string? referencia,
            SesionActiva sesion, DateTime fecha) => new()
    {
        NumeroCuenta = cuenta.Numero,
        Numero = cuenta.ContadorMovimientos,
        CodigoTipo = tipo,
        CodigoEmpleado = sesion.CodigoEmpleado,
        CuentaReferencia = referencia,
        FechaHora = fecha,
        Importe = importe
    };

    private static string Monto(Cuenta cuenta, decimal valor) =>
            cuenta.SimboloMoneda + valor.ToString("0.00", CultureInfo.InvariantCulture);
}
