@echo off
rem ============================================================================
rem  EurekaBank GR08 - Servidor RESTful .NET (WS_EurekaBank_Dotnet_RESTFUL, puerto 5000)
rem  Prepara esta computadora para que los clientes de OTRAS computadoras
rem  (consola, escritorio, web o movil) se conecten a su servidor:
rem    1. Abre el puerto 5000 en el firewall de Windows (solo la primera vez).
rem    2. Verifica que el servicio MySQL80 este iniciado (solo el servidor usa MySQL).
rem    3. Muestra las IP de esta computadora para escribirlas en los clientes.
rem    4. Arranca el servidor REST.
rem ============================================================================
chcp 65001 >nul
setlocal
title EurekaBank GR08 - Servidor RESTful .NET (puerto 5000)

rem Abrir el puerto en el firewall necesita permisos de administrador
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo Solicitando permisos de administrador...
    powershell -NoProfile -Command "Start-Process -FilePath '%~f0' -Verb RunAs"
    exit /b
)

set "REGLA=EurekaBank GR08 REST 5000"
netsh advfirewall firewall show rule name="%REGLA%" >nul 2>&1
if %errorlevel% neq 0 (
    netsh advfirewall firewall add rule name="%REGLA%" dir=in action=allow protocol=TCP localport=5000 profile=any >nul
    echo [OK] Se abrió el puerto 5000 en el firewall de Windows.
) else (
    echo [OK] El puerto 5000 ya estaba abierto en el firewall de Windows.
)

sc query MySQL80 | find "RUNNING" >nul 2>&1
if %errorlevel% neq 0 (
    echo [AVISO] El servicio MySQL80 no está iniciado: el servidor no podrá leer ni guardar cuentas.
    echo         Inícielo en services.msc o con: net start MySQL80
) else (
    echo [OK] El servicio MySQL80 está iniciado.
)

echo.
echo IP de esta computadora. En los clientes de la otra computadora use "Configurar servidor"
echo (o "Cambiar servidor" en el móvil) y escriba una de estas IP:
powershell -NoProfile -Command "Get-NetIPConfiguration | Where-Object { $_.IPv4DefaultGateway -and $_.NetAdapter.Status -eq 'Up' } | ForEach-Object { '    ' + $_.IPv4Address.IPAddress + '    (' + $_.InterfaceAlias + ')' }"
echo.
echo Para comprobar la conexion, en la otra computadora abra en el navegador esta direccion (copiela tal cual).
echo Debe mostrar {"codigo":401,"mensaje":"Acceso denegado: no se encontró el token..."}:
powershell -NoProfile -Command "Get-NetIPConfiguration | Where-Object { $_.IPv4DefaultGateway -and $_.NetAdapter.Status -eq 'Up' } | ForEach-Object { '    http://' + $_.IPv4Address.IPAddress + ':5000/api/cuentas/00100001/movimientos' }"
echo.
echo Ambas computadoras deben estar en la misma red (de preferencia el hotspot de un celular).
echo.

cd /d "%~dp0WS_EurekaBank_Dotnet_RESTFUL"
dotnet run --launch-profile http
pause
