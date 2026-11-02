@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Cliente de Consola (GR08)
cd /d "%~dp004. CLICON\RestClienteConsola"
java -cp "dist\RestClienteConsola.jar;dist\lib\gson-2.7.jar" ec.edu.monster.restclienteconsola.RestClienteConsola
pause

