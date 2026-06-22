package com.banking;


import java.sql.*;

public class DepositService {
    static Connection con = DBConnection.getConnection();

    public static void execute(int acNo, int amount) {
        try {
            con.setAutoCommit(false);
            
            PreparedStatement ps = con.prepareStatement("UPDATE customer SET balance = balance + ? WHERE ac_no = ?");
            ps.setInt(1, amount);
            ps.setInt(2, acNo);
            ps.executeUpdate();

            PreparedStatement psReceipt = con.prepareStatement("INSERT INTO transactions (sender_ac, receiver_name, amount) VALUES (?, 'Self (Deposit)', ?)");
            psReceipt.setInt(1, acNo);
            psReceipt.setInt(2, amount);
            psReceipt.executeUpdate();

            con.commit();
            System.out.println("Deposit of " + amount + " was successful!");
        } catch (Exception e) {
            try { con.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
        }
    }
}