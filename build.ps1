$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$src = Join-Path $root "src\main\java"
$resources = Join-Path $root "src\main\resources"
$out = Join-Path $root "target\classes"
$lib = Join-Path $root "lib"

if (-not (Test-Path $lib)) {
    New-Item -ItemType Directory -Path $lib | Out-Null
    Write-Host "Téléchargez les dépendances avec Maven, ou placez les JAR dans nakhlapp-modern\lib :"
    Write-Host "  flatlaf-3.5.4.jar, HikariCP-5.1.0.jar, mysql-connector-j-8.0.33.jar"
    Write-Host "Puis relancez ce script."
}

New-Item -ItemType Directory -Force -Path $out | Out-Null

$jars = Get-ChildItem -Path $lib -Filter *.jar -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName }
if ($jars.Count -eq 0) {
    $legacyMysql = Join-Path (Split-Path $root -Parent) "mysql-connector-java-5.1.24\mysql-connector-java-5.1.24-bin.jar"
    if (Test-Path $legacyMysql) {
        $jars = @($legacyMysql)
        Write-Warning "Utilisation du connecteur MySQL legacy. Installez Maven pour une build complète."
    } else {
        throw "Aucune dépendance trouvée. Exécutez: mvn dependency:copy-dependencies -DoutputDirectory=lib"
    }
}

$cp = ($jars -join ";") + ";" + $out
$javaFiles = Get-ChildItem -Path $src -Filter *.java -Recurse | ForEach-Object { $_.FullName }

Write-Host "Compilation de $($javaFiles.Count) fichiers..."
& javac -encoding UTF-8 -cp $cp -d $out @javaFiles
if ($LASTEXITCODE -ne 0) { throw "Compilation échouée" }

Copy-Item -Path (Join-Path $resources "*") -Destination $out -Recurse -Force -ErrorAction SilentlyContinue
Write-Host "Build OK -> $out"
