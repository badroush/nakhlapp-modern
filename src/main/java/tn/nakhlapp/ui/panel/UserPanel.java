package tn.nakhlapp.ui.panel;

import tn.nakhlapp.model.AppUser;
import tn.nakhlapp.repository.UserRepository;
import tn.nakhlapp.ui.component.CrudPanel;
import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserPanel extends JPanel implements Refreshable {

    private final UserRepository repository = new UserRepository();
    private final JTextField pseudoField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final JComboBox<String> roleBox = new JComboBox<>(new String[]{"admin", "USER"});
    private final JTextField companyField = new JTextField("1", 6);
    private final CrudPanel crud;

    public UserPanel() {
        super(new BorderLayout(8, 8));
        crud = new CrudPanel(new String[]{"الشركة", "النوع", "المستخدم"}, this::loadData);

        add(crud.formPanel(
                labeled("المستخدم", pseudoField),
                labeled("كلمة العبور", passwordField),
                labeled("النوع", roleBox),
                labeled("معرف الشركة", companyField)
        ), BorderLayout.NORTH);
        add(crud, BorderLayout.CENTER);

        JButton add = UiTheme.primaryButton("إضافة");
        add.addActionListener(e -> {
            try {
                repository.insert(
                        pseudoField.getText().trim(),
                        new String(passwordField.getPassword()),
                        (String) roleBox.getSelectedItem(),
                        Integer.parseInt(companyField.getText().trim())
                );
                pseudoField.setText("");
                passwordField.setText("");
                loadData();
            } catch (Exception ex) {
                showError(ex);
            }
        });
        JButton delete = new JButton("حذف");
        delete.addActionListener(e -> {
            int row = crud.getTable().getSelectedRow();
            if (row < 0) return;
            int modelRow = crud.getTable().convertRowIndexToModel(row);
            String pseudo = (String) crud.getTable().getModel().getValueAt(modelRow, 2);
            int confirm = JOptionPane.showConfirmDialog(this, "تأكيد الحذف؟");
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    repository.delete(pseudo);
                    loadData();
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        });
        add(crud.actionBar(add, delete), BorderLayout.SOUTH);
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
            List<AppUser> users = repository.findAll();
            Object[][] rows = new Object[users.size()][3];
            for (int i = 0; i < users.size(); i++) {
                AppUser u = users.get(i);
                rows[i] = new Object[]{u.companyId(), u.role(), u.pseudo()};
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
