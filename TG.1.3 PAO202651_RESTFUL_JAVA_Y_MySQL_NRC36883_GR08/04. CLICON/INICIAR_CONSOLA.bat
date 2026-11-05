@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Cliente de Consola (GR08)
cd /d "%~dp0RestClienteConsola"

echo ============================================================================
echo   EUREKABANK RESTFUL JAVA - CLIENTE DE CONSOLA (GRUPO 08)
echo ============================================================================
echo.

if not exist "dist\RestClienteConsola.jar" (
    echo [ERROR] No se encontro el archivo: dist\RestClienteConsola.jar
    echo Por favor compila el proyecto en NetBeans o ejecuta un git pull.
    echo.
    pause
    exit /b 1
)

set "JAVA_CMD="

where java >nul 2>&1
if not errorlevel 1 set "JAVA_CMD=java"
if defined JAVA_CMD goto :lanzar

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
)
if defined JAVA_CMD goto :lanzar

for /d %%i in ("%ProgramFiles%\Java\jdk*") do (
    if not defined JAVA_CMD if exist "%%i\bin\java.exe" set "JAVA_CMD=%%i\bin\java.exe"
)
if defined JAVA_CMD goto :lanzar

for /d %%i in ("%ProgramFiles%\Java\jre*") do (
    if not defined JAVA_CMD if exist "%%i\bin\java.exe" set "JAVA_CMD=%%i\bin\java.exe"
)
if defined JAVA_CMD goto :lanzar

for /d %%i in ("%ProgramFiles%\Eclipse Adoptium\jdk*") do (
    if not defined JAVA_CMD if exist "%%i\bin\java.exe" set "JAVA_CMD=%%i\bin\java.exe"
)
if defined JAVA_CMD goto :lanzar

echo ============================================================================
echo   [ERROR] No se encontro Java instalado ni configurado en el sistema PATH.
echo ============================================================================
echo   Para ejecutar el Cliente de Consola necesitas tener instalado:
echo   - Java Runtime Environment (JRE) o Java Development Kit (JDK 17 o superior)
echo ============================================================================
echo.
pause
exit /b 1

:lanzar
echo [OK] Ejecutando cliente de consola con: %JAVA_CMD%
echo.
"%JAVA_CMD%" -jar "dist\RestClienteConsola.jar"
echo.
pause
