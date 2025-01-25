package com.yahtzee;

import java.util.*;

public class Dice {

    // Static method to roll a single die (generates a random number between 1 and 6)
    public static int rollDie() {
        Random random = new Random();
        return random.nextInt(6) + 1;
    }

    // Static method to roll a specified number of dice and return their values in a list
    public static List<Integer> rollDice(int numDice) {
        List<Integer> diceRolls = new ArrayList<>(numDice);

        for (int i = 0; i < numDice; i++) {
            diceRolls.add(rollDie());
        }

        return diceRolls;
    }
}