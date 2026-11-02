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

# Guardar servidor_ip.txt en raiz, CLICON y CLIESC (sin BOM)
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText((Join-Path $RootDir "servidor_ip.txt"), $CleanHost, $utf8NoBom)
[System.IO.File]::WriteAllText((Join-Path $RootDir "04. CLICON\RestClienteConsola\servidor_ip.txt"), $CleanHost, $utf8NoBom)
[System.IO.File]::WriteAllText((Join-Path $RootDir "04. CLICON\RestClienteConsola\dist\servidor_ip.txt"), $CleanHost, $utf8NoBom)
[System.IO.File]::WriteAllText((Join-Path $RootDir "04. CLIESC\RestClienteEscritorio\servidor_ip.txt"), $CleanHost, $utf8NoBom)
[System.IO.File]::WriteAllText((Join-Path $RootDir "04. CLIESC\RestClienteEscritorio\dist\servidor_ip.txt"), $CleanHost, $utf8NoBom)

# 1. Actualizar CLICON CoreBancario.java
$cliconCb = Join-Path $RootDir "04. CLICON\RestClienteConsola\src\ec\edu\monster\ws\CoreBancario.java"
if (Test-Path $cliconCb) {
    $c = [System.IO.File]::ReadAllText($cliconCb, [System.Text.Encoding]::UTF8)
    if ($c.StartsWith("`u{FEFF}")) { $c = $c.Substring(1) }
    $c = $c -replace 'http://[^/:]+:8080/Rest_EurekaBank/webresources', ($BaseUri.Replace("/coreBancario", ""))
    [System.IO.File]::WriteAllText($cliconCb, $c, $utf8NoBom)
    Write-Host "[OK] Actualizado CLICON: $cliconCb" -ForegroundColor Green
}

# 2. Actualizar CLIESC CoreBancario.java
$cliescCb = Join-Path $RootDir "04. CLIESC\RestClienteEscritorio\src\ec\edu\monster\ws\CoreBancario.java"
if (Test-Path $cliescCb) {
    $c = [System.IO.File]::ReadAllText($cliescCb, [System.Text.Encoding]::UTF8)
    if ($c.StartsWith("`u{FEFF}")) { $c = $c.Substring(1) }
    $c = $c -replace 'http://[^/:]+:8080/Rest_EurekaBank/webresources', ($BaseUri.Replace("/coreBancario", ""))
    [System.IO.File]::WriteAllText($cliescCb, $c, $utf8NoBom)
    Write-Host "[OK] Actualizado CLIESC: $cliescCb" -ForegroundColor Green
}

# 3. Actualizar CLIWEB EurekaController.java
$cliwebCtrl = Join-Path $RootDir "04. CLIWEB\RestClienteWeb\src\java\ec\edu\monster\controlador\EurekaController.java"
if (Test-Path $cliwebCtrl) {
    $c = [System.IO.File]::ReadAllText($cliwebCtrl, [System.Text.Encoding]::UTF8)
    if ($c.StartsWith("`u{FEFF}")) { $c = $c.Substring(1) }
    $c = $c -replace 'http://[^/:]+:8080/Rest_EurekaBank/webresources/coreBancario', $BaseUri
    [System.IO.File]::WriteAllText($cliwebCtrl, $c, $utf8NoBom)
    Write-Host "[OK] Actualizado CLIWEB: $cliwebCtrl" -ForegroundColor Green
}

# 4. Actualizar CLIMOV RestApiClient.java
$climovRest = Join-Path $RootDir "04. CLIMOV\RestClienteMovil\app\src\main\java\ec\edu\monster\ws\RestApiClient.java"
if (Test-Path $climovRest) {
    $c = [System.IO.File]::ReadAllText($climovRest, [System.Text.Encoding]::UTF8)
    if ($c.StartsWith("`u{FEFF}")) { $c = $c.Substring(1) }
    $c = $c -replace 'activeHost\s*=\s*"[^"]+"', ('activeHost = "' + $CleanHost + '"')
    [System.IO.File]::WriteAllText($climovRest, $c, $utf8NoBom)
    Write-Host "[OK] Actualizado CLIMOV: $climovRest" -ForegroundColor Green
}

Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "  Todos los clientes RESTful Java apuntan ahora a: $CleanHost" -ForegroundColor Green
Write-Host "======================================================================" -ForegroundColor Cyan
