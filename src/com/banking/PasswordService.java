
package com.banking;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class PasswordService {
    static Connection con = DBConnection.getConnection();

    public static void execute(int acNo) {
        try {
            BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter your current password: ");
            int oldPass = Integer.parseInt(sc.readLine());

            PreparedStatement psCheck = con.prepareStatement("SELECT * FROM customer WHERE ac_no = ? AND pass_code = ?");
            psCheck.setInt(1, acNo);
            psCheck.setInt(2, oldPass);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                System.out.print("Enter your NEW password: ");
                int newPass = Integer.parseInt(sc.readLine());

                PreparedStatement psUpdate = con.prepareStatement("UPDATE customer SET pass_code = ? WHERE ac_no = ?");
                psUpdate.setInt(1, newPass);
                psUpdate.setInt(2, acNo);
                psUpdate.executeUpdate();

                System.out.println("Password updated successfully!");
            } else {
                System.out.println("Incorrect current password! Access denied.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}