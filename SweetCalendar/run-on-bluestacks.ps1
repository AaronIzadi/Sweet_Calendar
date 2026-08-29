$ErrorActionPreference = "Stop"
$sdk = "C:\Users\M2\AppData\Local\Android\Sdk"
$project = Split-Path -Parent $MyInvocation.MyCommand.Path
$adb = "C:\Program Files\BlueStacks_nxt\HD-Adb.exe"
$javaHome = "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3\jbr"

function Test-SdkReady {
    (Test-Path "$sdk\build-tools\34.0.0\aapt2.exe") -and
    (Test-Path "$sdk\platforms\android-34\android.jar")
}

if (-not (Test-SdkReady)) {
    Write-Host "Android SDK packages are missing."
    Write-Host "In Android Studio: Settings > Languages & Frameworks > Android SDK"
    Write-Host "Install: Android SDK Platform 34, Build-Tools 34, Platform-Tools"
    Write-Host "Waiting for SDK installation..."
    $deadline = (Get-Date).AddMinutes(10)
    while ((Get-Date) -lt $deadline) {
        if (Test-SdkReady) { break }
        Start-Sleep -Seconds 5
    }
    if (-not (Test-SdkReady)) {
        throw "SDK not ready. Install packages in Android Studio SDK Manager, then rerun this script."
    }
}

$env:JAVA_HOME = $javaHome
Push-Location $project
try {
    .\gradlew.bat assembleDebug
    $apk = Get-ChildItem "app\build\outputs\apk\debug\*.apk" | Select-Object -First 1
    if (-not $apk) { throw "APK not found after build." }
    & $adb install -r $apk.FullName
    & $adb shell am start -n "com.example.calendartodo/.MainActivity"
    Write-Host "Installed and launched Sweet Calendar on BlueStacks."
}
finally {
    Pop-Location
}
