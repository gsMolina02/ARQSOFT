@echo off
set TARGET_IP=%1
if "%TARGET_IP%"=="" set /p TARGET_IP="Ingrese la IP del Servidor RESTful Java (ej. 192.168.100.15 o localhost): "
if "%TARGET_IP%"=="" set TARGET_IP=192.168.100.15
powershell -ExecutionPolicy Bypass -File "%~dp0CONFIGURAR_IP_CLIENTES.ps1" -NuevaIP "%TARGET_IP%"
pause
