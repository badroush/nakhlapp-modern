package tn.nakhlapp.repository;

import tn.nakhlapp.model.Cage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CageRepository extends BaseRepository {

    public List<Cage> findAll() throws SQLException {
        List<Cage> list = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement("SELECT id, nom, coef FROM cage ORDER BY nom")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public Optional<Cage> findById(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("SELECT id, nom, coef FROM cage WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        }
        return Optional.empty();
    }

    public int insert(String name, double coefficient) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO cage (nom, coef) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name.toUpperCase());
            ps.setString(2, String.valueOf(coefficient));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public void update(int id, String name, double coefficient) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "UPDATE cage SET nom = ?, coef = ? WHERE id = ?")) {
            ps.setString(1, name.toUpperCase());
            ps.setString(2, String.valueOf(coefficient));
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM cage WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Cage map(ResultSet rs) throws SQLException {
        return new Cage(rs.getInt("id"), rs.getString("nom"), Double.parseDouble(rs.getString("coef")));
    }
}
