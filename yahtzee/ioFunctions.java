package com.yahtzee;

import java.util.*;
import java.io.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ioFunctions {

    // Prompts the user with a yes/no question and returns true for 'y' and false
    // for 'n'.
    public static boolean getYesNo(String prompt) {
        Scanner scanner = new Scanner(System.in);
        String response;
        while (true) {
            System.out.print(prompt + " (y/n): ");
            response = scanner.nextLine();
            System.out.println();
            if (response.equals("y")) {
                return true;
            } else if (response.equals("n")) {
                return false;
            } else {
                System.out.println("Invalid response. Please enter 'y' or 'n'.");
            }
        }
    }

    // Prompts the user to input a manual die roll value between 1 and 6 and
    // validates the input.
    public static int getManualDieRoll() {
        Scanner scanner = new Scanner(System.in);
        String response;
        while (true) {
            System.out.print("Enter the value of the die roll: ");
            response = scanner.nextLine();
            try {
                int i = Integer.parseInt(response);
                if (i >= 1 && i <= 6) {
                    return i;
                } else {
                    System.out.println("Invalid response. Please enter an integer between 1 and 6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid response. Please enter an integer between 1 and 6.");
            }
        }
    }

    // Automatically generates a random die roll value using the Dice class.
    public static int getAutoDieRoll() {
        return Dice.rollDie();
    }

    // Determines whether to manually roll a set of dice or automatically roll them
    // based on user input.
    public static int getDieRoll() {
        boolean manualRoll = getYesNo("Would you like to manually roll the die?");
        if (manualRoll) {
            return getManualDieRoll();
        } else {
            return getAutoDieRoll();
        }
    }

    // Prompts the user to manually input a set of dice rolls and validates the
    // input.
    public static List<Integer> getManualDiceRoll(int numDice) {
        Scanner scanner = new Scanner(System.in);
        List<Integer> diceRolls = new ArrayList<>();
        String response;

        System.out.print("Enter " + numDice + " dice rolls separated by spaces: ");
        response = scanner.nextLine();
        Scanner iss = new Scanner(response);
        while (iss.hasNextInt()) {
            int i = iss.nextInt();
            if (i >= 1 && i <= 6) {
                diceRolls.add(i);
            } else {
                System.out.println("Invalid response. Please enter an integer between 1 and 6. Please try again.");
                return getManualDiceRoll(numDice);
            }
        }

        if (diceRolls.size() != numDice) {
            System.out.println(
                    "Invalid response. Please enter " + numDice + " integers between 1 and 6. Please try again.");
            return getManualDiceRoll(numDice);
        }

        return diceRolls;
    }

    // Automatically generates a set of random dice rolls.
    public static List<Integer> getAutoDiceRoll(int numDice) {
        List<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < numDice; i++) {
            rolls.add(getAutoDieRoll());
        }
        return rolls;
    }

    // Determines whether to manually roll a set of dice or automatically roll them
    // based on user input.
    public static List<Integer> getDiceRoll(int numDice) {
        boolean manualRoll = getYesNo("Would you like to manually roll the dice?");
        if (manualRoll) {
            return getManualDiceRoll(numDice);
        } else {
            return getAutoDiceRoll(numDice);
        }
    }

    // Displays a simple message to the user
    public static void showMessage(String message) {
        System.out.println(message);
    }

    // Conducts a tie-breaker where both human and computer roll a die. Returns true
    // if the human wins.

    public static boolean humanWantsToRollForComputer() {
        return getYesNo("Would you like to roll for the computer?");
    }
    public static boolean humanWonTieBreaker() {

        int humanRoll = getDieRoll();
        int computerRoll;
        System.out.println("\n");
        if (humanWantsToRollForComputer()) {
            computerRoll = getManualDieRoll();
        } else {
            computerRoll = getAutoDieRoll();
        }

        System.out.println("You rolled a " + humanRoll);
        System.out.println("The computer rolled a " + computerRoll);

        if (humanRoll > computerRoll) {
            System.out.println("You go first!\n");
            return true;
        } else if (computerRoll > humanRoll) {
            System.out.println("The computer goes first!\n");
            return false;
        }

        System.out.println("It's a tie! Rolling again.\n");
        return humanWonTieBreaker();
    }

    // Prompts the user to select specific dice to keep and validates that they were
    // originally rolled.
    public static List<Integer> getDiceToKeep(List<Integer> diceRolls) {
        Scanner scanner = new Scanner(System.in);
        List<Integer> diceToKeep = new ArrayList<>();
        String response;

        System.out.print("Enter the dice you would like to keep separated by spaces: ");
        response = scanner.nextLine();
        Scanner iss = new Scanner(response);
        while (iss.hasNextInt()) {
            int i = iss.nextInt();
            if (i >= 1 && i <= 6) {
                diceToKeep.add(i);
            } else {
                System.out.println("Invalid response. Please enter an integer between 1 and 6. Please try again.");
                return getDiceToKeep(diceRolls);
            }
        }

        for (int die : diceToKeep) {
            if (!diceRolls.contains(die)) {
                System.out.println("Invalid response. Please enter a die that was rolled. Please try again.");
                return getDiceToKeep(diceRolls);
            }
        }

        return diceToKeep;
    }

    // Converts a list of integers into a string representation.
    public static String toStringVector(List<Integer> vec) {
        StringBuilder str = new StringBuilder("[");
        for (int i = 0; i < vec.size(); i++) {
            str.append(vec.get(i));
            if (i != vec.size() - 1) {
                str.append(", ");
            }
        }
        str.append("]");
        return str.toString();
    }

    // // Displays the available score categories to the user.
    // public static void showCategories(List<Category> categories) {
    // for (int i = 0; i < categories.size(); i++) {
    // System.out.print(Category.CATEGORY_NAMES[categories.get(i)]);
    // if (i != categories.size() - 1) {
    // System.out.print("\n");
    // }
    // }
    // System.out.print('\n');
    // }

    // Displays the available score categories to the user.
    public static void showCategories(List<Category> categories) {
        for (int i = 0; i < categories.size(); i++) {
            System.out.print(Category.CATEGORY_NAMES.get(categories.get(i)));
            if (i != categories.size() - 1) {
                System.out.print("\n");
            }
        }
        System.out.print('\n');
    }

    // Prompts the user to load a saved game.
    public static boolean userWantsToLoadGame() {
        return getYesNo("Would you like to load a saved game?");
    }

    // Prompts the user for the name of the file to load a saved game and ensures
    // the file exists and is non-empty.
    // public static String getSerial() {
    // Scanner scanner = new Scanner(System.in);
    // String serial;
    // System.out.print("Enter the name of the file you would like to load: ");
    // serial = scanner.nextLine();

    // File file = new File(serial);
    // if (!file.exists() || !file.canRead()) {
    // System.err.println("Error: File does not exist or cannot be opened.");
    // System.out.println("Please try again.");
    // return getSerial();
    // }

    // try {
    // StringBuilder serialString = new StringBuilder();
    // Scanner fileScanner = new Scanner(file);
    // while (fileScanner.hasNextLine()) {
    // serialString.append(fileScanner.nextLine());
    // }
    // if (serialString.length() == 0) {
    // System.err.println("Error: File is empty.");
    // System.out.println("Please try again.");
    // return getSerial();
    // }
    // return serialString.toString();
    // } catch (FileNotFoundException e) {
    // System.err.println("Error: File not found.");
    // System.out.println("Please try again.");
    // return getSerial();
    // }
    // }
    public static String getSerial() {
        Scanner scanner = new Scanner(System.in);
        String serial;
        System.out.print("Enter the name of the file you would like to load: ");
        serial = scanner.nextLine();

        File file = new File(serial);
        if (!file.exists() || !file.canRead()) {
            System.err.println("Error: File does not exist or cannot be opened.");
            System.out.println("Please try again.");
            return getSerial();
        }

        try (Scanner fileScanner = new Scanner(file)) {
            StringBuilder serialString = new StringBuilder();
            while (fileScanner.hasNextLine()) {
                serialString.append(fileScanner.nextLine()).append("\n");
            }
            if (serialString.length() == 0) {
                System.err.println("Error: File is empty.");
                System.out.println("Please try again.");
                return getSerial();
            }
            return serialString.toString();
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found.");
            System.out.println("Please try again.");
            return getSerial();
        }
    }

    

    public static void saveGameProcedure(String serial) {
        if (getYesNo("Would you like to save the game and exit?")) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the name of the file you would like to save: ");
            String fileName = scanner.nextLine();
    
            try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
                out.print(serial);
                System.out.println("Game saved successfully.");
                System.exit(0);
            } catch (IOException e) {
                System.err.println("Error: Unable to save the file.");
            }
        }
    }

    // Asks the user if they want to stand (not reroll dice).
    public static boolean wantsToStand() {
        return getYesNo("Would you like to stand?");
    }

    // // Displays potential scoring opportunities for the current roll.
    // public static void showCategoryPursuits(Map<Category, Reason>
    // categoryPursuits) {
    // List<Reason> reasons = new ArrayList<>(categoryPursuits.values());
    // reasons.sort(Comparator.comparingInt(a -> a.maxScore));

    // System.out.println("Current dice: " +
    // toStringVector(reasons.get(0).currentDice));

    // for (Map.Entry<Category, Reason> entry : categoryPursuits.entrySet()) {
    // Reason reason = entry.getValue();
    // if (reason.minScore == 0) {
    // System.out.println("Can get " +
    // Category.CATEGORY_NAMES[reason.pursuedCategory] +
    // " with a score of " + reason.maxScore +
    // " by rolling " + toStringVector(reason.rollToGetMax));
    // continue;
    // }
    // System.out.println("Can get " +
    // Category.CATEGORY_NAMES[reason.pursuedCategory] +
    // " with a minimum score of " + reason.minScore +
    // " by getting " + toStringVector(reason.rollToGetMin) +
    // " and a maximum score of " + reason.maxScore +
    // " by rolling " + toStringVector(reason.rollToGetMax));
    // }
    // }

    // Displays potential scoring opportunities for the current roll.
    public static void showCategoryPursuits(Map<Category, Reason> categoryPursuits) {
        List<Reason> reasons = new ArrayList<>(categoryPursuits.values());
        reasons.sort(Comparator.comparingInt(a -> a.getMaxScore()));

        System.out.println("Current dice: " + toStringVector(reasons.get(0).getCurrentDice()));

        for (Map.Entry<Category, Reason> entry : categoryPursuits.entrySet()) {
            Reason reason = entry.getValue();
            if (reason.getMinScore() == 0) {
                System.out.println("Can get " + Category.CATEGORY_NAMES.get(reason.getPursuedCategory()) +
                        " with a score of " + reason.getMaxScore() +
                        " by rolling " + toStringVector(reason.getRollToGetMax()));
                continue;
            }
            System.out.println("Can get " + Category.CATEGORY_NAMES.get(reason.getPursuedCategory()) +
                    " with a minimum score of " + reason.getMinScore() +
                    " by getting " + toStringVector(reason.getRollToGetMin()) +
                    " and a maximum score of " + reason.getMaxScore() +
                    " by rolling " + toStringVector(reason.getRollToGetMax()));
        }
    }

    // Asks if human wants help or not
    public static boolean wantsHelp() {
        return getYesNo("Would you like help?");
    }

    // Asks if human wants to manually input for computer or not

}