package com.banking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class BeneficiaryService {
    static Connection con = DBConnection.getConnection();

    public static void execute(int ownerAc) {
        try {
            BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("\n--- SAVED BENEFICIARIES ---");
            System.out.println("1) Add New Beneficiary");
            System.out.println("2) View Saved Beneficiaries");
            System.out.print("Select Option: ");
            int choice = Integer.parseInt(sc.readLine());

            if (choice == 1) {
                System.out.print("Enter friend's exact username to save: ");
                String friendName = sc.readLine();

                // Save the friend to the database, linked to the current logged-in user
                PreparedStatement ps = con.prepareStatement("INSERT INTO beneficiaries (owner_ac, friend_name) VALUES (?, ?)");
                ps.setInt(1, ownerAc);
                ps.setString(2, friendName);
                ps.executeUpdate();
                System.out.println("✅ " + friendName + " has been added to your saved beneficiaries!");
                
            } else if (choice == 2) {
                // Retrieve only the friends saved by this specific user
                PreparedStatement ps = con.prepareStatement("SELECT friend_name FROM beneficiaries WHERE owner_ac = ?");
                ps.setInt(1, ownerAc);
                ResultSet rs = ps.executeQuery();
                
                System.out.println("\n--- YOUR SAVED FRIENDS ---");
                boolean hasFriends = false;
                while (rs.next()) {
                    hasFriends = true;
                    System.out.println("- " + rs.getString("friend_name"));
                }
                if (!hasFriends) {
                    System.out.println("You haven't saved any beneficiaries yet.");
                }
                System.out.println("--------------------------");
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}