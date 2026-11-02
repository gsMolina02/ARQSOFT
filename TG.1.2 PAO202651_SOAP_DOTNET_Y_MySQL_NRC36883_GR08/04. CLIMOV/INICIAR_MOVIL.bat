@echo off
chcp 65001 >nul
title EurekaBank SOAP .NET - Cliente Movil MAUI (GR08)
echo ============================================================================
echo   EUREKABANK SOAP .NET - CLIENTE MOVIL .NET MAUI (GRUPO 08)
echo ============================================================================
echo.

if exist "%LOCALAPPDATA%\Android\Sdk" (
    set "ANDROID_SDK_ROOT=%LOCALAPPDATA%\Android\Sdk"
    set "PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools;%PATH%"
)

set "MAUI_DIR=%~dp0CLIMOV_EurekaBank_SOAPDOTNET_GR08\CLIMOV_EurekaBank_SOAPDOTNET_GR08"
if not exist "%MAUI_DIR%" (
    echo [ERROR] No se encontro la carpeta del cliente MAUI en:
    echo         "%MAUI_DIR%"
    pause
    exit /b 1
)
cd /d "%MAUI_DIR%"

:: 1. Detectar dispositivos ADB
echo [1/2] Detectando dispositivos Android...
where adb >nul 2>&1
if %errorlevel% equ 0 (
    adb devices
    adb reverse tcp:62278 tcp:62278 >nul 2>&1
    adb reverse tcp:5150 tcp:5150 >nul 2>&1
) else (
    echo       [AVISO] adb no esta en el PATH o no se detecto Android SDK.
)
echo.

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
