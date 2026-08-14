package tn.nakhlapp.ui.panel;

import tn.nakhlapp.model.AppUser;
import tn.nakhlapp.service.AuthService;
import tn.nakhlapp.ui.MainFrame;
import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginPanel extends JPanel {

    private final JTextField userField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);

    public LoginPanel(MainFrame frame, AuthService authService) {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.ACCENT, 2),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = UiTheme.sectionTitle("تسجيل الدخول");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title, gbc);

        addField(card, gbc, 1, "إسم المستخدم", userField);
        addField(card, gbc, 2, "كلمة العبور", passwordField);

        gbc.gridy = 3;
        statusLabel.setForeground(UiTheme.DANGER);
        card.add(statusLabel, gbc);

        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        JButton backButton = new JButton("رجوع");
        backButton.addActionListener(e -> frame.showDatabaseConnect());
        card.add(backButton, gbc);

        gbc.gridx = 0;
        JButton loginButton = UiTheme.primaryButton("دخول");
        loginButton.addActionListener(e -> login(frame, authService));
        card.add(loginButton, gbc);

        passwordField.addActionListener(e -> login(frame, authService));
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

    private void login(MainFrame frame, AuthService authService) {
        try {
            Optional<AppUser> user = authService.login(
                    userField.getText().trim(),
                    new String(passwordField.getPassword())
            );
            if (user.isPresent()) {
                frame.showApp();
            } else {
                statusLabel.setText("خطأ في المعطيات المدخلة");
            }
        } catch (Exception ex) {
            statusLabel.setText("خطأ: " + ex.getMessage());
        }
    }
}
