package tn.nakhlapp.ui.panel;

import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;

public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String title, String message) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JLabel heading = UiTheme.sectionTitle(title);
        heading.setHorizontalAlignment(SwingConstants.RIGHT);
        add(heading, BorderLayout.NORTH);
        JTextArea area = new JTextArea(message);
        area.setEditable(false);
        area.setFont(UiTheme.BODY);
        add(area, BorderLayout.CENTER);
    }
}
