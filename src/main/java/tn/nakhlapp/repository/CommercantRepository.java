package tn.nakhlapp.repository;

import tn.nakhlapp.model.Commercant;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CommercantRepository extends BaseRepository {

    public List<Commercant> findAll() throws SQLException {
        List<Commercant> list = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT id, nom, adresse, tel FROM commercant ORDER BY nom")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Commercant(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("adresse"),
                            String.valueOf(rs.getInt("tel"))
                    ));
                }
            }
        }
        return list;
    }

    public int insert(String name, String address, String phone) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO commercant (nom, adresse, tel) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name.toUpperCase());
            ps.setString(2, address.toUpperCase());
            ps.setString(3, phone);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public void update(int id, String name, String address, String phone) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "UPDATE commercant SET nom = ?, adresse = ?, tel = ? WHERE id = ?")) {
            ps.setString(1, name.toUpperCase());
            ps.setString(2, address.toUpperCase());
            ps.setString(3, phone);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM commercant WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
