$ErrorActionPreference = "Stop"

$sdk = "C:\Users\M2\AppData\Local\Android\Sdk"
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$packagesDir = Join-Path $projectRoot "sdk-packages"
$localProperties = Join-Path $projectRoot "local.properties"

$dirs = @(
    "build-tools", "cmdline-tools\latest", "emulator", "licenses",
    "platform-tools", "platforms", "skins", "system-images"
)
foreach ($dir in $dirs) {
    New-Item -ItemType Directory -Force -Path (Join-Path $sdk $dir) | Out-Null
}
New-Item -ItemType Directory -Force -Path $packagesDir | Out-Null

@"
sdk.dir=C\:\\Users\\M2\\AppData\\Local\\Android\\Sdk
"@ | Set-Content -Path $localProperties -Encoding UTF8

@"
### User Sources for Android SDK
"@ | Set-Content -Path (Join-Path $sdk "repositories.cfg") -Encoding ASCII

$licenseFiles = @{
    "android-sdk-license" = "24333f8a63b6825ea9c5514f83c2829b004d1fee"
    "android-sdk-preview-license" = "84831b9409646a918e30573bab355c93c27076a1"
}
foreach ($entry in $licenseFiles.GetEnumerator()) {
    Set-Content -Path (Join-Path $sdk "licenses" $entry.Key) -Value $entry.Value -Encoding ASCII
}

Write-Host "SDK folders created."
Write-Host ""
Write-Host "If SDK Platforms / SDK Tools are EMPTY in Android Studio,"
Write-Host "Google's SDK servers are blocked from your network."
Write-Host ""
Write-Host "Quick fix: turn on a VPN, then in Android Studio:"
Write-Host "  File > Settings > Languages & Frameworks > Android SDK"
Write-Host "  SDK Platforms: check Android 14.0 (API 34)"
Write-Host "  SDK Tools: check Build-Tools 34, Platform-Tools, Command-line Tools"
Write-Host "  Click Apply"
Write-Host ""
Write-Host "Offline fix: download SDK zips and run install-sdk-offline.ps1"
Write-Host "  Put zips in: $packagesDir"
