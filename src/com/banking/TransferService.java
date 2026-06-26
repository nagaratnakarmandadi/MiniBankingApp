package com.banking;
import java.sql.*;

public class TransferService {
    static Connection con = DBConnection.getConnection();

    public static void execute(int senderAc, String receiverName, int amount) {
        try {
            // 1. Turn off auto-save! We manually save only if BOTH steps succeed.
            con.setAutoCommit(false); 

            PreparedStatement psCheck = con.prepareStatement("SELECT balance FROM customer WHERE ac_no = ?");
            psCheck.setInt(1, senderAc);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next() && rs.getInt("balance") - amount >= 500) {
                
                // Step 2: Deduct from Sender
                PreparedStatement ps1 = con.prepareStatement("UPDATE customer SET balance = balance - ? WHERE ac_no = ?");
                ps1.setInt(1, amount);
                ps1.setInt(2, senderAc);
                int row1 = ps1.executeUpdate();

                // Step 3: Add to Receiver
                PreparedStatement ps2 = con.prepareStatement("UPDATE customer SET balance = balance + ? WHERE cname = ?");
                ps2.setInt(1, amount);
                ps2.setString(2, receiverName);
                int row2 = ps2.executeUpdate();

                // 4. Verify both steps worked perfectly
                if (row1 == 1 && row2 == 1) {
                    con.commit(); // ✅ SAFE TO SAVE!
                    System.out.println("✅ Transfer of ₹" + amount + " to " + receiverName + " was successful!");
                } else {
                    con.rollback(); // ❌ Something failed, undo everything!
                    System.out.println("❌ Transfer failed. Receiver not found. Your money has been refunded.");
                }
            } else {
                System.out.println("❌ Transfer failed. Insufficient funds (Minimum balance of 500 required).");
            }

        } catch (Exception e) {
            try { 
                con.rollback(); // ❌ Crash happened, undo everything!
                System.out.println("❌ Critical error. Transaction rolled back safely.");
            } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ex) { }
        }
    }
}