package com.pcstore.utils;

import com.pcstore.repository.RepositoryFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp quản lý kết nối đến cơ sở dữ liệu
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    private String url = "jdbc:sqlserver://localhost:1433;databaseName=ComputerStoreManagement;trustServerCertificate=true";
    private String username = "sa";
    private String password = "123456";
    private String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    
    public DatabaseConnection() {
        try {
            Class.forName(driver);
            this.connection = DriverManager.getConnection(url, username, password);
            // System.out.println("Kết nối thành công đến CSDL");
        } catch (ClassNotFoundException | SQLException e) {
            // System.out.println("Lỗi kết nối đến CSDL: " + e.getMessage());
            throw new RuntimeException("Lỗi kết nối đến CSDL: " + e.getMessage());
        }
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else {
            try {
                if (instance.getConnection().isClosed()) {
                    instance = new DatabaseConnection();
                }
            } catch (SQLException e) {
                instance = new DatabaseConnection();
            }
        }
        return instance;
    }
    
    // public static RepositoryFactory getRepositoryFactory() {
    //     return new RepositoryFactory(getInstance().getConnection());
    // }
    
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed successfully");
                instance = null; // Reset instance để lần sau sẽ tạo kết nối mới
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối database: " + e.getMessage());
            }
        }
    }
}