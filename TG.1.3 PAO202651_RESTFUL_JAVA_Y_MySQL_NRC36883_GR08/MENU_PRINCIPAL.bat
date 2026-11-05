@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Panel Principal (GR08)
set "ROOT=%~dp0"

:menu
cls
echo ============================================================================
echo   EUREKABANK RESTFUL JAVA - PANEL PRINCIPAL (GRUPO 08)
echo ============================================================================
echo.
echo   [0] SERVIDOR   : Iniciar Payara Server + MySQL (Solo en Laptop 1)
echo   [1] ESCRITORIO : Iniciar Cliente Swing (04. CLIESC)
echo   [2] CONSOLA    : Iniciar Cliente de Consola (04. CLICON)
echo   [3] WEB        : Abrir Portal Web JSP (04. CLIWEB)
echo   [4] MOVIL      : Gestionar Cliente Android (04. CLIMOV)
echo   [5] FIREWALL   : Desbloquear Puertos LAN para otra Laptop (04. SERVIDOR)
echo   [6] CELULAR    : Conectar Celular Inalambrico Wi-Fi (04. CLIMOV)
echo   [7] TUNEL      : Iniciar Puente Cloudflare con Web (04. SERVIDOR)
echo   [8] CONFIG     : Cambiar IP / URL del Servidor
echo   [9] Salir
echo.
set /p op="Seleccione una opcion (0-9): "

if "%op%"=="0" ( start "EurekaBank REST - Servidor Payara" cmd /c ""%ROOT%04. SERVIDOR\INICIAR_SERVIDOR_REST.bat"" & goto menu )
if "%op%"=="1" ( start "EurekaBank REST - Escritorio Swing" cmd /c ""%ROOT%04. CLIESC\INICIAR_ESCRITORIO.bat"" & goto menu )
if "%op%"=="2" ( start "EurekaBank REST - Consola" cmd /c ""%ROOT%04. CLICON\INICIAR_CONSOLA.bat"" & goto menu )
if "%op%"=="3" ( start "EurekaBank REST - Web JSP" cmd /c ""%ROOT%04. CLIWEB\INICIAR_WEB.bat"" & goto menu )
if "%op%"=="4" ( start "EurekaBank REST - Movil" cmd /c ""%ROOT%04. CLIMOV\INICIAR_MOVIL.bat"" & goto menu )
if "%op%"=="5" ( call "%ROOT%04. SERVIDOR\DESBLOQUEAR_FIREWALL.bat" & goto menu )
if "%op%"=="6" ( call "%ROOT%04. CLIMOV\CONECTAR_CELULAR_WIFI.bat" & goto menu )
if "%op%"=="7" ( start "EurekaBank REST - Tunel Cloudflare" cmd /c ""%ROOT%04. SERVIDOR\INICIAR_TUNEL_CLOUD.bat"" & goto menu )
if "%op%"=="8" ( call "%ROOT%CAMBIAR_IP_SERVIDOR.bat" & goto menu )
if "%op%"=="9" ( exit /b )
goto menu
