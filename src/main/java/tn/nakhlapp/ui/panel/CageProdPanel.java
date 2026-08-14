package tn.nakhlapp.ui.panel;

import tn.nakhlapp.model.Cage;
import tn.nakhlapp.model.CageProd;
import tn.nakhlapp.model.Produit;
import tn.nakhlapp.repository.CageProdRepository;
import tn.nakhlapp.repository.CageRepository;
import tn.nakhlapp.repository.ProduitRepository;
import tn.nakhlapp.ui.component.CrudPanel;
import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CageProdPanel extends JPanel implements Refreshable {

    private final CageProdRepository repository = new CageProdRepository();
    private final ProduitRepository produitRepository = new ProduitRepository();
    private final CageRepository cageRepository = new CageRepository();
    private final JComboBox<String> productBox = new JComboBox<>();
    private final JComboBox<String> cageBox = new JComboBox<>();
    private final JTextField buyPrice = new JTextField(10);
    private final JTextField sellPrice = new JTextField(10);
    private final CrudPanel crud;
    private Map<String, Integer> productIds = new HashMap<>();
    private Map<String, Integer> cageIds = new HashMap<>();

    public CageProdPanel() {
        super(new BorderLayout(8, 8));
        crud = new CrudPanel(new String[]{"س.بيع", "س.شراء", "قفص", "منتج", "المعرف"}, this::loadData);
        crud.setOnDelete(id -> {
            try {
                repository.delete(id);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        JPanel form = crud.formPanel(
                labeled("المنتج", productBox),
                labeled("القفص", cageBox),
                labeled("سعر الشراء", buyPrice),
                labeled("سعر البيع", sellPrice)
        );
        add(form, BorderLayout.NORTH);
        add(crud, BorderLayout.CENTER);

        JButton add = UiTheme.primaryButton("إضافة");
        add.addActionListener(e -> save(false));
        JButton update = new JButton("تحيين");
        update.addActionListener(e -> save(true));
        JButton delete = new JButton("حذف");
        delete.addActionListener(e -> crud.deleteSelected(4));
        add(crud.actionBar(add, update, delete), BorderLayout.SOUTH);
    }

    @Override
    public void refreshData() {
        reloadCombos();
        loadData();
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.add(new JLabel(label, SwingConstants.RIGHT), BorderLayout.EAST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private void reloadCombos() {
        try {
            productBox.removeAllItems();
            cageBox.removeAllItems();
            productIds.clear();
            cageIds.clear();
            for (Produit p : produitRepository.findAll()) {
                productBox.addItem(p.name());
                productIds.put(p.name(), p.id());
            }
            for (Cage c : cageRepository.findAll()) {
                cageBox.addItem(c.name());
                cageIds.put(c.name(), c.id());
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void save(boolean update) {
        String productName = (String) productBox.getSelectedItem();
        String cageName = (String) cageBox.getSelectedItem();
        if (productName == null || cageName == null) {
            return;
        }
        try {
            int productId = productIds.get(productName);
            int cageId = cageIds.get(cageName);
            if (update) {
                Integer id = crud.selectedId(4);
                if (id == null) return;
                repository.update(id, productId, cageId, buyPrice.getText(), sellPrice.getText());
            } else {
                repository.insert(productId, cageId, buyPrice.getText(), sellPrice.getText());
            }
            buyPrice.setText("");
            sellPrice.setText("");
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadData() {
        try {
            List<CageProd> list = repository.findAll();
            Map<Integer, String> products = new HashMap<>();
            Map<Integer, String> cages = new HashMap<>();
            produitRepository.findAll().forEach(p -> products.put(p.id(), p.name()));
            cageRepository.findAll().forEach(c -> cages.put(c.id(), c.name()));

            Object[][] rows = new Object[list.size()][5];
            for (int i = 0; i < list.size(); i++) {
                CageProd cp = list.get(i);
                rows[i] = new Object[]{
                        cp.sellPrice(),
                        cp.buyPrice(),
                        cages.getOrDefault(cp.cageId(), String.valueOf(cp.cageId())),
                        products.getOrDefault(cp.productId(), String.valueOf(cp.productId())),
                        cp.id()
                };
            }
            crud.setRows(rows);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
    }
}
