package tn.nakhlapp;

import tn.nakhlapp.ui.MainFrame;
import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;

public final class NakhlappApplication {

    private NakhlappApplication() {
    }

    public static void main(String[] args) {
        UiTheme.install();
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
