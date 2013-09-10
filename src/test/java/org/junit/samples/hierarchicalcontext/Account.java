package org.junit.samples.hierarchicalcontext;

public class Account {
    private double balance = 0.0;
    private double interestRate = Bank.currentInterestRate;

    public double getBalance() {
        return balance;
    }

    public double getInterestRate() {
        return interestRate;
    }
}
