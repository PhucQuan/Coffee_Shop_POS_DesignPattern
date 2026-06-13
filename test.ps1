$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$out = Join-Path $root "build\test-classes"
$sourceList = Join-Path $root "build\test-sources.tmp"
New-Item -ItemType Directory -Force -Path $out | Out-Null
$mainSources = Get-ChildItem -Path (Join-Path $root "src\main\java") -Recurse -Filter *.java | ForEach-Object { $_.FullName }
$testSources = Get-ChildItem -Path (Join-Path $root "src\test\java") -Recurse -Filter *.java | ForEach-Object { $_.FullName }
Set-Content -Path $sourceList -Value ($mainSources + $testSources) -Encoding UTF8
javac -encoding UTF-8 -d $out "@$sourceList"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Remove-Item -LiteralPath $sourceList -ErrorAction SilentlyContinue
$resources = Join-Path $root "src\main\resources"
if (Test-Path $resources) { Copy-Item -Path (Join-Path $resources "*") -Destination $out -Recurse -Force }
java -cp $out com.coffeeshop.TestRunner
