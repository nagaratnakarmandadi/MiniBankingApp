package com.banking;

public class CheckingAccount extends Customer {
    public CheckingAccount(int acNo, String cname, int balance) {
        super(acNo, cname, balance);
    }

    @Override
    public void processEndOfMonth() {
        balance = balance - 50;
        System.out.println("⚠️ " + cname + " (Checking) was charged a ₹50 maintenance fee. New Balance: " + balance);
    }
}