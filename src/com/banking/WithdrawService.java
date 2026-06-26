package com.banking;
import java.sql.*;

public class WithdrawService {
    static Connection con = DBConnection.getConnection();

    public static void execute(int acNo, int amount) {
        try {
            con.setAutoCommit(false);

            PreparedStatement psCheck = con.prepareStatement("SELECT * FROM customer WHERE ac_no = ?");
            psCheck.setInt(1, acNo);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                // FIXED: We build a SavingsAccount object
                Customer currentCustomer = new SavingsAccount(
                    rs.getInt("ac_no"), 
                    rs.getString("cname"), 
                    rs.getInt("balance")
                );

                if (currentCustomer.getBalance() - amount < 500) {
                    throw new InsufficientFundsException("Withdrawal Failed! You must maintain a minimum balance of 500. Your current balance is " + currentCustomer.getBalance());
                }
            }

            PreparedStatement ps = con.prepareStatement("UPDATE customer SET balance = balance - ? WHERE ac_no = ?");
            ps.setInt(1, amount);
            ps.setInt(2, acNo);
            ps.executeUpdate();

            PreparedStatement psReceipt = con.prepareStatement("INSERT INTO transactions (sender_ac, receiver_name, amount) VALUES (?, 'Self (Withdrawal)', ?)");
            psReceipt.setInt(1, acNo);
            psReceipt.setInt(2, amount);
            psReceipt.executeUpdate();

            con.commit();
            System.out.println("✅ Withdrawal of " + amount + " was successful!");

        } catch (InsufficientFundsException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            try { con.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
        }
    }
}