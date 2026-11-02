namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Helpers
{
    public static class UserInfoHelper
    {
        public static (string Titular, List<string> CuentasAsignadas, string Rol) GetInfo(string user)
        {
            string u = (user ?? "").Trim().ToLower();
            if (u == "monster" || u == "eduardo" || u == "ecampana")
                return ("Eduardo Mauricio Campaña Ortega", new List<string> { "00300001", "00300002" }, "Cliente / Administrador");
            if (u == "gustavo" || u == "gmolina")
                return ("Gustavo Stiven Molina", new List<string> { "00200001", "00200003" }, "Cliente Titular");
            if (u == "daniela" || u == "dtituana")
                return ("Daniela Lissette Tituaña", new List<string> { "00200002", "00200004" }, "Cliente Titular");
            if (u == "cromero")
                return ("Carlos Romero", new List<string> { "00100001" }, "Empleado Operador");
            return (!string.IsNullOrEmpty(user) ? user : "Usuario Eureka", new List<string> { "00100001", "00100002" }, "Usuario del Sistema");
        }
    }
}
