package tn.nakhlapp.repository;

import tn.nakhlapp.model.Reglement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReglementRepository extends BaseRepository {

    public List<Reglement> findAll() throws SQLException {
        List<Reglement> list = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT id, date, heur, idclient, montant, type, maniere, num FROM reglement ORDER BY id DESC")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Reglement> searchByClientOrReference(String term) throws SQLException {
        List<Reglement> list = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT r.id, r.date, r.heur, r.idclient, r.montant, r.type, r.maniere, r.num "
                        + "FROM reglement r JOIN client c ON c.id = r.idclient "
                        + "WHERE c.nom LIKE ? OR r.num LIKE ? ORDER BY r.id DESC")) {
            String pattern = "%" + term + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public int insert(Reglement reglement) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO reglement (date, heur, idclient, montant, type, maniere, num) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(reglement.date()));
            ps.setTime(2, Time.valueOf(reglement.time()));
            ps.setInt(3, reglement.clientId());
            ps.setString(4, reglement.amount());
            ps.setString(5, reglement.type());
            ps.setString(6, reglement.method());
            ps.setString(7, reglement.reference());
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
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM reglement WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Reglement map(ResultSet rs) throws SQLException {
        return new Reglement(
                rs.getInt("id"),
                rs.getDate("date").toLocalDate(),
                rs.getTime("heur").toLocalTime(),
                rs.getInt("idclient"),
                rs.getString("montant"),
                rs.getString("type"),
                rs.getString("maniere"),
                rs.getString("num")
        );
    }
}
