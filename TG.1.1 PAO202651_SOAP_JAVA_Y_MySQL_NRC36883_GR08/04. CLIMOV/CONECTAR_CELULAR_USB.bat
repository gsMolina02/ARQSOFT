@echo off
title Conectar Celular para Presentacion - EurekaBank SOAP GR08
set ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe
set APK=%LOCALAPPDATA%\EurekaBankGR8_SOAP\app\outputs\apk\debug\app-debug.apk
set PAQUETE=ec.edu.monster.eurekabank.soap.gr08
echo ====================================================================
echo     PREPARANDO CONEXION PARA PRESENTACION (EUREKABANK SOAP JAVA GR08)
echo ====================================================================
echo.
if not exist "%ADB%" (
    echo No se encontro adb en %ADB%
    echo Instale el Android SDK o revise la ruta.
    pause
    exit /b 1
)
echo 1. Verificando celular conectado por USB...
"%ADB%" devices
echo.
echo 2. Activando tunel inverso USB (Puerto 8080 de Payara)...
"%ADB%" reverse tcp:8080 tcp:8080
echo.
if exist "%APK%" (
    echo 3. Instalando la ultima version de la aplicacion...
    "%ADB%" install -r "%APK%"
) else (
    echo 3. No hay APK compilado; se abre la version ya instalada.
)
echo.
echo 4. Abriendo la aplicacion en el celular...
"%ADB%" shell monkey -p %PAQUETE% -c android.intent.category.LAUNCHER 1 >nul 2>&1
echo.
echo ====================================================================
echo  LISTO! El celular llega al Servidor SOAP por el cable USB.
echo  En la app pulse "Cambiar servidor" y escriba 127.0.0.1
echo  (se completa a http://127.0.0.1:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank).
echo  No depende del Wi-Fi de la universidad.
echo ====================================================================
echo.
pause
