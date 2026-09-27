$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$reactDir = Join-Path $root "frontend-react"
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "  Starting PurrCoffee React Frontend POS..." -ForegroundColor Green
Write-Host "  URL: http://localhost:3000" -ForegroundColor Yellow
Write-Host "===================================================" -ForegroundColor Cyan
Set-Location -Path $reactDir
npm run dev
