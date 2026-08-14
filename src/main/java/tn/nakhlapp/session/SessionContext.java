package tn.nakhlapp.session;

public final class SessionContext {

    private static String username;
    private static String role;
    private static int companyId;
    private static String databaseName;

    private SessionContext() {
    }

    public static void setUser(String user, String userRole, int socId) {
        username = user;
        role = userRole;
        companyId = socId;
    }

    public static void setDatabaseName(String name) {
        databaseName = name;
    }

    public static void clearUser() {
        username = null;
        role = null;
        companyId = 0;
    }

    public static void clearDatabase() {
        databaseName = null;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRole() {
        return role;
    }

    public static int getCompanyId() {
        return companyId;
    }

    public static String getDatabaseName() {
        return databaseName;
    }

    public static boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }
}
