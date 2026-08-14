package tn.nakhlapp.ui.panel;

import tn.nakhlapp.model.Cage;
import tn.nakhlapp.model.CageMovement;
import tn.nakhlapp.model.Client;
import tn.nakhlapp.repository.CageMovementRepository;
import tn.nakhlapp.repository.CageRepository;
import tn.nakhlapp.repository.ClientRepository;
import tn.nakhlapp.ui.component.CrudPanel;
import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CageMovementPanel extends JPanel implements Refreshable {

    private final CageMovementRepository repository = new CageMovementRepository();
    private final ClientRepository clientRepository = new ClientRepository();
    private final CageRepository cageRepository = new CageRepository();

    private final JComboBox<CageMovement.MovementType> typeBox = new JComboBox<>(CageMovement.MovementType.values());
    private final JComboBox<String> clientBox = new JComboBox<>();
    private final JComboBox<String> cageBox = new JComboBox<>();
    private final JTextField quantityField = new JTextField(10);
    private final CrudPanel crud;
    private final Map<String, Integer> clientIds = new HashMap<>();
    private final Map<String, Integer> cageIds = new HashMap<>();

    public CageMovementPanel() {
        super(new BorderLayout(8, 8));
        crud = new CrudPanel(new String[]{"الكمية", "القفص", "الفلاح", "التاريخ", "المعرف"}, this::loadData);

        typeBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CageMovement.MovementType type) {
                    setText(switch (type) {
                        case RETURN -> "إرجاع أقفاص";
                        case OUT -> "خروج أقفاص";
                        case STOCK -> "مخزون أقفاص";
                    });
                }
                return c;
            }
        });

        add(crud.formPanel(
                labeled("نوع الحركة", typeBox),
                labeled("الفلاح", clientBox),
                labeled("القفص", cageBox),
                labeled("العدد", quantityField)
        ), BorderLayout.NORTH);
        add(crud, BorderLayout.CENTER);

        typeBox.addActionListener(e -> {
            updateClientEnabled();
            loadData();
        });
        JButton add = UiTheme.primaryButton("تسجيل");
        add.addActionListener(e -> saveMovement());
        add(crud.actionBar(add), BorderLayout.SOUTH);
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

    private void updateClientEnabled() {
        CageMovement.MovementType type = (CageMovement.MovementType) typeBox.getSelectedItem();
        clientBox.setEnabled(type != CageMovement.MovementType.STOCK);
    }

    private void reloadCombos() {
        try {
            clientBox.removeAllItems();
            cageBox.removeAllItems();
            clientIds.clear();
            cageIds.clear();
            for (Client c : clientRepository.findAll()) {
                clientBox.addItem(c.name());
                clientIds.put(c.name(), c.id());
            }
            for (Cage c : cageRepository.findAll()) {
                cageBox.addItem(c.name());
                cageIds.put(c.name(), c.id());
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void saveMovement() {
        CageMovement.MovementType type = (CageMovement.MovementType) typeBox.getSelectedItem();
        String cageName = (String) cageBox.getSelectedItem();
        if (type == null || cageName == null) {
            return;
        }
        try {
            Integer clientId = null;
            if (type != CageMovement.MovementType.STOCK) {
                String clientName = (String) clientBox.getSelectedItem();
                if (clientName == null) {
                    return;
                }
                clientId = clientIds.get(clientName);
            }
            CageMovement movement = new CageMovement(
                    0,
                    LocalDate.now(),
                    LocalTime.now().withNano(0),
                    clientId,
                    cageIds.get(cageName),
                    quantityField.getText(),
                    type
            );
            repository.insert(movement);
            quantityField.setText("");
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadData() {
        try {
            CageMovement.MovementType type = (CageMovement.MovementType) typeBox.getSelectedItem();
            if (type == null) {
                return;
            }
            List<CageMovement> list = repository.findByType(type, null);
            Map<Integer, String> clients = new HashMap<>();
            Map<Integer, String> cages = new HashMap<>();
            clientRepository.findAll().forEach(c -> clients.put(c.id(), c.name()));
            cageRepository.findAll().forEach(c -> cages.put(c.id(), c.name()));

            Object[][] rows = new Object[list.size()][5];
            for (int i = 0; i < list.size(); i++) {
                CageMovement m = list.get(i);
                rows[i] = new Object[]{
                        m.quantity(),
                        cages.getOrDefault(m.cageId(), String.valueOf(m.cageId())),
                        m.clientId() == null ? "-" : clients.getOrDefault(m.clientId(), String.valueOf(m.clientId())),
                        m.date(),
                        m.id()
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
