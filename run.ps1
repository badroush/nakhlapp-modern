$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$out = Join-Path $root "target\classes"
$lib = Join-Path $root "lib"

$jars = Get-ChildItem -Path $lib -Filter *.jar -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName }
if ($jars.Count -eq 0) {
    $legacyMysql = Join-Path (Split-Path $root -Parent) "mysql-connector-java-5.1.24\mysql-connector-java-5.1.24-bin.jar"
    if (Test-Path $legacyMysql) { $jars = @($legacyMysql) }
}
$cp = ($jars -join ";") + ";" + $out
& java -cp $cp tn.nakhlapp.NakhlappApplication
