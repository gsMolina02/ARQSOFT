param(
    [Parameter(Mandatory=$false)]
    [string]$NuevaIP = "192.168.100.15"
)

$ErrorActionPreference = "SilentlyContinue"
$RootDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "  ACTUALIZANDO IP DEL SERVIDOR SOAP .NET EN LOS 4 CLIENTES (GR08)" -ForegroundColor Cyan
Write-Host "  Nueva direccion del Servidor SOAP: http://${NuevaIP}:62278" -ForegroundColor Yellow
Write-Host "======================================================================" -ForegroundColor Cyan

# Guardar servidor_ip.txt en raiz y clientes
Set-Content -Path (Join-Path $RootDir "servidor_ip.txt") -Value $NuevaIP -Encoding UTF8

# 1. Actualizar CLICON App.config y .exe.config
$cliconConfigs = @(
    (Join-Path $RootDir "04. CLICON\CLICON_EuBank_SOAPDOTNET_GR08\App.config"),
    (Join-Path $RootDir "04. CLICON\CLICON_EuBank_SOAPDOTNET_GR08\bin\Debug\CLICON_EuBank_SOAPDOTNET_GR08.exe.config")
)
foreach ($cfg in $cliconConfigs) {
    if (Test-Path $cfg) {
        $c = Get-Content $cfg -Raw
        $c = $c -replace "http://[^/:]+:62278", "http://${NuevaIP}:62278"
        Set-Content -Path $cfg -Value $c -Encoding UTF8
        Write-Host "[OK] Actualizado CLICON: $cfg" -ForegroundColor Green
    }
}

# 2. Actualizar CLIESC App.config y .exe.config
$cliescConfigs = @(
    (Join-Path $RootDir "04. CLIESC\CLIESC_EuBank_SOAPDOTNET_GR08\App.config"),
    (Join-Path $RootDir "04. CLIESC\CLIESC_EuBank_SOAPDOTNET_GR08\bin\Debug\CLIESC_EuBank_SOAPDOTNET_GR08.exe.config")
)
foreach ($cfg in $cliescConfigs) {
    if (Test-Path $cfg) {
        $c = Get-Content $cfg -Raw
        $c = $c -replace "http://[^/:]+:62278", "http://${NuevaIP}:62278"
        Set-Content -Path $cfg -Value $c -Encoding UTF8
        Write-Host "[OK] Actualizado CLIESC: $cfg" -ForegroundColor Green
    }
}

# 3. Actualizar CLIWEB appsettings.json
$cliwebConfigs = @(
    (Join-Path $RootDir "04. CLIWEB\CLIWEB_EuBank_SOAPDOTNET_GR08\CLIWEB_EuBank_SOAPDOTNET_GR08\appsettings.json"),
    (Join-Path $RootDir "04. CLIWEB\CLIWEB_EuBank_SOAPDOTNET_GR08\CLIWEB_EuBank_SOAPDOTNET_GR08\appsettings.Development.json")
)
foreach ($cfg in $cliwebConfigs) {
    if (Test-Path $cfg) {
        $c = Get-Content $cfg -Raw
        $c = $c -replace '"BaseUrl":\s*"http://[^"]+"', ('"BaseUrl": "http://' + $NuevaIP + ':62278/"')
        Set-Content -Path $cfg -Value $c -Encoding UTF8
        Write-Host "[OK] Actualizado CLIWEB: $cfg" -ForegroundColor Green
    }
}

# 4. Actualizar CLIMOV EurekaBankClient.cs
$climovFile = Join-Path $RootDir "04. CLIMOV\CLIMOV_EurekaBank_SOAPDOTNET_GR08\CLIMOV_EurekaBank_SOAPDOTNET_GR08\Services\EurekaBankClient.cs"
if (Test-Path $climovFile) {
    $c = Get-Content $climovFile -Raw
    $c = $c -replace 'http://[^/:]+:62278', "http://${NuevaIP}:62278"
    Set-Content -Path $climovFile -Value $c -Encoding UTF8
    Write-Host "[OK] Actualizado CLIMOV: $climovFile" -ForegroundColor Green
}

Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "  Todos los clientes SOAP .NET apuntan ahora a http://${NuevaIP}:62278" -ForegroundColor Green
Write-Host "======================================================================" -ForegroundColor Cyan
