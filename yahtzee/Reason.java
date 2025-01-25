package com.yahtzee;

import java.util.List;

public class Reason {
    // The current dice values the player has at the moment
    private List<Integer> currentDice;

    // The category that the player is currently pursuing based on their dice
    private Category pursuedCategory;

    // The maximum possible score that can be achieved by pursuing this category
    private int maxScore;

    // The dice values the player would need to roll in order to achieve the maximum score in this category
    private List<Integer> rollToGetMax;

    // The minimum possible score that can be achieved by pursuing this category
    private int minScore;

    // The dice values the player would need to roll in order to achieve the minimum score in this category
    private List<Integer> rollToGetMin;

    // Constructor
    public Reason(List<Integer> currentDice, Category pursuedCategory, int maxScore, List<Integer> rollToGetMax, int minScore, List<Integer> rollToGetMin) {
        this.currentDice = currentDice;
        this.pursuedCategory = pursuedCategory;
        this.maxScore = maxScore;
        this.rollToGetMax = rollToGetMax;
        this.minScore = minScore;
        this.rollToGetMin = rollToGetMin;
    }

    // Getters
    public List<Integer> getCurrentDice() {
        return currentDice;
    }

    public Category getPursuedCategory() {
        return pursuedCategory;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public List<Integer> getRollToGetMax() {
        return rollToGetMax;
    }

    public int getMinScore() {
        return minScore;
    }

    public List<Integer> getRollToGetMin() {
        return rollToGetMin;
    }
}