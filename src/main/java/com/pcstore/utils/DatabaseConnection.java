package com.pcstore.utils;

import com.pcstore.repository.RepositoryFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
    
    private DatabaseConnection() {
        try {
            connection = createConnection();
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo DatabaseConnection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    public Connection getConnection() {
        try {
            // Kiểm tra và tạo mới kết nối nếu cần
            if (connection == null || connection.isClosed()) {
                System.out.println("DatabaseConnection: Kết nối null hoặc đã đóng, tạo mới...");
                connection = createConnection();
            } else if (!connection.isValid(2)) {
                System.out.println("DatabaseConnection: Kết nối không hợp lệ, tạo mới...");
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                connection = createConnection();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra kết nối: " + e.getMessage());
            try {
                connection = createConnection();
            } catch (Exception ex) {
                System.err.println("Không thể tạo lại kết nối: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        
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

    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("DatabaseConnection: Đã đóng kết nối database");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
            e.printStackTrace();
        } finally {
            connection = null;
            instance = null;  // Reset instance để lần sau tạo mới
        }
    }
        /**
     * Tạo kết nối mới đến database
     * @return Kết nối database mới
     * @throws SQLException nếu không thể tạo kết nối
     */
    public Connection createConnection() {
        try {
            // Load driver
            Class.forName(driver);
            
            Connection newConnection = DriverManager.getConnection(url, username, password);
            
            // Kiểm tra kết nối
            try (Statement stmt = newConnection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1")) {
                if (rs.next()) {
                    System.out.println("DatabaseConnection: Tạo kết nối database thành công");
                }
            }
            
            return newConnection;
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi không tìm thấy JDBC driver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không tìm thấy JDBC driver", e);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kết nối database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể kết nối đến database", e);
        }
    }
}