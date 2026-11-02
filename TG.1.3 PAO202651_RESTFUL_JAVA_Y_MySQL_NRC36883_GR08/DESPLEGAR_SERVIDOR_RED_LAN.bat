@echo off
chcp 65001 >nul
echo ============================================================================
echo   EUREKABANK RESTFUL JAVA + MYSQL - DESPLIEGUE EN RED LOCAL LAN (GRUPO 08)
echo   Autores: Gustavo Molina y Daniela Tituana - NRC 36883
echo ============================================================================
echo.
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4"') do (
    set "LOCAL_IP=%%a"
    goto :found_ip
)
:found_ip
set LOCAL_IP=%LOCAL_IP: =%
if "%LOCAL_IP%"=="" set LOCAL_IP=192.168.100.15
echo [1/4] Direccion IP LAN detectada en este equipo servidor: %LOCAL_IP%
echo.
echo [2/4] Habilitando puertos 8080 (Payara/GlassFish REST) y 3306 (MySQL) en Firewall...
netsh advfirewall firewall add rule name="EurekaBank_REST_Java_8080" dir=in action=allow protocol=TCP localport=8080 >nul 2>&1
netsh advfirewall firewall add rule name="EurekaBank_MySQL_3306" dir=in action=allow protocol=TCP localport=3306 >nul 2>&1
echo       Puertos 8080 y 3306 habilitados para conexiones entrantes en la red local.
echo.
echo [3/4] Endpoints RESTful JSON disponibles en la red local LAN:
echo       - Base URI REST       : http://%LOCAL_IP%:8080/Rest_EurekaBank/webresources/coreBancario
echo       - GET Movimientos     : http://%LOCAL_IP%:8080/Rest_EurekaBank/webresources/coreBancario/movimientos/00100001
echo       - POST Login / Op     : /login , /deposito , /retiro , /transferencia
echo       - Cliente Web JSP     : http://%LOCAL_IP%:8080/RestClienteWeb/
echo       - Base de Datos MySQL : %LOCAL_IP%:3306 (esquema: eurekabank)
echo.
echo [4/4] Para configurar los 4 clientes en otra computadora hacia este servidor:
echo       CAMBIAR_IP_SERVIDOR.bat %LOCAL_IP%
echo ============================================================================
pause
