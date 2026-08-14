package tn.nakhlapp.repository;

import tn.nakhlapp.model.Produit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProduitRepository extends BaseRepository {

    public List<Produit> findAll() throws SQLException {
        List<Produit> list = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement("SELECT id, nom FROM produit ORDER BY nom")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Produit(rs.getInt("id"), rs.getString("nom")));
                }
            }
        }
        return list;
    }

    public Optional<Produit> findById(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("SELECT id, nom FROM produit WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Produit(rs.getInt("id"), rs.getString("nom")));
                }
            }
        }
        return Optional.empty();
    }

    public int insert(String name) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO produit (nom) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name.toUpperCase());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public void update(int id, String name) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("UPDATE produit SET nom = ? WHERE id = ?")) {
            ps.setString(1, name.toUpperCase());
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM produit WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
