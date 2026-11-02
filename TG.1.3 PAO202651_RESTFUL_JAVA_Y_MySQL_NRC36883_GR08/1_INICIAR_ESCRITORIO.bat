@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Cliente de Escritorio Swing (GR08)
cd /d "%~dp004. CLIESC\RestClienteEscritorio"
start "" javaw -cp "dist\RestClienteEscritorio.jar;dist\lib\gson-2.7.jar" ec.edu.monster.restclienteescritorio.RestClienteEscritorio 2>nul || start "" java -cp "dist\RestClienteEscritorio.jar;dist\lib\gson-2.7.jar" ec.edu.monster.restclienteescritorio.RestClienteEscritorio
exit
