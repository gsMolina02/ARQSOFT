@echo off
chcp 65001 >nul
title EurekaBank SOAP .NET - Cliente de Escritorio (GR08)
set "APP_DIR=%~dp0CLIESC_EuBank_SOAPDOTNET_GR08\bin\Debug"

if not exist "%APP_DIR%\CLIESC_EuBank_SOAPDOTNET_GR08.exe" (
    echo [INFO] Compilando Cliente de Escritorio...
    dotnet build "%~dp0CLIESC_EuBank_SOAPDOTNET_GR08\CLIESC_EuBank_SOAPDOTNET_GR08.csproj"
)

if exist "%APP_DIR%\CLIESC_EuBank_SOAPDOTNET_GR08.exe" (
    cd /d "%APP_DIR%"
    start "" "CLIESC_EuBank_SOAPDOTNET_GR08.exe"
) else (
    echo [ERROR] No se encontro el ejecutable en: "%APP_DIR%\CLIESC_EuBank_SOAPDOTNET_GR08.exe"
    pause
)
