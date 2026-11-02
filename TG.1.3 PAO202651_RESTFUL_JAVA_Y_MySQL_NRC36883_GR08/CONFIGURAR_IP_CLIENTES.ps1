param(
    [Parameter(Mandatory=$false)]
    [string]$NuevaIP = "192.168.100.15"
)

$ErrorActionPreference = "SilentlyContinue"
$RootDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# Normalizar URI base
$NuevaIP = $NuevaIP.Trim()
if ($NuevaIP -match "^https?://") {
    $BaseUri = $NuevaIP.TrimEnd('/') + "/Rest_EurekaBank/webresources/coreBancario"
    $CleanHost = $NuevaIP
} else {
    $HostPort = if ($NuevaIP -match ":") { $NuevaIP } else { "${NuevaIP}:8080" }
    $BaseUri = "http://${HostPort}/Rest_EurekaBank/webresources/coreBancario"
    $CleanHost = $HostPort
}

Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "  ACTUALIZANDO SERVIDOR RESTFUL JAVA EN TODOS LOS CLIENTES (GR08)" -ForegroundColor Cyan
Write-Host "  Destino: $CleanHost" -ForegroundColor Yellow
Write-Host "  URI REST: $BaseUri" -ForegroundColor Yellow
Write-Host "======================================================================" -ForegroundColor Cyan

# Guardar servidor_ip.txt en raiz, CLICON y CLIESC (tanto en src como en dist)
Set-Content -Path (Join-Path $RootDir "servidor_ip.txt") -Value $CleanHost -Encoding UTF8
Set-Content -Path (Join-Path $RootDir "04. CLICON\RestClienteConsola\servidor_ip.txt") -Value $CleanHost -Encoding UTF8
Set-Content -Path (Join-Path $RootDir "04. CLICON\RestClienteConsola\dist\servidor_ip.txt") -Value $CleanHost -Encoding UTF8
Set-Content -Path (Join-Path $RootDir "04. CLIESC\RestClienteEscritorio\servidor_ip.txt") -Value $CleanHost -Encoding UTF8
Set-Content -Path (Join-Path $RootDir "04. CLIESC\RestClienteEscritorio\dist\servidor_ip.txt") -Value $CleanHost -Encoding UTF8

# 1. Actualizar CLICON CoreBancario.java
$cliconCb = Join-Path $RootDir "04. CLICON\RestClienteConsola\src\ec\edu\monster\ws\CoreBancario.java"
if (Test-Path $cliconCb) {
    $c = Get-Content $cliconCb -Raw
    $c = $c -replace 'http://[^/:]+:8080/Rest_EurekaBank/webresources', ($BaseUri.Replace("/coreBancario", ""))
    Set-Content -Path $cliconCb -Value $c -Encoding UTF8
    Write-Host "[OK] Actualizado CLICON: $cliconCb" -ForegroundColor Green
}

# 2. Actualizar CLIESC CoreBancario.java
$cliescCb = Join-Path $RootDir "04. CLIESC\RestClienteEscritorio\src\ec\edu\monster\ws\CoreBancario.java"
if (Test-Path $cliescCb) {
    $c = Get-Content $cliescCb -Raw
    $c = $c -replace 'http://[^/:]+:8080/Rest_EurekaBank/webresources', ($BaseUri.Replace("/coreBancario", ""))
    Set-Content -Path $cliescCb -Value $c -Encoding UTF8
    Write-Host "[OK] Actualizado CLIESC: $cliescCb" -ForegroundColor Green
}

# 3. Actualizar CLIWEB EurekaController.java
$cliwebCtrl = Join-Path $RootDir "04. CLIWEB\RestClienteWeb\src\java\ec\edu\monster\controlador\EurekaController.java"
if (Test-Path $cliwebCtrl) {
    $c = Get-Content $cliwebCtrl -Raw
    $c = $c -replace 'http://[^/:]+:8080/Rest_EurekaBank/webresources/coreBancario', $BaseUri
    Set-Content -Path $cliwebCtrl -Value $c -Encoding UTF8
    Write-Host "[OK] Actualizado CLIWEB: $cliwebCtrl" -ForegroundColor Green
}

# 4. Actualizar CLIMOV RestApiClient.java
$climovRest = Join-Path $RootDir "04. CLIMOV\RestClienteMovil\app\src\main\java\ec\edu\monster\ws\RestApiClient.java"
if (Test-Path $climovRest) {
    $c = Get-Content $climovRest -Raw
    $c = $c -replace 'activeHost\s*=\s*"[^"]+"', ('activeHost = "' + $CleanHost + '"')
    Set-Content -Path $climovRest -Value $c -Encoding UTF8
    Write-Host "[OK] Actualizado CLIMOV: $climovRest" -ForegroundColor Green
}

Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "  Todos los clientes RESTful Java apuntan ahora a: $CleanHost" -ForegroundColor Green
Write-Host "======================================================================" -ForegroundColor Cyan
