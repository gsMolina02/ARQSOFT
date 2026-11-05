using System.Data.Common;
using MySqlConnector;

namespace ec.edu.monster.Datos;

/// <summary>
/// Conexiones MySQL con el usuario Monster (cadena "EurekaBank" de appsettings.json).
/// Solo el servidor se conecta a MySQL; los clientes nunca lo hacen.
/// </summary>
public class FabricaConexionMySql(string cadenaConexion) : IFabricaConexion
{
    public async Task<IUnidadTrabajo> AbrirAsync(bool transaccional = false)
    {
        var conexion = new MySqlConnection(cadenaConexion);
        try
        {
            await conexion.OpenAsync();
            MySqlTransaction? transaccion = transaccional ? await conexion.BeginTransactionAsync() : null;
            return new UnidadTrabajoMySql(conexion, transaccion);
        }
        catch
        {
            await conexion.DisposeAsync();
            throw;
        }
    }

    private sealed class UnidadTrabajoMySql(MySqlConnection conexion, MySqlTransaction? transaccion) : IUnidadTrabajo
    {
        private bool confirmada;

        public DbConnection Conexion => conexion;

        public DbTransaction? Transaccion => transaccion;

        public async Task ConfirmarAsync()
        {
            if (transaccion is not null)
            {
                await transaccion.CommitAsync();
                confirmada = true;
            }
        }

        public async ValueTask DisposeAsync()
        {
            if (transaccion is not null)
            {
                if (!confirmada)
                {
                    try
                    {
                        await transaccion.RollbackAsync();
                    }
                    catch (MySqlException)
                    {
                        // La conexión ya se perdió: MySQL descarta la transacción por su cuenta
                    }
                }
                await transaccion.DisposeAsync();
            }
            await conexion.DisposeAsync();
        }
    }
}
