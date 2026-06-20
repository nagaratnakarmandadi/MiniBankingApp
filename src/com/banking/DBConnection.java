package com.banking;

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;

public class DBConnection { 
    
    static Connection con; 

    public static Connection getConnection() {
        try {
            String mysqlJDBCDriver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/BANK";
            String user = "root";
            String pass = "root";

            Class.forName(mysqlJDBCDriver);
            con = DriverManager.getConnection(url, user, pass);
            
        } catch (Exception e) {
            System.out.println("Connection Failed! " + e.getMessage());
        }
        return con;
    }
}