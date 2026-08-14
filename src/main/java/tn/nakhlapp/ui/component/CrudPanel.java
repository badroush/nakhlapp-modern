package tn.nakhlapp.ui.component;

import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.function.Consumer;

public class CrudPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final Runnable onRefresh;
    private Consumer<Integer> onDelete;

    public CrudPanel(String[] columns, Runnable onRefresh) {
        super(new BorderLayout(12, 12));
        this.onRefresh = onRefresh;
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(tableModel);
        UiTheme.styleTable(table);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, center);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    public void setRows(Object[][] rows) {
        tableModel.setRowCount(0);
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    public Integer selectedId(int idColumn) {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (Integer) tableModel.getValueAt(table.convertRowIndexToModel(row), idColumn);
    }

    public JTable getTable() {
        return table;
    }

    public JPanel formPanel(JComponent... fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("المعطيات"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(fields[i], gbc);
        }
        return panel;
    }

    public JPanel actionBar(JButton... buttons) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        for (JButton button : buttons) {
            bar.add(button);
        }
        return bar;
    }

    public void refresh() {
        if (onRefresh != null) {
            onRefresh.run();
        }
    }

    public void setOnDelete(Consumer<Integer> onDelete) {
        this.onDelete = onDelete;
    }

    public void deleteSelected(int idColumn) {
        Integer id = selectedId(idColumn);
        if (id == null) {
            JOptionPane.showMessageDialog(this, "يرجى اختيار سجل", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "تأكيد الحذف؟", "حذف", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && onDelete != null) {
            onDelete.accept(id);
            refresh();
        }
    }
}
