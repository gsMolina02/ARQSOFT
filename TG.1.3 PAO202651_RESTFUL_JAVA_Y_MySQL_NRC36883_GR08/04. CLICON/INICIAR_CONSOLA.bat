@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Cliente de Consola (GR08)
cd /d "%~dp0RestClienteConsola"
java -jar "dist\RestClienteConsola.jar"
pause

