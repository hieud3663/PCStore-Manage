/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pcstore.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOImpl {
    private Connection conn;

    private String url = "jdbc:sqlserver://localhost:1433;databaseName=ComputerStoreManagement;username=sa;password=123456;trustServerCertificate=true";
    private String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public DAOImpl(){
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url);
            System.out.println("Kết nối thành công");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Kết nối thất bại");
            System.out.println(e.getMessage());
        }
    }
}