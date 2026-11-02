@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Cliente de Escritorio Swing (GR08)
cd /d "%~dp0RestClienteEscritorio"
start "" javaw -jar "dist\RestClienteEscritorio.jar" 2>nul || start "" java -jar "dist\RestClienteEscritorio.jar"
exit

