package tn.nakhlapp.service;

import tn.nakhlapp.config.DatabaseManager;
import tn.nakhlapp.model.AppUser;
import tn.nakhlapp.repository.AuthRepository;
import tn.nakhlapp.session.SessionContext;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {

    private final AuthRepository authRepository = new AuthRepository();

    public void connectDatabase(String database, String user, String password) throws SQLException {
        DatabaseManager.connect(database, user, password);
    }

    public Optional<AppUser> login(String pseudo, String password) throws SQLException {
        Optional<AppUser> user = authRepository.authenticate(pseudo, password);
        user.ifPresent(u -> SessionContext.setUser(u.pseudo(), u.role(), u.companyId()));
        return user;
    }

    public void logout() {
        SessionContext.clearUser();
    }

    public void disconnectDatabase() {
        DatabaseManager.close();
        SessionContext.clearUser();
    }
}
