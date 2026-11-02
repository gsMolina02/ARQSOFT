@echo off
chcp 65001 >nul
title EurekaBank SOAP .NET - Cliente de Consola (GR08)
set "APP_DIR=%~dp0CLICON_EuBank_SOAPDOTNET_GR08\CLICON_EuBank_SOAPDOTNET_GR08\bin\Debug"

if not exist "%APP_DIR%\CLICON_EuBank_SOAPDOTNET_GR08.exe" (
    echo [INFO] Compilando Cliente de Consola...
    dotnet build "%~dp0CLICON_EuBank_SOAPDOTNET_GR08\CLICON_EuBank_SOAPDOTNET_GR08.sln"
)

if exist "%APP_DIR%\CLICON_EuBank_SOAPDOTNET_GR08.exe" (
    cd /d "%APP_DIR%"
    "CLICON_EuBank_SOAPDOTNET_GR08.exe"
    pause
) else (
    echo [ERROR] No se encontro el ejecutable en: "%APP_DIR%\CLICON_EuBank_SOAPDOTNET_GR08.exe"
    pause
)
