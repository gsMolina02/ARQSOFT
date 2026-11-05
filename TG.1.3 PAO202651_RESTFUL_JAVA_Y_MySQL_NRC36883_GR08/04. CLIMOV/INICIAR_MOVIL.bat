@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Cliente Movil Android (GR08)
echo ============================================================================
echo   EUREKABANK RESTFUL JAVA - CLIENTE MOVIL ANDROID (GRUPO 08)
echo ============================================================================
echo.

if exist "%LOCALAPPDATA%\Android\Sdk" (
    set "ANDROID_SDK_ROOT=%LOCALAPPDATA%\Android\Sdk"
    set "PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools;C:\Program Files\Android\Android Studio\bin;%PATH%"
)
cd /d "%~dp0RestClienteMovil"

:: 1. Detectar dispositivos ADB
echo [1/3] Detectando dispositivos / emuladores conectados...
adb devices
echo.

:: 2. Configurar reverse port forwarding para depuraci?n USB si hay dispositivo
adb reverse tcp:8080 tcp:8080 >nul 2>&1
if %errorlevel% equ 0 (
    echo       [OK] Puerto 8080 redirigido por USB (adb reverse tcp:8080 tcp:8080).
)

echo.
echo ============================================================================
echo   OPCIONES DISPONIBLES:
echo   [1] Instalar y ejecutar en el dispositivo conectado (installDebug)
echo   [2] Compilar APK Debug (gradlew assembleDebug)
echo   [3] Abrir proyecto en Android Studio
echo   [4] Configurar IP del servidor (Wi-Fi LAN)
echo   [5] Conectar celular 100%% inal?mbrico por Wi-Fi (sin cables)
echo ============================================================================
echo.
set /p op="Seleccione una opcion (1-5): "

if "%op%"=="1" (
    echo.
    echo Compilando e instalando APK en el dispositivo...
    call gradlew.bat installDebug
    echo Iniciando aplicacion en el telefono...
    adb shell am start -n ec.edu.monster.wseurekaclient/ec.edu.monster.servicio.LoginActivity
    pause
    goto :fin
)

if "%op%"=="2" (
    echo.
    echo Compilando APK Debug...
    call gradlew.bat assembleDebug
    echo.
    echo APK generado en: app\build\outputs\apk\debug\app-debug.apk
    pause
    goto :fin
)

if "%op%"=="3" (
    echo.
    echo Abriendo proyecto en Android Studio...
    start "" "C:\Program Files\Android\Android Studio\bin\studio64.exe" "%~dp0RestClienteMovil"
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

