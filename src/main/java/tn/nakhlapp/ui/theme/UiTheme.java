package tn.nakhlapp.ui.theme;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public final class UiTheme {

    public static final Color PRIMARY = new Color(0, 120, 215);
    public static final Color ACCENT = new Color(255, 153, 0);
    public static final Color SUCCESS = new Color(46, 125, 50);
    public static final Color DANGER = new Color(198, 40, 40);
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);

    private UiTheme() {
    }

    public static void install() {
        FlatLightLaf.setup();
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ScrollBar.width", 14);
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.putClientProperty("JButton.buttonType", "default");
        return button;
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(TITLE);
        label.setForeground(PRIMARY);
        return label;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(BODY);
        table.getTableHeader().setFont(SUBTITLE);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
    }
}
