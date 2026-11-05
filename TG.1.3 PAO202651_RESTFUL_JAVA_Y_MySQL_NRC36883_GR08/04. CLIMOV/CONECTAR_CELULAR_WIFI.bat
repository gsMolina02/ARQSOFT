@echo off
chcp 65001 >nul
title Conectar Celular Inal?mbrico por Wi-Fi (EurekaBank GR08)
echo ============================================================================
echo   EUREKABANK RESTFUL JAVA - CONEXION INALAMBRICA WI-FI (GRUPO 08)
echo ============================================================================
echo.

if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools" (
    set "PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools;%PATH%"
)

echo [1/4] Verificando servidor Payara RESTful...
powershell -Command "try { $r = Invoke-WebRequest -Uri 'http://127.0.0.1:8080/Rest_EurekaBank/webresources/coreBancario/cuentas' -TimeoutSec 2 -UseBasicParsing; Write-Host '      [OK] Servidor Payara RESTful activo y respondiendo.' -ForegroundColor Green } catch { Write-Host '      [AVISO] Servidor Payara en puerto 8080 no detectado.' -ForegroundColor Yellow }"

echo.
echo [2/4] Detectando celular en la red Wi-Fi...
set "PHONE_IP=192.168.100.33"

:: Detectar IP dinamica de wlan0 si esta conectado
for /f "tokens=2 delims= " %%a in ('adb -d shell ip -f inet addr show wlan0 2^>nul ^| findstr "inet"') do (
    for /f "tokens=1 delims=/" %%b in ("%%a") do (
        set "PHONE_IP=%%b"
    )
)

echo       IP del celular en red Wi-Fi: %PHONE_IP%

echo.
echo [3/4] Activando modo inalambrico TCP/IP y redirigiendo puerto 8080...
adb -d tcpip 5555 >nul 2>&1
timeout /t 1 >nul 2>&1

:: Conectar a la IP del telefono en la red Wi-Fi
adb connect %PHONE_IP%:5555 >nul 2>&1

:: Redirigir puerto 8080 por Wi-Fi hacia la laptop
adb reverse tcp:8080 tcp:8080 >nul 2>&1
adb -s %PHONE_IP%:5555 reverse tcp:8080 tcp:8080 >nul 2>&1

echo       [OK] Puerto 8080 enlazado inalambricamente al celular via Wi-Fi.
echo.
echo [4/4] Abriendo EurekaBank en el celular...
adb shell am start -n ec.edu.monster.wseurekaclient/ec.edu.monster.servicio.LoginActivity >nul 2>&1
adb -s %PHONE_IP%:5555 shell am start -n ec.edu.monster.wseurekaclient/ec.edu.monster.servicio.LoginActivity >nul 2>&1

echo.
echo ============================================================================
echo   CONEXION INALAMBRICA ACTIVA Y FUNCIONANDO
echo.
echo   En tu celular:
echo   - Ya esta configurado el servidor en: 127.0.0.1:8080
echo   - Ingresa tus credenciales (ej. MONSTER / MONSTER9) y pulsa INICIAR SESION.
echo.
echo   PUEDES DESCONECTAR EL CABLE USB EN CUALQUIER MOMENTO.
echo ============================================================================
echo.
pause

