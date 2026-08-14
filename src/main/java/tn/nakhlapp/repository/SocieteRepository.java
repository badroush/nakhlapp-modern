package tn.nakhlapp.repository;

import tn.nakhlapp.model.Societe;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SocieteRepository extends BaseRepository {

    public List<Societe> findAll() throws SQLException {
        List<Societe> list = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT id, nom, adresse, tel, nomfr, adressefr, matriculef, gsm, email FROM societe ORDER BY id DESC")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public Optional<Societe> findPrimary() throws SQLException {
        List<Societe> all = findAll();
        return all.isEmpty() ? Optional.empty() : Optional.of(all.get(0));
    }

    public void insert(Societe societe, String photoPath) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO societe (nom, adresse, tel, photo, nomfr, adressefr, matriculef, gsm, email) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            bind(ps, societe, photoPath);
            ps.executeUpdate();
        }
    }

    public void update(int id, Societe societe, String photoPath) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "UPDATE societe SET nom=?, adresse=?, tel=?, photo=?, nomfr=?, adressefr=?, matriculef=?, gsm=?, email=? WHERE id=?")) {
            bind(ps, societe, photoPath);
            ps.setInt(10, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM societe WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Societe s, String photoPath) throws SQLException {
        ps.setString(1, s.nameAr().toUpperCase());
        ps.setString(2, s.addressAr().toUpperCase());
        ps.setString(3, s.phone());
        ps.setString(4, photoPath == null ? "" : photoPath);
        ps.setString(5, s.nameFr());
        ps.setString(6, s.addressFr());
        ps.setString(7, s.taxId());
        ps.setString(8, s.gsm());
        ps.setString(9, s.email());
    }

    private Societe map(ResultSet rs) throws SQLException {
        return new Societe(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("adresse"),
                String.valueOf(rs.getInt("tel")),
                rs.getString("nomfr"),
                rs.getString("adressefr"),
                rs.getString("matriculef"),
                rs.getString("gsm"),
                rs.getString("email")
        );
    }
}
