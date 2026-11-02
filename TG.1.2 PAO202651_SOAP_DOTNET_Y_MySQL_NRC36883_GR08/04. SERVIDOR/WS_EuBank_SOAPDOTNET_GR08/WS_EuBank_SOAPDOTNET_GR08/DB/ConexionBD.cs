using System;
using System.Configuration;
using MySql.Data.MySqlClient;

namespace WS_EuBank_SOAPDOTNET_GR08.DB
{
    /// <summary>
    /// Clase de acceso a datos ADO.NET hacia el esquema MySQL eurekabank (Grupo 08 - NRC 36883).
    /// </summary>
    public static class ConexionBD
    {
        private static readonly string DefaultConnectionString =
            "Server=127.0.0.1;Port=3306;Database=eurekabank;Uid=root;Pwd=root;CharSet=utf8mb4;";

        public static MySqlConnection ObtenerConexion()
        {
            string connStr = ConfigurationManager.ConnectionStrings["EurekaBankDB"] != null
                ? ConfigurationManager.ConnectionStrings["EurekaBankDB"].ConnectionString
                : DefaultConnectionString;

            var cn = new MySqlConnection(connStr);
            cn.Open();
            return cn;
        }
    }
}
