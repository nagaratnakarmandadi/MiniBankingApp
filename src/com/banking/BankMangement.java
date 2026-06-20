package com.banking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class BankMangement {

	private static final int NULL = 0;
	static Connection con = DBConnection.getConnection();

	// 1. Create Account
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

			int rows = ps.executeUpdate();
			if (rows == 1) {
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

	// 2. Login Account
	public static boolean loginAccount(String name, int passCode) {
		if (name.isEmpty() || passCode == NULL) {
			System.out.println("All fields are required!");
			return false;
		}

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

				while (true) {
					System.out.println("\n Hello, " + rs.getString("cname") + "! What would you like to do?");
					System.out.println("1) Transfer Money");
					System.out.println("2) View Balance");
					System.out.println("3) Change Password");
					System.out.println("4) Transaction History");
					System.out.println("5) Logout");
					System.out.print("Enter Choice: ");
					ch = Integer.parseInt(sc.readLine());

					if (ch == 1) {
						System.out.print("Enter Receiver's Username: ");
						String receiverName = sc.readLine();
						System.out.print("Enter Amount: ");
						int amt = Integer.parseInt(sc.readLine());

						if (transferMoney(senderAc, receiverName, amt)) {
							System.out.println("Transaction successful!");
						} else {
							System.out.println("Transaction failed! Please try again.");
						}
					} else if (ch == 2) {
						getBalance(senderAc);
					} else if (ch == 3) {
						changePassword(senderAc);
					} else if (ch == 4) {
						getHistory(senderAc);
					} else if (ch == 5) {
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

	// 3. Get Balance
	public static void getBalance(int acNo) {
		try {
			String sql = "SELECT * FROM customer WHERE ac_no = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, acNo);
			ResultSet rs = ps.executeQuery();

			System.out.println("\n-------------------------------------------------");
			System.out.printf("%12s %15s %10s\n", "Account No", "Customer Name", "Balance");

			while (rs.next()) {
				System.out.printf("%12d %15s %10d.00\n", rs.getInt("ac_no"), rs.getString("cname"),
						rs.getInt("balance"));
			}
			System.out.println("-------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 4. Transfer Money (Now saves a receipt!)
	public static boolean transferMoney(int sender_ac, String receiver_name, int amount) {
		if (receiver_name.isEmpty() || amount == NULL) {
			System.out.println("All fields are required!");
			return false;
		}

		try {
			con.setAutoCommit(false);

			// Check Sender Balance
			String checkBalance = "SELECT balance FROM customer WHERE ac_no = ?";
			PreparedStatement ps = con.prepareStatement(checkBalance);
			ps.setInt(1, sender_ac);
			ResultSet rs = ps.executeQuery();

			if (rs.next() && rs.getInt("balance") < amount) {
				System.out.println("Insufficient Balance!");
				return false;
			}

			// Debit Sender
			String debit = "UPDATE customer SET balance = balance - ? WHERE ac_no = ?";
			PreparedStatement psDebit = con.prepareStatement(debit);
			psDebit.setInt(1, amount);
			psDebit.setInt(2, sender_ac);
			psDebit.executeUpdate();

			// Credit Receiver
			String credit = "UPDATE customer SET balance = balance + ? WHERE cname = ?";
			PreparedStatement psCredit = con.prepareStatement(credit);
			psCredit.setInt(1, amount);
			psCredit.setString(2, receiver_name);

			int rowsUpdated = psCredit.executeUpdate();
			if (rowsUpdated == 0) {
				System.out.println("Receiver username not found!");
				con.rollback();
				return false;
			}

			// Save Receipt to History
			String receipt = "INSERT INTO transactions (sender_ac, receiver_name, amount) VALUES (?, ?, ?)";
			PreparedStatement psReceipt = con.prepareStatement(receipt);
			psReceipt.setInt(1, sender_ac);
			psReceipt.setString(2, receiver_name);
			psReceipt.setInt(3, amount);
			psReceipt.executeUpdate();

			con.commit();
			return true;

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		}
		return false;
	}

	// 5. Change Password
	public static void changePassword(int acNo) {
		try {
			BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Enter your current password: ");
			int oldPass = Integer.parseInt(sc.readLine());

			String checkSql = "SELECT * FROM customer WHERE ac_no = ? AND pass_code = ?";
			PreparedStatement psCheck = con.prepareStatement(checkSql);
			psCheck.setInt(1, acNo);
			psCheck.setInt(2, oldPass);
			ResultSet rs = psCheck.executeQuery();

			if (rs.next()) {
				System.out.print("Enter your NEW password: ");
				int newPass = Integer.parseInt(sc.readLine());

				String updateSql = "UPDATE customer SET pass_code = ? WHERE ac_no = ?";
				PreparedStatement psUpdate = con.prepareStatement(updateSql);
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

	// 6. View Transaction History
	public static void getHistory(int acNo) {
		try {
			String sql = "SELECT * FROM transactions WHERE sender_ac = ? ORDER BY t_date DESC";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, acNo);
			ResultSet rs = ps.executeQuery();

			System.out.println("\n-------------------------------------------------------------");
			System.out.printf("%10s %15s %10s %20s\n", "Transfer ID", "Sent To", "Amount", "Date & Time");

			boolean hasHistory = false;
			while (rs.next()) {
				hasHistory = true;
				System.out.printf("%10d %15s %10d %20s\n", rs.getInt("t_id"), rs.getString("receiver_name"),
						rs.getInt("amount"), rs.getTimestamp("t_date").toString());
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