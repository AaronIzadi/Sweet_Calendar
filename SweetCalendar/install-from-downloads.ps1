$ErrorActionPreference = "Stop"
$sdk = "C:\Users\M2\AppData\Local\Android\Sdk"

# Look for an extracted full SDK in common locations.
$sources = @(
    "C:\Users\M2\Downloads\Android.SDK.Release.26.1.1.Win",
    "C:\Users\M2\Downloads\Android SDK",
    "C:\Users\M2\Downloads\android-sdk",
    "$env:USERPROFILE\Downloads\SDK"
)

$found = $null
foreach ($src in $sources) {
    if (Test-Path (Join-Path $src "platform-tools\adb.exe")) {
        $found = $src
        break
    }
}

if (-not $found) {
    Write-Host "Full SDK not found yet."
    Write-Host ""
    Write-Host "You installed SDK Tools 26.1.1 (legacy tools/) - that is now in your SDK."
    Write-Host "But you still need: platform-tools, build-tools, and platforms."
    Write-Host ""
    Write-Host "Option 1: Extract Android.SDK.Release.26.1.1.Win.rar with the"
    Write-Host "password from the soft98.ir download page, then copy the extracted"
    Write-Host "folder to: C:\Users\M2\Downloads\Android.SDK.Release.26.1.1.Win"
    Write-Host "and run this script again."
    Write-Host ""
    Write-Host "Option 2: Turn on VPN (v2rayN / Psiphon), then run:"
    Write-Host '  $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-8.0.472.8-hotspot"'
    Write-Host '  & "$env:LOCALAPPDATA\Android\Sdk\tools\bin\sdkmanager.bat" --sdk_root="$env:LOCALAPPDATA\Android\Sdk" "platform-tools" "platforms;android-34" "build-tools;34.0.0"'
    exit 1
}

foreach ($dir in @("platform-tools", "build-tools", "platforms", "system-images", "emulator")) {
    $srcDir = Join-Path $found $dir
    if (Test-Path $srcDir) {
        $dest = Join-Path $sdk $dir
        if (Test-Path $dest) { Remove-Item $dest -Recurse -Force }
        Copy-Item $srcDir $dest -Recurse -Force
        Write-Host "Installed $dir"
    }
}

Write-Host ""
Write-Host "SDK ready. Run: .\run-on-bluestacks.ps1"
