package org.example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.jdatepicker.DateLabelFormatter;
import org.jdatepicker.UtilDateModel;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class KlinikManagement extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable tablePasien;
    private DefaultTableModel model;


    public KlinikManagement() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("Klinik Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton btnTambahPasien = new JButton("Tambah Pasien");
        btnTambahPasien.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // create a new frame for adding a new patient
                JFrame frame = new JFrame("Tambah Pasien");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setLayout(new BorderLayout());

                // create a panel for the input form
                JPanel formPanel = new JPanel(new GridLayout(6, 2));
                JLabel lblNama = new JLabel("Nama:");
                JTextField txtNama = new JTextField();
                JLabel lblAlamat = new JLabel("Alamat:");
                JTextField txtAlamat = new JTextField();
                JLabel lblNik = new JLabel("NIK:");
                JTextField txtNik = new JTextField();
                JLabel lblTanggalLahir = new JLabel("Tanggal Lahir:");
                UtilDateModel model = new UtilDateModel();
                Properties properties = new Properties();
                properties.put("text.today", "Hari Ini");
                properties.put("text.month", "Bulan");
                properties.put("text.year", "Tahun");
                JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
                JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

                formPanel.add(lblNama);
                formPanel.add(txtNama);
                formPanel.add(lblAlamat);
                formPanel.add(txtAlamat);
                formPanel.add(lblNik);
                formPanel.add(txtNik);
                formPanel.add(lblTanggalLahir);
                formPanel.add(datePicker);



                // create a panel for buttons
                JPanel buttonPanel = new JPanel(new FlowLayout());
                JButton btnSave = new JButton("Simpan");
                btnSave.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // get the input values from the form
                        String nama = txtNama.getText();
                        String alamat = txtAlamat.getText();
                        long nik = Long.parseLong(txtNik.getText());
                        Date tanggalLahir = (Date) datePicker.getModel().getValue();

                        // insert the new patient data into the database
                        Connection conn = null;
                        try {
                            conn = ConnectionManager.getConnection();
                            System.out.println("Koneksi ke database berhasil dibuat.");

                            PreparedStatement checkNikPs = conn.prepareStatement("SELECT COUNT(*) FROM pasien WHERE nik = ?");
                            checkNikPs.setString(1, String.valueOf(nik));
                            ResultSet checkNikRs = checkNikPs.executeQuery();
                            checkNikRs.next();
                            int existingNikCount = checkNikRs.getInt(1);

                            if (existingNikCount > 0) {
                                JOptionPane.showMessageDialog(frame, "NIK ini sudah terdaftar dalam database.", "NIK sudah ada", JOptionPane.WARNING_MESSAGE);
                                return;
                            }

                            // Get the total number of records in the pasien table
                            PreparedStatement countPs = conn.prepareStatement("SELECT COUNT(*) FROM pasien");
                            ResultSet rs = countPs.executeQuery();
                            rs.next();
                            int totalRecords = rs.getInt(1);
                            // Insert a new record with the next available ID
                            PreparedStatement ps = conn.prepareStatement("INSERT INTO pasien (id, patient_name, address, nik, date_of_birth) VALUES (?, ?, ?, ?, ?)");
                            ps.setInt(1, totalRecords + 1);
                            ps.setString(2, nama);
                            ps.setString(3, alamat);
                            ps.setString(4, String.valueOf(nik));
                            ps.setObject(5, new java.sql.Date(tanggalLahir.getTime()));

                            int result = ps.executeUpdate();
                            if(result > 0) {
                                JOptionPane.showMessageDialog(frame, "Data pasien berhasil disimpan.");
                                frame.dispose(); // close the add patient frame
                                loadData(); // refresh the table data
                            }



                            // close the add patient frame
                            frame.dispose();
                        } catch (SQLException ex) {
                            System.err.println("Terjadi kesalahan saat mengambil data: " + ex.getMessage());
                        } catch (ClassNotFoundException ex) {
                            System.err.println("Driver JDBC tidak ditemukan: " + ex.getMessage());
                        } finally {
                            DbUtils.closeQuietly(conn);
                        }
                    }
                });
                JButton btnCancel = new JButton("Batal");
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                    }
                });
                buttonPanel.add(btnSave);
                buttonPanel.add(btnCancel);

                // add the input form and buttons to the frame
                frame.add(formPanel, BorderLayout.CENTER);
                frame.add(buttonPanel, BorderLayout.SOUTH);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        add(btnTambahPasien, BorderLayout.NORTH);

        tablePasien = new JTable();
        model = new DefaultTableModel(new Object[][] {}, new String[] { "No", "Nama Pasien", "NIK", "Tanggal Lahir", "Alamat", "Edit", "Delete" });
        tablePasien.setModel(model);
        // add the button renderer and editor to the "Aksi" column
        TableColumnModel columnModel = tablePasien.getColumnModel();
        ButtonRenderer buttonRenderer = new ButtonRenderer();
        ButtonEditor buttonEditor = new ButtonEditor(tablePasien);
        ButtonDelete buttonDelete = new ButtonDelete(tablePasien);
        columnModel.getColumn(5).setCellRenderer(buttonRenderer);
        columnModel.getColumn(5).setCellEditor(buttonEditor);
        columnModel.getColumn(6).setCellRenderer(buttonRenderer);
        columnModel.getColumn(6).setCellEditor(buttonDelete);

        JScrollPane scrollPane = new JScrollPane(tablePasien);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnTutup = new JButton("Tutup Aplikasi");
        btnTutup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(btnTutup, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

  public  void loadData() {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            System.out.println("Koneksi ke database berhasil dibuat.");

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM pasien");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = { rs.getInt("id"), rs.getString("patient_name"), rs.getString("address"),
                        rs.getLong("nik"), rs.getDate("date_of_birth") , "Edit", "Delete"};
                model.addRow(row);
            }
        } catch (SQLException e) {
            System.err.println("Terjadi kesalahan saat mengambil data: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC tidak ditemukan: " + e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }

    }


}
