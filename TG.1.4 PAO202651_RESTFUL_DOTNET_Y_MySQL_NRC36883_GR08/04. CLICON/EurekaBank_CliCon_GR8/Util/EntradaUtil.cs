using System.Globalization;
using System.Text;
using System.Text.RegularExpressions;

namespace ec.edu.monster.Util;

/// <summary>
/// Lectura de datos desde el teclado. Cada método repite la pregunta hasta recibir un dato válido.
/// </summary>
public static class EntradaUtil
{
    // [0-9] y no \d: en .NET \d también acepta dígitos de otros alfabetos (por ejemplo, ٣)
    private static readonly Regex PatronEntero = new(@"^[0-9]{1,9}$");

    public static string LeerTextoNoVacio(string mensaje)
    {
        while (true)
        {
            Console.Write(mensaje);
            string texto = LeerLinea().Trim();
            if (texto.Length > 0)
            {
                return texto;
            }
            MostrarError("Entrada inválida. El valor no puede quedar vacío.");
        }
    }

    public static int LeerEntero(string mensaje)
    {
        while (true)
        {
            Console.Write(mensaje);
            string valor = LeerLinea().Trim();
            if (PatronEntero.IsMatch(valor))
            {
                return int.Parse(valor, CultureInfo.InvariantCulture);
            }
            MostrarError("Entrada inválida. Debe ingresar un número entero.");
        }
    }

    public static string LeerCuenta(string mensaje)
    {
        while (true)
        {
            try
            {
                return ValidacionUtil.ValidarCuenta(LeerTextoNoVacio(mensaje));
            }
            catch (ArgumentException ex)
            {
                MostrarError(ex.Message + ".");
            }
        }
    }

    public static decimal LeerImporte(string mensaje)
    {
        while (true)
        {
            try
            {
                return ValidacionUtil.InterpretarImporte(LeerTextoNoVacio(mensaje));
            }
            catch (ArgumentException ex)
            {
                MostrarError(ex.Message + ", por ejemplo 100 o 25.50.");
            }
        }
    }

    /// <summary>
    /// Lee la contraseña mostrando * por cada carácter. Si la entrada está redirigida (pruebas o
    /// scripts) no se puede ocultar y se lee la línea completa.
    /// </summary>
    public static string LeerClave(string mensaje)
    {
        while (true)
        {
            Console.Write(mensaje);
            string clave = Console.IsInputRedirected ? LeerLinea() : LeerOculto();
            if (clave.Length > 0)
            {
                return clave;
            }
            MostrarError("Entrada inválida. La contraseña no puede quedar vacía.");
        }
    }

    public static bool LeerSiNo(string mensaje)
    {
        while (true)
        {
            string respuesta = LeerTextoNoVacio(mensaje + " (S/N): ").ToUpperInvariant();
            if (respuesta == "S")
            {
                return true;
            }
            if (respuesta == "N")
            {
                return false;
            }
            MostrarError("Responda S o N.");
        }
    }

    private static string LeerOculto()
    {
        var texto = new StringBuilder();
        while (true)
        {
            ConsoleKeyInfo tecla = Console.ReadKey(intercept: true);
            if (tecla.Key == ConsoleKey.Enter)
            {
                Console.WriteLine();
                return texto.ToString();
            }
            if (tecla.Key == ConsoleKey.Backspace)
            {
                if (texto.Length > 0)
                {
                    texto.Length--;
                    Console.Write("\b \b");
                }
            }
            else if (!char.IsControl(tecla.KeyChar))
            {
                texto.Append(tecla.KeyChar);
                Console.Write('*');
            }
        }
    }

    // ReadLine devuelve null si se cierra la entrada (Ctrl+Z): se termina el programa en lugar de repetir sin fin
    private static string LeerLinea()
    {
        return Console.ReadLine() ?? throw new EndOfStreamException("Se cerró la entrada de la consola.");
    }

    private static void MostrarError(string mensaje)
    {
        Console.ForegroundColor = ConsoleColor.Red;
        Console.WriteLine("  [ERROR] " + mensaje);
        Console.ResetColor();
    }
}
