@echo off
title EurekaBank SOAP .NET - Servidor WCF (Puerto 62278)
echo ============================================================================
echo   INICIANDO SERVIDOR SOAP WCF EUREKABANK (GRUPO 08)
echo   Puerto: 62278 | Base de Datos: MySQL 8.0 (eurekabank)
echo ============================================================================
echo.
set "PATH_SERVER=%~dp004. SERVIDOR\WS_EuBank_SOAPDOTNET_GR08\WS_EuBank_SOAPDOTNET_GR08"
"C:\Program Files\IIS Express\iisexpress.exe" /path:"%PATH_SERVER%" /port:62278
pause

