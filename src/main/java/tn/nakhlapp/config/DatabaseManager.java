package tn.nakhlapp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import tn.nakhlapp.session.SessionContext;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseManager {

    private static HikariDataSource dataSource;

    private DatabaseManager() {
    }

    public static synchronized void connect(String database, String user, String password) throws SQLException {
        close();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + AppConfig.dbHost() + ":" + AppConfig.dbPort() + "/"
                + database
                + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true");
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("NakhlappPool");
        dataSource = new HikariDataSource(config);
        try (Connection ignored = dataSource.getConnection()) {
            SessionContext.setDatabaseName(database);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Aucune connexion active. Connectez-vous d'abord à la base de données.");
        }
        return dataSource.getConnection();
    }

    public static boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }

    public static synchronized void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
        SessionContext.clearDatabase();
    }
}
