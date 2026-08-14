package tn.nakhlapp.repository;

import tn.nakhlapp.model.Client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepository extends BaseRepository {

    public List<Client> findAll() throws SQLException {
        List<Client> clients = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT id, nom, tel FROM client ORDER BY nom")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clients.add(map(rs));
                }
            }
        }
        return clients;
    }

    public List<Client> search(String term) throws SQLException {
        List<Client> clients = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT id, nom, tel FROM client WHERE nom LIKE ? ORDER BY nom")) {
            ps.setString(1, "%" + term + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clients.add(map(rs));
                }
            }
        }
        return clients;
    }

    public Optional<Client> findById(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT id, nom, tel FROM client WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        }
        return Optional.empty();
    }

    public int insert(String name, String phone) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO client (nom, tel) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name.toUpperCase());
            ps.setString(2, phone);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public void update(int id, String name, String phone) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "UPDATE client SET nom = ?, tel = ? WHERE id = ?")) {
            ps.setString(1, name.toUpperCase());
            ps.setString(2, phone);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM client WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Client map(ResultSet rs) throws SQLException {
        return new Client(rs.getInt("id"), rs.getString("nom"), String.valueOf(rs.getInt("tel")));
    }
}
