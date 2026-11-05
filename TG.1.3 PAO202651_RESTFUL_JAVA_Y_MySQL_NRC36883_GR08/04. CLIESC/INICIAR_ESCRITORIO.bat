@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Cliente de Escritorio Swing (GR08)
cd /d "%~dp0RestClienteEscritorio"

echo ============================================================================
echo   EUREKABANK RESTFUL JAVA - CLIENTE DE ESCRITORIO SWING (GRUPO 08)
echo ============================================================================
echo.

if not exist "dist\RestClienteEscritorio.jar" (
    echo [ERROR] No se encontro el archivo: dist\RestClienteEscritorio.jar
    echo Por favor compila el proyecto en NetBeans o ejecuta un git pull.
    echo.
    pause
    exit /b 1
)

set "JAVA_CMD="

where javaw >nul 2>&1
if not errorlevel 1 set "JAVA_CMD=javaw"
if defined JAVA_CMD goto :lanzar

where java >nul 2>&1
if not errorlevel 1 set "JAVA_CMD=java"
if defined JAVA_CMD goto :lanzar

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javaw.exe" set "JAVA_CMD=%JAVA_HOME%\bin\javaw.exe"
    if not defined JAVA_CMD if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
)
if defined JAVA_CMD goto :lanzar

for /d %%i in ("%ProgramFiles%\Java\jdk*") do (
    if not defined JAVA_CMD if exist "%%i\bin\javaw.exe" set "JAVA_CMD=%%i\bin\javaw.exe"
)
if defined JAVA_CMD goto :lanzar

for /d %%i in ("%ProgramFiles%\Java\jre*") do (
    if not defined JAVA_CMD if exist "%%i\bin\javaw.exe" set "JAVA_CMD=%%i\bin\javaw.exe"
)
if defined JAVA_CMD goto :lanzar

for /d %%i in ("%ProgramFiles%\Eclipse Adoptium\jdk*") do (
    if not defined JAVA_CMD if exist "%%i\bin\javaw.exe" set "JAVA_CMD=%%i\bin\javaw.exe"
)
if defined JAVA_CMD goto :lanzar

echo ============================================================================
echo   [ERROR] No se encontro Java instalado ni configurado en el sistema PATH.
echo ============================================================================
echo   Para ejecutar el Cliente de Escritorio Swing necesitas tener instalado:
echo   - Java Runtime Environment (JRE) o Java Development Kit (JDK 17 o superior)
echo   - Puedes descargarlo gratis desde: https://adoptium.net/
echo ============================================================================
echo.
pause
exit /b 1

:lanzar
echo [OK] Iniciando aplicacion con: %JAVA_CMD%
echo.
start "" "%JAVA_CMD%" -jar "dist\RestClienteEscritorio.jar"
exit /b 0
