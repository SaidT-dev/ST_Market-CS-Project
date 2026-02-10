package com.db;
import java.sql.*;

public class DBConnectionManager {

    private static final String URL_DB = "jdbc:mysql://localhost:3306/supermarket_db";
    private static final String USERNAME_DB = "root";
    private static final String PASSWORD_DB = "admin";

    private static DBConnectionManager instance;

    private DBConnectionManager() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur: Le pilote JDBC MySQL est manquant.");
            e.printStackTrace();
            throw  new RuntimeException("Pilote JDBC non trouve");
        }
    }

    public static DBConnectionManager getInstance() {
        if (instance == null) {
            instance = new DBConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL_DB, USERNAME_DB, PASSWORD_DB);
    }

    public static void close(java.sql.ResultSet rs, java.sql.Statement st) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
        }
    }
}
