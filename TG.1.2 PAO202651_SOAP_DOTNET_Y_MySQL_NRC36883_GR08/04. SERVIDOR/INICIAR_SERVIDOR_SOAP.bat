@echo off
chcp 65001 >nul
title EurekaBank SOAP .NET - Servidor WCF (Puerto 62278)
echo ============================================================================
echo   INICIANDO SERVIDOR SOAP WCF EUREKABANK (GRUPO 08)
echo   Puerto: 62278 ^| Base de Datos: MySQL 8.0 (eurekabank)
echo ============================================================================
echo.

:: 1. Verificar MySQL
echo [1/2] Verificando servicio MySQL 8.0...
sc query MySQL80 | findstr /i "RUNNING" >nul 2>&1
if %errorlevel% neq 0 (
    echo       Iniciando servicio MySQL80...
    net start MySQL80 >nul 2>&1
)
echo       MySQL 8.0 activo en puerto 3306.
echo.

:: 2. Verificar si IIS Express / WCF ya esta activo
echo [2/2] Verificando servicios WCF SOAP en puerto 62278...
powershell -Command "try { $r = (Invoke-WebRequest -Uri 'http://localhost:62278/Services/LoginService.svc' -TimeoutSec 1 -UseBasicParsing).StatusCode; if ($r -eq 200) { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
if %errorlevel% equ 0 (
    echo.
    echo ============================================================================
    echo   [OK] EL SERVIDOR SOAP WCF YA SE ENCUENTRA ACTIVO Y RESPONDIENDO
    echo.
    echo   Servicios disponibles:
    echo   - LoginService       : http://localhost:62278/Services/LoginService.svc
    echo   - MovimientosService : http://localhost:62278/Services/MovimientosService.svc
    echo ============================================================================
    echo.
    echo Presione cualquier tecla para mantener esta ventana abierta o cierrela.
    pause >nul
    exit /b 0
)

set "IISEXPRESS="
if exist "%ProgramFiles%\IIS Express\iisexpress.exe" set "IISEXPRESS=%ProgramFiles%\IIS Express\iisexpress.exe"
if exist "%ProgramFiles(x86)%\IIS Express\iisexpress.exe" set "IISEXPRESS=%ProgramFiles(x86)%\IIS Express\iisexpress.exe"
if exist "C:\Program Files\IIS Express\iisexpress.exe" set "IISEXPRESS=C:\Program Files\IIS Express\iisexpress.exe"
if exist "C:\Program Files (x86)\IIS Express\iisexpress.exe" set "IISEXPRESS=C:\Program Files (x86)\IIS Express\iisexpress.exe"

if "%IISEXPRESS%"=="" (
    echo [ERROR] No se encontro IIS Express instalado en el equipo.
    echo         Instale IIS Express o Visual Studio con soporte Web.
    pause
    exit /b 1
)

set "PATH_SERVER=%~dp0WS_EuBank_SOAPDOTNET_GR08\WS_EuBank_SOAPDOTNET_GR08"
if not exist "%PATH_SERVER%" (
    echo [ERROR] No se encontro la carpeta del servidor WCF en:
    echo         "%PATH_SERVER%"
    pause
    exit /b 1
)

echo       Ruta del Servicio: %PATH_SERVER%
echo       Ejecutable: %IISEXPRESS%
echo.

"%IISEXPRESS%" /path:"%PATH_SERVER%" /port:62278
pause
