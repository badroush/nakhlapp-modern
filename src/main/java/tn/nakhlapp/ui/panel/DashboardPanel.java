package tn.nakhlapp.ui.panel;

import tn.nakhlapp.ui.MainFrame;
import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DashboardPanel {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final MainFrame frame;
    private final Map<String, Supplier<JComponent>> panelFactories = new LinkedHashMap<>();
    private final Map<String, JComponent> loadedPanels = new LinkedHashMap<>();

    public DashboardPanel(MainFrame frame) {
        this.frame = frame;
        registerPanelFactories();
        showPanel("home");
    }

    private void registerPanelFactories() {
        panelFactories.put("home", HomePanel::new);
        panelFactories.put("clients", ClientPanel::new);
        panelFactories.put("commercants", CommercantPanel::new);
        panelFactories.put("cages", CagePanel::new);
        panelFactories.put("societe", SocietePanel::new);
        panelFactories.put("products", ProduitPanel::new);
        panelFactories.put("prices", CageProdPanel::new);
        panelFactories.put("users", UserPanel::new);
        panelFactories.put("purchases", PurchasePanel::new);
        panelFactories.put("payments", ReglementPanel::new);
        panelFactories.put("movements", CageMovementPanel::new);
        panelFactories.put("reports", () -> new PlaceholderPanel(
                "التقارير",
                "ReportsPanel - utilise les fichiers Jasper existants dans src/NakhlaReports"));
        panelFactories.put("settings", () -> new PlaceholderPanel(
                "الإعدادات",
                "Sauvegarde, banque, email - à connecter aux scripts existants"));
    }

    private void showPanel(String key) {
        JComponent panel = loadedPanels.computeIfAbsent(key, k -> {
            Supplier<JComponent> factory = panelFactories.get(k);
            if (factory == null) {
                throw new IllegalArgumentException("Panel inconnu: " + k);
            }
            JComponent created = factory.get();
            content.add(created, k);
            return created;
        });
        if (panel instanceof Refreshable refreshable) {
            refreshable.refreshData();
        }
        cardLayout.show(content, key);
    }

    public JComponent getSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 247, 250));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        JLabel brand = new JLabel("NAKHLA", SwingConstants.CENTER);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        brand.setForeground(UiTheme.PRIMARY);
        brand.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 8));
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(brand);

        Map<String, String> items = new LinkedHashMap<>();
        items.put("home", "الرئيسية");
        items.put("clients", "الفلاحون");
        items.put("commercants", "التجار");
        items.put("cages", "الأقفاص");
        items.put("societe", "الشركة");
        items.put("products", "المنتجات");
        items.put("prices", "أسعار المنتجات");
        items.put("users", "المستخدمون");
        items.put("purchases", "المشتريات");
        items.put("payments", "التسويات");
        items.put("movements", "حركة الأقفاص");
        items.put("reports", "التقارير");
        items.put("settings", "الإعدادات");

        for (Map.Entry<String, String> entry : items.entrySet()) {
            sidebar.add(navButton(entry.getKey(), entry.getValue()));
        }

        sidebar.add(Box.createVerticalGlue());
        JButton logout = new JButton("خروج");
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.addActionListener(e -> frame.showDatabaseConnect());
        sidebar.add(logout);
        sidebar.add(Box.createVerticalStrut(12));
        return sidebar;
    }

    private JButton navButton(String card, String label) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 42));
        button.setBackground(Color.WHITE);
        button.addActionListener(e -> showPanel(card));
        return button;
    }

    public JPanel getContent() {
        return content;
    }
}
