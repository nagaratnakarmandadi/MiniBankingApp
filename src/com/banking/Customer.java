package com.banking;

public abstract class Customer {
    // We use "protected" so SavingsAccount and CheckingAccount can see these
    protected int acNo;
    protected String cname;
    protected int balance;

    public Customer(int acNo, String cname, int balance) {
        this.acNo = acNo;
        this.cname = cname;
        this.balance = balance;
    }

    public int getAcNo() {
        return acNo;
    }

    public String getCname() {
        return cname;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    // The abstract method that forces child classes to define their own rules
    public abstract void processEndOfMonth(); 
}