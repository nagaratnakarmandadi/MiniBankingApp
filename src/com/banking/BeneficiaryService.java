package com.banking;
import java.sql.*;
import java.util.Scanner;

public class BeneficiaryService {
    static Connection con = DBConnection.getConnection();

    // Accepts the Customer object instead of an integer
    public static void execute(Customer owner) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- MANAGE BENEFICIARIES ---");
        System.out.println("1. Add a new Beneficiary");
        System.out.println("2. View my Beneficiaries");
        System.out.print("Choose an option: ");
        
        int choice = sc.nextInt();
        sc.nextLine(); 

        try {
            if (choice == 1) {
                System.out.print("Enter the name of the friend you want to save: ");
                String friendName = sc.nextLine();

                String sql = "INSERT INTO beneficiaries (owner_ac, friend_name) VALUES (?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, owner.getAcNo()); 
                ps.setString(2, friendName);
                ps.executeUpdate();

                System.out.println("✅ " + friendName + " has been safely added to your address book!");

            } else if (choice == 2) {
                System.out.println("\n--- " + owner.getCname() + "'s Saved Friends ---");

                String sql = "SELECT friend_name FROM beneficiaries WHERE owner_ac = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, owner.getAcNo());
                ResultSet rs = ps.executeQuery();

                boolean hasFriends = false;
                
                while (rs.next()) {
                    hasFriends = true;
                    System.out.println("👤 " + rs.getString("friend_name"));
                }

                if (!hasFriends) {
                    System.out.println("You haven't saved any beneficiaries yet.");
                }
                
            } else {
                System.out.println("❌ Invalid choice.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}