@echo off
chcp 65001 >nul
title EurekaBank SOAP .NET - Portal Web ASP.NET Core MVC (GR08)
echo ============================================================================
echo   INICIANDO PORTAL WEB EUREKABANK SOAP .NET (GRUPO 08)
echo   Puerto: 5150 ^| Acceso: http://localhost:5150
echo ============================================================================
echo.

:: Liberar puerto 5150 si ya habia una instancia abierta
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5150 ^| findstr LISTENING') do (
    echo Liberando instancia anterior en puerto 5150 PID %%a...
    taskkill /F /PID %%a >nul 2>&1
)

set "WEB_DIR=%~dp0CLIWEB_EuBank_SOAPDOTNET_GR08\CLIWEB_EuBank_SOAPDOTNET_GR08"
if not exist "%WEB_DIR%" (
    echo [ERROR] No se encontro la carpeta del proyecto Web en:
    echo         "%WEB_DIR%"
    pause
    exit /b 1
)

cd /d "%WEB_DIR%"
echo Iniciando servidor web Kestrel...
start "" "http://localhost:5150"
dotnet run
pause
