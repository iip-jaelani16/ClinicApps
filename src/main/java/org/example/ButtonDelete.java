package org.example;

import org.jdatepicker.DateLabelFormatter;
import org.jdatepicker.UtilDateModel;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

class ButtonDelete extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private int selectedRow;
    private JTable tablePasien;
    Connection conn = null;


    public ButtonDelete(JTable table) {
        super(new JCheckBox());
        this.tablePasien = table;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(UIManager.getColor("Button.background"));
        }

        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        selectedRow = row;
        return button;
    }

    public Object getCellEditorValue() {
        if (isPushed) {
            int no = (int) tablePasien.getValueAt(selectedRow, 0);

            try {
                conn = ConnectionManager.getConnection();
                Statement stmt = conn.createStatement();
                String query = "DELETE FROM pasien WHERE id = " + no;
                int result = stmt.executeUpdate(query);

                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Data pasien berhasil dihapus.");
                } else {
                    JOptionPane.showMessageDialog(null, "Data pasien gagal dihapus.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
        isPushed = false;
        return label;
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
