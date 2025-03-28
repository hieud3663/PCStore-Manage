package com.pcstore.controller;

import com.pcstore.dao.DatabaseConnection;
import com.pcstore.utils.PCrypt;

public class test {
    public static void main(String[] args) {
        
        // DatabaseConnection db = DatabaseConnection.getInstance();
        // System.out.println(db.getConnection());

        String hashpw = PCrypt.hashPassword("123456");
        System.out.println(hashpw);

        System.out.println(PCrypt.checkPassword("123456", hashpw));
    }
    
}
