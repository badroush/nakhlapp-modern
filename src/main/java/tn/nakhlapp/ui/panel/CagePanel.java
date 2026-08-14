package tn.nakhlapp.ui.panel;

import tn.nakhlapp.model.Cage;
import tn.nakhlapp.repository.CageRepository;
import tn.nakhlapp.ui.component.CrudPanel;
import tn.nakhlapp.ui.theme.UiTheme;
import tn.nakhlapp.util.NumberFormatUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CagePanel extends JPanel implements Refreshable {

    private final CageRepository repository = new CageRepository();
    private final JTextField nameField = new JTextField(20);
    private final JTextField coefField = new JTextField(10);
    private final CrudPanel crud;

    public CagePanel() {
        super(new BorderLayout(8, 8));
        crud = new CrudPanel(new String[]{"المعامل", "الاسم", "المعرف"}, this::loadData);
        crud.setOnDelete(id -> {
            try {
                repository.delete(id);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        add(crud.formPanel(
                labeled("الاسم", nameField),
                labeled("المعامل", coefField)
        ), BorderLayout.NORTH);
        add(crud, BorderLayout.CENTER);

        JButton add = UiTheme.primaryButton("إضافة");
        add.addActionListener(e -> {
            try {
                repository.insert(nameField.getText(), NumberFormatUtil.parseDouble(coefField.getText(), 0));
                nameField.setText("");
                coefField.setText("");
                loadData();
            } catch (Exception ex) {
                showError(ex);
            }
        });
        JButton update = new JButton("تحيين");
        update.addActionListener(e -> {
            Integer id = crud.selectedId(2);
            if (id == null) return;
            try {
                repository.update(id, nameField.getText(), NumberFormatUtil.parseDouble(coefField.getText(), 0));
                loadData();
            } catch (Exception ex) {
                showError(ex);
            }
        });
        JButton delete = new JButton("حذف");
        delete.addActionListener(e -> crud.deleteSelected(2));
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
            List<Cage> list = repository.findAll();
            Object[][] rows = new Object[list.size()][3];
            for (int i = 0; i < list.size(); i++) {
                Cage c = list.get(i);
                rows[i] = new Object[]{NumberFormatUtil.format3(c.coefficient()), c.name(), c.id()};
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
