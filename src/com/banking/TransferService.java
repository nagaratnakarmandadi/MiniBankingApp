package com.banking;


import java.sql.*;

public class TransferService {
    static Connection con = DBConnection.getConnection();

    public static boolean execute(int sender_ac, String receiver_name, int amount) {
        try {
            con.setAutoCommit(false);
            
            // Check Sender Balance
            PreparedStatement ps = con.prepareStatement("SELECT balance FROM customer WHERE ac_no = ?");
            ps.setInt(1, sender_ac);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt("balance") < amount) {
                System.out.println("Insufficient Balance!");
                return false;
            }

            // Debit Sender
            PreparedStatement psDebit = con.prepareStatement("UPDATE customer SET balance = balance - ? WHERE ac_no = ?");
            psDebit.setInt(1, amount);
            psDebit.setInt(2, sender_ac);
            psDebit.executeUpdate();

            // Credit Receiver
            PreparedStatement psCredit = con.prepareStatement("UPDATE customer SET balance = balance + ? WHERE cname = ?");
            psCredit.setInt(1, amount);
            psCredit.setString(2, receiver_name);
            if (psCredit.executeUpdate() == 0) {
                System.out.println("Receiver username not found!");
                con.rollback();
                return false;
            }

            // Save Receipt
            PreparedStatement psReceipt = con.prepareStatement("INSERT INTO transactions (sender_ac, receiver_name, amount) VALUES (?, ?, ?)");
            psReceipt.setInt(1, sender_ac);
            psReceipt.setString(2, receiver_name);
            psReceipt.setInt(3, amount);
            psReceipt.executeUpdate();

            con.commit();
            System.out.println("Transaction successful!");
            return true;
        } catch (Exception e) {
            try { con.rollback(); } catch (SQLException ex) {}
            System.out.println("Transaction failed! Please try again.");
            return false;
        }
    }
}