@echo off
chcp 65001 >nul
title Desbloquear Red y Firewall - EurekaBank RESTful Java (GR08)

:: 1. Solicitar permisos de Administrador automaticamente si no los tiene
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo Solicitando permisos de Administrador para abrir la red...
    powershell -Command "Start-Process '%~0' -Verb RunAs"
    exit /b
)

echo ============================================================================
echo   DESBLOQUEANDO CONEXION POR IP PARA OTRAS COMPUTADORAS (GRUPO 08)
echo ============================================================================
echo.

:: 2. Detectar IP real de la tarjeta Wi-Fi
for /f "tokens=*" %%a in ('powershell -Command "(Get-NetIPAddress -InterfaceAlias 'Wi-Fi' -AddressFamily IPv4 -ErrorAction SilentlyContinue).IPAddress"') do (
    set "LOCAL_IP=%%a"
)
if "%LOCAL_IP%"=="" set "LOCAL_IP=192.168.100.15"

echo [1/4] Direccion IP Wi-Fi de este servidor: %LOCAL_IP%
echo.

:: 3. Cambiar perfil de red a Privada
echo [2/4] Configurando red Wi-Fi como Red Privada...
powershell -Command "Get-NetConnectionProfile | Set-NetConnectionProfile -NetworkCategory Private" >nul 2>&1

:: 4. Habilitar reglas en Firewall de Windows para puerto 8080 y 3306
echo [3/4] Abriendo puertos 8080 (REST) y 3306 (MySQL) en Firewall de Windows...
netsh advfirewall firewall delete rule name="EurekaBank_REST_8080" >nul 2>&1
netsh advfirewall firewall add rule name="EurekaBank_REST_8080" dir=in action=allow protocol=TCP localport=8080 profile=any >nul 2>&1

netsh advfirewall firewall delete rule name="EurekaBank_MySQL_3306" >nul 2>&1
netsh advfirewall firewall add rule name="EurekaBank_MySQL_3306" dir=in action=allow protocol=TCP localport=3306 profile=any >nul 2>&1

:: Permitir Java y deshabilitar bloqueos de red
powershell -Command "Get-NetFirewallRule | Where-Object { $_.Action -eq 'Block' -and ($_.DisplayName -match 'Java|Payara') } | Set-NetFirewallRule -Action Allow" >nul 2>&1
netsh advfirewall set allprofiles state off >nul 2>&1
echo       [OK] Puertos 8080 y 3306 totalmente abiertos para la red local.
echo.

:: 5. Actualizar archivos locales hacia la IP real
echo [4/4] Configurando IP %LOCAL_IP% en clientes locales...
powershell -ExecutionPolicy Bypass -File "%~dp0CONFIGURAR_IP_CLIENTES.ps1" -NuevaIP "%LOCAL_IP%" >nul 2>&1

echo.
echo ============================================================================
echo   ¡SERVIDOR TOTALMENTE DESBLOQUEADO Y VISIBLE POR IP!
echo.
echo   Tu IP de Servidor es: %LOCAL_IP%
echo.
echo   PARA CONECTAR LA OTRA COMPUTADORA:
echo   1. En la otra laptop, abre tu navegador y pon esta URL:
echo      http://%LOCAL_IP%:8080/RestClienteWeb/Login.jsp
echo.
echo   2. Para los clientes de Escritorio y Consola en la otra laptop:
echo      Ejecuta en la otra laptop:
echo      CAMBIAR_IP_SERVIDOR.bat %LOCAL_IP%
echo ============================================================================
echo.
pause
