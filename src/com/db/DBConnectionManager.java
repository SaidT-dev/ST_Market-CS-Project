package com.db;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBConnectionManager {

    private static final Properties CONFIG = loadConfig();

    private static final String URL_DB = CONFIG.getProperty("db.url", "jdbc:mysql://localhost:3306/supermarket_db");
    private static final String USERNAME_DB = CONFIG.getProperty("db.username", "root");
    private static final String PASSWORD_DB = CONFIG.getProperty("db.password", "admin");

    private static DBConnectionManager instance;

    private Connection sharedConnection;

    private DBConnectionManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur: Le pilote JDBC MySQL est manquant.");
            e.printStackTrace();
            throw new RuntimeException("Pilote JDBC non trouve");
        }
    }

    public static DBConnectionManager getInstance() {
        if (instance == null) {
            instance = new DBConnectionManager();
        }
        return instance;
    }

    // Connexion distincte, à fermer par l'appelant (transactions).
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL_DB, USERNAME_DB, PASSWORD_DB);
    }

    // Connexion unique partagée par tous les DAO : une seule connexion ouverte pour toute l'application.
    public synchronized Connection getSharedConnection() {
        try {
            if (sharedConnection == null || sharedConnection.isClosed() || !sharedConnection.isValid(2)) {
                sharedConnection = DriverManager.getConnection(URL_DB, USERNAME_DB, PASSWORD_DB);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur DAO : Impossible d'obtenir la connexion à la base de données.", e);
        }
        return sharedConnection;
    }

    private static Properties loadConfig() {
        Properties properties = new Properties();
        try (InputStream in = DBConnectionManager.class.getClassLoader().getResourceAsStream("db_config.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            System.err.println("Lecture de db_config.properties impossible : " + e.getMessage());
        }
        return properties;
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
