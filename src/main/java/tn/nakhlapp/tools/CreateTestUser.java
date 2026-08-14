package tn.nakhlapp.tools;

import tn.nakhlapp.util.HashUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Crée un utilisateur applicatif de test dans la table {@code user}.
 * Usage:
 *   java ... CreateTestUser [host] [port] [database] [mysqlUser] [mysqlPassword] [appPseudo] [appPassword] [role]
 */
public final class CreateTestUser {

    private CreateTestUser() {
    }

    public static void main(String[] args) throws Exception {
        String host = arg(args, 0, "localhost");
        String port = arg(args, 1, "3306");
        String database = arg(args, 2, "tamr");
        String mysqlUser = arg(args, 3, "root");
        String mysqlPassword = arg(args, 4, "");
        String pseudo = arg(args, 5, "test");
        String password = arg(args, 6, "test123");
        String role = arg(args, 7, "admin");
        int companyId = 1;

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";

        System.out.println("Connexion à " + url + " avec " + mysqlUser + "...");

        try (Connection conn = DriverManager.getConnection(url, mysqlUser, mysqlPassword)) {
            ensureSocieteExists(conn);
            upsertAppUser(conn, pseudo, password, role, companyId);
            System.out.println();
            System.out.println("Utilisateur créé / mis à jour avec succès.");
            System.out.println("  Base de données : " + database);
            System.out.println("  Pseudo          : " + pseudo);
            System.out.println("  Mot de passe    : " + password);
            System.out.println("  Rôle            : " + role);
            System.out.println();
            System.out.println("Connectez-vous dans NAKHLA avec ces identifiants après la connexion MySQL.");
        }
    }

    private static void ensureSocieteExists(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM societe")) {
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO societe (nom, adresse, tel, photo, nomfr, adressefr, matriculef, gsm, email) "
                                + "VALUES ('SOCIETE TEST', 'ADRESSE', '0', '', 'SOCIETE TEST', '', '', '', 'test@local')")) {
                    ps.executeUpdate();
                    System.out.println("Société par défaut créée (id=1 attendu).");
                }
            }
        }
    }

    private static void upsertAppUser(Connection conn, String pseudo, String password, String role, int companyId)
            throws SQLException {
        String md5 = HashUtil.md5(password);
        try (PreparedStatement find = conn.prepareStatement("SELECT pseudo FROM user WHERE pseudo = ?")) {
            find.setString(1, pseudo);
            try (ResultSet rs = find.executeQuery()) {
                if (rs.next()) {
                    try (PreparedStatement update = conn.prepareStatement(
                            "UPDATE user SET password = ?, type = ?, soc = ? WHERE pseudo = ?")) {
                        update.setString(1, md5);
                        update.setString(2, role);
                        update.setInt(3, companyId);
                        update.setString(4, pseudo);
                        update.executeUpdate();
                        System.out.println("Utilisateur existant mis à jour: " + pseudo);
                    }
                    return;
                }
            }
        }
        try (PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO user (pseudo, password, type, soc) VALUES (?, ?, ?, ?)")) {
            insert.setString(1, pseudo);
            insert.setString(2, md5);
            insert.setString(3, role);
            insert.setInt(4, companyId);
            insert.executeUpdate();
            System.out.println("Nouvel utilisateur inséré: " + pseudo);
        }
    }

    private static String arg(String[] args, int index, String defaultValue) {
        return args.length > index && !args[index].isBlank() ? args[index] : defaultValue;
    }
}
