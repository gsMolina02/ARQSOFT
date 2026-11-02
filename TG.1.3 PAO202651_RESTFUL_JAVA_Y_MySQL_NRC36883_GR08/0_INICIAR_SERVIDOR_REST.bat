@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Iniciar Servidor (Payara + MySQL)
echo ============================================================================
echo   INICIANDO SERVIDOR RESTFUL JAVA EUREKABANK (GRUPO 08)
echo   Puerto HTTP: 8080 | Base de Datos: MySQL 8.0 (3306)
echo   Autores: Gustavo Molina y Daniela Tituaña - NRC 36883
echo ============================================================================
echo.

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
    call "C:\payara-7.2026.9\payara7\bin\asadmin.bat" start-domain domain1
) else (
    echo       Payara Server 7 ya se encuentra en ejecucion en el puerto 8080.
)
echo.

:: 3. Desplegar / Asegurar aplicaciones
echo [3/3] Desplegando servicios RESTful y Cliente Web...
call "C:\payara-7.2026.9\payara7\bin\asadmin.bat" deploy --force=true --contextroot /Rest_EurekaBank --name Rest_EurekaBank "%~dp004. SERVIDOR\WS_EurekaBank\dist\Rest_EurekaBank.war" >nul 2>&1
call "C:\payara-7.2026.9\payara7\bin\asadmin.bat" deploy --force=true --contextroot /RestClienteWeb --name RestClienteWeb "%~dp004. CLIWEB\RestClienteWeb\dist\RestClienteWeb.war" >nul 2>&1

echo.
echo ============================================================================
echo   SERVIDOR INICIADO CORRECTAMENTE
echo   - Base URI REST : http://localhost:8080/Rest_EurekaBank/webresources/coreBancario
echo   - Cliente Web   : http://localhost:8080/RestClienteWeb/Login.jsp
echo   - Admin Console : http://localhost:4848
echo ============================================================================
echo Abriendo endpoint de prueba en el navegador...
start "" "http://localhost:8080/Rest_EurekaBank/webresources/coreBancario/movimientos/00100001"
pause

