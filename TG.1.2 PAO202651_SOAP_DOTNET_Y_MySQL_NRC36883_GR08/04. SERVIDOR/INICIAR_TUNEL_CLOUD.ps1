$ErrorActionPreference = "SilentlyContinue"
$RootDir = Split-Path -Parent $PSScriptRoot

Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host "  EUREKABANK SOAP .NET - PUENTE / TUNEL CLOUD (GRUPO 08)" -ForegroundColor Cyan
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar Servidor SOAP (IIS Express 62278)
$listeningSoap = Get-NetTCPConnection -LocalPort 62278 -State Listen -ErrorAction SilentlyContinue
if (-not $listeningSoap) {
    Write-Host "[AVISO] El servidor SOAP WCF en puerto 62278 no parece estar activo." -ForegroundColor Yellow
    Write-Host "        Por favor inicie primero: INICIAR_SERVIDOR_SOAP.bat" -ForegroundColor Yellow
    Write-Host ""
}

# 2. Verificar o Iniciar Portal Web (Kestrel 5150)
$listeningWeb = Get-NetTCPConnection -LocalPort 5150 -State Listen -ErrorAction SilentlyContinue
if (-not $listeningWeb) {
    Write-Host "[INFO] Iniciando servidor Web ASP.NET Core en puerto 5150..." -ForegroundColor Cyan
    $webProj = Join-Path $RootDir "04. CLIWEB\CLIWEB_EuBank_SOAPDOTNET_GR08\CLIWEB_EuBank_SOAPDOTNET_GR08"
    Start-Process "dotnet" -ArgumentList "run" -WorkingDirectory $webProj -WindowStyle Minimized
    Start-Sleep -Seconds 4
}

# 3. Localizar binario de cloudflared
$cloudflaredPath = "C:\Program Files (x86)\cloudflared\cloudflared.exe"
if (-not (Test-Path $cloudflaredPath)) {
    $cloudflaredPath = "C:\Program Files\cloudflared\cloudflared.exe"
}
if (-not (Test-Path $cloudflaredPath)) {
    $cloudflaredPath = (Get-Command "cloudflared.exe" -ErrorAction SilentlyContinue).Source
}

if (-not $cloudflaredPath -or -not (Test-Path $cloudflaredPath)) {
    Write-Host "[ERROR] No se encontro el ejecutable de cloudflared." -ForegroundColor Red
    pause
    exit 1
}

Write-Host "[1/3] Iniciando Tunel Cloudflare hacia http://localhost:5150..." -ForegroundColor Green
Write-Host "[2/3] Negociando conexion segura y generando enlace publico..." -ForegroundColor Yellow

$psi = New-Object System.Diagnostics.ProcessStartInfo
$psi.FileName = $cloudflaredPath
$psi.Arguments = "tunnel --url http://localhost:5150"
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
    $webUrl = "$tunnelUrl/"

    # Actualizar configuraciones de clientes locales (servidor_ip.txt, App.config, etc.)
    & "$RootDir\CONFIGURAR_IP_CLIENTES.ps1" -NuevaIP $tunnelUrl | Out-Null

    # Copiar al portapapeles y abrir en el navegador automáticamente
    Set-Clipboard -Value $webUrl -ErrorAction SilentlyContinue
    Start-Process $webUrl

    Write-Host ""
    Write-Host "============================================================================" -ForegroundColor Green
    Write-Host "  ¡TUNEL CLOUD Y CLIENTES SOAP .NET LISTOS Y EN LINEA!" -ForegroundColor Green
    Write-Host "============================================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "  [1] ACCESO AL PORTAL WEB (En tu laptop, otra PC o Celular):" -ForegroundColor Cyan
    Write-Host "      $webUrl" -ForegroundColor Yellow
    Write-Host "      (Se abrio automaticamente en tu navegador y se copio al portapapeles)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  [2] CLIENTES LOCALES (Escritorio y Consola en esta PC):" -ForegroundColor Cyan
    Write-Host "      ¡Ya quedaron configurados automaticamente para conectarse al tunel!" -ForegroundColor Green
    Write-Host "      Solo abre [1] ESCRITORIO o [2] CONSOLA desde MENU_PRINCIPAL." -ForegroundColor Green
    Write-Host ""
    Write-Host "  [3] EN OTRA LAPTOP (Para Escritorio o Consola en otra laptop):" -ForegroundColor Cyan
    Write-Host "      Ejecuta en esa laptop: CAMBIAR_IP_SERVIDOR.bat $tunnelUrl" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "  [4] EN LA APP MOVIL (.NET MAUI):" -ForegroundColor Cyan
    Write-Host "      Pega esta URL en el campo Servidor: $tunnelUrl" -ForegroundColor Yellow
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
