package com.banking;
import java.util.ArrayList;

public class PolymorphismTest {
    public static void main(String[] args) {
        
        // We create a list that holds the generic base class "Customer"
        ArrayList<Customer> bankAccounts = new ArrayList<>();

        // We fill it with different specific types of accounts
        bankAccounts.add(new SavingsAccount(1001, "Alice", 1000));
        bankAccounts.add(new CheckingAccount(1002, "Aditya", 1000));
        bankAccounts.add(new SavingsAccount(1003, "Vedha Digital", 5000));

        System.out.println("--- RUNNING END OF MONTH SCRIPT ---");

        // THE POLYMORPHIC MAGIC: 
        // We run one single command, but Java automatically knows exactly 
        // which math to execute based on the specific object!
        for (Customer account : bankAccounts) {
            account.processEndOfMonth(); 
        }
    }
}