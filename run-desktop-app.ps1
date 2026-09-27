$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$reactDir = Join-Path $root "frontend-react"

Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "  Starting PurrCoffee POS as a Native Desktop App..." -ForegroundColor Green
Write-Host "===================================================" -ForegroundColor Cyan

Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "npm run dev" -WorkingDirectory $reactDir -WindowStyle Hidden
Start-Sleep -Seconds 2

$edgePaths = @(
    "${env:ProgramFiles(x86)}\Microsoft\Edge\Application\msedge.exe",
    "${env:ProgramFiles}\Microsoft\Edge\Application\msedge.exe",
    "${env:ProgramFiles}\Google\Chrome\Application\chrome.exe"
)

$launched = $false
foreach ($browser in $edgePaths) {
    if (Test-Path $browser) {
        Start-Process -FilePath $browser -ArgumentList "--app=http://localhost:3000", "--window-size=1280,820"
        $launched = $true
        break
    }
}

if (-not $launched) {
    Start-Process "http://localhost:3000"
}
