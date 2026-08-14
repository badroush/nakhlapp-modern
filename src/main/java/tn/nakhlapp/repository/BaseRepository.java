package tn.nakhlapp.repository;

import tn.nakhlapp.config.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

abstract class BaseRepository {

    protected Connection connection() throws SQLException {
        return DatabaseManager.getConnection();
    }
}
