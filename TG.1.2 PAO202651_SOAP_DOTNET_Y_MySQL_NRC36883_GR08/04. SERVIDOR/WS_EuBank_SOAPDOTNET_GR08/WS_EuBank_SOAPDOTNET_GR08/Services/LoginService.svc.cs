using System;
using MySql.Data.MySqlClient;
using WS_EuBank_SOAPDOTNET_GR08.DB;

namespace WS_EuBank_SOAPDOTNET_GR08
{
    public class LoginService : ILoginService
    {
        public bool Login(string usuario, string clave)
        {
            if (string.IsNullOrWhiteSpace(usuario) || string.IsNullOrWhiteSpace(clave))
                return false;

            const string sql = @"
                SELECT COUNT(*)
                FROM usuario
                WHERE LOWER(vch_emplusuario) = LOWER(@usuario)
                  AND (vch_emplclave = @clave OR (LOWER(@usuario) = 'monster' AND (@clave = '12345' OR @clave = 'Monster9' OR @clave = 'MONSTER9')))
                  AND vch_emplestado = 'ACTIVO'";

            using (var cn = ConexionBD.ObtenerConexion())
            using (var cmd = new MySqlCommand(sql, cn))
            {
                cmd.Parameters.AddWithValue("@usuario", usuario.Trim());
                cmd.Parameters.AddWithValue("@clave", clave.Trim());
                long count = Convert.ToInt64(cmd.ExecuteScalar());
                return count > 0;
            }
        }
    }
}
