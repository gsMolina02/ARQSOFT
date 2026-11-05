using System.Data.Common;

namespace ec.edu.monster.Datos;

/// <summary>
/// Abre conexiones con la base de datos eurekabank.
/// </summary>
public interface IFabricaConexion
{
    /// <param name="transaccional">true inicia una transacción que se confirma con ConfirmarAsync.</param>
    Task<IUnidadTrabajo> AbrirAsync(bool transaccional = false);
}

/// <summary>
/// Conexión abierta y, si la operación lo requiere, su transacción. Al liberarse sin ConfirmarAsync
/// la transacción se revierte (rollback): ningún saldo ni movimiento queda a medias.
/// </summary>
public interface IUnidadTrabajo : IAsyncDisposable
{
    DbConnection Conexion { get; }
    DbTransaction? Transaccion { get; }
    Task ConfirmarAsync();
}
