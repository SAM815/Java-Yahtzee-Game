package com.yahtzee;

import java.util.*;

class Player {
    private final String name;

    // Constructors
    public Player() {
        this.name = "";
    }

    public Player(String name) {
        this.name = name;
    }

    // Copy constructor
    public Player(Player other) {
        this.name = other.name;
    }

    // // Copy assignment operator
    // public Player assign(Player other) {
    //     if (this != other) {
    //         return new Player(other);
    //     }
    //     return this;
    // }

    public boolean equals(Player other) {
        return this.name.equals(other.name);
    }

    public boolean notEquals(Player other) {
        return !this.equals(other);
    }

    public boolean lessThan(Player other) {
        return this.name.compareTo(other.name) < 0;
    }

    public String getName() {
        return name;
    }

    public static int tossDie(String prompt) {
        ioFunctions.showMessage(prompt);
        return ioFunctions.getDieRoll();
    }

    public List<Integer> getDiceRoll(int numDice) {
        return ioFunctions.getDiceRoll(numDice);
    }

    public List<Integer> getDiceToKeep(ScoreCard scoreCard, List<Integer> diceRolls, List<Integer> keptDice) {
        ioFunctions.showMessage("Kept dice: " + ioFunctions.toStringVector(keptDice));
        ioFunctions.showMessage("Current dice rolls: " + ioFunctions.toStringVector(diceRolls));
        return ioFunctions.getDiceToKeep(diceRolls);
    }

    public Optional<Map<Category, Reason>> getCategoryPursuits(ScoreCard scoreCard, List<Integer> keptDice) {
        return Optional.empty();
    }

    public Optional<Map.Entry<Category, List<Integer>>> getTarget(ScoreCard scoreCard, List<Integer> keptDice) {
        return Optional.empty();
    }

    public boolean wantsToStand(ScoreCard scoreCard, List<Integer> keptDice, List<Integer> diceRolls) {
        ioFunctions.showMessage("Kept dice: " + ioFunctions.toStringVector(keptDice));
        ioFunctions.showMessage("Current dice rolls: " + ioFunctions.toStringVector(diceRolls));
        return ioFunctions.wantsToStand();
    }

    public boolean wantsHelp() {
        return ioFunctions.wantsHelp();
    }

    public void inform(String message) {
        System.out.println(name + ": " + message);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}