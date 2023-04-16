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

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private int selectedRow;
    private JTable tablePasien;
    Connection conn = null;


    public ButtonEditor(JTable table) {
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
            // ambil data dari DB mengunakan id dari no
            try {
                conn = ConnectionManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM pasien WHERE id = " + no);
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nama = rs.getString("patient_name");
                    String nik = rs.getString("nik");
                    String tanggalLahir = rs.getString("date_of_birth");
                    String alamat = rs.getString("address");

                    // create the edit form and pass the patient data to it
                    JFrame editFrame = new JFrame("Edit Patient Data");
                    editFrame.setLayout(new GridLayout(6, 2, 5, 5));
                    editFrame.setSize(400, 200);



                    JLabel namaLabel = new JLabel("Nama ");
                    JTextField namaField = new JTextField(nama);
                    editFrame.add(namaLabel);
                    editFrame.add(namaField);

                    JLabel nikLabel = new JLabel("NIK ");
                    JTextField nikField = new JTextField(nik);
                    editFrame.add(nikLabel);
                    editFrame.add(nikField);

                    JLabel lblTgl = new JLabel("Tanggal Lahir:");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date defaultDate = format.parse(tanggalLahir);
                    UtilDateModel model = new UtilDateModel(defaultDate);
                    Properties properties = new Properties();
                    properties.put("text.today", "Hari Ini");
                    properties.put("text.month", "Bulan");
                    properties.put("text.year", "Tahun");
                    JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
                    JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
                    editFrame.add(lblTgl);
                    editFrame.add(datePicker);

                    JLabel alamatLabel = new JLabel("Alamat ");
                    JTextField alamatField = new JTextField(alamat);
                    editFrame.add(alamatLabel);
                    editFrame.add(alamatField);

                    JButton simpanButton = new JButton("Simpan");
                    simpanButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // save the updated patient data
                           try {
                               Date tanggalLahir = (Date) datePicker.getModel().getValue();
                               // tambahakna function update
                               String sql = "UPDATE pasien SET patient_name=?, nik=?, date_of_birth=?, address=? WHERE id=?";

                               PreparedStatement ps = conn.prepareStatement(sql);
                               ps.setString(1, namaField.getText());
                               ps.setString(2, nikField.getText());
                               ps.setObject(3, new java.sql.Date(tanggalLahir.getTime()));
                               ps.setString(4, alamatField.getText());
                               ps.setInt(5, no);
                               ps.executeUpdate();

                               // refresh data di dalam tabel
                               DefaultTableModel model = (DefaultTableModel) tablePasien.getModel();
                               KlinikManagement c = new KlinikManagement();
                               model.setRowCount(0);
                               c.loadData();
                               model.fireTableDataChanged();
                               editFrame.dispose();
                           } catch (SQLException ex) {
                               throw new RuntimeException(ex);
                           }
                        }
                    });
                    editFrame.add(new JLabel());
                    editFrame.add(simpanButton);

                    editFrame.setVisible(true);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
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
