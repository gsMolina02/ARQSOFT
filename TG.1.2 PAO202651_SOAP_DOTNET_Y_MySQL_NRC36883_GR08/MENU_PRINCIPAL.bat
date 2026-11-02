@echo off
chcp 65001 >nul
title EurekaBank SOAP .NET - Panel Principal (GR08)
:menu
cls
echo ============================================================================
echo   EUREKABANK SOAP .NET - PANEL PRINCIPAL (GRUPO 08)
echo ============================================================================
echo.
echo   [0] SERVIDOR   : Iniciar IIS Express + MySQL (04. SERVIDOR)
echo   [1] ESCRITORIO : Iniciar Cliente Windows Forms (04. CLIESC)
echo   [2] CONSOLA    : Iniciar Cliente de Consola (04. CLICON)
echo   [3] WEB        : Abrir Portal Web ASP.NET Core (04. CLIWEB)
echo   [4] MOVIL      : Gestionar Cliente .NET MAUI (04. CLIMOV)
echo   [5] FIREWALL   : Desbloquear Puertos LAN para otra Laptop (04. SERVIDOR)
echo   [6] CELULAR    : Conectar Celular Inalambrico Wi-Fi (04. CLIMOV)
echo   [7] TUNEL      : Iniciar Puente Cloudflare con Web (04. SERVIDOR)
echo   [8] CONFIG     : Cambiar IP / URL del Servidor
echo   [9] Salir
echo.
set /p op="Seleccione una opcion (0-9): "

if "%op%"=="0" ( call "%~dp004. SERVIDOR\INICIAR_SERVIDOR_SOAP.bat" & goto menu )
if "%op%"=="1" ( call "%~dp004. CLIESC\INICIAR_ESCRITORIO.bat" & goto menu )
if "%op%"=="2" ( call "%~dp004. CLICON\INICIAR_CONSOLA.bat" & goto menu )
if "%op%"=="3" ( call "%~dp004. CLIWEB\INICIAR_WEB.bat" & goto menu )
if "%op%"=="4" ( call "%~dp004. CLIMOV\INICIAR_MOVIL.bat" & goto menu )
if "%op%"=="5" ( call "%~dp004. SERVIDOR\DESBLOQUEAR_FIREWALL.bat" & goto menu )
if "%op%"=="6" ( call "%~dp004. CLIMOV\CONECTAR_CELULAR_WIFI.bat" & goto menu )
if "%op%"=="7" ( call "%~dp004. SERVIDOR\INICIAR_TUNEL_CLOUD.bat" & goto menu )
if "%op%"=="8" ( call "%~dp0CAMBIAR_IP_SERVIDOR.bat" & goto menu )
if "%op%"=="9" ( exit /b )
goto menu
