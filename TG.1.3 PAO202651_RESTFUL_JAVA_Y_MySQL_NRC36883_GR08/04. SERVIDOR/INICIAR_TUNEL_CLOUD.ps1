$ErrorActionPreference = "SilentlyContinue"
$RootDir = Split-Path -Parent $PSScriptRoot

Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host "  EUREKABANK RESTFUL JAVA - PUENTE / TUNEL CLOUD (GRUPO 08)" -ForegroundColor Cyan
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar Payara
$listening = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if (-not $listening) {
    Write-Host "[AVISO] El servidor Payara 7 en puerto 8080 no parece estar activo." -ForegroundColor Yellow
    Write-Host "        Por favor inicie primero: INICIAR_SERVIDOR_REST.bat" -ForegroundColor Yellow
    Write-Host ""
}

# 2. Localizar binario de cloudflared
$cloudflaredPath = "C:\Program Files (x86)\cloudflared\cloudflared.exe"
if (-not (Test-Path $cloudflaredPath)) {
    $cloudflaredPath = (Get-Command "cloudflared.exe" -ErrorAction SilentlyContinue).Source
}

if (-not $cloudflaredPath -or -not (Test-Path $cloudflaredPath)) {
    Write-Host "[ERROR] No se encontro el ejecutable de cloudflared." -ForegroundColor Red
    pause
    exit 1
}

Write-Host "[1/3] Iniciando Tunel Cloudflare hacia http://localhost:8080..." -ForegroundColor Green
Write-Host "[2/3] Negociando conexion segura y generando enlace publico..." -ForegroundColor Yellow

$psi = New-Object System.Diagnostics.ProcessStartInfo
$psi.FileName = $cloudflaredPath
$psi.Arguments = "tunnel --url http://localhost:8080"
$psi.UseShellExecute = $false
$psi.RedirectStandardOutput = $true
$psi.RedirectStandardError = $true
$psi.CreateNoWindow = $true

$process = [System.Diagnostics.Process]::Start($psi)

$tunnelUrl = $null
$regex = 'https://[a-zA-Z0-9-]+\.trycloudflare\.com'

# Leer salida de stderr/stdout en tiempo real para capturar la URL
while (-not $process.HasExited -and -not $tunnelUrl) {
    $line = $process.StandardError.ReadLine()
    if ($line) {
        if ($line -match $regex) {
            $tunnelUrl = $matches[0]
            break
        }
    }
}

if ($tunnelUrl) {
    $webUrl = "$tunnelUrl/RestClienteWeb/Login.jsp"
    $restUrl = "$tunnelUrl/Rest_EurekaBank/webresources/coreBancario"

    # Actualizar configuraciones de clientes locales (servidor_ip.txt, etc.)
    & "$RootDir\CONFIGURAR_IP_CLIENTES.ps1" -NuevaIP $tunnelUrl | Out-Null

    # Copiar al portapapeles y abrir en el navegador automáticamente
    Set-Clipboard -Value $webUrl -ErrorAction SilentlyContinue
    Start-Process $webUrl

    Write-Host ""
    Write-Host "============================================================================" -ForegroundColor Green
    Write-Host "  ¡TUNEL Y CLIENTE WEB LISTOS Y EN LINEA!" -ForegroundColor Green
    Write-Host "============================================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "  [WEB] Cliente Web JSP en la nube: " -ForegroundColor Cyan
    Write-Host "        $webUrl" -ForegroundColor Yellow
    Write-Host "        (Se acaba de abrir en tu navegador y copiar a tu portapapeles)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  [REST] Endpoint RESTful Java: " -ForegroundColor Cyan
    Write-Host "        $restUrl" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "  EN CUALQUIER OTRA COMPUTADORA O CELULAR (Incluso en la ESPE o con datos):" -ForegroundColor White
    Write-Host "  1. Para Web: Abre la URL de arriba en cualquier navegador." -ForegroundColor Gray
    Write-Host "  2. Para Escritorio / Consola: Ejecuta: CAMBIAR_IP_SERVIDOR.bat $tunnelUrl" -ForegroundColor Gray
    Write-Host "  3. Para App Movil Android: Pega $tunnelUrl en 'Configurar Servidor'." -ForegroundColor Gray
    Write-Host ""
    Write-Host "  IMPORTANTE: Manten esta ventana abierta mientras uses la conexion." -ForegroundColor Magenta
    Write-Host "============================================================================" -ForegroundColor Green
    Write-Host ""

    # Continuar enviando logs de cloudflared
    while (-not $process.HasExited) {
        $l = $process.StandardError.ReadLine()
        if ($l -and -not ($l -match "metrics")) {
            Write-Host $l -ForegroundColor DarkGray
        }
    }
} else {
    Write-Host "[ERROR] No se pudo obtener la URL del tunel Cloudflare." -ForegroundColor Red
}
