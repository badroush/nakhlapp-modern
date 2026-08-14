package tn.nakhlapp.ui;

import tn.nakhlapp.service.AuthService;
import tn.nakhlapp.session.SessionContext;
import tn.nakhlapp.ui.panel.DatabaseConnectPanel;
import tn.nakhlapp.ui.panel.DashboardPanel;
import tn.nakhlapp.ui.panel.LoginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private final AuthService authService = new AuthService();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private JPanel appPanel;

    public MainFrame() {
        super("منظومة التصرف في التمور - NAKHLA");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 800));
        setLocationRelativeTo(null);

        content.add(new DatabaseConnectPanel(this, authService), "db");
        content.add(new LoginPanel(this, authService), "login");

        add(content, BorderLayout.CENTER);
        cardLayout.show(content, "db");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                authService.disconnectDatabase();
            }
        });
    }

    private void ensureAppPanel() {
        if (appPanel != null) {
            content.remove(appPanel);
        }
        DashboardPanel dashboard = new DashboardPanel(this);
        appPanel = new JPanel(new BorderLayout());
        appPanel.add(dashboard.getSidebar(), BorderLayout.WEST);
        appPanel.add(dashboard.getContent(), BorderLayout.CENTER);
        content.add(appPanel, "app");
    }

    public void showLogin() {
        cardLayout.show(content, "login");
    }

    public void showApp() {
        ensureAppPanel();
        cardLayout.show(content, "app");
        setTitle("NAKHLA - " + SessionContext.getUsername() + " (" + SessionContext.getRole() + ")");
    }

    public void showDatabaseConnect() {
        authService.disconnectDatabase();
        if (appPanel != null) {
            content.remove(appPanel);
            appPanel = null;
        }
        cardLayout.show(content, "db");
        setTitle("منظومة التصرف في التمور - NAKHLA");
    }
}
