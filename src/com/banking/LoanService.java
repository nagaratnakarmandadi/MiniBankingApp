package com.banking;

import java.sql.*;

public class LoanService {
    static Connection con = DBConnection.getConnection();

    public static void execute(int acNo) {
        try {
            con.setAutoCommit(false);

            // Check if user has "good credit" (balance >= 1000)
            PreparedStatement psCheck = con.prepareStatement("SELECT balance FROM customer WHERE ac_no = ?");
            psCheck.setInt(1, acNo);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                if (rs.getInt("balance") < 1000) {
                    System.out.println("❌ Loan Denied: You must have a minimum balance of 1000 to qualify for a loan.");
                    return;
                }
            }

            int loanAmount = 5000; // Fixed loan amount

            // Add loan to balance
            PreparedStatement psUpdate = con.prepareStatement("UPDATE customer SET balance = balance + ? WHERE ac_no = ?");
            psUpdate.setInt(1, loanAmount);
            psUpdate.setInt(2, acNo);
            psUpdate.executeUpdate();

            // Save receipt to transaction history
            PreparedStatement psReceipt = con.prepareStatement("INSERT INTO transactions (sender_ac, receiver_name, amount) VALUES (?, 'BANK LOAN APPROVED', ?)");
            psReceipt.setInt(1, acNo);
            psReceipt.setInt(2, loanAmount);
            psReceipt.executeUpdate();

            con.commit();
            System.out.println("✅ Congratulations! A loan of " + loanAmount + " has been credited to your account.");

        } catch (Exception e) {
            try { con.rollback(); } catch (SQLException ex) {}
            System.out.println("Loan processing failed.");
            e.printStackTrace();
        }
    }
}