package tn.nakhlapp.repository;

import tn.nakhlapp.model.AppUser;
import tn.nakhlapp.util.HashUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository extends BaseRepository {

    public List<AppUser> findAll() throws SQLException {
        List<AppUser> users = new ArrayList<>();
        try (PreparedStatement ps = connection().prepareStatement("SELECT pseudo, type, soc FROM user ORDER BY pseudo")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(new AppUser(rs.getString("pseudo"), rs.getString("type"), rs.getInt("soc")));
                }
            }
        }
        return users;
    }

    public void insert(String pseudo, String password, String type, int companyId) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO user (pseudo, password, type, soc) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, pseudo);
            ps.setString(2, HashUtil.md5(password));
            ps.setString(3, type);
            ps.setInt(4, companyId);
            ps.executeUpdate();
        }
    }

    public void updatePassword(String pseudo, String password) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "UPDATE user SET password = ? WHERE pseudo = ?")) {
            ps.setString(1, HashUtil.md5(password));
            ps.setString(2, pseudo);
            ps.executeUpdate();
        }
    }

    public void delete(String pseudo) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM user WHERE pseudo = ?")) {
            ps.setString(1, pseudo);
            ps.executeUpdate();
        }
    }
}
