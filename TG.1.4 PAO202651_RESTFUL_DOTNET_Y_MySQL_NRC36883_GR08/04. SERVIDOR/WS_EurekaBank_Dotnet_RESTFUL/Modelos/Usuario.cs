using System.Security.Cryptography;
using System.Text;

namespace ec.edu.monster.Modelos;

/// <summary>
/// Credenciales de acceso de un empleado. La contraseña se guarda como hash SHA-256 en hexadecimal.
/// </summary>
public class Usuario
{
    public const string EstadoActivo = "ACTIVO";

    public required string NombreUsuario { get; init; }
    public required string CodigoEmpleado { get; init; }
    public required string NombreEmpleado { get; init; }
    public required string HashClave { get; init; }
    public required string Estado { get; init; }

    public bool EstaActivo() => Estado == EstadoActivo;

    public bool VerificarClave(string clave)
    {
        byte[] calculado = Encoding.ASCII.GetBytes(CalcularHash(clave));
        byte[] guardado = Encoding.ASCII.GetBytes(HashClave.Trim().ToLowerInvariant());
        // Comparación en tiempo constante: no revela cuántos caracteres coinciden
        return CryptographicOperations.FixedTimeEquals(calculado, guardado);
    }

    public static string CalcularHash(string clave) =>
            Convert.ToHexStringLower(SHA256.HashData(Encoding.UTF8.GetBytes(clave)));
}
