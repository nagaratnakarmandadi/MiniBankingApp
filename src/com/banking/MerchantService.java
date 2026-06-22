package com.banking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class MerchantService {
    static Connection con = DBConnection.getConnection();

    public static void execute(int senderAc) {
        try {
            BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("\n--- PAY A BUSINESS ---");
            System.out.println("1) Vedha Digital Galaxy (Electronics & Accessories)");
            System.out.println("2) City Power Board (Electricity Bill)");
            System.out.println("3) Cancel");
            System.out.print("Select Merchant: ");
            int choice = Integer.parseInt(sc.readLine());

            String merchantName = "";
            if (choice == 1) merchantName = "Vedha Digital Galaxy";
            else if (choice == 2) merchantName = "City Power Board";
            else return;

            System.out.print("Enter payment amount: ");
            int amt = Integer.parseInt(sc.readLine());

            con.setAutoCommit(false);

            // 1. Check sender's balance
            PreparedStatement psCheck = con.prepareStatement("SELECT balance FROM customer WHERE ac_no = ?");
            psCheck.setInt(1, senderAc);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next() && rs.getInt("balance") < amt) {
                System.out.println("Insufficient Balance!");
                return;
            }

            // 2. Deduct money from the sender
            PreparedStatement psDebit = con.prepareStatement("UPDATE customer SET balance = balance - ? WHERE ac_no = ?");
            psDebit.setInt(1, amt);
            psDebit.setInt(2, senderAc);
            psDebit.executeUpdate();

            // 3. Generate a specialized receipt for the business payment
            PreparedStatement psReceipt = con.prepareStatement("INSERT INTO transactions (sender_ac, receiver_name, amount) VALUES (?, ?, ?)");
            psReceipt.setInt(1, senderAc);
            psReceipt.setString(2, "MERCHANT: " + merchantName);
            psReceipt.setInt(3, amt);
            psReceipt.executeUpdate();

            con.commit();
            System.out.println("✅ Payment of " + amt + " to " + merchantName + " was successful!");

        } catch (Exception e) {
            try { con.rollback(); } catch (SQLException ex) {}
            System.out.println("Payment failed. Please try again.");
        }
    }
}