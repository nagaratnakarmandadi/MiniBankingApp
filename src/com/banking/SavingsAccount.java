package com.banking;

public class SavingsAccount extends Customer {
    public SavingsAccount(int acNo, String cname, int balance) {
        super(acNo, cname, balance);
    }

    @Override
    public void processEndOfMonth() {
        int interest = (int) (balance * 0.03);
        balance = balance + interest;
        System.out.println("✅ " + cname + " (Savings) earned ₹" + interest + " in interest. New Balance: " + balance);
    }
}