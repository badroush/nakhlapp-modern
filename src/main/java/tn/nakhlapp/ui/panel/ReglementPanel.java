package tn.nakhlapp.ui.panel;

import tn.nakhlapp.model.Client;
import tn.nakhlapp.model.Reglement;
import tn.nakhlapp.repository.ClientRepository;
import tn.nakhlapp.repository.ReglementRepository;
import tn.nakhlapp.ui.component.CrudPanel;
import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReglementPanel extends JPanel implements Refreshable {

    private final ReglementRepository repository = new ReglementRepository();
    private final ClientRepository clientRepository = new ClientRepository();
    private final JComboBox<String> clientBox = new JComboBox<>();
    private final JTextField amountField = new JTextField(12);
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Avance", "Solde", "Total"});
    private final JComboBox<String> methodBox = new JComboBox<>(new String[]{"Espèce", "Chèque", "Virement"});
    private final JTextField referenceField = new JTextField(16);
    private final JTextField searchField = new JTextField(16);
    private final CrudPanel crud;
    private final Map<String, Integer> clientIds = new HashMap<>();

    public ReglementPanel() {
        super(new BorderLayout(8, 8));
        crud = new CrudPanel(new String[]{"المرجع", "الطريقة", "النوع", "المبلغ", "الفلاح", "التاريخ", "المعرف"}, this::loadData);
        crud.setOnDelete(id -> {
            try {
                repository.delete(id);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        JPanel form = crud.formPanel(
                labeled("الفلاح", clientBox),
                labeled("المبلغ", amountField),
                labeled("النوع", typeBox),
                labeled("الطريقة", methodBox),
                labeled("المرجع / الشيك", referenceField),
                labeled("بحث", searchField)
        );

        searchField.addActionListener(e -> search());

        add(form, BorderLayout.NORTH);
        add(crud, BorderLayout.CENTER);

        JButton add = UiTheme.primaryButton("إضافة");
        add.addActionListener(e -> addPayment());
        JButton delete = new JButton("حذف");
        delete.addActionListener(e -> crud.deleteSelected(6));
        JButton searchBtn = new JButton("بحث");
        searchBtn.addActionListener(e -> search());
        add(crud.actionBar(add, delete, searchBtn), BorderLayout.SOUTH);
    }

    @Override
    public void refreshData() {
        reloadClients();
        loadData();
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.add(new JLabel(label, SwingConstants.RIGHT), BorderLayout.EAST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private void reloadClients() {
        try {
            clientBox.removeAllItems();
            clientIds.clear();
            for (Client c : clientRepository.findAll()) {
                clientBox.addItem(c.name());
                clientIds.put(c.name(), c.id());
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addPayment() {
        String client = (String) clientBox.getSelectedItem();
        if (client == null) {
            return;
        }
        try {
            Reglement reglement = new Reglement(
                    0,
                    LocalDate.now(),
                    LocalTime.now().withNano(0),
                    clientIds.get(client),
                    amountField.getText(),
                    (String) typeBox.getSelectedItem(),
                    (String) methodBox.getSelectedItem(),
                    referenceField.getText()
            );
            repository.insert(reglement);
            amountField.setText("");
            referenceField.setText("");
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void search() {
        try {
            List<Reglement> list = repository.searchByClientOrReference(searchField.getText().trim());
            fillTable(list);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadData() {
        try {
            fillTable(repository.findAll());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void fillTable(List<Reglement> list) throws Exception {
        Map<Integer, String> clients = new HashMap<>();
        clientRepository.findAll().forEach(c -> clients.put(c.id(), c.name()));
        Object[][] rows = new Object[list.size()][7];
        for (int i = 0; i < list.size(); i++) {
            Reglement r = list.get(i);
            rows[i] = new Object[]{
                    r.reference(),
                    r.method(),
                    r.type(),
                    r.amount(),
                    clients.getOrDefault(r.clientId(), String.valueOf(r.clientId())),
                    r.date(),
                    r.id()
            };
        }
        crud.setRows(rows);
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
    }
}
