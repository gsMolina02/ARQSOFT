using System.Data.Common;
using ec.edu.monster.Datos;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Pruebas.Falsos;

// Dobles de prueba: reemplazan MySQL por colecciones en memoria para probar las reglas de negocio.

public class UnidadFalsa(bool transaccional) : IUnidadTrabajo
{
    public bool Transaccional { get; } = transaccional;
    public bool Confirmada { get; private set; }
    public bool Liberada { get; private set; }

    public DbConnection Conexion => throw new NotSupportedException("Los repositorios falsos no usan la conexión.");

    public DbTransaction? Transaccion => null;

    public Task ConfirmarAsync()
    {
        Confirmada = true;
        return Task.CompletedTask;
    }

    public ValueTask DisposeAsync()
    {
        Liberada = true;
        return ValueTask.CompletedTask;
    }
}

public class FabricaFalsa : IFabricaConexion
{
    public List<UnidadFalsa> Unidades { get; } = [];

    public Task<IUnidadTrabajo> AbrirAsync(bool transaccional = false)
    {
        var unidad = new UnidadFalsa(transaccional);
        Unidades.Add(unidad);
        return Task.FromResult<IUnidadTrabajo>(unidad);
    }
}

public class RepositorioCuentaFalso : IRepositorioCuenta
{
    public Dictionary<string, Cuenta> Cuentas { get; } = [];
    public List<string> Bloqueadas { get; } = [];
    public List<string> Actualizadas { get; } = [];

    public RepositorioCuentaFalso Con(params Cuenta[] cuentas)
    {
        foreach (Cuenta cuenta in cuentas)
        {
            Cuentas[cuenta.Numero] = cuenta;
        }
        return this;
    }

    public Task<Cuenta?> BuscarAsync(IUnidadTrabajo unidad, string numero) =>
            Task.FromResult(Cuentas.TryGetValue(numero, out Cuenta? cuenta) ? Copiar(cuenta) : null);

    public Task<IReadOnlyList<Cuenta>> ListarAsync(IUnidadTrabajo unidad) =>
            Task.FromResult<IReadOnlyList<Cuenta>>(Cuentas.Values.OrderBy(c => c.Numero).Select(Copiar).ToList());

    public Task<Cuenta?> BloquearAsync(IUnidadTrabajo unidad, string numero)
    {
        Bloqueadas.Add(numero);
        return BuscarAsync(unidad, numero);
    }

    public Task ActualizarAsync(IUnidadTrabajo unidad, Cuenta cuenta)
    {
        Actualizadas.Add(cuenta.Numero);
        Cuentas[cuenta.Numero] = Copiar(cuenta);
        return Task.CompletedTask;
    }

    // El servicio recibe una copia, como si la leyera de la base: sus cambios solo cuentan si llama a ActualizarAsync
    private static Cuenta Copiar(Cuenta c) => new()
    {
        Numero = c.Numero, Saldo = c.Saldo, FechaApertura = c.FechaApertura, Estado = c.Estado,
        ContadorMovimientos = c.ContadorMovimientos, CodigoCliente = c.CodigoCliente, CedulaCliente = c.CedulaCliente,
        NombreCliente = c.NombreCliente, CodigoMoneda = c.CodigoMoneda, NombreMoneda = c.NombreMoneda,
        SimboloMoneda = c.SimboloMoneda, CodigoSucursal = c.CodigoSucursal, NombreSucursal = c.NombreSucursal
    };
}

public class RepositorioMovimientoFalso : IRepositorioMovimiento
{
    public List<Movimiento> Existentes { get; } = [];
    public List<Movimiento> Registrados { get; } = [];

    public Task<IReadOnlyList<Movimiento>> ListarAsync(IUnidadTrabajo unidad, string numeroCuenta) =>
            Task.FromResult<IReadOnlyList<Movimiento>>(Existentes.Where(m => m.NumeroCuenta == numeroCuenta)
                    .OrderBy(m => m.Numero).ToList());

    public Task RegistrarAsync(IUnidadTrabajo unidad, Movimiento movimiento)
    {
        Registrados.Add(movimiento);
        return Task.CompletedTask;
    }
}

public class RepositorioUsuarioFalso : IRepositorioUsuario
{
    public Dictionary<string, Usuario> Usuarios { get; } = [];

    public Task<Usuario?> BuscarAsync(IUnidadTrabajo unidad, string nombreUsuario) =>
            Task.FromResult(Usuarios.GetValueOrDefault(nombreUsuario));

    public Task ActualizarClaveAsync(IUnidadTrabajo unidad, string nombreUsuario, string hashClave)
    {
        Usuario actual = Usuarios[nombreUsuario];
        Usuarios[nombreUsuario] = new Usuario
        {
            NombreUsuario = actual.NombreUsuario, CodigoEmpleado = actual.CodigoEmpleado,
            NombreEmpleado = actual.NombreEmpleado, HashClave = hashClave, Estado = actual.Estado
        };
        return Task.CompletedTask;
    }
}

public class RepositorioSesionFalso : IRepositorioSesion
{
    public List<Sesion> Registradas { get; } = [];
    public Dictionary<string, SesionActiva> Abiertas { get; } = [];
    public Dictionary<string, DateTime> Actividad { get; } = [];
    public Dictionary<string, DateTime> Cerradas { get; } = [];

    public Task RegistrarAsync(IUnidadTrabajo unidad, Sesion sesion)
    {
        Registradas.Add(sesion);
        return Task.CompletedTask;
    }

    public Task<SesionActiva?> BuscarAbiertaAsync(IUnidadTrabajo unidad, string token) =>
            Task.FromResult(Cerradas.ContainsKey(token) ? null : Abiertas.GetValueOrDefault(token));

    public Task ActualizarActividadAsync(IUnidadTrabajo unidad, string token, DateTime fecha)
    {
        Actividad[token] = fecha;
        return Task.CompletedTask;
    }

    public Task CerrarAsync(IUnidadTrabajo unidad, string token, DateTime fecha)
    {
        Cerradas[token] = fecha;
        return Task.CompletedTask;
    }
}

/// <summary>Reloj fijo: la hora local es la hora UTC indicada.</summary>
public class RelojFalso(DateTime ahora) : TimeProvider
{
    public DateTime Ahora { get; set; } = ahora;

    public override DateTimeOffset GetUtcNow() => new(DateTime.SpecifyKind(Ahora, DateTimeKind.Utc));

    public override TimeZoneInfo LocalTimeZone => TimeZoneInfo.Utc;
}

public static class DatosPrueba
{
    public static readonly DateTime Ahora = new(2026, 11, 4, 10, 30, 0);

    public static SesionActiva SesionMonster(DateTime? ultimaActividad = null) => new()
    {
        Token = "11111111-2222-3333-4444-555555555555",
        NombreUsuario = "Monster",
        CodigoEmpleado = "0001",
        NombreEmpleado = "Monster EurekaBank",
        UltimaActividad = ultimaActividad ?? Ahora
    };

    public static Usuario UsuarioMonster(string estado = Usuario.EstadoActivo) => new()
    {
        NombreUsuario = "Monster",
        CodigoEmpleado = "0001",
        NombreEmpleado = "Monster EurekaBank",
        HashClave = Usuario.CalcularHash("Monster9"),
        Estado = estado
    };

    public static Cuenta Cuenta(string numero, decimal saldo, string estado = Modelos.Cuenta.EstadoActivo,
            string moneda = "USD", int contador = 1) => new()
    {
        Numero = numero, Saldo = saldo, FechaApertura = new DateTime(2026, 1, 15), Estado = estado,
        ContadorMovimientos = contador, CodigoCliente = "00001", CedulaCliente = "1712345675",
        NombreCliente = "Carlos Andrés Pérez Salazar", CodigoMoneda = moneda,
        NombreMoneda = moneda == "USD" ? "Dólar estadounidense" : "Otra moneda", SimboloMoneda = "$",
        CodigoSucursal = "001", NombreSucursal = "Matriz Quito"
    };
}
