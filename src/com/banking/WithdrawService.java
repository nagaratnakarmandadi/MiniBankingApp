package com.banking;
import java.sql.*;

public class WithdrawService {
    static Connection con = DBConnection.getConnection();

    public static void execute(int acNo, int amount) {
        try {
            con.setAutoCommit(false);

            PreparedStatement psCheck = con.prepareStatement("SELECT balance FROM customer WHERE ac_no = ?");
            psCheck.setInt(1, acNo);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                if (rs.getInt("balance") - amount < 500) {
                    System.out.println("Withdrawal Failed! You must maintain a minimum balance of 500.");
                    return;
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
            System.out.println("Withdrawal of " + amount + " was successful!");
        } catch (Exception e) {
            try { con.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
        }
    }
}