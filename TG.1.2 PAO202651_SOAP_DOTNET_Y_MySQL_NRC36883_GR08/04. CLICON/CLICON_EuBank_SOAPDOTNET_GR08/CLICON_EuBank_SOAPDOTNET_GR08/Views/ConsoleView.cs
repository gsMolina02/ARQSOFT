using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using CLICON_EuBank_SOAPDOTNET_GR08.Models;

namespace CLICON_EuBank_SOAPDOTNET_GR08.Views
{
    public class ConsoleView
    {
        public void ShowWelcome()
        {
            Console.OutputEncoding = System.Text.Encoding.UTF8;
            Console.WriteLine();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("╔═══════════════════════════════════════════╗");
            Console.WriteLine("║  Cliente SOAP - EuBank (Consola)          ║");
            Console.WriteLine("╚═══════════════════════════════════════════╝");
            Console.ResetColor();
        }

        public Tuple<string, string> GetLoginCredentials()
        {
            Console.WriteLine();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("╔══════════════════════════════════════════╗");
            Console.WriteLine("║         Sistema de Login - EuBank        ║");
            Console.WriteLine("╚══════════════════════════════════════════╝");
            Console.ResetColor();

            Console.ForegroundColor = ConsoleColor.Green;
            Console.Write("\nUsuario: ");
            Console.ResetColor();
            string usuario = Console.ReadLine();

            Console.ForegroundColor = ConsoleColor.Green;
            Console.Write("Contraseña: ");
            Console.ResetColor();
            string clave = Console.ReadLine();

            return Tuple.Create(usuario, clave);
        }

        public void ShowLoginSuccess(string user)
        {
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine("\n✓ Autenticación exitosa. Bienvenido, " + user + "!");
            Console.ResetColor();
        }

        public void ShowLoginError(string message)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("\n✗ " + message);
            Console.ResetColor();
        }

        public void ShowError(string message)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("\n✗ " + message);
            Console.ResetColor();
        }

        public void ShowMessage(string message)
        {
            Console.WriteLine(message);
        }

        public bool AskRetry()
        {
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.Write("¿Desea intentar de nuevo? (s/n): ");
            Console.ResetColor();
            string opcion = Console.ReadLine();
            if (opcion != null)
                opcion = opcion.Trim().ToLower();
            return opcion == "s";
        }

        public void ShowAuthenticating()
        {
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.Write("\nAutenticando (SOAP)...");
            Console.ResetColor();
        }

        public void ShowAuthOk()
        {
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine(" ✓");
            Console.ResetColor();
        }

        public void ShowAuthFail()
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine(" ✗");
            Console.ResetColor();
        }

        public int ShowDashboardMenu(string titular, string user, string cuenta)
        {
            Console.WriteLine();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("╔══════════════════════════════════════════════════════════╗");
            Console.WriteLine("║               Dashboard - EuBank (SOAP)                  ║");
            Console.WriteLine("╠══════════════════════════════════════════════════════════╣");
            Console.WriteLine(string.Format("║ 👤 Titular: {0,-43} ║", titular + " (" + user + ")"));
            Console.WriteLine(string.Format("║ 💳 Tu Cuenta Principal: {0,-32} ║", cuenta));
            Console.WriteLine("╚══════════════════════════════════════════════════════════╝");
            Console.ResetColor();
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine("\n1. ⭐ Ver Mis Movimientos (Tu Cuenta: " + cuenta + ")");
            Console.ResetColor();
            Console.WriteLine("2. Ver Cuentas de Clientes");
            Console.WriteLine("3. Ver Movimientos (por cuenta o todos)");
            Console.WriteLine("4. Registrar Depósito");
            Console.WriteLine("5. Registrar Retiro");
            Console.WriteLine("6. Registrar Transferencia");
            Console.WriteLine("7. Salir");
            Console.ForegroundColor = ConsoleColor.Green;
            Console.Write("\nSeleccione una opción: ");
            Console.ResetColor();
            string input = Console.ReadLine();
            return int.TryParse(input, out int opcion) ? opcion : 0;
        }

        public void DisplayMisMovimientos(string titular, string cuenta, List<Movement> movimientos)
        {
            Console.WriteLine();
            Console.ForegroundColor = ConsoleColor.Magenta;
            Console.WriteLine("╔════════════════════════════════════════════════════════════════════════════╗");
            Console.WriteLine("║                        ⭐ MIS MOVIMIENTOS BANCARIOS                        ║");
            Console.WriteLine(string.Format("║ Titular: {0,-35} | Cuenta: {1,-20} ║", titular, cuenta));
            Console.WriteLine("╚════════════════════════════════════════════════════════════════════════════╝");
            Console.ResetColor();

            if (movimientos != null && movimientos.Any())
            {
                Console.ForegroundColor = ConsoleColor.Cyan;
                string header = string.Format("{0,-6} | {1,-12} | {2,-24} | {3,-15} | {4,12} | {5,12}",
                        "NRO", "FECHA", "TIPO", "REFERENCIA", "IMPORTE", "SALDO");
                Console.WriteLine(header);
                Console.WriteLine(new string('-', header.Length));
                Console.ResetColor();

                foreach (var m in movimientos)
                {
                    string tipo = m.Tipo ?? "N/A";
                    string refStr = !string.IsNullOrEmpty(m.Referencia) ? m.Referencia : "-";
                    Console.WriteLine("{0,-6} | {1,-12} | {2,-24} | {3,-15} | {4,12:N2} | {5,12:N2}",
                            "#" + m.Numero,
                            m.Fecha.ToString("dd/MM/yyyy"),
                            tipo,
                            refStr,
                            m.Importe,
                            m.SaldoActual);
                }

                Console.ForegroundColor = ConsoleColor.Cyan;
                Console.WriteLine(new string('-', header.Length));
                Console.ResetColor();
                var ultimo = movimientos.FirstOrDefault();
                if (ultimo != null)
                {
                    Console.ForegroundColor = ConsoleColor.Green;
                    Console.WriteLine("💰 Saldo Actual de tu Cuenta ({0}): {1:N2}", cuenta, ultimo.SaldoActual);
                    Console.ResetColor();
                }
            }
            else
            {
                Console.ForegroundColor = ConsoleColor.Yellow;
                Console.WriteLine("No se encontraron movimientos registrados para tu cuenta " + cuenta + ".");
                Console.ResetColor();
            }
            Console.WriteLine();
        }

        public void DisplayCuentas(List<Account> cuentas)
        {
            Console.WriteLine();
            if (cuentas.Any())
            {
                Console.ForegroundColor = ConsoleColor.Yellow;
                string header = string.Format("{0,-10} | {1,-30} | {2,-10} | {3,-10} | {4,12}",
                        "CUENTA", "CLIENTE", "MONEDA", "ESTADO", "SALDO");
                Console.WriteLine(header);
                Console.WriteLine(new string('-', header.Length));
                Console.ResetColor();

                foreach (var c in cuentas)
                {
                    Console.WriteLine("{0,-10} | {1,-30} | {2,-10} | {3,-10} | {4,12:N2}",
                            c.Codigo,
                            c.NombreCliente,
                            c.Moneda,
                            c.Estado,
                            c.Saldo);
                }

                Console.ForegroundColor = ConsoleColor.Yellow;
                Console.WriteLine(new string('-', header.Length));
                Console.ResetColor();
            }
            else
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("No se encontraron cuentas.");
                Console.ResetColor();
            }
            Console.WriteLine();
        }

        public string GetMovimientoFilter()
        {
            Console.ForegroundColor = ConsoleColor.Green;
            Console.Write("Ingrese el número de cuenta (ej. 00100001) o presione Enter para ver todos: ");
            Console.ResetColor();
            string input = Console.ReadLine();
            return input != null ? input.Trim() : "";
        }

        public void DisplayMovimientos(List<Movement> movimientos)
        {
            Console.WriteLine();
            if (movimientos.Any())
            {
                Console.ForegroundColor = ConsoleColor.Yellow;
                string header = string.Format("{0,-10} | {1,-4} | {2,-12} | {3,-25} | {4,12} | {5,12}",
                        "CUENTA", "NRO", "FECHA", "TIPO", "IMPORTE", "SALDO");
                Console.WriteLine(header);
                Console.WriteLine(new string('-', header.Length));
                Console.ResetColor();

                foreach (var m in movimientos)
                {
                    string tipo = m.Tipo != null ? m.Tipo : "N/A";
                    Console.WriteLine("{0,-10} | {1,-4} | {2,-12} | {3,-25} | {4,12:N2} | {5,12:N2}",
                            m.CodigoCuenta,
                            m.Numero,
                            m.Fecha.ToString("dd/MM/yyyy"),
                            tipo,
                            m.Importe,
                            m.SaldoActual);
                }

                Console.ForegroundColor = ConsoleColor.Yellow;
                Console.WriteLine(new string('-', header.Length));
                Console.ResetColor();
            }
            else
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("No se encontraron movimientos.");
                Console.ResetColor();
            }
            Console.WriteLine();
        }

        public void MostrarListaCuentas(List<Account> cuentas)
        {
            if (cuentas == null || !cuentas.Any()) return;
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine("\nCuentas registradas disponibles para seleccionar:");
            for (int i = 0; i < cuentas.Count; i++)
            {
                var c = cuentas[i];
                Console.WriteLine(string.Format("  [{0,2}] {1} | {2,-25} | {3,-7} | Saldo: {4,10:N2}",
                    i + 1, c.Codigo, c.NombreCliente, c.Moneda, c.Saldo));
            }
            Console.WriteLine(new string('-', 72));
            Console.ResetColor();
        }

        private string ResolverCuenta(string input, List<Account> cuentas, string fallback)
        {
            if (string.IsNullOrWhiteSpace(input)) return fallback;
            input = input.Trim();
            if (int.TryParse(input, out int idx) && cuentas != null && idx >= 1 && idx <= cuentas.Count)
            {
                return cuentas[idx - 1].Codigo;
            }
            return input;
        }

        public MovementRequest GetDepositoData(List<Account> cuentas = null, string cuentaUsuario = "")
        {
            Console.WriteLine();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("╔══════════════════════════════════════════╗");
            Console.WriteLine("║            Registrar Depósito            ║");
            Console.WriteLine("╚══════════════════════════════════════════╝");
            Console.ResetColor();

            MostrarListaCuentas(cuentas);

            Console.ForegroundColor = ConsoleColor.Green;
            string prompt = !string.IsNullOrEmpty(cuentaUsuario)
                ? $"Número o Código de Cuenta [Enter = Tu cuenta {cuentaUsuario}]: "
                : "Número o Código de Cuenta: ";
            Console.Write(prompt);
            Console.ResetColor();
            string input = Console.ReadLine();
            string cuenta = ResolverCuenta(input, cuentas, cuentaUsuario);

            if (string.IsNullOrEmpty(cuenta))
            {
                ShowError("La cuenta no puede estar vacía.");
                return null;
            }

            Console.ForegroundColor = ConsoleColor.Green;
            Console.Write("Importe a Depositar: ");
            Console.ResetColor();
            string importeStr = Console.ReadLine();
            if (!decimal.TryParse(importeStr != null ? importeStr.Trim().Replace(',', '.') : "", NumberStyles.Any, CultureInfo.InvariantCulture, out decimal importe) || importe <= 0)
            {
                ShowError("Importe no válido.");
                return null;
            }

            return new MovementRequest { Tipo = "DEPOSITO", CuentaOrigen = cuenta, CuentaDestino = null, Importe = importe };
        }

        public MovementRequest GetRetiroData(List<Account> cuentas = null, string cuentaUsuario = "")
        {
            Console.WriteLine();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("╔══════════════════════════════════════════╗");
            Console.WriteLine("║             Registrar Retiro             ║");
            Console.WriteLine("╚══════════════════════════════════════════╝");
            Console.ResetColor();

            MostrarListaCuentas(cuentas);

            Console.ForegroundColor = ConsoleColor.Green;
            string prompt = !string.IsNullOrEmpty(cuentaUsuario)
                ? $"Número o Código de Cuenta [Enter = Tu cuenta {cuentaUsuario}]: "
                : "Número o Código de Cuenta: ";
            Console.Write(prompt);
            Console.ResetColor();
            string input = Console.ReadLine();
            string cuenta = ResolverCuenta(input, cuentas, cuentaUsuario);

            if (string.IsNullOrEmpty(cuenta))
            {
                ShowError("La cuenta no puede estar vacía.");
                return null;
            }

            Console.ForegroundColor = ConsoleColor.Green;
            Console.Write("Importe a Retirar: ");
            Console.ResetColor();
            string importeStr = Console.ReadLine();
            if (!decimal.TryParse(importeStr != null ? importeStr.Trim().Replace(',', '.') : "", NumberStyles.Any, CultureInfo.InvariantCulture, out decimal importe) || importe <= 0)
            {
                ShowError("Importe no válido.");
                return null;
            }

            return new MovementRequest { Tipo = "RETIRO", CuentaOrigen = cuenta, CuentaDestino = null, Importe = importe };
        }

        public MovementRequest GetTransferenciaData(List<Account> cuentas = null, string cuentaUsuario = "")
        {
            Console.WriteLine();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("╔══════════════════════════════════════════╗");
            Console.WriteLine("║         Registrar Transferencia          ║");
            Console.WriteLine("╚══════════════════════════════════════════╝");
            Console.ResetColor();

            MostrarListaCuentas(cuentas);

            Console.ForegroundColor = ConsoleColor.Green;
            string promptOrigen = !string.IsNullOrEmpty(cuentaUsuario)
                ? $"Seleccione Cuenta Origen [1-{cuentas?.Count ?? 0} o Enter = {cuentaUsuario}]: "
                : "Seleccione Cuenta Origen [1-{cuentas?.Count ?? 0} o Código]: ";
            Console.Write(promptOrigen);
            Console.ResetColor();
            string inputOrigen = Console.ReadLine();
            string origen = ResolverCuenta(inputOrigen, cuentas, cuentaUsuario);

            if (string.IsNullOrEmpty(origen))
            {
                ShowError("La cuenta origen no puede estar vacía.");
                return null;
            }

            Console.ForegroundColor = ConsoleColor.Green;
            Console.Write($"Seleccione Cuenta Destino (Beneficiario) [1-{cuentas?.Count ?? 0} o Código]: ");
            Console.ResetColor();
            string inputDestino = Console.ReadLine();
            string destino = ResolverCuenta(inputDestino, cuentas, "");

            if (string.IsNullOrEmpty(destino))
            {
                ShowError("La cuenta destino no puede estar vacía.");
                return null;
            }

            if (origen.Equals(destino, StringComparison.OrdinalIgnoreCase))
            {
                ShowError("Las cuentas de origen y destino no pueden ser la misma.");
                return null;
            }

            Console.ForegroundColor = ConsoleColor.Green;
            Console.Write("Importe a Transferir: ");
            Console.ResetColor();
            string importeStr = Console.ReadLine();
            if (!decimal.TryParse(importeStr != null ? importeStr.Trim().Replace(',', '.') : "", NumberStyles.Any, CultureInfo.InvariantCulture, out decimal importe) || importe <= 0)
            {
                ShowError("Importe no válido.");
                return null;
            }

            return new MovementRequest { Tipo = "TRANSFERENCIA", CuentaOrigen = origen, CuentaDestino = destino, Importe = importe };
        }

        public void ShowOperationResult(CrearMovimientoResponse response)
        {
            if (response.Success)
            {
                Console.ForegroundColor = ConsoleColor.Green;
                Console.WriteLine("\n¡Operación exitosa!");
                Console.ResetColor();
            }
            else
            {
                ShowError(response.Message != null ? response.Message : "Error desconocido al procesar el movimiento.");
            }
        }
    }
}
