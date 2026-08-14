package tn.nakhlapp.ui.panel;

import tn.nakhlapp.model.Commercant;
import tn.nakhlapp.repository.CommercantRepository;
import tn.nakhlapp.ui.component.CrudPanel;
import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CommercantPanel extends JPanel implements Refreshable {

    private final CommercantRepository repository = new CommercantRepository();
    private final JTextField nameField = new JTextField(20);
    private final JTextField addressField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final CrudPanel crud;

    public CommercantPanel() {
        super(new BorderLayout(8, 8));
        crud = new CrudPanel(new String[]{"الهاتف", "العنوان", "الاسم", "المعرف"}, this::loadData);
        crud.setOnDelete(id -> {
            try {
                repository.delete(id);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        add(crud.formPanel(
                labeled("الاسم", nameField),
                labeled("العنوان", addressField),
                labeled("الهاتف", phoneField)
        ), BorderLayout.NORTH);
        add(crud, BorderLayout.CENTER);

        JButton add = UiTheme.primaryButton("إضافة");
        add.addActionListener(e -> {
            try {
                repository.insert(nameField.getText(), addressField.getText(), phoneField.getText());
                clear();
                loadData();
            } catch (Exception ex) {
                showError(ex);
            }
        });
        JButton update = new JButton("تحيين");
        update.addActionListener(e -> {
            Integer id = crud.selectedId(3);
            if (id == null) return;
            try {
                repository.update(id, nameField.getText(), addressField.getText(), phoneField.getText());
                loadData();
            } catch (Exception ex) {
                showError(ex);
            }
        });
        JButton delete = new JButton("حذف");
        delete.addActionListener(e -> crud.deleteSelected(3));
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
            List<Commercant> list = repository.findAll();
            Object[][] rows = new Object[list.size()][4];
            for (int i = 0; i < list.size(); i++) {
                Commercant c = list.get(i);
                rows[i] = new Object[]{c.phone(), c.address(), c.name(), c.id()};
            }
            crud.setRows(rows);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void clear() {
        nameField.setText("");
        addressField.setText("");
        phoneField.setText("");
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
    }
}
