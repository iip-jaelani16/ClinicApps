package org.example;

import javax.swing.*;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            System.out.println("Koneksi ke database berhasil dibuat.");
            // pertanyaan membuat tabel pasien
            Scanner scanner = new Scanner(System.in);
            System.out.print("Apakah anda ingin membuat tabel pasien? [y/n] ");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("y")) {
                createTablePasien(conn);
            }

            // Pertanyaan membuat dummy data
            System.out.print("Apakah anda ingin membuat dummy data pasien? [y/n] ");
            answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("y")) {
                deleteAllPasien(conn);
                createDummyPasien(conn);
            }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    KlinikManagement klinikManagement = new KlinikManagement();
                    klinikManagement.setVisible(true);
                }
            });
        } catch (SQLException e) {
            System.err.println("Terjadi kesalahan saat membuat koneksi: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC tidak ditemukan: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                    System.out.println("Koneksi ke database ditutup.");
                }
            } catch (SQLException e) {
                System.err.println("Terjadi kesalahan saat menutup koneksi: " + e.getMessage());
            }
        }
    }
    private static void createTablePasien(Connection conn) throws SQLException {
        // Periksa apakah tabel pasien sudah ada
        try (ResultSet rs = conn.getMetaData().getTables(null, null, "pasien", null)) {
            if (rs.next()) {
                // Jika tabel sudah ada, beritahu user dan keluar dari method
                System.out.println("Tabel pasien sudah ada.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Terjadi kesalahan saat memeriksa tabel pasien: " + e.getMessage());
            return;
        }

        // Buat statement SQL untuk membuat tabel pasien
        String sql = "CREATE TABLE pasien ("
                + "id SERIAL PRIMARY KEY,"
                + "patient_name VARCHAR(20),"
                + "address CHAR(50),"
                + "nik CHAR(15) UNIQUE,"
                + "date_of_birth DATE"
                + ")";

        // Eksekusi statement SQL untuk membuat tabel pasien
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Tabel pasien berhasil dibuat.");
        } catch (SQLException e) {
            System.err.println("Terjadi kesalahan saat membuat tabel pasien: " + e.getMessage());
        }
    }

    private static void deleteAllPasien(Connection conn) {
        // Buat statement SQL untuk menghapus semua data pasien
        String sql = "DELETE FROM pasien";

        // Eksekusi statement SQL untuk menghapus semua data pasien
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Data pasien berhasil dihapus.");
        } catch (SQLException e) {
            System.err.println("Terjadi kesalahan saat menghapus data pasien: " + e.getMessage());
        }
    }

    private static void createDummyPasien(Connection conn) throws SQLException {
        String[] names = {"Budi", "Susi", "Rina", "Agus", "Dewi"};
        String[] addresses = {"Jl. Raya 1", "Jl. Raya 2", "Jl. Raya 3", "Jl. Raya 4", "Jl. Raya 5"};
        String[] niks = {"123456789012345", "234567890123456", "345678901234567", "456789012345678", "567890123456789"};
        String[] dobs = {"1990-01-01", "1992-02-02", "1995-03-03", "1998-04-04", "2000-05-05"};

        // Hapus data sebelumnya (jika ada)
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM pasien");
        }

        // Insert 5 data dummy
        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO pasien (id, patient_name, address, nik, date_of_birth) VALUES (?, ?, ?, ?, ?)")) {
            for (int i = 0; i < 5; i++) {
                pstmt.setInt(1, i+1);
                pstmt.setString(2, names[i]);
                pstmt.setString(3, addresses[i]);
                pstmt.setString(4, niks[i]);
                pstmt.setDate(5, Date.valueOf(dobs[i]));
                pstmt.executeUpdate();
            }
            System.out.println("Berhasil menambahkan 5 data pasien dummy.");
        } catch (SQLException e) {
            System.err.println("Terjadi kesalahan saat menambahkan data pasien dummy: " + e.getMessage());
        }
    }


}
