package com.banking;

public abstract class Customer {
    protected int acNo;
    protected String cname;
    protected int balance;
    
    // NEW: We added the role variable
    protected String role = "customer"; 

    public Customer(int acNo, String cname, int balance) {
        this.acNo = acNo;
        this.cname = cname;
        this.balance = balance;
    }

    public int getAcNo() { return acNo; }
    public String getCname() { return cname; }
    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }

    // NEW: Getter and Setter for the Role
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public abstract void processEndOfMonth(); 
}