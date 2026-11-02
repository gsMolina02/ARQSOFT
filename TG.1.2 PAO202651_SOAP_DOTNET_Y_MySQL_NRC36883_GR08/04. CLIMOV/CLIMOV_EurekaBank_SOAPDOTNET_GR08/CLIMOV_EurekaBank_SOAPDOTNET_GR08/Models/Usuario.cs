using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CLIMOV_EurekaBank_SOAPDOTNET_GR08.Models
{
    public class Usuario
    {
        public string nombreUsuario { get; set; }
        public string contrasena { get; set; }

        public Usuario() { }

        public Usuario(string usuario, string clave)
        {
            nombreUsuario = usuario;
            contrasena = clave;
        }
    }
}
