@echo off
chcp 65001 >nul
echo ============================================================================
echo   EUREKABANK SOAP .NET + MYSQL - DESPLIEGUE EN RED LOCAL LAN (GRUPO 08)
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
echo [2/4] Habilitando permisos HTTP (netsh urlacl) y puertos en Firewall de Windows...
netsh http add urlacl url=http://*:62278/ user=Todos >nul 2>&1
netsh http add urlacl url=http://*:62278/ user=Everyone >nul 2>&1
netsh advfirewall firewall add rule name="EurekaBank_SOAP_62278" dir=in action=allow protocol=TCP localport=62278 >nul 2>&1
netsh advfirewall firewall add rule name="EurekaBank_CLIWEB_5150" dir=in action=allow protocol=TCP localport=5150 >nul 2>&1
netsh advfirewall firewall add rule name="EurekaBank_MySQL_3306" dir=in action=allow protocol=TCP localport=3306 >nul 2>&1
echo       Puertos 62278 (SOAP WCF), 5150 (CLIWEB) y 3306 (MySQL) habilitados.
echo.
echo [3/4] Endpoints publicados para otras computadoras y dispositivos moviles en la red:
echo       - LoginService WSDL      : http://%LOCAL_IP%:62278/Services/LoginService.svc?wsdl
echo       - MovimientosService WSDL: http://%LOCAL_IP%:62278/Services/MovimientosService.svc?wsdl
echo       - Portal Web (CLIWEB)    : http://%LOCAL_IP%:5150/
echo       - Base de Datos MySQL    : %LOCAL_IP%:3306 (esquema: eurekabank)
echo.
echo [4/4] Para apuntar todos los clientes de esta u otra computadora a esta IP, ejecute:
echo       CAMBIAR_IP_SERVIDOR.bat %LOCAL_IP%
echo ============================================================================
pause
