param(
    [Parameter(Mandatory=$false)]
    [string]$NuevaIP = "192.168.100.15"
)

$ErrorActionPreference = "SilentlyContinue"
$RootDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# Sanitizar entrada (quitar comillas, espacios y sufijos de servicio si los pegaron por error)
$NuevaIP = $NuevaIP.Trim().Trim('"').Trim("'")
$NuevaIP = $NuevaIP -replace '(?i)/services(/.*)?$', ''
$NuevaIP = $NuevaIP.TrimEnd('/')

if ($NuevaIP -match "^https?://") {
    $TargetUrl = $NuevaIP
    $CleanHost = $TargetUrl
    $IsHttps = $TargetUrl.StartsWith("https://", [System.StringComparison]::OrdinalIgnoreCase)
} else {
    $HostPort = if ($NuevaIP -match ":") { $NuevaIP } else { "${NuevaIP}:62278" }
    $TargetUrl = "http://${HostPort}"
    $CleanHost = $HostPort
    $IsHttps = $false
}

$SecMode = if ($IsHttps) { "Transport" } else { "None" }

Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "  ACTUALIZANDO SERVIDOR SOAP .NET EN LOS 4 CLIENTES (GR08)" -ForegroundColor Cyan
Write-Host "  Destino: $TargetUrl" -ForegroundColor Yellow
Write-Host "  Modo de Seguridad WCF: $SecMode" -ForegroundColor Yellow
Write-Host "======================================================================" -ForegroundColor Cyan

# Guardar servidor_ip.txt sin BOM
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText((Join-Path $RootDir "servidor_ip.txt"), $TargetUrl, $utf8NoBom)

# 1. Actualizar CLICON App.config y .exe.config
$cliconConfigs = @(
    (Join-Path $RootDir "04. CLICON\CLICON_EuBank_SOAPDOTNET_GR08\CLICON_EuBank_SOAPDOTNET_GR08\App.config"),
    (Join-Path $RootDir "04. CLICON\CLICON_EuBank_SOAPDOTNET_GR08\CLICON_EuBank_SOAPDOTNET_GR08\bin\Debug\CLICON_EuBank_SOAPDOTNET_GR08.exe.config")
)
foreach ($cfg in $cliconConfigs) {
    if (Test-Path $cfg) {
        $c = [System.IO.File]::ReadAllText($cfg, [System.Text.Encoding]::UTF8)
        $c = $c -replace 'https?://[^"''<>\s]+/LoginService\.svc', "${TargetUrl}/Services/LoginService.svc"
        $c = $c -replace 'https?://[^"''<>\s]+/MovimientosService\.svc', "${TargetUrl}/Services/MovimientosService.svc"
        $c = $c -replace '<security mode="(None|Transport)"\s*/>', "<security mode=""$SecMode"" />"
        [System.IO.File]::WriteAllText($cfg, $c, $utf8NoBom)
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
        $c = [System.IO.File]::ReadAllText($cfg, [System.Text.Encoding]::UTF8)
        $c = $c -replace 'https?://[^"''<>\s]+/LoginService\.svc', "${TargetUrl}/Services/LoginService.svc"
        $c = $c -replace 'https?://[^"''<>\s]+/MovimientosService\.svc', "${TargetUrl}/Services/MovimientosService.svc"
        $c = $c -replace '<security mode="(None|Transport)"\s*/>', "<security mode=""$SecMode"" />"
        [System.IO.File]::WriteAllText($cfg, $c, $utf8NoBom)
        Write-Host "[OK] Actualizado CLIESC: $cfg" -ForegroundColor Green
    }
}

# 3. Actualizar CLIWEB appsettings.json
$cliwebConfigs = @(
    (Join-Path $RootDir "04. CLIWEB\CLIWEB_EuBank_SOAPDOTNET_GR08\CLIWEB_EuBank_SOAPDOTNET_GR08\appsettings.json"),
    (Join-Path $RootDir "04. CLIWEB\CLIWEB_EuBank_SOAPDOTNET_GR08\CLIWEB_EuBank_SOAPDOTNET_GR08\appsettings.Development.json"),
    (Join-Path $RootDir "04. CLIWEB\CLIWEB_EuBank_SOAPDOTNET_GR08\CLIWEB_EuBank_SOAPDOTNET_GR08\bin\Debug\net10.0\appsettings.json")
)
foreach ($cfg in $cliwebConfigs) {
    if (Test-Path $cfg) {
        $c = [System.IO.File]::ReadAllText($cfg, [System.Text.Encoding]::UTF8)
        $c = $c -replace '"BaseUrl":\s*"https?://[^"]+"', ('"BaseUrl": "' + $TargetUrl + '/"')
        [System.IO.File]::WriteAllText($cfg, $c, $utf8NoBom)
        Write-Host "[OK] Actualizado CLIWEB: $cfg" -ForegroundColor Green
    }
}

# 4. Actualizar CLIMOV EurekaBankClient.cs
$climovFile = Join-Path $RootDir "04. CLIMOV\CLIMOV_EurekaBank_SOAPDOTNET_GR08\CLIMOV_EurekaBank_SOAPDOTNET_GR08\Services\EurekaBankClient.cs"
if (Test-Path $climovFile) {
    $c = [System.IO.File]::ReadAllText($climovFile, [System.Text.Encoding]::UTF8)
    $c = $c -replace 'BaseUrl\s*\{\s*get;\s*set;\s*\}\s*=\s*"[^"]+";', ('BaseUrl { get; set; } = "' + $TargetUrl + '";')
    [System.IO.File]::WriteAllText($climovFile, $c, $utf8NoBom)
    Write-Host "[OK] Actualizado CLIMOV: $climovFile" -ForegroundColor Green
}

Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "  Todos los clientes SOAP .NET apuntan ahora a: $TargetUrl" -ForegroundColor Green
Write-Host "======================================================================" -ForegroundColor Cyan
