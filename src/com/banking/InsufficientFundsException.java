package com.banking;

// By extending Exception, we create a brand new rule for our Java compiler!
public class InsufficientFundsException extends Exception {
    
    public InsufficientFundsException(String message) {
        super(message); // Passes the message up to the built-in Exception class
    }
}