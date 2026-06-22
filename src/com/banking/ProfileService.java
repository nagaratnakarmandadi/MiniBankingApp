package com.banking;
import java.sql.*;

public class ProfileService {
    static Connection con = DBConnection.getConnection();

    public static void execute(int acNo) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM customer WHERE ac_no = ?");
            ps.setInt(1, acNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("\n--- ACCOUNT PROFILE ---");
                System.out.println("Username   : " + rs.getString("cname"));
                System.out.println("Account No : " + rs.getInt("ac_no"));
                System.out.println("Balance    : " + rs.getInt("balance"));
                System.out.println("-----------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
