package com.banking;
import java.sql.*;

public class BalanceService {
    static Connection con = DBConnection.getConnection();

    public static void execute(int acNo) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT balance FROM customer WHERE ac_no = ?");
            ps.setInt(1, acNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("\n>>> Current Balance: " + rs.getInt("balance") + ".00");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}