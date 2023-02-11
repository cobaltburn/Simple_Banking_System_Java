package org.example;

import org.sqlite.SQLiteDataSource;

import java.util.Scanner;


public class Main {

    static SQLiteDataSource accounts;

    public static void main(String[] args) {
        String dbName = "card.s3db";
        accounts = DatabaseHandler.initializeDatabase(dbName);
        Scanner scanner = new Scanner(System.in);
        boolean exitFlag = false;

        do {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
            String input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> DatabaseHandler.createAccount(accounts);
                case "2" -> exitFlag = logIn(accounts);
                case "0" -> exitFlag = true;
            }

        } while (!exitFlag);
    }

    static boolean logIn(SQLiteDataSource accounts) {
        Account user = validateCard(accounts);
        if (user == null) {
            System.out.println("Wrong card number or PIN!");
            return false;
        }
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("You have successfully logged in!");
        do {
            System.out.println();
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");
            input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> System.out.println("Balance: " + user.getBalance());
                case "2" -> addBalance(accounts, user);
                case "3" -> transfer(accounts, user);
                case "4" -> {
                    closeAccount(accounts, user);
                    return false;
                }
                case "5" -> {
                    System.out.println("You have successfully logged out!");
                    return false;
                }
                case "0" -> {
                    return true;
                }
            }
        } while (true);

    }

    static Account validateCard(SQLiteDataSource accounts) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your card number:");
        String cardNum = scanner.next();
        System.out.println("Enter your PIN:");
        String pin = scanner.next();
        return DatabaseHandler.validateAccount(accounts, cardNum, pin);
    }


    static void addBalance(SQLiteDataSource accounts, Account user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter income:");
        int income = scanner.nextInt();
        DatabaseHandler.modifyBalance(accounts, user, income);
        System.out.println("Income was added!\n");
    }
    static void transfer(SQLiteDataSource accounts, Account sender) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String cardNum = scanner.next();

        int proof = Card.luhnAlgorithm(cardNum.substring(0, cardNum.length() - 1));
        if (!Integer.toString(proof).equals(cardNum.substring(cardNum.length()-1))) {
            System.out.println("Probably you made a mistake in the card number.\n" +
                    "Please try again!");
            return;
        }

        Account receiver = DatabaseHandler.findAccount(accounts, cardNum);
        if (receiver == null) {
            System.out.println("Such a card does not exist.");
            return;
        }

        System.out.println("Enter how much money you want to transfer:");
        int amount = scanner.nextInt();
        if (!DatabaseHandler.transferMoney(accounts, sender, receiver, amount)) {
            System.out.println("Not enough money!");
            return;
        }

        System.out.println("Success!");
    }

    static void closeAccount(SQLiteDataSource accounts, Account user) {
        DatabaseHandler.closeAccount(accounts, user);
        System.out.println("The account has been closed!");
    }

}
