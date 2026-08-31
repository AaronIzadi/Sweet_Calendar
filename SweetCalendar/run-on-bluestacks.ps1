$ErrorActionPreference = "Stop"

$project = Split-Path -Parent $MyInvocation.MyCommand.Path
$sdk = "C:\Users\M2\AppData\Local\Android\Sdk"
$adb = "C:\Program Files\BlueStacks_nxt\HD-Adb.exe"
$localProperties = Join-Path $project "local.properties"

# JDK 17 is required (Java 8 / Java 25 break this Android Gradle build).
$javaCandidates = @(
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot",
    "C:\Program Files\Eclipse Adoptium\jdk-17*",
    "C:\Program Files\Android\Android Studio\jbr",
    "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3\jbr"
)

function Resolve-JavaHome {
    foreach ($candidate in $javaCandidates) {
        $matches = @(Get-Item $candidate -ErrorAction SilentlyContinue)
        foreach ($path in $matches) {
            $javaExe = Join-Path $path.FullName "bin\java.exe"
            if (-not (Test-Path $javaExe)) { continue }
            $versionOutput = cmd /c "`"$javaExe`" -version 2>&1"
            if ($versionOutput -match 'version "1\.8' -or $versionOutput -match 'version "25\.') { continue }
            if ($versionOutput -match 'version "17\.') { return $path.FullName }
        }
    }
    return $null
}

function Test-SdkReady {
    (Test-Path "$sdk\build-tools\34.0.0\aapt2.exe") -and
    (Test-Path "$sdk\platforms\android-34\android.jar")
}

function Ensure-LocalProperties {
    $content = "sdk.dir=$($sdk.Replace('\', '/'))`n"
    Set-Content -Path $localProperties -Value $content -Encoding ASCII -NoNewline
    Add-Content -Path $localProperties -Value "" -Encoding ASCII
}

function Invoke-BlueStacksAdb {
    param(
        [switch]$AllowFailure,
        [Parameter(ValueFromRemainingArguments = $true)][string[]]$Args
    )
    if (-not (Test-Path $adb)) {
        throw "BlueStacks ADB not found at: $adb`nInstall BlueStacks 5 or update the path in run-on-bluestacks.ps1"
    }
    $output = & $adb @Args 2>&1
    if (-not $AllowFailure -and $LASTEXITCODE -ne 0) {
        throw "ADB failed: $adb $($Args -join ' ')`n$output"
    }
    return $output
}

function Wait-ForBlueStacksDevice {
    $ports = @("5555", "5556", "5565", "5575")
    foreach ($port in $ports) {
        try {
            Invoke-BlueStacksAdb -AllowFailure connect "127.0.0.1:$port" | Out-Null
        } catch {
            # Port not open; try the next one.
        }
    }

    $deadline = (Get-Date).AddSeconds(20)
    while ((Get-Date) -lt $deadline) {
        $devices = Invoke-BlueStacksAdb devices
        $connected = $devices | Where-Object { $_ -match "device$" -and $_ -notmatch "List of devices" }
        if ($connected) { return $true }
        Start-Sleep -Seconds 2
    }
    return $false
}

$javaHome = Resolve-JavaHome
if (-not $javaHome) {
    Write-Host "JDK 17 not found."
    Write-Host "Install it from: https://adoptium.net/temurin/releases/?version=17"
    Write-Host "Or in Android Studio: File > Settings > Build Tools > Gradle > Gradle JDK = 17"
    exit 1
}

if (-not (Test-SdkReady)) {
    Write-Host "Android SDK packages are missing."
    Write-Host "In Android Studio: Settings > Languages & Frameworks > Android SDK"
    Write-Host "Install: Android SDK Platform 34, Build-Tools 34, Platform-Tools"
    exit 1
}

Ensure-LocalProperties

$env:JAVA_HOME = $javaHome
$env:ANDROID_HOME = $sdk
$env:ANDROID_SDK_ROOT = $sdk
# Avoid mixing SDK platform-tools adb (v41) with BlueStacks HD-Adb (v36).
$env:PATH = ($env:PATH -split ';' | Where-Object { $_ -notmatch 'platform-tools' }) -join ';'

Write-Host "Using JAVA_HOME: $javaHome"
Write-Host "Using ANDROID_HOME: $sdk"

Push-Location $project
try {
    .\gradlew.bat assembleDebug
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle build failed (exit code $LASTEXITCODE)."
    }

    $apk = Get-ChildItem "app\build\outputs\apk\debug\*.apk" | Select-Object -First 1
    if (-not $apk) { throw "APK not found after build." }

    Write-Host ""
    Write-Host "Looking for BlueStacks..."
    Write-Host "Make sure BlueStacks is running and ADB is enabled:"
    Write-Host "  BlueStacks > Settings (gear) > Advanced > Android Debug Bridge = ON"
    Write-Host ""

    Invoke-BlueStacksAdb -AllowFailure kill-server | Out-Null
    Invoke-BlueStacksAdb start-server | Out-Null

    if (-not (Wait-ForBlueStacksDevice)) {
        throw @"
No BlueStacks device found.

1. Open BlueStacks and wait until the home screen loads.
2. Settings > Advanced > turn ON 'Android Debug Bridge'.
3. Run this script again.
"@
    }

    Invoke-BlueStacksAdb install -r $apk.FullName
    Invoke-BlueStacksAdb shell am start -n "com.example.calendartodo/.MainActivity"
    Write-Host ""
    Write-Host "Installed and launched Sweet Calendar on BlueStacks."
}
catch {
    Write-Host ""
    Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
finally {
    Pop-Location
}
