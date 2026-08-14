package tn.nakhlapp.ui.panel;

import tn.nakhlapp.service.AuthService;
import tn.nakhlapp.ui.MainFrame;
import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;

public class DatabaseConnectPanel extends JPanel {

    private final JTextField databaseField = new JTextField(20);
    private final JTextField userField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);

    public DatabaseConnectPanel(MainFrame frame, AuthService authService) {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.PRIMARY, 2),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = UiTheme.sectionTitle("الإتصال بقاعدة البيانات");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title, gbc);

        addField(card, gbc, 1, "قاعدة البيانات", databaseField);
        addField(card, gbc, 2, "المستخدم", userField);
        addField(card, gbc, 3, "كلمة العبور", passwordField);

        gbc.gridy = 4;
        gbc.gridwidth = 2;
        statusLabel.setForeground(UiTheme.DANGER);
        card.add(statusLabel, gbc);

        gbc.gridy = 5;
        JButton connectButton = UiTheme.primaryButton("الإتصال");
        connectButton.addActionListener(e -> connect(frame, authService));
        card.add(connectButton, gbc);

        add(card);
    }

    private void addField(JPanel card, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        card.add(new JLabel(label, SwingConstants.RIGHT), gbc);
        gbc.gridx = 0;
        field.setFont(UiTheme.BODY);
        card.add(field, gbc);
    }

    private void connect(MainFrame frame, AuthService authService) {
        try {
            authService.connectDatabase(
                    databaseField.getText().trim(),
                    userField.getText().trim(),
                    new String(passwordField.getPassword())
            );
            statusLabel.setForeground(UiTheme.SUCCESS);
            statusLabel.setText("تم الإتصال بقاعدة البيانات بنجاح");
            frame.showLogin();
        } catch (Exception ex) {
            statusLabel.setForeground(UiTheme.DANGER);
            statusLabel.setText("فشل في الإتصال: " + ex.getMessage());
        }
    }
}
