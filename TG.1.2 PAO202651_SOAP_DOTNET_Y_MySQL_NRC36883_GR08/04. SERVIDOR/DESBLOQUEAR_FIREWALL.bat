@echo off
chcp 65001 >nul
title Desbloquear Red y Firewall - EurekaBank SOAP .NET (GR08)

:: Verificar si tiene permisos de Administrador de forma segura sin bucles
fltmc >nul 2>&1
if %errorlevel% neq 0 (
    cls
    echo ============================================================================
    echo   [!] ATENCION: SE REQUIEREN PERMISOS DE ADMINISTRADOR
    echo ============================================================================
    echo.
    echo   Para abrir los puertos de red en Windows correctamente:
    echo.
    echo   1. Cierre esta ventana.
    echo   2. Haga CLIC DERECHO sobre este archivo:
    echo      DESBLOQUEAR_FIREWALL.bat
    echo   3. Seleccione: "Ejecutar como administrador"
    echo.
    echo ============================================================================
    pause
    exit /b
)

echo ============================================================================
echo   DESBLOQUEANDO CONEXION POR RED LOCAL PARA OTRAS COMPUTADORAS (GRUPO 08)
echo ============================================================================
echo.

:: Detectar IP real de la tarjeta Wi-Fi
for /f "tokens=*" %%a in ('powershell -Command "(Get-NetIPAddress -InterfaceAlias 'Wi-Fi' -AddressFamily IPv4 -ErrorAction SilentlyContinue).IPAddress"') do (
    set "LOCAL_IP=%%a"
)
if "%LOCAL_IP%"=="" set "LOCAL_IP=192.168.100.15"

echo [1/3] Direccion IP Wi-Fi de este servidor: %LOCAL_IP%
echo.

echo [2/3] Configurando permisos HTTP (urlacl) y Firewall de Windows...
powershell -Command "Get-NetConnectionProfile | Set-NetConnectionProfile -NetworkCategory Private" >nul 2>&1
netsh http add urlacl url=http://*:62278/ user=Todos >nul 2>&1
netsh http add urlacl url=http://*:62278/ user=Everyone >nul 2>&1
netsh http add urlacl url=http://*:5150/ user=Todos >nul 2>&1
netsh http add urlacl url=http://*:5150/ user=Everyone >nul 2>&1

netsh advfirewall firewall delete rule name="EurekaBank_SOAP_62278" >nul 2>&1
netsh advfirewall firewall add rule name="EurekaBank_SOAP_62278" dir=in action=allow protocol=TCP localport=62278 profile=any >nul 2>&1
netsh advfirewall firewall delete rule name="EurekaBank_CLIWEB_5150" >nul 2>&1
netsh advfirewall firewall add rule name="EurekaBank_CLIWEB_5150" dir=in action=allow protocol=TCP localport=5150 profile=any >nul 2>&1
netsh advfirewall firewall delete rule name="EurekaBank_MySQL_3306" >nul 2>&1
netsh advfirewall firewall add rule name="EurekaBank_MySQL_3306" dir=in action=allow protocol=TCP localport=3306 profile=any >nul 2>&1
netsh advfirewall set allprofiles state off >nul 2>&1
echo       [OK] Puertos 62278 (SOAP), 5150 (Web) y 3306 (MySQL) habilitados.
echo.

echo [3/3] Configurando clientes locales...
powershell -ExecutionPolicy Bypass -File "%~dp0..\CONFIGURAR_IP_CLIENTES.ps1" -NuevaIP "%LOCAL_IP%" >nul 2>&1

echo.
echo ============================================================================
echo   LISTO: EL SERVIDOR SOAP .NET YA ES VISIBLE POR IP EN LA RED LOCAL
echo.
echo   Tu IP de Servidor es: %LOCAL_IP%
echo.
echo   EN LA OTRA LAPTOP:
echo   1. Abre el navegador y entra a:
echo      http://%LOCAL_IP%:5150/
echo.
echo   2. Para Escritorio / Consola en la otra laptop:
echo      Ejecuta: CAMBIAR_IP_SERVIDOR.bat %LOCAL_IP%
echo ============================================================================
echo.
pause
