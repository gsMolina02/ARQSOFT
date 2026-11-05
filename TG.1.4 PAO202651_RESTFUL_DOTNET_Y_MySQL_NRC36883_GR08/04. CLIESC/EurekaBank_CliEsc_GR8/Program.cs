using ec.edu.monster.Controlador;
using ec.edu.monster.Vista;

namespace ec.edu.monster
{
    internal static class Program
    {
        /// <summary>
        ///  Punto de entrada del cliente de escritorio del servicio RESTful EurekaBank.
        /// </summary>
        [STAThread]
        static void Main()
        {
            ApplicationConfiguration.Initialize();

            // La dirección del servidor REST se puede cambiar desde "Configurar servidor" en el inicio de sesión
            Application.Run(new FormLogin(new ServidorControlador()));
        }
    }
}
