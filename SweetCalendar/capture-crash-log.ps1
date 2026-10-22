# Save logcat after a crash. Run immediately after the emulator or app fails.
$ErrorActionPreference = "Stop"

$sdk = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { "$env:LOCALAPPDATA\Android\Sdk" }
$adb = Join-Path $sdk "platform-tools\adb.exe"
$outDir = Join-Path (Split-Path -Parent $MyInvocation.MyCommand.Path) "crash-logs"
$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$outFile = Join-Path $outDir "logcat-$stamp.txt"

if (-not (Test-Path $adb)) {
    Write-Host "adb not found at $adb"
    exit 1
}

New-Item -ItemType Directory -Force -Path $outDir | Out-Null
& $adb logcat -d -v time *> $outFile
Write-Host "Saved: $outFile"
Write-Host ""
Write-Host "App-related errors (if any):"
Select-String -Path $outFile -Pattern "sweetcalendar|AndroidRuntime|FATAL" | Select-Object -Last 40
