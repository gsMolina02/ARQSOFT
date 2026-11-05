using System.Text;
using ec.edu.monster.Vista;

// Cliente de consola del servicio RESTful EurekaBank (patrón MVC).
// La dirección del servidor se cambia en "Configurar servidor" o con la variable de entorno WS_BASE_URL.
Console.OutputEncoding = Encoding.UTF8;
Console.InputEncoding = Encoding.UTF8;
Console.Title = "EurekaBank GR08 - Cliente Consola REST";

try
{
    await new LoginVista().IniciarAsync();
}
catch (EndOfStreamException)
{
    // El usuario cerró la entrada (Ctrl+Z): se termina sin error
}
