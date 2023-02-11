package org.example;

public class Account {
    private final int id;
    private final String cardNum;
    private final String pin;
    private int balance;
    public int getBalance() {
        return balance;
    }
    public void changeBalance(int amount) {
        this.balance += amount;
    }

    public int getId() {
        return id;
    }

    public String getCardNum() {
        return cardNum;
    }

    public String getPin() {
        return pin;
    }

    Account(int id, String cardNum, String pin, int balance) {
        this.id = id;
        this.cardNum = cardNum;
        this.pin = pin;
        this.balance = balance;
    }
}
