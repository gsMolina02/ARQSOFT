@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Iniciar Servidor (Payara + MySQL)
set "ROOT=%~dp0"

echo ============================================================================
echo   INICIANDO SERVIDOR RESTFUL JAVA EUREKABANK (GRUPO 08)
echo   Puerto HTTP: 8080 ^| Base de Datos: MySQL 8.0 (3306)
echo   Autores: Gustavo Molina y Daniela Tituaña - NRC 36883
echo ============================================================================
echo.

:: 0. Verificar si Payara esta instalado en esta maquina
set "ASADMIN_EXE="
if exist "C:\payara-7.2026.9\payara7\bin\asadmin.bat" set "ASADMIN_EXE=C:\payara-7.2026.9\payara7\bin\asadmin.bat"
if exist "C:\payara7\bin\asadmin.bat" set "ASADMIN_EXE=C:\payara7\bin\asadmin.bat"
if exist "C:\Program Files\payara-7.2026.9\payara7\bin\asadmin.bat" set "ASADMIN_EXE=C:\Program Files\payara-7.2026.9\payara7\bin\asadmin.bat"

if not defined ASADMIN_EXE (
    echo ============================================================================
    echo   [AVISO IMPORTANTE - ROL DE ESTA COMPUTADORA]
    echo ============================================================================
    echo   No se encontro Payara Server instalado en este equipo.
    echo   Esta computadora esta configurada para actuar como CLIENTE.
    echo.
    echo   - El SERVIDOR backend debe ejecutarse en la COMPUTADORA 1 (Laptop Servidor).
    echo   - Para conectar esta computadora al servidor remoto, regresa al menu
    echo     y presiona [8] CONFIG para ingresar la IP o URL del Servidor.
    echo ============================================================================
    echo.
    pause
    exit /b
)

:: 1. Verificar MySQL
echo [1/3] Verificando servicio de base de datos MySQL 8.0...
sc query MySQL80 | findstr /i "RUNNING" >nul 2>&1
if %errorlevel% neq 0 (
    echo       Iniciando servicio MySQL80...
    net start MySQL80 >nul 2>&1
)
echo       MySQL 8.0 activo en puerto 3306.
echo.

:: 2. Verificar o Iniciar Payara Server
echo [2/3] Verificando servidor de aplicaciones Payara Server 7...
netstat -ano | findstr :8080 | findstr LISTENING >nul 2>&1
if %errorlevel% neq 0 (
    echo       Iniciando dominio domain1 en Payara Server 7...
    call "%ASADMIN_EXE%" start-domain domain1
) else (
    echo       Payara Server 7 ya se encuentra en ejecucion en el puerto 8080.
)
echo.

:: 3. Desplegar / Asegurar aplicaciones
echo [3/3] Desplegando servicios RESTful y Cliente Web...
if exist "%ROOT%WS_EurekaBank\dist\Rest_EurekaBank.war" (
    pushd "%ROOT%WS_EurekaBank\dist"
    call "%ASADMIN_EXE%" deploy --force=true --contextroot /Rest_EurekaBank --name Rest_EurekaBank Rest_EurekaBank.war >nul 2>&1
    popd
)

if exist "%ROOT%..\04. CLIWEB\RestClienteWeb\dist\RestClienteWeb.war" (
    pushd "%ROOT%..\04. CLIWEB\RestClienteWeb\dist"
    call "%ASADMIN_EXE%" deploy --force=true --contextroot /RestClienteWeb --name RestClienteWeb RestClienteWeb.war >nul 2>&1
    popd
)

echo.
echo ============================================================================
echo   SERVIDOR INICIADO CORRECTAMENTE
echo   - Base URI REST : http://localhost:8080/Rest_EurekaBank/webresources/coreBancario
echo   - Cliente Web   : http://localhost:8080/RestClienteWeb/Login.jsp
echo   - Admin Console : http://localhost:4848
echo ============================================================================
echo Abriendo endpoint de prueba en el navegador...
start "" "http://localhost:8080/Rest_EurekaBank/webresources/coreBancario/movimientos/00100001"
echo Servidor en ejecucion. No cierres esta ventana mientras uses el sistema.
pause
