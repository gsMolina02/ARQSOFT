@echo off
chcp 65001 >nul
title EurekaBank RESTful Java - Tunel Cloudflare (Opcional - GR08)
echo ============================================================================
echo   EUREKABANK RESTFUL JAVA - PUENTE / TUNEL CLOUD (GRUPO 08)
echo   (Ideal para redes con aislamiento como la ESPE sin importar el Wi-Fi)
echo ============================================================================
echo.
echo [1/2] Verificando que el servidor Payara este activo en el puerto 8080...
netstat -ano | findstr :8080 | findstr LISTENING >nul 2>&1
if %errorlevel% neq 0 (
    echo [AVISO] Payara no parece estar activo en el puerto 8080.
    echo         Inicie primero el servidor con: 0_INICIAR_SERVIDOR_REST.bat
    echo.
)

echo [2/2] Iniciando Puente Cloudflare hacia http://localhost:8080...
echo.
echo ----------------------------------------------------------------------------
echo INSTRUCCIONES DE USO:
echo 1. Espere unos segundos hasta que aparezca una linea con:
echo    https://xxxx.trycloudflare.com
echo.
echo 2. EN LA OTRA LAPTOP (en cualquier red o Wi-Fi de la ESPE):
echo    - Para ver la Web: Pegue esa URL en el navegador:
echo      https://xxxx.trycloudflare.com/RestClienteWeb/Login.jsp
echo    - Para Consola / Escritorio:
echo      Ejecute: CAMBIAR_IP_SERVIDOR.bat https://xxxx.trycloudflare.com
echo.
echo 3. EN EL CELULAR:
echo    - Abra EurekaBank y presione "Configurar Servidor"
echo    - Pegue la URL https://xxxx.trycloudflare.com y guarde.
echo ----------------------------------------------------------------------------
echo.

if exist "C:\Program Files (x86)\cloudflared\cloudflared.exe" (
    "C:\Program Files (x86)\cloudflared\cloudflared.exe" tunnel --url http://localhost:8080
) else (
    cloudflared.exe tunnel --url http://localhost:8080
)

pause
