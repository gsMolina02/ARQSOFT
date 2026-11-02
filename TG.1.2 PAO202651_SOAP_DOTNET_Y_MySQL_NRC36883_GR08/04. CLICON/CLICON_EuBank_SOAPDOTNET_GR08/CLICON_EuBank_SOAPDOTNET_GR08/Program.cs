using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using CLICON_EuBank_SOAPDOTNET_GR08.Models;
using CLICON_EuBank_SOAPDOTNET_GR08.Services;
using CLICON_EuBank_SOAPDOTNET_GR08.Views;

class Program
{
    static async Task Main(string[] args)
    {
        Console.OutputEncoding = System.Text.Encoding.UTF8;
        Console.InputEncoding = System.Text.Encoding.UTF8;

        var service = new EuBankApiService();
        var view = new ConsoleView();

        view.ShowWelcome();

        // Login con máximo 3 intentos
        int intentos = 0;
        const int MAX_INTENTOS = 3;
        string currentUser = null;

        while (intentos < MAX_INTENTOS)
        {
            var creds = view.GetLoginCredentials();
            string usuario = creds.Item1;
            string clave = creds.Item2;

            if (string.IsNullOrEmpty(usuario))
            {
                view.ShowError("El usuario no puede estar vacío.");
                continue;
            }

            if (string.IsNullOrEmpty(clave))
            {
                view.ShowError("La contraseña no puede estar vacía.");
                continue;
            }

            view.ShowAuthenticating();
            try
            {
                var loginRequest = new LoginRequest { Usuario = usuario, Clave = clave };
                if (await service.LoginAsync(loginRequest))
                {
                    view.ShowAuthOk();
                    view.ShowLoginSuccess(usuario);
                    currentUser = usuario;
                    break;
                }
                else
                {
                    view.ShowAuthFail();
                    intentos++;
                    view.ShowLoginError("Autenticación fallida. (Intento " + intentos + "/" + MAX_INTENTOS + ")");
                    if (intentos >= MAX_INTENTOS)
                    {
                        view.ShowError("Máximo de intentos alcanzado.");
                        break;
                    }
                    if (!view.AskRetry())
                    {
                        break;
                    }
                }
            }
            catch (Exception ex)
            {
                view.ShowAuthFail();
                intentos++;
                view.ShowLoginError("Error inesperado: " + ex.Message + " (Intento " + intentos + "/" + MAX_INTENTOS + ")");
                if (intentos >= MAX_INTENTOS)
                {
                    view.ShowError("Máximo de intentos alcanzado.");
                    break;
                }
                if (!view.AskRetry())
                {
                    break;
                }
            }
        }

        if (currentUser == null)
        {
            view.ShowMessage("\nNo se pudo autenticar. Saliendo del sistema...");
            view.ShowMessage("\nPresione cualquier tecla para salir...");
            Console.ReadKey();
            return;
        }

        // Resolucion de cuenta y nombre del usuario autenticado
        string u = (currentUser ?? "").Trim().ToLower();
        string titular = currentUser;
        string miCuenta = "00200001";
        if (u == "monster" || u == "eduardo" || u == "ecampana")
        {
            titular = "Eduardo Mauricio Campaña Ortega";
            miCuenta = "00300001";
        }
        else if (u == "gustavo" || u == "gmolina")
        {
            titular = "Gustavo Molina";
            miCuenta = "00200001";
        }
        else if (u == "daniela" || u == "dtituana")
        {
            titular = "Daniela Tituaña";
            miCuenta = "00200002";
        }

        // Dashboard
        while (true)
        {
            int opcion = view.ShowDashboardMenu(titular, currentUser, miCuenta);

            switch (opcion)
            {
                case 1:
                    try
                    {
                        var movimientos = await service.GetMovementsAsync(miCuenta);
                        view.DisplayMisMovimientos(titular, miCuenta, movimientos != null ? movimientos : new List<Movement>());
                    }
                    catch (Exception ex)
                    {
                        view.ShowError("Error al cargar tus movimientos: " + ex.Message);
                    }
                    break;

                case 2:
                    try
                    {
                        var cuentas = await service.GetAllAccountsAsync();
                        view.DisplayCuentas(cuentas != null ? cuentas : new List<Account>());
                    }
                    catch (Exception ex)
                    {
                        view.ShowError("Error al cargar cuentas: " + ex.Message);
                    }
                    break;

                case 3:
                    try
                    {
                        string filtro = view.GetMovimientoFilter();
                        var movimientos = await service.GetMovementsAsync(filtro);
                        view.DisplayMovimientos(movimientos != null ? movimientos : new List<Movement>());
                    }
                    catch (Exception ex)
                    {
                        view.ShowError("Error al cargar movimientos: " + ex.Message);
                    }
                    break;

                case 4:
                    try
                    {
                        var cuentas = await service.GetAllAccountsAsync(); var reqDep = view.GetDepositoData(cuentas, miCuenta);
                        if (reqDep != null)
                        {
                            var resDep = await service.ProcessMovementAsync(reqDep);
                            view.ShowOperationResult(resDep);
                        }
                    }
                    catch (Exception ex)
                    {
                        view.ShowError("Error al registrar depósito: " + ex.Message);
                    }
                    break;

                case 5:
                    try
                    {
                        var cuentas = await service.GetAllAccountsAsync(); var reqRet = view.GetRetiroData(cuentas, miCuenta);
                        if (reqRet != null)
                        {
                            var resRet = await service.ProcessMovementAsync(reqRet);
                            view.ShowOperationResult(resRet);
                        }
                    }
                    catch (Exception ex)
                    {
                        view.ShowError("Error al registrar retiro: " + ex.Message);
                    }
                    break;

                case 6:
                    try
                    {
                        var cuentas = await service.GetAllAccountsAsync(); var reqTra = view.GetTransferenciaData(cuentas, miCuenta);
                        if (reqTra != null)
                        {
                            var resTra = await service.ProcessMovementAsync(reqTra);
                            view.ShowOperationResult(resTra);
                        }
                    }
                    catch (Exception ex)
                    {
                        view.ShowError("Error al registrar transferencia: " + ex.Message);
                    }
                    break;

                case 7:
                    view.ShowMessage("\n¡Gracias por usar EuBank! ¡Adiós!");
                    view.ShowMessage("\nPresione cualquier tecla para salir...");
                    Console.ReadKey();
                    return;

                default:
                    view.ShowError("Opción no válida.");
                    break;
            }
        }
    }
}
