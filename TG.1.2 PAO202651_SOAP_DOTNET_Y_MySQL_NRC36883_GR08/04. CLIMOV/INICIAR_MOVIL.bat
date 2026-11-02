@echo off
chcp 65001 >nul
title EurekaBank SOAP .NET - Cliente Movil MAUI (GR08)
echo ============================================================================
echo   EUREKABANK SOAP .NET - CLIENTE MOVIL .NET MAUI (GRUPO 08)
echo ============================================================================
echo.

set "ANDROID_SDK_ROOT=C:\Users\danie\AppData\Local\Android\Sdk"
set "PATH=C:\Users\danie\AppData\Local\Android\Sdk\platform-tools;%PATH%"
cd /d "%~dp0CLIMOV_EurekaBank_SOAPDOTNET_GR08\CLIMOV_EurekaBank_SOAPDOTNET_GR08"

:: 1. Detectar dispositivos ADB
echo [1/2] Detectando dispositivos Android...
adb devices
echo.

:: 2. Configurar reverse ports para depuracion
adb reverse tcp:62278 tcp:62278 >nul 2>&1
adb reverse tcp:5150 tcp:5150 >nul 2>&1

echo ============================================================================
echo   OPCIONES DISPONIBLES:
echo   [1] Compilar y ejecutar en celular conectado (dotnet build -t:Run)
echo   [2] Compilar APK Release (dotnet publish)
echo   [3] Abrir solucion en Visual Studio
echo   [4] Configurar IP / URL del servidor
echo   [5] Conectar celular inalambrico por Wi-Fi
echo ============================================================================
echo.
set /p op="Seleccione una opcion (1-5): "

if "%op%"=="1" (
    echo.
    echo Compilando y ejecutando en dispositivo Android...
    dotnet build -t:Run -f net9.0-android
    pause
    goto :fin
)

if "%op%"=="2" (
    echo.
    echo Generando APK...
    dotnet publish -f net9.0-android -c Release
    pause
    goto :fin
)

if "%op%"=="3" (
    start "" "%~dp0CLIMOV_EurekaBank_SOAPDOTNET_GR08\CLIMOV_EurekaBank_SOAPDOTNET_GR08.sln"
    goto :fin
)

if "%op%"=="4" (
    call "%~dp0..\CAMBIAR_IP_SERVIDOR.bat"
    goto :fin
)

if "%op%"=="5" (
    call "%~dp0CONECTAR_CELULAR_WIFI.bat"
    goto :fin
)

:fin
