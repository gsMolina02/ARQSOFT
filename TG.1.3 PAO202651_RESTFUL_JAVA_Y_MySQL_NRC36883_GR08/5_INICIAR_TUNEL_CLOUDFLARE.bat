@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Tunel Cloudflare Inalambrico (GR08)
echo ============================================================================
echo   EUREKABANK RESTFUL JAVA - TUNEL INALAMBRICO CLOUDFLARE (GRUPO 08)
echo ============================================================================
echo.
echo [1/2] Verificando que el servidor Payara este activo en el puerto 8080...
netstat -ano | findstr 8080 >nul 2>&1
if %errorlevel% neq 0 (
    echo [AVISO] Payara no parece estar activo en el puerto 8080.
    echo         Inicie primero el servidor con 0_INICIAR_SERVIDOR_REST.bat
    echo.
)

echo [2/2] Iniciando Tunel Cloudflare hacia http://localhost:8080...
echo.
echo ----------------------------------------------------------------------------
echo INSTRUCCIONES PARA CONEXION 100%% INALAMBRICA (SIN CABLES):
echo 1. Busque abajo la linea que dice: https://xxxx.trycloudflare.com
echo 2. En el celular (o en otra computadora), abra la app EurekaBank:
echo    - Presione el boton: "Configurar Servidor"
echo    - Pegue esa URL completa (ejemplo: https://xxxx.trycloudflare.com)
echo 3. Presione "Probar y Guardar".
echo Listo! Funcionara por Wi-Fi o datos moviles en cualquier lugar del mundo.
echo ----------------------------------------------------------------------------
echo.

if exist "C:\Program Files (x86)\cloudflared\cloudflared.exe" (
    "C:\Program Files (x86)\cloudflared\cloudflared.exe" tunnel --url http://localhost:8080
) else (
    cloudflared.exe tunnel --url http://localhost:8080
)

pause
