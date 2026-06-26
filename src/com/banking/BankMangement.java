package com.banking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class BankMangement {
    private static final int NULL = 0;
    static Connection con = DBConnection.getConnection();

    // --------------------------------------------------------
    // 1. HELPER METHOD: Stops the app from crashing if they type a letter
    // --------------------------------------------------------
    private static int getValidInt(BufferedReader sc) {
        while (true) {
            try {
                return Integer.parseInt(sc.readLine());
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid input! Please type a number: ");
            } catch (Exception e) {
                return -1;
            }
        }
    }

    public static boolean createAccount(String name, int passCode) {
        if (name.isEmpty() || passCode == NULL) {
            System.out.println("All fields are required!");
            return false;
        }
        try {
            String sql = "INSERT INTO customer(cname, balance, pass_code) VALUES (?, 1000, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            // 2. SECURITY UPGRADE: Hash the password before saving!
            ps.setString(2, SecurityUtil.hashPassword(passCode)); 
            
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
            // 3. SECURITY UPGRADE: Hash what they typed to compare with the database!
            ps.setString(2, SecurityUtil.hashPassword(passCode));
            ResultSet rs = ps.executeQuery();

            BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));

            if (rs.next()) {
                int senderAc = rs.getInt("ac_no");
                int ch;

                Customer currentCustomer = new SavingsAccount(
                    rs.getInt("ac_no"),
                    rs.getString("cname"),
                    rs.getInt("balance")
                );
                currentCustomer.setRole(rs.getString("role"));

                while (true) {
                    System.out.println("\n==================================");
                    System.out.println("WELCOME TO VALOR");
                    System.out.println(" Hello, " + rs.getString("cname") + "!");
                    System.out.println(" Account Type: " + currentCustomer.getRole().toUpperCase());
                    System.out.println("==================================");

                    // --- MERCHANT MENU ---
                    if (currentCustomer.getRole().equals("merchant")) {
                        System.out.println("1) View Daily Sales Report");
                        System.out.println("2) Process Customer Refund");
                        System.out.println("3) Pay Supplier / Vendor");
                        System.out.println("4) Logout");
                        System.out.print("Enter Choice: ");
                        
                        // 4. CRASH FIX: Using our new helper method
                        ch = getValidInt(sc);

                        if (ch == 1) {
                            System.out.println("📊 Generating business sales report... (Coming Soon)");
                        } else if (ch == 2) {
                            System.out.println("💸 Processing refund... (Coming Soon)");
                        } else if (ch == 3) {
                            System.out.println("📦 Paying supplier... (Coming Soon)");
                        } else if (ch == 4) {
                            System.out.println("Merchant logged out successfully.");
                            break;
                        } else {
                            System.out.println("Invalid choice! Try again.");
                        }

                    // --- REGULAR CUSTOMER MENU ---
                    } else {
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
                        
                        // CRASH FIX: Using our new helper method
                        ch = getValidInt(sc);

                        if (ch == 1) {
                            System.out.print("Enter Receiver's Username: ");
                            String receiverName = sc.readLine();
                            System.out.print("Enter Amount: ");
                            int amt = getValidInt(sc); // CRASH FIX applied here too
                            TransferService.execute(senderAc, receiverName, amt);
                        } else if (ch == 2) {
                            System.out.print("Enter Amount to Deposit: ");
                            int amt = getValidInt(sc);
                            DepositService.execute(senderAc, amt);
                        } else if (ch == 3) {
                            System.out.print("Enter Amount to Withdraw: ");
                            int amt = getValidInt(sc);
                            WithdrawService.execute(senderAc, amt);
                        } else if (ch == 4) {
                            BalanceService.execute(senderAc);
                        } else if (ch == 5) {
                            ProfileService.execute(senderAc);
                        } else if (ch == 6) {
                            // Note: PasswordService needs the hash update too if you are building it!
                            PasswordService.execute(senderAc);
                        } else if (ch == 7) {
                            HistoryService.execute(senderAc);
                        } else if (ch == 8) {
                            MerchantService.execute(senderAc);
                        } else if (ch == 9) {
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