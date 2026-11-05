using System.Data.Common;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Datos;

public interface IRepositorioUsuario
{
    /// <summary>Usuario con el nombre de su empleado; null si no existe.</summary>
    Task<Usuario?> BuscarAsync(IUnidadTrabajo unidad, string nombreUsuario);

    Task ActualizarClaveAsync(IUnidadTrabajo unidad, string nombreUsuario, string hashClave);
}

public class RepositorioUsuario : IRepositorioUsuario
{
    public async Task<Usuario?> BuscarAsync(IUnidadTrabajo unidad, string nombreUsuario)
    {
        await using DbCommand comando = ComandoSql.Crear(unidad, """
            select u.nombre_usuario, u.codigo_empleado, u.hash_clave, u.estado,
                   concat(e.nombres, ' ', e.apellidos) as empleado
            from usuario u
            join empleado e on e.codigo_empleado = u.codigo_empleado
            where u.nombre_usuario = @usuario
            """, ("@usuario", nombreUsuario));
        await using DbDataReader lector = await comando.ExecuteReaderAsync();
        if (!await lector.ReadAsync())
        {
            return null;
        }
        return new Usuario
        {
            NombreUsuario = lector.Texto("nombre_usuario"),
            CodigoEmpleado = lector.Texto("codigo_empleado"),
            NombreEmpleado = lector.Texto("empleado"),
            HashClave = lector.Texto("hash_clave"),
            Estado = lector.Texto("estado")
        };
    }

    public async Task ActualizarClaveAsync(IUnidadTrabajo unidad, string nombreUsuario, string hashClave)
    {
        await using DbCommand comando = ComandoSql.Crear(unidad,
                "update usuario set hash_clave = @hash where nombre_usuario = @usuario",
                ("@hash", hashClave), ("@usuario", nombreUsuario));
        await comando.ExecuteNonQueryAsync();
    }
}
