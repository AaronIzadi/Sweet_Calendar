param(
    [switch]$ColdBoot
)

$ErrorActionPreference = "Stop"

$project = Split-Path -Parent $MyInvocation.MyCommand.Path
$sdk = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { "$env:LOCALAPPDATA\Android\Sdk" }
$adb = Join-Path $sdk "platform-tools\adb.exe"
$emulator = Join-Path $sdk "emulator\emulator.exe"
$localProperties = Join-Path $project "local.properties"
$defaultAvd = "Android35_Test"

function Ensure-LocalProperties {
    $escaped = $sdk -replace "\\", "\\\\"
    $content = "sdk.dir=$escaped`n"
    [System.IO.File]::WriteAllText($localProperties, $content, (New-Object System.Text.UTF8Encoding $false))
}

function Get-AdbDevices {
    & $adb devices 2>&1 | Where-Object { $_ -match "^\S+\s+\S+$" -and $_ -notmatch "List of devices" }
}

function Get-BootedDevice {
    Get-AdbDevices | Where-Object { $_ -match "\sdevice$" }
}

function Get-OfflineDevice {
    Get-AdbDevices | Where-Object { $_ -match "\soffline$" }
}

function Wait-ForEmulator {
    param([int]$TimeoutSeconds = 240)
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        if ((Get-BootedDevice).Count -gt 0) {
            & $adb wait-for-device shell getprop sys.boot_completed 2>$null | Out-Null
            $booted = & $adb shell getprop sys.boot_completed 2>$null
            if ($booted -match "1") { return $true }
        }
        Start-Sleep -Seconds 3
    }
    return $false
}

function Start-EmulatorAvd {
    param([string]$Avd, [switch]$Cold)
    $args = @(
        "-avd", $Avd,
        "-gpu", "angle_indirect",
        "-no-boot-anim",
        "-netdelay", "none",
        "-netspeed", "full"
    )
    if ($Cold) {
        $args += "-no-snapshot-load"
    }
    Write-Host "Starting emulator: $Avd ($($args -join ' '))"
    Start-Process -FilePath $emulator -ArgumentList $args | Out-Null
}

if (-not (Test-Path $adb)) {
    Write-Host "adb not found. Install Android SDK Platform-Tools in Android Studio."
    exit 1
}

Ensure-LocalProperties
$env:ANDROID_HOME = $sdk
$env:ANDROID_SDK_ROOT = $sdk

& $adb start-server | Out-Null

if ($ColdBoot) {
    try { & $adb emu kill 2>$null | Out-Null } catch { }
    Start-Sleep -Seconds 2
}

if ((Get-OfflineDevice).Count -gt 0) {
    Write-Host 'Emulator is offline; restarting ADB and cold-booting the AVD.'
    & $adb kill-server | Out-Null
    Start-Sleep -Seconds 1
    & $adb start-server | Out-Null
    $ColdBoot = $true
}

if ((Get-BootedDevice).Count -eq 0) {
    if (-not (Test-Path $emulator)) {
        Write-Host "No emulator running. Start an AVD in Device Manager, then retry."
        exit 1
    }

    $avds = @(& $emulator -list-avds 2>$null)
    $avd = if ($avds -contains $defaultAvd) { $defaultAvd } elseif ($avds.Count -gt 0) { $avds[0] } else { $null }
    if (-not $avd) {
        Write-Host "No AVD found. Create Android35_Test in Device Manager."
        exit 1
    }

    if ($ColdBoot) {
        Write-Host "Tip: if the emulator keeps dying, close it and run .\fix-emulator.ps1 -ColdBoot once."
    }
    Start-EmulatorAvd -Avd $avd -Cold:$ColdBoot
}

if (-not (Wait-ForEmulator)) {
    Write-Host "Emulator did not finish booting."
    Write-Host 'Try: close emulator, run .\fix-emulator.ps1 -ColdBoot, then .\run-on-emulator.ps1 -ColdBoot'
    exit 1
}

Push-Location $project
try {
    .\gradlew.bat installDebug
    if ($LASTEXITCODE -ne 0) { throw "Gradle installDebug failed (exit $LASTEXITCODE)." }

    & $adb shell am start -n "com.sweetcalendar/.MainActivity"
    Write-Host "Sweet Calendar installed and launched on the emulator."
}
finally {
    Pop-Location
}
