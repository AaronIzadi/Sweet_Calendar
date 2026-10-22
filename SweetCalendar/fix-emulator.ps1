# Stabilizes Android35_Test (API 35) — Bluetooth stack crashes often take down the whole emulator.
# Close the emulator first, then run: .\fix-emulator.ps1
# Use -ColdBoot to delete quick-boot snapshots and force a clean start.

param(
    [string]$AvdName = "Android35_Test",
    [switch]$ColdBoot
)

$ErrorActionPreference = "Stop"

$sdk = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { "$env:LOCALAPPDATA\Android\Sdk" }
$adb = Join-Path $sdk "platform-tools\adb.exe"
$avdHome = Join-Path $env:USERPROFILE ".android\avd"
$avdDir = Join-Path $avdHome "$AvdName.avd"
$configIni = Join-Path $avdDir "config.ini"
$qemuIni = Join-Path $avdDir "hardware-qemu.ini"
$snapshots = Join-Path $avdDir "snapshots"

if (-not (Test-Path $configIni)) {
    Write-Host "AVD not found: $configIni"
    Write-Host "Create $AvdName in Device Manager (API 35, x86_64, Google APIs)."
    exit 1
}

if (Test-Path $adb) {
    try { & $adb emu kill 2>$null | Out-Null } catch { }
    Get-Process -Name "qemu-system*","emulator" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
}

function Set-IniValue {
    param([string]$Path, [string]$Key, [string]$Value)
    $lines = Get-Content $Path -ErrorAction Stop
    $pattern = "^\s*$Key\s*="
    $replaced = $false
    $out = foreach ($line in $lines) {
        if ($line -match $pattern) {
            $replaced = $true
            "$Key = $Value"
        } else {
            $line
        }
    }
    if (-not $replaced) {
        $out += "$Key = $Value"
    }
    Set-Content -Path $Path -Value $out -Encoding UTF8
}

Write-Host "Tuning AVD: $AvdName"

Set-IniValue $configIni "hw.ramSize" "4096"
Set-IniValue $configIni "hw.gpu.enabled" "yes"
Set-IniValue $configIni "hw.gpu.mode" "angle_indirect"
Set-IniValue $configIni "fastboot.forceColdBoot" "yes"
Set-IniValue $configIni "fastboot.forceFastBoot" "no"

if (Test-Path $qemuIni) {
    Set-IniValue $qemuIni "hw.ramSize" "4096"
    Set-IniValue $qemuIni "hw.gpu.enabled" "true"
    Set-IniValue $qemuIni "hw.gpu.mode" "angle_indirect"
    Set-IniValue $qemuIni "fastboot.forceColdBoot" "true"
}

if ($ColdBoot -and (Test-Path $snapshots)) {
    Write-Host "Removing quick-boot snapshots..."
    Remove-Item -Recurse -Force $snapshots
}

Write-Host ""
Write-Host "Done. Start the emulator from Device Manager (Cold Boot), or run:"
Write-Host "  .\run-on-emulator.ps1 -ColdBoot"
