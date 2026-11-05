namespace ec.edu.monster.Modelo;

/// <summary>
/// Textos y formato de la consola: banner, títulos, tablas y mensajes de éxito, aviso y error con color.
/// </summary>
public class MensajesModelo
{
    private const int Ancho = 66;

    // Arte ASCII ("EurekaBank")
    private static readonly string[] Logo =
    [
        @" _____           _         ____              _    ",
        @"| ____|   _ _ __| | ____ _| __ )  __ _ _ __ | | __",
        @"|  _|| | | | '__| |/ / _` |  _ \ / _` | '_ \| |/ /",
        @"| |__| |_| | |  |   < (_| | |_) | (_| | | | |   < ",
        @"|_____\__,_|_|  |_|\_\__,_|____/ \__,_|_| |_|_|\_\"
    ];

    public void MostrarBanner()
    {
        Escribir(Borde('='), ConsoleColor.Cyan);
        foreach (string linea in Logo)
        {
            Escribir(LineaCentrada(linea), ConsoleColor.Cyan);
        }
        Escribir(Borde('='), ConsoleColor.Cyan);
        Escribir(LineaCentrada("SISTEMA DE CUENTAS DE AHORRO  -  GR08"), ConsoleColor.Cyan);
        Escribir(LineaCentrada("Cliente Consola  -  Servicio RESTful .NET"), ConsoleColor.Cyan);
        Escribir(LineaCentrada("Movimientos | Depósitos | Retiros | Transferencias"), ConsoleColor.Cyan);
        Escribir(Borde('='), ConsoleColor.Cyan);
    }

    public void MostrarTitulo(string texto)
    {
        Console.WriteLine();
        Escribir(Borde('-'), ConsoleColor.Cyan);
        Escribir(LineaCentrada(texto), ConsoleColor.Cyan);
        Escribir(Borde('-'), ConsoleColor.Cyan);
    }

    public void MostrarMensaje(string mensaje)
    {
        Console.WriteLine(mensaje);
    }

    public void MostrarDetalle(string mensaje)
    {
        Escribir(mensaje, ConsoleColor.DarkGray);
    }

    public void MostrarAviso(string mensaje)
    {
        Escribir(mensaje, ConsoleColor.Yellow);
    }

    public void MostrarExito(string mensaje)
    {
        Escribir("  [OK] " + mensaje, ConsoleColor.Green);
    }

    public void MostrarError(string mensaje)
    {
        Escribir("  [ERROR] " + mensaje, ConsoleColor.Red);
    }

    /// <summary>Par etiqueta: valor alineado, por ejemplo "  Saldo        : $540.00".</summary>
    public void MostrarDato(string etiqueta, string valor)
    {
        Console.WriteLine($"  {etiqueta,-18}: {valor}");
    }

    /// <summary>
    /// Tabla con columnas del ancho de su dato más largo. Las columnas indicadas en alinearDerecha
    /// (por ejemplo, los importes) se alinean a la derecha.
    /// </summary>
    public void MostrarTabla(string[] encabezados, IReadOnlyList<string[]> filas, params int[] alinearDerecha)
    {
        int[] anchos = encabezados.Select((encabezado, columna) =>
                Math.Max(encabezado.Length, filas.Select(fila => fila[columna].Length).DefaultIfEmpty(0).Max())).ToArray();

        string separador = "  +" + string.Join("+", anchos.Select(ancho => new string('-', ancho + 2))) + "+";
        Escribir(separador, ConsoleColor.Cyan);
        Escribir(Fila(encabezados, anchos, alinearDerecha), ConsoleColor.Cyan);
        Escribir(separador, ConsoleColor.Cyan);
        foreach (string[] fila in filas)
        {
            Console.WriteLine(Fila(fila, anchos, alinearDerecha));
        }
        Escribir(separador, ConsoleColor.Cyan);
    }

    private static string Fila(string[] celdas, int[] anchos, int[] alinearDerecha)
    {
        IEnumerable<string> textos = celdas.Select((celda, columna) => alinearDerecha.Contains(columna)
                ? celda.PadLeft(anchos[columna])
                : celda.PadRight(anchos[columna]));
        return "  | " + string.Join(" | ", textos) + " |";
    }

    private static void Escribir(string texto, ConsoleColor color)
    {
        Console.ForegroundColor = color;
        Console.WriteLine(texto);
        Console.ResetColor();
    }

    private static string Borde(char relleno)
    {
        return "+" + new string(relleno, Ancho - 2) + "+";
    }

    private static string LineaCentrada(string texto)
    {
        int espacio = Ancho - 4 - texto.Length;
        int izquierda = Math.Max(espacio / 2, 0);
        int derecha = Math.Max(espacio - izquierda, 0);
        return "| " + new string(' ', izquierda) + texto + new string(' ', derecha) + " |";
    }
}
