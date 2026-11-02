@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Cliente Web JSP (GR08)

set "TARGET_HOST=localhost:8080"
if exist "%~dp0servidor_ip.txt" (
    for /f "tokens=*" %%a in ('type "%~dp0servidor_ip.txt"') do (
        set "TARGET_HOST=%%a"
    )
)

set TARGET_HOST=%TARGET_HOST: =%
if "%TARGET_HOST:~0,4%"=="http" (
    set "WEB_URL=%TARGET_HOST%/RestClienteWeb/Login.jsp"
) else (
    set "WEB_URL=http://%TARGET_HOST%/RestClienteWeb/Login.jsp"
)

echo ============================================================================
echo   ABRIENDO BANCA WEB JSP EUREKABANK RESTFUL (GRUPO 08)
echo   URL: %WEB_URL%
echo ============================================================================
echo.
start "" "%WEB_URL%"
