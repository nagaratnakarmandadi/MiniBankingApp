package com.banking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class BankMangement {
    private static final int NULL = 0;
    static Connection con = DBConnection.getConnection();

    public static boolean createAccount(String name, int passCode) {
        if (name.isEmpty() || passCode == NULL) {
            System.out.println("All fields are required!");
            return false;
        }
        try {
            String sql = "INSERT INTO customer(cname, balance, pass_code) VALUES (?, 1000, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, passCode);
            if (ps.executeUpdate() == 1) {
                System.out.println("Account created successfully! You can now login.");
                return true;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Username already exists! Try another one.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean loginAccount(String name, int passCode) {
        if (name.isEmpty() || passCode == NULL) return false;

        try {
            String sql = "SELECT * FROM customer WHERE cname = ? AND pass_code = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, passCode);
            ResultSet rs = ps.executeQuery();

            BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));

            if (rs.next()) {
                int senderAc = rs.getInt("ac_no");
                int ch;

                // FIXED: Create the Customer object immediately upon login
                Customer currentCustomer = new SavingsAccount(
                    rs.getInt("ac_no"),
                    rs.getString("cname"),
                    rs.getInt("balance")
                );

                while (true) {
                    System.out.println("\n==================================");
                    System.out.println("WELCOME TO VALOR");
                    System.out.println(" Hello, " + rs.getString("cname") + "!");
                    System.out.println("==================================");
                    System.out.println("1) Transfer Money");
                    System.out.println("2) Deposit Cash");
                    System.out.println("3) Withdraw Cash");
                    System.out.println("4) View Balance");
                    System.out.println("5) View Profile");
                    System.out.println("6) Change Password");
                    System.out.println("7) Transaction History");
                    System.out.println("8) Pay a Merchant");
                    System.out.println("9) Manage Beneficiaries");
                    System.out.println("10) Apply for a Loan");
                    System.out.println("11) Logout");
                    System.out.print("Enter Choice: ");
                    ch = Integer.parseInt(sc.readLine());

                    if (ch == 1) {
                        System.out.print("Enter Receiver's Username: ");
                        String receiverName = sc.readLine();
                        System.out.print("Enter Amount: ");
                        int amt = Integer.parseInt(sc.readLine());
                        TransferService.execute(senderAc, receiverName, amt);
                    } else if (ch == 2) {
                        System.out.print("Enter Amount to Deposit: ");
                        int amt = Integer.parseInt(sc.readLine());
                        DepositService.execute(senderAc, amt);
                    } else if (ch == 3) {
                        System.out.print("Enter Amount to Withdraw: ");
                        int amt = Integer.parseInt(sc.readLine());
                        WithdrawService.execute(senderAc, amt);
                    } else if (ch == 4) {
                        BalanceService.execute(senderAc);
                    } else if (ch == 5) {
                        ProfileService.execute(senderAc);
                    } else if (ch == 6) {
                        PasswordService.execute(senderAc);
                    } else if (ch == 7) {
                        HistoryService.execute(senderAc);
                    } else if (ch == 8) {
                        MerchantService.execute(senderAc);
                    } else if (ch == 9) {
                        // FIXED: Pass the Object to the service instead of the int
                        BeneficiaryService.execute(currentCustomer);
                    } else if (ch == 10) {
                        LoanService.execute(senderAc);
                    } else if (ch == 11) {
                        System.out.println("Logged out successfully. Returning to main menu.");
                        break;
                    } else {
                        System.out.println("Invalid choice! Try again.");
                    }
                }
                return true;
            } else {
                System.out.println("Invalid username or password!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}