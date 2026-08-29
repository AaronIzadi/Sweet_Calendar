$ErrorActionPreference = "Stop"

$sdk = "C:\Users\M2\AppData\Local\Android\Sdk"
$packagesDir = Join-Path (Split-Path -Parent $MyInvocation.MyCommand.Path) "sdk-packages"

if (-not (Test-Path $packagesDir)) {
    New-Item -ItemType Directory -Path $packagesDir | Out-Null
}

function Expand-ZipTo($zipPath, $destination) {
    if (Test-Path $destination) { Remove-Item $destination -Recurse -Force }
    New-Item -ItemType Directory -Force -Path $destination | Out-Null
    Expand-Archive -Path $zipPath -DestinationPath $destination -Force
}

function Install-CmdlineTools($zipPath) {
    $temp = Join-Path $env:TEMP "clt-extract"
    if (Test-Path $temp) { Remove-Item $temp -Recurse -Force }
    Expand-Archive -Path $zipPath -DestinationPath $temp -Force
    $latest = Join-Path $sdk "cmdline-tools\latest"
    if (Test-Path $latest) { Remove-Item $latest -Recurse -Force }
    New-Item -ItemType Directory -Force -Path (Join-Path $sdk "cmdline-tools") | Out-Null
  Move-Item (Join-Path $temp "cmdline-tools") $latest
}

function Install-PlatformTools($zipPath) {
    $dest = Join-Path $sdk "platform-tools"
    if (Test-Path $dest) { Remove-Item $dest -Recurse -Force }
    Expand-ZipTo $zipPath (Split-Path $dest -Parent)
    $inner = Get-ChildItem (Split-Path $dest -Parent) -Directory | Where-Object { $_.Name -eq "platform-tools" } | Select-Object -First 1
    if (-not $inner) { throw "platform-tools folder not found in zip" }
}

function Install-BuildTools($zipPath, $version = "34.0.0") {
    $temp = Join-Path $env:TEMP "bt-extract"
    if (Test-Path $temp) { Remove-Item $temp -Recurse -Force }
    Expand-Archive -Path $zipPath -DestinationPath $temp -Force
    $dest = Join-Path $sdk "build-tools\$version"
    if (Test-Path $dest) { Remove-Item $dest -Recurse -Force }
    New-Item -ItemType Directory -Force -Path $dest | Out-Null
    $androidDir = Get-ChildItem $temp -Directory | Where-Object { $_.Name -like "android-*" } | Select-Object -First 1
    if ($androidDir) {
        Copy-Item "$($androidDir.FullName)\*" $dest -Recurse -Force
    } else {
        Copy-Item "$temp\*" $dest -Recurse -Force
    }
}

function Install-Platform($zipPath) {
    $temp = Join-Path $env:TEMP "plat-extract"
    if (Test-Path $temp) { Remove-Item $temp -Recurse -Force }
    Expand-Archive -Path $zipPath -DestinationPath $temp -Force
    $androidDir = Get-ChildItem $temp -Directory | Where-Object { $_.Name -like "android-*" } | Select-Object -First 1
    if (-not $androidDir) { throw "android platform folder not found in zip" }
    $dest = Join-Path $sdk "platforms\$($androidDir.Name)"
    if (Test-Path $dest) { Remove-Item $dest -Recurse -Force }
    New-Item -ItemType Directory -Force -Path (Split-Path $dest -Parent) | Out-Null
    Move-Item $androidDir.FullName $dest
}

$zips = Get-ChildItem $packagesDir -Filter "*.zip" -ErrorAction SilentlyContinue
if (-not $zips) {
    Write-Host "No SDK zip files found."
    Write-Host ""
    Write-Host "Google SDK downloads are blocked on this network, so Android Studio"
    Write-Host "shows empty SDK Platforms / SDK Tools lists."
    Write-Host ""
    Write-Host "Fix option A (recommended): use a VPN, then in Android Studio:"
    Write-Host "  Settings > Languages & Frameworks > Android SDK > Apply"
    Write-Host ""
    Write-Host "Fix option B (offline): download these zips with a VPN or another PC,"
    Write-Host "place them in:"
    Write-Host "  $packagesDir"
    Write-Host ""
    Write-Host "Required files:"
    Write-Host "  commandlinetools-win-*_latest.zip"
    Write-Host "  platform-tools-*-windows.zip  (or platform-tools-latest-windows.zip)"
    Write-Host "  build-tools_r34-windows.zip"
    Write-Host "  platform-34*.zip"
    Write-Host ""
    Write-Host "Download page:"
    Write-Host "  https://developer.android.com/studio#command-line-tools-only"
    exit 1
}

foreach ($zip in $zips) {
    $name = $zip.Name.ToLower()
    Write-Host "Installing $($zip.Name)..."
    if ($name -like "commandlinetools*") { Install-CmdlineTools $zip.FullName }
    elseif ($name -like "platform-tools*") { Install-PlatformTools $zip.FullName }
    elseif ($name -like "build-tools*") { Install-BuildTools $zip.FullName }
    elseif ($name -like "platform-34*") { Install-Platform $zip.FullName }
    else { Write-Host "  Skipped (unknown package type)" }
}

$sdkmanager = Join-Path $sdk "cmdline-tools\latest\bin\sdkmanager.bat"
if (Test-Path $sdkmanager) {
    Write-Host "Running sdkmanager to verify packages..."
    & $sdkmanager --sdk_root=$sdk --list_installed
}

Write-Host ""
Write-Host "Done. Restart Android Studio, then run: .\run-on-bluestacks.ps1"
