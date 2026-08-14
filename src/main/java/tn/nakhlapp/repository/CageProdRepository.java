package tn.nakhlapp.repository;

import tn.nakhlapp.model.CageProd;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CageProdRepository extends BaseRepository {

    public List<CageProd> findAll() throws SQLException {
        List<CageProd> list = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT id, idproduit, idcage, prixa, prixv FROM cageprod ORDER BY id DESC")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new CageProd(
                            rs.getInt("id"),
                            rs.getInt("idproduit"),
                            rs.getInt("idcage"),
                            rs.getString("prixa"),
                            rs.getString("prixv")
                    ));
                }
            }
        }
        return list;
    }

    public Optional<String> findBuyPrice(int productId, int cageId) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT prixa FROM cageprod WHERE idproduit = ? AND idcage = ?")) {
            ps.setInt(1, productId);
            ps.setInt(2, cageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getString("prixa"));
                }
            }
        }
        return Optional.empty();
    }

    public int insert(int productId, int cageId, String buyPrice, String sellPrice) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO cageprod (idproduit, idcage, prixa, prixv) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, productId);
            ps.setInt(2, cageId);
            ps.setString(3, buyPrice);
            ps.setString(4, sellPrice);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public void update(int id, int productId, int cageId, String buyPrice, String sellPrice) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "UPDATE cageprod SET idproduit=?, idcage=?, prixa=?, prixv=? WHERE id=?")) {
            ps.setInt(1, productId);
            ps.setInt(2, cageId);
            ps.setString(3, buyPrice);
            ps.setString(4, sellPrice);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM cageprod WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
