package com.banking;
import java.sql.*;

public class HistoryService {
    static Connection con = DBConnection.getConnection();

    public static void execute(int acNo) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM transactions WHERE sender_ac = ? ORDER BY t_date DESC");
            ps.setInt(1, acNo);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n-------------------------------------------------------------");
            System.out.printf("%10s %20s %10s %20s\n", "ID", "Sent To / Action", "Amount", "Date & Time");

            boolean hasHistory = false;
            while (rs.next()) {
                hasHistory = true;
                System.out.printf("%10d %20s %10d %20s\n", 
                        rs.getInt("t_id"), 
                        rs.getString("receiver_name"),
                        rs.getInt("amount"), 
                        rs.getTimestamp("t_date").toString());
            }
            if (!hasHistory) {
                System.out.println("        No recent transactions found.");
            }
            System.out.println("-------------------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}