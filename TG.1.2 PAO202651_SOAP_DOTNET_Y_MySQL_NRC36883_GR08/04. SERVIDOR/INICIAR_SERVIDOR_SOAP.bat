@echo off
chcp 65001 >nul
title EurekaBank SOAP .NET - Servidor WCF (Puerto 62278)
echo ============================================================================
echo   INICIANDO SERVIDOR SOAP WCF EUREKABANK (GRUPO 08)
echo   Puerto: 62278 | Base de Datos: MySQL 8.0 (eurekabank)
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

:: 2. Iniciar IIS Express en puerto 62278
echo [2/2] Iniciando IIS Express para servicios WCF SOAP...
set "PATH_SERVER=%~dp0WS_EuBank_SOAPDOTNET_GR08\WS_EuBank_SOAPDOTNET_GR08"
"C:\Program Files\IIS Express\iisexpress.exe" /path:"%PATH_SERVER%" /port:62278
pause
