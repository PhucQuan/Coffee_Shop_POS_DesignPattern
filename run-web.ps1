$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$out = Join-Path $root "build\classes"
$sourceList = Join-Path $root "build\run-sources.tmp"
$libs = (Get-ChildItem -Path (Join-Path $root "libs\*.jar") | ForEach-Object { $_.FullName }) -join ";"
New-Item -ItemType Directory -Force -Path $out | Out-Null
$sources = Get-ChildItem -Path (Join-Path $root "src\main\java") -Recurse -Filter *.java | ForEach-Object { $_.FullName }
[System.IO.File]::WriteAllLines($sourceList, $sources, [System.Text.UTF8Encoding]::new($false))
javac --release 17 -encoding UTF-8 -cp $libs -d $out "@$sourceList"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Remove-Item -LiteralPath $sourceList -ErrorAction SilentlyContinue
$resources = Join-Path $root "src\main\resources"
if (Test-Path $resources) { Copy-Item -Path (Join-Path $resources "*") -Destination $out -Recurse -Force }
Write-Host "Starting Coffee Shop POS Web Server on http://localhost:8088 ..." -ForegroundColor Green
Start-Process "http://localhost:8088"
java -cp "$out;$libs" com.coffeeshop.api.WebServer
