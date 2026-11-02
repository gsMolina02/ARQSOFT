@echo off
chcp 65001 >nul
title Conectar Celular Inalámbrico por Wi-Fi - SOAP .NET (GR08)
echo ============================================================================
echo   EUREKABANK SOAP .NET - CONEXION INALAMBRICA WI-FI (GRUPO 08)
echo ============================================================================
echo.

set "PATH=C:\Users\danie\AppData\Local\Android\Sdk\platform-tools;%PATH%"

echo [1/3] Detectando celular en la red Wi-Fi...
set "PHONE_IP=192.168.100.33"

for /f "tokens=2 delims= " %%a in ('adb -d shell ip -f inet addr show wlan0 2^>nul ^| findstr "inet"') do (
    for /f "tokens=1 delims=/" %%b in ("%%a") do (
        set "PHONE_IP=%%b"
    )
)

echo       IP del celular en red Wi-Fi: %PHONE_IP%

echo.
echo [2/3] Activando modo inalambrico TCP/IP y redirigiendo puertos 62278 y 5150...
adb -d tcpip 5555 >nul 2>&1
timeout /t 1 >nul 2>&1

adb connect %PHONE_IP%:5555 >nul 2>&1

adb reverse tcp:62278 tcp:62278 >nul 2>&1
adb reverse tcp:5150 tcp:5150 >nul 2>&1
adb -s %PHONE_IP%:5555 reverse tcp:62278 tcp:62278 >nul 2>&1
adb -s %PHONE_IP%:5555 reverse tcp:5150 tcp:5150 >nul 2>&1

echo       [OK] Puertos enlazados inalambricamente al celular via Wi-Fi.
echo.
echo [3/3] Abriendo EurekaBank en el celular...
adb shell am start -n com.companyname.CLIMOV_EurekaBank_SOAPDOTNET_GR08/crc649dcba7d4ce91630b.MainActivity >nul 2>&1

echo.
echo ============================================================================
echo   CONEXION INALAMBRICA ACTIVA Y FUNCIONANDO
echo.
echo   En tu celular:
echo   - Servidor por defecto en: http://127.0.0.1:62278
echo   - Ingresa tus credenciales (ej. MONSTER / MONSTER9) y pulsa INICIAR SESION.
echo.
echo   PUEDES DESCONECTAR EL CABLE USB EN CUALQUIER MOMENTO.
echo ============================================================================
echo.
pause
