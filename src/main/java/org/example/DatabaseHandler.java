package org.example;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Arrays;

public class DatabaseHandler {
    public static SQLiteDataSource initializeDatabase(String dbName){
        SQLiteDataSource db = new SQLiteDataSource();
        db.setUrl("jdbc:sqlite:" + dbName);
        try (Connection con = db.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("Create TABLE IF NOT EXISTS card(" +
                        "id INT PRIMARY KEY, " +
                        "number TEXT, " +
                        "pin TEXT, " +
                        "balance INT DEFAULT 0)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return db;
    }

    public static void createAccount(SQLiteDataSource db) {
        String cardNum = Card.generateCardNum();
        String pin = Card.generatePin();
        System.out.println("Your card has been created\n"
                + "Your card number:");
        System.out.println(cardNum);
        System.out.println("Your card PIN:");
        System.out.println(pin);
        System.out.println();
        try (Connection con = db.getConnection()) {
            String input = "INSERT  INTO  card (number, pin) VALUES (?, ?)";
            try (PreparedStatement statement = con.prepareStatement(input)) {
                statement.setString(1, cardNum);
                statement.setString(2, pin);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Account validateAccount(SQLiteDataSource db, String cardNum, String pin) {
        Account account = null;
        try (Connection con = db.getConnection()) {
            String input = "SELECT * FROM card WHERE number = ? AND pin = ?";
            try(PreparedStatement statement = con.prepareStatement(input)) {
                statement.setString(1, cardNum);
                statement.setString(2, pin);
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    account = new Account(result.getInt("id"), result.getString("number"),
                            result.getString("pin"), result.getInt("balance"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    public static Account findAccount(SQLiteDataSource db, String cardNum) {
        Account account = null;
        try (Connection con = db.getConnection()) {
            String input = "SELECT * FROM card WHERE number = ?";
            try(PreparedStatement statement = con.prepareStatement(input)) {
                statement.setString(1, cardNum);
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    account = new Account(result.getInt("id"), result.getString("number"),
                            result.getString("pin"), result.getInt("balance"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }
    public static void modifyBalance(SQLiteDataSource db, Account user, int amount) {
        try (Connection con = db.getConnection()) {
            String input = "UPDATE card SET balance = balance + ? WHERE number = ?";
            try(PreparedStatement statement = con.prepareStatement(input)) {
                statement.setInt(1, amount);
                statement.setString(2, user.getCardNum());
                statement.executeUpdate();
                user.changeBalance(amount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean transferMoney(SQLiteDataSource db, Account sender, Account receiver, int amount) {

        if (sender.getBalance() < amount) {
            return false;
        }
        try (Connection con = db.getConnection()) {
            con.setAutoCommit(false);
            String senderInput = "UPDATE card SET balance = balance - ? WHERE  number = ?";
            String receiverInput = "UPDATE card SET balance = balance + ? WHERE  number = ?";
            try(PreparedStatement senderStatement = con.prepareStatement(senderInput);
                PreparedStatement receiverStatement = con.prepareStatement(receiverInput)) {

                senderStatement.setInt(1, amount);
                senderStatement.setString(2, sender.getCardNum());
                senderStatement.executeUpdate();

                receiverStatement.setInt(1, amount);
                receiverStatement.setString(2, receiver.getCardNum());
                receiverStatement.executeUpdate();

                con.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static void closeAccount(SQLiteDataSource db, Account user) {
        try (Connection con = db.getConnection()) {
            String input = "DELETE FROM card WHERE number = ?";
            try(PreparedStatement statement = con.prepareStatement(input)) {
                statement.setString(1, user.getCardNum());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
