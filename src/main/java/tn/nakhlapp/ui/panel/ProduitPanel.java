package tn.nakhlapp.ui.panel;

import tn.nakhlapp.model.Produit;
import tn.nakhlapp.repository.ProduitRepository;
import tn.nakhlapp.ui.component.CrudPanel;
import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProduitPanel extends JPanel implements Refreshable {

    private final ProduitRepository repository = new ProduitRepository();
    private final JTextField nameField = new JTextField(20);
    private final CrudPanel crud;

    public ProduitPanel() {
        super(new BorderLayout(8, 8));
        crud = new CrudPanel(new String[]{"الاسم", "المعرف"}, this::loadData);
        crud.setOnDelete(id -> {
            try {
                repository.delete(id);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        add(crud.formPanel(labeled("الاسم", nameField)), BorderLayout.NORTH);
        add(crud, BorderLayout.CENTER);

        JButton add = UiTheme.primaryButton("إضافة");
        add.addActionListener(e -> {
            try {
                repository.insert(nameField.getText());
                nameField.setText("");
                loadData();
            } catch (Exception ex) {
                showError(ex);
            }
        });
        JButton update = new JButton("تحيين");
        update.addActionListener(e -> {
            Integer id = crud.selectedId(1);
            if (id == null) return;
            try {
                repository.update(id, nameField.getText());
                loadData();
            } catch (Exception ex) {
                showError(ex);
            }
        });
        JButton delete = new JButton("حذف");
        delete.addActionListener(e -> crud.deleteSelected(1));
        add(crud.actionBar(add, update, delete), BorderLayout.SOUTH);
    }

    @Override
    public void refreshData() {
        loadData();
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.add(new JLabel(label, SwingConstants.RIGHT), BorderLayout.EAST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private void loadData() {
        try {
            List<Produit> list = repository.findAll();
            Object[][] rows = new Object[list.size()][2];
            for (int i = 0; i < list.size(); i++) {
                rows[i] = new Object[]{list.get(i).name(), list.get(i).id()};
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
