package com.yahtzee;

import java.util.*;

public class Turn {
    // Static function to play a turn for a given player and scorecard
    public static List<Integer> playTurn(Player player, ScoreCard scoreCard) {
        List<Integer> keptDice = new ArrayList<>();
        int currentRoll = 1;

        // Loop through up to 3 rolls in the player's turn
        while (currentRoll <= 3) {
            System.out.println("\nRoll " + currentRoll + " of 3\n");

            System.out.println("\nAvailable Categories:");
            // Show potential categories based on kept dice
            List<Category> potentialCategories = scoreCard.getPossibleCategories(keptDice);
            ioFunctions.showCategories(potentialCategories);

            // Display the player's current kept dice
            System.out.println(player.getName() + "'s current dice: " + ioFunctions.toStringVector(keptDice) + '\n');

            // Get new dice rolls from the player (roll only the dice not kept)
            List<Integer> diceRolls = player.getDiceRoll(5 - keptDice.size());
            System.out.println(player.getName() + " rolled: " + ioFunctions.toStringVector(diceRolls) + '\n');

            // Recalculate potential categories with the newly rolled dice and show potential categories based on kept dice
            potentialCategories = scoreCard.getPossibleCategories(keptDice);
            System.out.println("Potential categories:");
            ioFunctions.showCategories(potentialCategories);

            // If this is the third roll, the turn automatically ends
            if (currentRoll == 3) {
                System.out.println("\nEnd of turn.");
                keptDice.addAll(diceRolls);
                break;
            }

            // Check if the player wants help (only applies to the computer, where the help system is triggered)
            if (player.wantsHelp()) {
                String help = new Computer().getHelp(scoreCard, keptDice, diceRolls);
                System.out.println("Help: \n" + help + '\n');
            }

            // Check if the player decides to "stand" and keep their dice (ending the turn early)
            if (player.wantsToStand(scoreCard, keptDice, diceRolls)) {
                System.out.println(player.getName() + " chose to stand.");
                keptDice.addAll(diceRolls);
                break;
            }

            // Determine which dice the player wants to keep based on the dice rolled and kept
            List<Integer> diceToKeep = player.getDiceToKeep(scoreCard, diceRolls, keptDice);
            System.out.println(player.getName() + " kept: " + ioFunctions.toStringVector(diceToKeep) + '\n');

            // Add the kept dice to the keptDice list
            keptDice.addAll(diceToKeep);

            // If all 5 dice are kept, the turn ends
            if (keptDice.size() == 5) {
                System.out.println("All dice kept. End of turn.\n");
                break;
            }

            // Optionally, show the player's pursuit strategy (what category they are aiming for)
            Optional<Map<Category, Reason>> userPursuit = player.getCategoryPursuits(scoreCard, keptDice);
            if (userPursuit.isPresent()) {
                System.out.println(player.getName() + "'s pursuit:");
                ioFunctions.showCategoryPursuits(userPursuit.get());
            }

            // Show the specific dice the player is aiming to roll for their target category
            Optional<Map.Entry<Category, List<Integer>>> userTarget = player.getTarget(scoreCard, keptDice);
            if (userTarget.isPresent()) {
                System.out.println(player.getName() + "'s target: " + Category.CATEGORY_NAMES.get(userTarget.get().getKey()) +
                        " by rolling " + ioFunctions.toStringVector(userTarget.get().getValue()) + '\n');
            }

            currentRoll++;
        }

        // Output the final set of dice that the player ended with after their turn
        System.out.println(player.getName() + "'s final dice for round " + player.getName() + ": " +
        ioFunctions.toStringVector(keptDice) + '\n');
        // Return the kept dice (the result of the player's turn)
        return keptDice;
    }
}