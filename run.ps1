$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$out = Join-Path $root "build\classes"
$sourceList = Join-Path $root "build\run-sources.tmp"
$sqliteJar = Join-Path $root "libs\sqlite-jdbc-3.46.1.3.jar"
New-Item -ItemType Directory -Force -Path $out | Out-Null
$sources = Get-ChildItem -Path (Join-Path $root "src\main\java") -Recurse -Filter *.java | ForEach-Object { $_.FullName }
[System.IO.File]::WriteAllLines($sourceList, $sources, [System.Text.UTF8Encoding]::new($false))
javac -encoding UTF-8 -cp $sqliteJar -d $out "@$sourceList"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Remove-Item -LiteralPath $sourceList -ErrorAction SilentlyContinue
$resources = Join-Path $root "src\main\resources"
if (Test-Path $resources) { Copy-Item -Path (Join-Path $resources "*") -Destination $out -Recurse -Force }
java -cp "$out;$sqliteJar" com.coffeeshop.Main
