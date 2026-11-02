param(
    [Parameter(Mandatory=$false)]
    [string]$NuevaIP = "192.168.100.15"
)

$ErrorActionPreference = "SilentlyContinue"
$RootDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "  ACTUALIZANDO IP DEL SERVIDOR RESTFUL JAVA EN LOS 4 CLIENTES (GR08)" -ForegroundColor Cyan
Write-Host "  Nueva URI Base REST: http://${NuevaIP}:8080/Rest_EurekaBank/webresources/coreBancario" -ForegroundColor Yellow
Write-Host "======================================================================" -ForegroundColor Cyan

# Guardar servidor_ip.txt en raiz, CLICON y CLIESC
Set-Content -Path (Join-Path $RootDir "servidor_ip.txt") -Value $NuevaIP -Encoding UTF8
Set-Content -Path (Join-Path $RootDir "04. CLICON\RestClienteConsola\servidor_ip.txt") -Value $NuevaIP -Encoding UTF8
Set-Content -Path (Join-Path $RootDir "04. CLIESC\RestClienteEscritorio\servidor_ip.txt") -Value $NuevaIP -Encoding UTF8

# 1. Actualizar CLICON CoreBancario.java
$cliconCb = Join-Path $RootDir "04. CLICON\RestClienteConsola\src\ec\edu\monster\ws\CoreBancario.java"
if (Test-Path $cliconCb) {
    $c = Get-Content $cliconCb -Raw
    $c = $c -replace "http://[^/:]+:8080/Rest_EurekaBank/webresources", "http://${NuevaIP}:8080/Rest_EurekaBank/webresources"
    Set-Content -Path $cliconCb -Value $c -Encoding UTF8
    Write-Host "[OK] Actualizado CLICON: $cliconCb" -ForegroundColor Green
}

# 2. Actualizar CLIESC CoreBancario.java
$cliescCb = Join-Path $RootDir "04. CLIESC\RestClienteEscritorio\src\ec\edu\monster\ws\CoreBancario.java"
if (Test-Path $cliescCb) {
    $c = Get-Content $cliescCb -Raw
    $c = $c -replace "http://[^/:]+:8080/Rest_EurekaBank/webresources", "http://${NuevaIP}:8080/Rest_EurekaBank/webresources"
    Set-Content -Path $cliescCb -Value $c -Encoding UTF8
    Write-Host "[OK] Actualizado CLIESC: $cliescCb" -ForegroundColor Green
}

# 3. Actualizar CLIWEB EurekaController.java
$cliwebCtrl = Join-Path $RootDir "04. CLIWEB\RestClienteWeb\src\java\ec\edu\monster\controlador\EurekaController.java"
if (Test-Path $cliwebCtrl) {
    $c = Get-Content $cliwebCtrl -Raw
    $c = $c -replace "http://[^/:]+:8080/Rest_EurekaBank/webresources/coreBancario", "http://${NuevaIP}:8080/Rest_EurekaBank/webresources/coreBancario"
    Set-Content -Path $cliwebCtrl -Value $c -Encoding UTF8
    Write-Host "[OK] Actualizado CLIWEB: $cliwebCtrl" -ForegroundColor Green
}

# 4. Actualizar CLIMOV RestApiClient.java
$climovRest = Join-Path $RootDir "04. CLIMOV\RestClienteMovil\app\src\main\java\ec\edu\monster\ws\RestApiClient.java"
if (Test-Path $climovRest) {
    $c = Get-Content $climovRest -Raw
    $c = $c -replace 'serverHost\s*=\s*"[^"]+"', ('serverHost = "' + $NuevaIP + ':8080"')
    Set-Content -Path $climovRest -Value $c -Encoding UTF8
    Write-Host "[OK] Actualizado CLIMOV: $climovRest" -ForegroundColor Green
}

Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "  Todos los clientes RESTful Java apuntan ahora a http://${NuevaIP}:8080" -ForegroundColor Green
Write-Host "======================================================================" -ForegroundColor Cyan
