package tn.nakhlapp.ui.panel;

import tn.nakhlapp.model.Client;
import tn.nakhlapp.repository.ClientRepository;
import tn.nakhlapp.ui.component.CrudPanel;
import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ClientPanel extends JPanel implements Refreshable {

    private final ClientRepository repository = new ClientRepository();
    private final JTextField nameField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final CrudPanel crud;

    public ClientPanel() {
        super(new BorderLayout(8, 8));
        crud = new CrudPanel(new String[]{"الهاتف", "الاسم", "المعرف"}, this::loadData);
        crud.setOnDelete(id -> {
            try {
                repository.delete(id);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        nameField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        phoneField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel form = crud.formPanel(
                labeled("الاسم", nameField),
                labeled("الهاتف", phoneField)
        );

        JButton add = UiTheme.primaryButton("إضافة");
        add.addActionListener(e -> addClient());
        JButton update = new JButton("تحيين");
        update.addActionListener(e -> updateClient());
        JButton delete = new JButton("حذف");
        delete.addActionListener(e -> crud.deleteSelected(2));

        add(form, BorderLayout.NORTH);
        add(crud, BorderLayout.CENTER);
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
            List<Client> clients = repository.findAll();
            Object[][] rows = new Object[clients.size()][3];
            for (int i = 0; i < clients.size(); i++) {
                Client c = clients.get(i);
                rows[i] = new Object[]{c.phone(), c.name(), c.id()};
            }
            crud.setRows(rows);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addClient() {
        if (nameField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "الاسم مطلوب");
            return;
        }
        try {
            repository.insert(nameField.getText().trim(), phoneField.getText().trim());
            nameField.setText("");
            phoneField.setText("");
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void updateClient() {
        Integer id = crud.selectedId(2);
        if (id == null) {
            return;
        }
        try {
            repository.update(id, nameField.getText().trim(), phoneField.getText().trim());
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
    }
}
