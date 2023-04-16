package org.example;

import java.sql.*;

public class ConnectionManager {
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        String url = "jdbc:postgresql://localhost:5433/postgres"; // ganti "mydatabase" dengan nama database Anda
        String username = "postgres"; // ganti "myusername" dengan nama pengguna Anda
        String password = "password"; // ganti "mypassword" dengan kata sandi Anda

        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
