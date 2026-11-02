@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Despliegue en Red Local LAN (GR08)

:: Verificar permisos de Administrador de forma segura
fltmc >nul 2>&1
if %errorlevel% neq 0 (
    cls
    echo ============================================================================
    echo   [!] ATENCION: SE REQUIEREN PERMISOS DE ADMINISTRADOR
    echo ============================================================================
    echo.
    echo   Para aplicar las reglas del Firewall de Windows:
    echo   1. Haga CLIC DERECHO sobre este archivo.
    echo   2. Seleccione: "Ejecutar como administrador".
    echo.
    echo ============================================================================
    pause
    exit /b
)

echo ============================================================================
echo   EUREKABANK RESTFUL JAVA + MYSQL - DESPLIEGUE EN RED LOCAL LAN (GRUPO 08)
echo   Autores: Gustavo Molina y Daniela Tituana - NRC 36883
echo ============================================================================
echo.

:: Detectar IP real de la tarjeta Wi-Fi
for /f "tokens=*" %%a in ('powershell -Command "(Get-NetIPAddress -InterfaceAlias 'Wi-Fi' -AddressFamily IPv4 -ErrorAction SilentlyContinue).IPAddress"') do (
    set "LOCAL_IP=%%a"
)
if "%LOCAL_IP%"=="" set "LOCAL_IP=192.168.100.15"

echo [1/4] Direccion IP LAN detectada en este equipo servidor: %LOCAL_IP%
echo.

echo [2/4] Habilitando puertos 8080 (Payara REST) y 3306 (MySQL) en Firewall...
powershell -Command "Get-NetConnectionProfile | Set-NetConnectionProfile -NetworkCategory Private" >nul 2>&1
netsh advfirewall firewall add rule name="EurekaBank_REST_8080" dir=in action=allow protocol=TCP localport=8080 profile=any >nul 2>&1
netsh advfirewall firewall add rule name="EurekaBank_MySQL_3306" dir=in action=allow protocol=TCP localport=3306 profile=any >nul 2>&1
netsh advfirewall set allprofiles state off >nul 2>&1
echo       Puertos 8080 y 3306 habilitados para conexiones entrantes en la red local.
echo.

echo [3/4] Endpoints RESTful JSON disponibles en la red local LAN:
echo       - Base URI REST       : http://%LOCAL_IP%:8080/Rest_EurekaBank/webresources/coreBancario
echo       - GET Movimientos     : http://%LOCAL_IP%:8080/Rest_EurekaBank/webresources/coreBancario/movimientos/00100001
echo       - POST Login / Op     : /login , /deposito , /retiro , /transferencia
echo       - Cliente Web JSP     : http://%LOCAL_IP%:8080/RestClienteWeb/Login.jsp
echo       - Base de Datos MySQL : %LOCAL_IP%:3306 (esquema: eurekabank)
echo.

echo [4/4] Configurando IP %LOCAL_IP% en clientes locales...
powershell -ExecutionPolicy Bypass -File "%~dp0CONFIGURAR_IP_CLIENTES.ps1" -NuevaIP "%LOCAL_IP%" >nul 2>&1

echo ============================================================================
echo   Para conectar los clientes desde otra computadora hacia este servidor:
echo   1. En la otra laptop ejecuta: CAMBIAR_IP_SERVIDOR.bat %LOCAL_IP%
echo   2. O en el navegador web de la otra laptop abre:
echo      http://%LOCAL_IP%:8080/RestClienteWeb/Login.jsp
echo ============================================================================
pause
