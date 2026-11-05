using System.Data.Common;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Datos;

public interface IRepositorioSesion
{
    Task RegistrarAsync(IUnidadTrabajo unidad, Sesion sesion);

    /// <summary>Sesión sin fecha de salida de un usuario ACTIVO; null si el token no existe o ya se cerró.</summary>
    Task<SesionActiva?> BuscarAbiertaAsync(IUnidadTrabajo unidad, string token);

    Task ActualizarActividadAsync(IUnidadTrabajo unidad, string token, DateTime fecha);

    Task CerrarAsync(IUnidadTrabajo unidad, string token, DateTime fecha);
}

public class RepositorioSesion : IRepositorioSesion
{
    public async Task RegistrarAsync(IUnidadTrabajo unidad, Sesion sesion)
    {
        await using DbCommand comando = ComandoSql.Crear(unidad, """
            insert into sesion (token, nombre_usuario, fecha_ingreso, ultima_actividad, fecha_salida, ip)
            values (@token, @usuario, @ingreso, @actividad, @salida, @ip)
            """,
            ("@token", sesion.Token), ("@usuario", sesion.NombreUsuario), ("@ingreso", sesion.FechaIngreso),
            ("@actividad", sesion.UltimaActividad), ("@salida", sesion.FechaSalida), ("@ip", sesion.Ip));
        await comando.ExecuteNonQueryAsync();
    }

    public async Task<SesionActiva?> BuscarAbiertaAsync(IUnidadTrabajo unidad, string token)
    {
        await using DbCommand comando = ComandoSql.Crear(unidad, """
            select s.token, s.ultima_actividad, u.nombre_usuario, u.codigo_empleado,
                   concat(e.nombres, ' ', e.apellidos) as empleado
            from sesion s
            join usuario u on u.nombre_usuario = s.nombre_usuario
            join empleado e on e.codigo_empleado = u.codigo_empleado
            where s.token = @token and s.fecha_salida is null and u.estado = 'ACTIVO'
            """, ("@token", token));
        await using DbDataReader lector = await comando.ExecuteReaderAsync();
        if (!await lector.ReadAsync())
        {
            return null;
        }
        return new SesionActiva
        {
            Token = lector.Texto("token"),
            NombreUsuario = lector.Texto("nombre_usuario"),
            CodigoEmpleado = lector.Texto("codigo_empleado"),
            NombreEmpleado = lector.Texto("empleado"),
            UltimaActividad = lector.Fecha("ultima_actividad")
        };
    }

    public async Task ActualizarActividadAsync(IUnidadTrabajo unidad, string token, DateTime fecha)
    {
        await using DbCommand comando = ComandoSql.Crear(unidad,
                "update sesion set ultima_actividad = @fecha where token = @token",
                ("@fecha", fecha), ("@token", token));
        await comando.ExecuteNonQueryAsync();
    }

    public async Task CerrarAsync(IUnidadTrabajo unidad, string token, DateTime fecha)
    {
        await using DbCommand comando = ComandoSql.Crear(unidad,
                "update sesion set fecha_salida = @fecha where token = @token and fecha_salida is null",
                ("@fecha", fecha), ("@token", token));
        await comando.ExecuteNonQueryAsync();
    }
}
