package tn.nakhlapp.repository;

import tn.nakhlapp.model.AppUser;
import tn.nakhlapp.util.HashUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AuthRepository extends BaseRepository {

    public Optional<AppUser> authenticate(String pseudo, String password) throws SQLException {
        String sql = "SELECT pseudo, type, soc FROM user WHERE pseudo = ? AND password = ?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, pseudo.trim());
            ps.setString(2, HashUtil.md5(password));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new AppUser(
                            rs.getString("pseudo"),
                            rs.getString("type"),
                            rs.getInt("soc")
                    ));
                }
            }
        }
        return Optional.empty();
    }
}
