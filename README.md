# NAKHLA Modern - منظومة التصرف في التمور

Application Java modernisée conservant le schéma MySQL et la logique métier de la version NetBeans (2023).

## Améliorations par rapport à l'ancienne version

| Ancien | Modernisé |
|--------|-----------|
| NetBeans Ant + 80+ JAR locaux | Maven + dépendances centralisées |
| SQL concaténé (injection SQL) | PreparedStatement |
| Variables statiques globales (`Pass.nm`, `ListeAgr.nmagr`...) | SessionContext |
| IP MySQL codée en dur (`192.168.1.200`) | `application.properties` / variables d'environnement |
| Bug Pass0 (pas d'appel `getConnection`) | Corrigé dans l'ancien + nouveau flux propre |
| Une JFrame par écran | Fenêtre unique + navigation latérale |
| Java 8 / Nimbus | Java 17 + FlatLaf |

## Prérequis

- Java 17+
- Maven 3.9+ (ou utiliser le script PowerShell)
- MySQL avec la base `tamr` (voir `../nakhla_db.sql`)

## Configuration

Éditer `src/main/resources/application.properties` :

```properties
db.host=localhost
db.port=3306
```

Ou variables d'environnement : `DB_HOST`, `DB_PORT`.

## Lancement avec Maven

```bash
cd nakhlapp-modern
mvn package
java -jar target/nakhlapp-modern-2.0.0.jar
```

## Lancement sans Maven (PowerShell)

```powershell
cd nakhlapp-modern
.\build.ps1
.\run.ps1
```

## Sections disponibles

1. **Connexion BD** – équivalent `Pass0`
2. **Login** – équivalent `Pass` (MD5 compatible)
3. **Tableau de bord** – équivalent `FirstPage`
4. **الفلاحون** – `Agriculteur` / table `client`
5. **التجار** – `Commercant`
6. **الأقفاص** – `Cage`
7. **الشركة** – `Societe`
8. **المنتجات** – `produit`
9. **أسعار المنتجات** – `Prixproduit` / `cageprod`
10. **المستخدمون** – `Users`
11. **المشتريات** – `Index` / table `operation`
12. **التسويات** – `Reglement`
13. **حركة الأقفاص** – `Mouvement_cage`
14. **التقارires / الإعدادات** – placeholders (JasperReports `src/NakhlaReports`)

## Compte par défaut (base exemple)

- Utilisateur : `admin`
- Mot de passe : `admin` (MD5 dans la table `user`)

## Ancienne application

Le projet NetBeans d'origine reste dans `../src/nakhlapp`. Des corrections ont été appliquées à `Pass0.java` et `DbCon.java`.
