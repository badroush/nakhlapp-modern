$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$out = Join-Path $root "target\classes"
$lib = Join-Path $root "lib"
$cp = ((Get-ChildItem $lib -Filter *.jar).FullName -join ";") + ";" + $out

Write-Host "Création de l'utilisateur de test NAKHLA..."
Write-Host "Paramètres MySQL (Entrée = valeur par défaut)"
$dbHost = Read-Host "Hôte MySQL [localhost]"
if ([string]::IsNullOrWhiteSpace($dbHost)) { $dbHost = "localhost" }
$dbPort = Read-Host "Port [3306]"
if ([string]::IsNullOrWhiteSpace($dbPort)) { $dbPort = "3306" }
$database = Read-Host "Base de données [tamr]"
if ([string]::IsNullOrWhiteSpace($database)) { $database = "tamr" }
$mysqlUser = Read-Host "Utilisateur MySQL [root]"
if ([string]::IsNullOrWhiteSpace($mysqlUser)) { $mysqlUser = "root" }
$mysqlPass = Read-Host "Mot de passe MySQL (vide si aucun)" -AsSecureString
$mysqlPassPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($mysqlPass))

$appUser = Read-Host "Pseudo application [test]"
if ([string]::IsNullOrWhiteSpace($appUser)) { $appUser = "test" }
$appPass = Read-Host "Mot de passe application [test123]"
if ([string]::IsNullOrWhiteSpace($appPass)) { $appPass = "test123" }

& java -cp $cp tn.nakhlapp.tools.CreateTestUser $dbHost $dbPort $database $mysqlUser $mysqlPassPlain $appUser $appPass admin
