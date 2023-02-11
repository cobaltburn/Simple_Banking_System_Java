package org.example;

import java.util.Random;

public class Card {

    public static String generateCardNum() {
        Random random = new Random(System.nanoTime());
        StringBuilder cardNumber = new StringBuilder("400000");
        for (int i = 0; i < 9; i++) {
            cardNumber.append(random.nextInt(9));
        }
        int lastDigit = luhnAlgorithm(cardNumber.toString());
        cardNumber.append(lastDigit);
        return cardNumber.toString();
    }
    public static String generatePin() {
        Random random = new Random(System.nanoTime());
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            pin.append(random.nextInt(9));
        }
        return pin.toString();
    }

    public static int luhnAlgorithm(String cardVal) {
        int sum = 0;
        for (int i = 0; i < cardVal.length(); i++) {
            int dig = Integer.parseInt(String.valueOf(cardVal.charAt(i)));
            if (i % 2 == 0) {
                dig = dig * 2;
                if (dig > 9) {
                    dig = (dig % 10) + 1;
                }
            }
            sum += dig;
        }
        return (10 - (sum % 10)) % 10;
    }

}

