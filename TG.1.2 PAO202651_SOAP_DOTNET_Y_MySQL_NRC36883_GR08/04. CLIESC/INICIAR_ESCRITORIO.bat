@echo off
chcp 65001 >nul
title EurekaBank SOAP .NET - Cliente de Escritorio (GR08)
set "APP_DIR=%~dp0CLIESC_EuBank_SOAPDOTNET_GR08\bin\Debug"

if not exist "%APP_DIR%\CLIESC_EuBank_SOAPDOTNET_GR08.exe" (
    echo [INFO] Compilando Cliente de Escritorio...
    set "MSBUILD_EXE="
    if exist "%ProgramFiles%\Microsoft Visual Studio\18\Community\MSBuild\Current\Bin\MSBuild.exe" set "MSBUILD_EXE=%ProgramFiles%\Microsoft Visual Studio\18\Community\MSBuild\Current\Bin\MSBuild.exe"
    if exist "%ProgramFiles%\Microsoft Visual Studio\2022\Community\MSBuild\Current\Bin\MSBuild.exe" set "MSBUILD_EXE=%ProgramFiles%\Microsoft Visual Studio\2022\Community\MSBuild\Current\Bin\MSBuild.exe"
    if exist "%ProgramFiles%\Microsoft Visual Studio\2022\Professional\MSBuild\Current\Bin\MSBuild.exe" set "MSBUILD_EXE=%ProgramFiles%\Microsoft Visual Studio\2022\Professional\MSBuild\Current\Bin\MSBuild.exe"
    if exist "%ProgramFiles%\Microsoft Visual Studio\2022\Enterprise\MSBuild\Current\Bin\MSBuild.exe" set "MSBUILD_EXE=%ProgramFiles%\Microsoft Visual Studio\2022\Enterprise\MSBuild\Current\Bin\MSBuild.exe"
    if exist "%ProgramFiles(x86)%\Microsoft Visual Studio\2019\Community\MSBuild\Current\Bin\MSBuild.exe" set "MSBUILD_EXE=%ProgramFiles(x86)%\Microsoft Visual Studio\2019\Community\MSBuild\Current\Bin\MSBuild.exe"
    
    if defined MSBUILD_EXE (
        echo [INFO] Usando MSBuild de Visual Studio...
        "%MSBUILD_EXE%" "%~dp0CLIESC_EuBank_SOAPDOTNET_GR08\CLIESC_EuBank_SOAPDOTNET_GR08.csproj" /t:Build /p:Configuration=Debug
    ) else (
        echo [INFO] Compilando con dotnet build...
        dotnet build "%~dp0CLIESC_EuBank_SOAPDOTNET_GR08\CLIESC_EuBank_SOAPDOTNET_GR08.csproj"
    )
)

if exist "%APP_DIR%\CLIESC_EuBank_SOAPDOTNET_GR08.exe" (
    cd /d "%APP_DIR%"
    start "" "CLIESC_EuBank_SOAPDOTNET_GR08.exe"
) else (
    echo [ERROR] No se encontro el ejecutable en: "%APP_DIR%\CLIESC_EuBank_SOAPDOTNET_GR08.exe"
    echo [TIP] Haz un git pull para descargar los binarios precompilados de CLIESC.
    pause
)
