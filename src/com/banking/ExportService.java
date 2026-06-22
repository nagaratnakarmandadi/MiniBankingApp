package com.banking;

import java.io.FileWriter;
import java.sql.*;

public class ExportService {
    static Connection con = DBConnection.getConnection();

    public static void execute(int acNo, String username) {
        try {
            // Fetch history from DB
            PreparedStatement ps = con.prepareStatement("SELECT * FROM transactions WHERE sender_ac = ? ORDER BY t_date DESC");
            ps.setInt(1, acNo);
            ResultSet rs = ps.executeQuery();

            // Create a text file named after the user
            String filename = username + "_Statement.txt";
            FileWriter writer = new FileWriter(filename);
            
            writer.write("=============================================================\n");
            writer.write("                 INBANK - OFFICIAL STATEMENT\n");
            writer.write("=============================================================\n");
            writer.write(String.format("%-10s %-20s %-10s %-20s\n", "ID", "Sent To / Action", "Amount", "Date & Time"));
            writer.write("-------------------------------------------------------------\n");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                writer.write(String.format("%-10d %-20s %-10d %-20s\n",
                        rs.getInt("t_id"),
                        rs.getString("receiver_name"),
                        rs.getInt("amount"),
                        rs.getTimestamp("t_date").toString()));
            }
            
            if (!hasData) {
                writer.write("No transactions found.\n");
            }
            
            writer.write("=============================================================\n");
            writer.close(); // Save and close the file

            System.out.println("\n✅ Success! Your statement has been downloaded to your project folder as: " + filename);

        } catch (Exception e) {
            System.out.println("Failed to download statement.");
            e.printStackTrace();
        }
    }
}