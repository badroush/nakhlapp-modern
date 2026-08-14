package tn.nakhlapp.ui.panel;

import tn.nakhlapp.model.Societe;
import tn.nakhlapp.repository.SocieteRepository;
import tn.nakhlapp.ui.component.CrudPanel;
import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SocietePanel extends JPanel implements Refreshable {

    private final SocieteRepository repository = new SocieteRepository();
    private final JTextField nameAr = new JTextField(18);
    private final JTextField addressAr = new JTextField(18);
    private final JTextField phone = new JTextField(12);
    private final JTextField nameFr = new JTextField(18);
    private final JTextField addressFr = new JTextField(18);
    private final JTextField taxId = new JTextField(12);
    private final JTextField gsm = new JTextField(12);
    private final JTextField email = new JTextField(18);
    private final CrudPanel crud;

    public SocietePanel() {
        super(new BorderLayout(8, 8));
        crud = new CrudPanel(new String[]{"البريد", "GSM", "الاسم", "المعرف"}, this::loadData);
        crud.setOnDelete(id -> {
            try {
                repository.delete(id);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("بيانات الشركة"));
        form.add(labeled("الاسم (ع)", nameAr));
        form.add(labeled("العنوان (ع)", addressAr));
        form.add(labeled("الهاتف", phone));
        form.add(labeled("الاسم (Fr)", nameFr));
        form.add(labeled("العنوان (Fr)", addressFr));
        form.add(labeled("Matricule", taxId));
        form.add(labeled("GSM", gsm));
        form.add(labeled("Email", email));

        add(form, BorderLayout.NORTH);
        add(crud, BorderLayout.CENTER);

        JButton add = UiTheme.primaryButton("إضافة");
        add.addActionListener(e -> saveNew());
        JButton update = new JButton("تحيين");
        update.addActionListener(e -> updateSelected());
        JButton delete = new JButton("حذف");
        delete.addActionListener(e -> crud.deleteSelected(3));
        add(crud.actionBar(add, update, delete), BorderLayout.SOUTH);
    }

    @Override
    public void refreshData() {
        loadData();
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.add(new JLabel(label), BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private Societe currentForm(int id) {
        return new Societe(
                id,
                nameAr.getText(),
                addressAr.getText(),
                phone.getText(),
                nameFr.getText(),
                addressFr.getText(),
                taxId.getText(),
                gsm.getText(),
                email.getText()
        );
    }

    private void saveNew() {
        try {
            repository.insert(currentForm(0), "");
            clear();
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void updateSelected() {
        Integer id = crud.selectedId(3);
        if (id == null) return;
        try {
            repository.update(id, currentForm(id), "");
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadData() {
        try {
            List<Societe> list = repository.findAll();
            Object[][] rows = new Object[list.size()][4];
            for (int i = 0; i < list.size(); i++) {
                Societe s = list.get(i);
                rows[i] = new Object[]{s.email(), s.gsm(), s.nameAr(), s.id()};
            }
            crud.setRows(rows);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void clear() {
        nameAr.setText("");
        addressAr.setText("");
        phone.setText("");
        nameFr.setText("");
        addressFr.setText("");
        taxId.setText("");
        gsm.setText("");
        email.setText("");
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
    }
}
