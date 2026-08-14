package tn.nakhlapp.repository;

import tn.nakhlapp.model.Operation;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class OperationRepository extends BaseRepository {

    public List<Operation> findRecent(int limit) throws SQLException {
        List<Operation> list = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT id, date, heur, idclient, idproduit, idcage, qbrut, nbcage, pu, coef "
                        + "FROM operation ORDER BY id DESC LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public int insert(Operation operation) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO operation (date, heur, idclient, idproduit, idcage, qbrut, nbcage, pu, coef) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(operation.date()));
            ps.setTime(2, Time.valueOf(operation.time()));
            ps.setInt(3, operation.clientId());
            ps.setInt(4, operation.productId());
            ps.setInt(5, operation.cageId());
            ps.setString(6, operation.grossWeight());
            ps.setDouble(7, operation.cageCount());
            ps.setString(8, operation.unitPrice());
            ps.setDouble(9, operation.coefficient());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM operation WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Operation map(ResultSet rs) throws SQLException {
        return new Operation(
                rs.getInt("id"),
                rs.getDate("date").toLocalDate(),
                rs.getTime("heur").toLocalTime(),
                rs.getInt("idclient"),
                rs.getInt("idproduit"),
                rs.getInt("idcage"),
                rs.getString("qbrut"),
                rs.getDouble("nbcage"),
                rs.getString("pu"),
                rs.getDouble("coef")
        );
    }
}
