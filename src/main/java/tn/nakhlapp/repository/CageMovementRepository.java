package tn.nakhlapp.repository;

import tn.nakhlapp.model.CageMovement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CageMovementRepository extends BaseRepository {

    public List<CageMovement> findByType(CageMovement.MovementType type, Integer clientId) throws SQLException {
        List<CageMovement> list = new ArrayList<>();
        String sql = buildSelectSql(type, clientId);
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            if (clientId != null && type != CageMovement.MovementType.STOCK) {
                ps.setInt(1, clientId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new CageMovement(
                            rs.getInt("id"),
                            rs.getDate("date").toLocalDate(),
                            rs.getTime("heur").toLocalTime(),
                            rs.getObject("client") == null ? null : rs.getInt("client"),
                            rs.getInt("cage"),
                            rs.getString("qty"),
                            type
                    ));
                }
            }
        }
        return list;
    }

    private String buildSelectSql(CageMovement.MovementType type, Integer clientId) {
        if (type == CageMovement.MovementType.STOCK) {
            return "SELECT id, date, heur, NULL AS client, cage, stockcage AS qty FROM stock_cage ORDER BY id DESC";
        }
        String table = type.table();
        if (clientId != null) {
            return "SELECT id, date, heur, client, cage, nbcage AS qty FROM " + table + " WHERE client = ? ORDER BY id DESC";
        }
        return "SELECT id, date, heur, client, cage, nbcage AS qty FROM " + table + " ORDER BY id DESC";
    }

    public void insert(CageMovement movement) throws SQLException {
        switch (movement.type()) {
            case RETURN -> insertReturn(movement);
            case OUT -> insertOut(movement);
            case STOCK -> insertStock(movement);
        }
    }

    private void insertReturn(CageMovement movement) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO retourcage (date, heur, client, cage, nbcage) VALUES (?, ?, ?, ?, ?)")) {
            ps.setDate(1, Date.valueOf(movement.date()));
            ps.setTime(2, Time.valueOf(movement.time()));
            ps.setInt(3, movement.clientId());
            ps.setInt(4, movement.cageId());
            ps.setString(5, movement.quantity());
            ps.executeUpdate();
        }
    }

    private void insertOut(CageMovement movement) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO sortiecage (date, heur, client, cage, nbcage) VALUES (?, ?, ?, ?, ?)")) {
            ps.setDate(1, Date.valueOf(movement.date()));
            ps.setTime(2, Time.valueOf(movement.time()));
            ps.setInt(3, movement.clientId());
            ps.setInt(4, movement.cageId());
            ps.setString(5, movement.quantity());
            ps.executeUpdate();
        }
    }

    private void insertStock(CageMovement movement) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO stock_cage (date, heur, cage, stockcage) VALUES (?, ?, ?, ?)")) {
            ps.setDate(1, Date.valueOf(movement.date()));
            ps.setTime(2, Time.valueOf(movement.time()));
            ps.setInt(3, movement.cageId());
            ps.setString(4, movement.quantity());
            ps.executeUpdate();
        }
    }
}
