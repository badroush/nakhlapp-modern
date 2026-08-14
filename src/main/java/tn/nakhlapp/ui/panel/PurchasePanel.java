package tn.nakhlapp.ui.panel;

import tn.nakhlapp.model.Cage;
import tn.nakhlapp.model.Client;
import tn.nakhlapp.model.Operation;
import tn.nakhlapp.model.Produit;
import tn.nakhlapp.repository.CageRepository;
import tn.nakhlapp.repository.ClientRepository;
import tn.nakhlapp.repository.ProduitRepository;
import tn.nakhlapp.service.PurchaseService;
import tn.nakhlapp.ui.theme.UiTheme;
import tn.nakhlapp.util.NumberFormatUtil;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchasePanel extends JPanel implements Refreshable {

    private final PurchaseService purchaseService = new PurchaseService();
    private final ClientRepository clientRepository = new ClientRepository();
    private final ProduitRepository produitRepository = new ProduitRepository();
    private final CageRepository cageRepository = new CageRepository();

    private final JComboBox<String> clientBox = new JComboBox<>();
    private final JComboBox<String> productBox = new JComboBox<>();
    private final JComboBox<String> cageBox = new JComboBox<>();
    private final JTextField cageCountField = new JTextField("0", 8);
    private final JTextField grossWeightField = new JTextField("0", 8);
    private final JTextField unitPriceField = new JTextField("0.000", 10);
    private final JLabel netWeightLabel = new JLabel("0.000");
    private final JLabel totalLabel = new JLabel("0.000");
    private final JTable historyTable;

    private final Map<String, Integer> clientIds = new HashMap<>();
    private final Map<String, Integer> productIds = new HashMap<>();
    private final Map<String, Integer> cageIds = new HashMap<>();

    public PurchasePanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel form = buildForm();
        historyTable = new JTable();
        UiTheme.styleTable(historyTable);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(historyTable), BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        reloadCombos();
        reloadHistory();
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("عملية شراء"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(panel, gbc, row++, "الفلاح", clientBox);
        addRow(panel, gbc, row++, "المنتج", productBox);
        addRow(panel, gbc, row++, "القفص", cageBox);
        addRow(panel, gbc, row++, "عدد الأقفاص", cageCountField);
        addRow(panel, gbc, row++, "الوزن brut", grossWeightField);
        addRow(panel, gbc, row++, "سعر الوحدة", unitPriceField);
        addRow(panel, gbc, row++, "الوزن net", netWeightLabel);
        addRow(panel, gbc, row++, "المجموع", totalLabel);

        productBox.addActionListener(e -> updatePriceFromSelection());
        cageBox.addActionListener(e -> updatePriceFromSelection());
        grossWeightField.addActionListener(e -> recalculate());
        cageCountField.addActionListener(e -> recalculate());

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JButton save = UiTheme.primaryButton("حفظ العملية");
        save.addActionListener(e -> savePurchase());
        panel.add(save, gbc);

        return panel;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label, SwingConstants.RIGHT), gbc);
        gbc.gridx = 0;
        panel.add(field, gbc);
    }

    private void reloadCombos() {
        try {
            clientBox.removeAllItems();
            productBox.removeAllItems();
            cageBox.removeAllItems();
            clientIds.clear();
            productIds.clear();
            cageIds.clear();

            for (Client c : clientRepository.findAll()) {
                clientBox.addItem(c.name());
                clientIds.put(c.name(), c.id());
            }
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

    private void updatePriceFromSelection() {
        try {
            String product = (String) productBox.getSelectedItem();
            String cage = (String) cageBox.getSelectedItem();
            if (product == null || cage == null) {
                return;
            }
            purchaseService.resolveUnitPrice(productIds.get(product), cageIds.get(cage))
                    .ifPresent(price -> unitPriceField.setText(price));
            recalculate();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void recalculate() {
        try {
            String cage = (String) cageBox.getSelectedItem();
            if (cage == null) {
                return;
            }
            PurchaseService.PurchaseCalculation calc = purchaseService.calculate(
                    NumberFormatUtil.parseDouble(grossWeightField.getText(), 0),
                    NumberFormatUtil.parseDouble(cageCountField.getText(), 0),
                    cageIds.get(cage),
                    unitPriceField.getText()
            );
            netWeightLabel.setText(NumberFormatUtil.format3(calc.netWeight()));
            totalLabel.setText(NumberFormatUtil.format3(calc.totalAmount()));
            unitPriceField.setText(calc.unitPrice());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void savePurchase() {
        try {
            String client = (String) clientBox.getSelectedItem();
            String product = (String) productBox.getSelectedItem();
            String cage = (String) cageBox.getSelectedItem();
            if (client == null || product == null || cage == null) {
                return;
            }
            PurchaseService.PurchaseCalculation calc = purchaseService.calculate(
                    NumberFormatUtil.parseDouble(grossWeightField.getText(), 0),
                    NumberFormatUtil.parseDouble(cageCountField.getText(), 0),
                    cageIds.get(cage),
                    unitPriceField.getText()
            );
            purchaseService.savePurchase(
                    clientIds.get(client),
                    productIds.get(product),
                    cageIds.get(cage),
                    calc.grossWeight(),
                    NumberFormatUtil.parseDouble(cageCountField.getText(), 0),
                    calc.unitPrice(),
                    calc.coefficient()
            );
            JOptionPane.showMessageDialog(this, "تم حفظ العملية");
            reloadHistory();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void reloadHistory() {
        try {
            List<Operation> operations = purchaseService.recentOperations(100);
            String[] columns = {"المعرف", "التاريخ", "الفلاح", "المنتج", "القفص", "brut", "أقفاص", "PU", "coef"};
            Object[][] rows = new Object[operations.size()][columns.length];
            Map<Integer, String> clients = new HashMap<>();
            Map<Integer, String> products = new HashMap<>();
            Map<Integer, String> cages = new HashMap<>();
            clientRepository.findAll().forEach(c -> clients.put(c.id(), c.name()));
            produitRepository.findAll().forEach(p -> products.put(p.id(), p.name()));
            cageRepository.findAll().forEach(c -> cages.put(c.id(), c.name()));

            for (int i = 0; i < operations.size(); i++) {
                Operation op = operations.get(i);
                rows[i] = new Object[]{
                        op.id(),
                        op.date(),
                        clients.getOrDefault(op.clientId(), String.valueOf(op.clientId())),
                        products.getOrDefault(op.productId(), String.valueOf(op.productId())),
                        cages.getOrDefault(op.cageId(), String.valueOf(op.cageId())),
                        op.grossWeight(),
                        op.cageCount(),
                        op.unitPrice(),
                        op.coefficient()
                };
            }
            historyTable.setModel(new javax.swing.table.DefaultTableModel(rows, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
    }
}
