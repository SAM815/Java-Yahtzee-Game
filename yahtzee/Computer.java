package com.yahtzee;

import java.util.*;

class Computer extends Player {

    // Constructor: Initialize the player with the name "Computer"
    public Computer() {
        super("Computer");
    }

    // Override: Handles the dice roll for the computer's turn
    @Override
    public List<Integer> getDiceRoll(int numDice) {
        boolean humanWantsToRoll = ioFunctions.humanWantsToRollForComputer();
        if (humanWantsToRoll) {
            return super.getDiceRoll(numDice);
        }
        return Dice.rollDice(numDice);
    }

    // Generates all possible final rolls based on the kept dice
    public static List<List<Integer>> generatePossibleFinalRolls(List<Integer> keptDice) {
        List<List<Integer>> possibleRolls = helperFunctions.diceCombinations(5 - keptDice.size());
        List<List<Integer>> possibleFinalRolls = new ArrayList<>();

        for (List<Integer> roll : possibleRolls) {
            List<Integer> finalRoll = helperFunctions.concatenate(keptDice, roll);
            Collections.sort(finalRoll);
            possibleFinalRolls.add(finalRoll);
        }

        return possibleFinalRolls;
    }

    // Calculates scores for all possible final rolls based on open categories in
    // the scorecard
    public static List<Map.Entry<List<Integer>, Integer>> calculateScores(List<List<Integer>> finalRolls,
            ScoreCard scoreCard) {
        List<Map.Entry<List<Integer>, Integer>> scores = new ArrayList<>();
        List<Category> openCategories = scoreCard.getOpenCategories();

        for (List<Integer> finalRoll : finalRolls) {
            for (Category category : openCategories) {
                int score = ScoreCard.getScore(finalRoll, category);
                scores.add(new AbstractMap.SimpleEntry<>(finalRoll, score));
            }
        }

        scores.sort((a, b) -> b.getValue() - a.getValue());
        return scores;
    }

    // Finds the best roll by maximizing score and minimizing the difference from
    // the current roll
    public static List<Integer> findBestRoll(List<Map.Entry<List<Integer>, Integer>> scores, List<Integer> diceRolls) {
        int maxScore = scores.get(0).getValue();
        List<Integer> bestRoll = scores.get(0).getKey();
        int bestRollDiffSize = helperFunctions.difference(bestRoll, diceRolls).size();

        for (Map.Entry<List<Integer>, Integer> entry : scores) {
            if (entry.getValue() < maxScore)
                break;

            List<Integer> rollDiff = helperFunctions.difference(entry.getKey(), diceRolls);
            if (rollDiff.size() < bestRollDiffSize) {
                bestRoll = entry.getKey();
                bestRollDiffSize = rollDiff.size();
            }
        }

        return bestRoll;
    }

    // Determines which dice to keep based on the best roll
    public static List<Integer> determineDiceToKeep(List<Integer> bestRoll, List<Integer> diceRolls,
            List<Integer> keptDice) {
        List<Integer> diceNeeded = helperFunctions.difference(bestRoll, keptDice);
        return helperFunctions.intersection(diceNeeded, diceRolls);
    }

    // Finds the best roll the computer should aim for, based on the scorecard and
    // kept dice
    public static List<Integer> getBestRoll(ScoreCard scoreCard, List<Integer> keptDice) {
        List<List<Integer>> possibleFinalRolls = generatePossibleFinalRolls(keptDice);
        List<Map.Entry<List<Integer>, Integer>> scores = calculateScores(possibleFinalRolls, scoreCard);
        return findBestRoll(scores, keptDice);
    }

    // Determines which dice to keep for the next roll
    @Override
    public List<Integer> getDiceToKeep(ScoreCard scoreCard, List<Integer> diceRolls, List<Integer> keptDice) {
        List<Integer> finalRoll = helperFunctions.concatenate(keptDice, diceRolls);
        List<Category> openCategories = scoreCard.getOpenCategories();

        if (openCategories.contains(Category.YAHTZEE) && Category.isApplicableCategory(finalRoll, Category.YAHTZEE)) {
            return diceRolls;
        }
        if (openCategories.contains(Category.FIVE_STRAIGHT)) {
            List<Integer> uniqueDice = helperFunctions.getUnique(diceRolls);
            finalRoll = helperFunctions.concatenate(keptDice, uniqueDice);
            if (Category.isApplicableCategory(finalRoll, Category.FIVE_STRAIGHT)) {
                return uniqueDice;
            }
        }
        if (openCategories.contains(Category.FOUR_STRAIGHT)) {
            List<Integer> uniqueDice = helperFunctions.getUnique(diceRolls);
            finalRoll = helperFunctions.concatenate(keptDice, uniqueDice);
            if (Category.isApplicableCategory(finalRoll, Category.FOUR_STRAIGHT)) {
                return uniqueDice;
            }
        }

        List<List<Integer>> possibleFinalRolls = generatePossibleFinalRolls(keptDice);
        List<Map.Entry<List<Integer>, Integer>> scores = calculateScores(possibleFinalRolls, scoreCard);
        List<Integer> bestRoll = findBestRoll(scores, diceRolls);
        return determineDiceToKeep(bestRoll, diceRolls, keptDice);
    }

    // Returns potential categories that the computer could pursue based on the
    // current dice and scorecard
    @Override
    public Optional<Map<Category, Reason>> getCategoryPursuits(ScoreCard scoreCard, List<Integer> keptDice) {
        List<List<Integer>> possibleFinalRolls = generatePossibleFinalRolls(keptDice);
        List<Category> possibleCategories = scoreCard.getPossibleCategories(keptDice);
        //write code to print possible categories and possible final rolls
       

        Map<Category, Reason> categoryPursuits = new HashMap<>();

        for (Category category : possibleCategories) {
            
            int minScore = Integer.MAX_VALUE;
            int maxScore = Integer.MIN_VALUE;
            int minDiceDiff = Integer.MAX_VALUE;
            int maxDiceDiff = Integer.MAX_VALUE;
            List<Integer> rollToGetMax = new ArrayList<>();
            List<Integer> rollToGetMin = new ArrayList<>();


            for (List<Integer> roll : possibleFinalRolls) {
                
                int diceDiff = helperFunctions.difference(roll, keptDice).size();
                int score = Category.getScore(roll, category);
                
                
                if (score >= maxScore && diceDiff <= maxDiceDiff) {
                    maxScore = score;
                    rollToGetMax = helperFunctions.difference(roll, keptDice);
                    maxDiceDiff = diceDiff;
                }

                if (score< minScore && diceDiff <= minDiceDiff) {
                    if (minScore == 0)
                        continue;
                    minScore = score;
                    rollToGetMin = helperFunctions.difference(roll, keptDice);
                    minDiceDiff = diceDiff;
                }
            }

            categoryPursuits.put(category,
                    new Reason(keptDice, category, maxScore, rollToGetMax, minScore, rollToGetMin));
        }
        return Optional.of(categoryPursuits);
    }

    // Identifies the best category and strategy based on current kept dice and
    // scorecard
    @Override
    public Optional<Map.Entry<Category, List<Integer>>> getTarget(ScoreCard scoreCard, List<Integer> keptDice) {
        List<Integer> bestRoll = getBestRoll(scoreCard, keptDice);
        Optional<Category> category = scoreCard.getMaxScoringCategory(bestRoll);
        if (!category.isPresent()) {
            return Optional.empty();
        }
        return Optional
                .of(new AbstractMap.SimpleEntry<>(category.get(), helperFunctions.difference(bestRoll, keptDice)));
    }

    // Decides if the computer should stop rolling
    @Override
    public boolean wantsToStand(ScoreCard scoreCard, List<Integer> keptDice, List<Integer> diceRolls) {
        List<Integer> diceToKeep = getDiceToKeep(scoreCard, diceRolls, keptDice);
        return helperFunctions.unorderedEqual(diceToKeep, diceRolls);
    }

    // Determines if the computer wants assistance (returns false because the
    // computer operates autonomously)
    @Override
    public boolean wantsHelp() {
        return false;
    }

    // // Generates advice for a human player based on the current game state
    // public String getHelp(ScoreCard scoreCard, List<Integer> keptDice,
    // List<Integer> diceRolls) {
    // List<Integer> diceToKeep = getDiceToKeep(scoreCard, diceRolls, keptDice);
    // List<Integer> helpDice = helperFunctions.concatenate(keptDice, diceToKeep);
    // Optional<Map<Category, Reason>> categoryPursuits =
    // getCategoryPursuits(scoreCard, helpDice);
    // Optional<Map.Entry<Category, List<Integer>>> target = getTarget(scoreCard,
    // helpDice);

    // StringBuilder helpMessage = new StringBuilder("You should keep: " +
    // ioFunctions.toStringVector(diceToKeep) + " because:\n");

    // for (Map.Entry<Category, Reason> entry : categoryPursuits.get().entrySet()) {
    // Reason reason = entry.getValue();
    // if (reason.minScore == 0) {
    // helpMessage.append(" - You can get
    // ").append(Category.CATEGORY_NAMES[reason.pursuedCategory.ordinal()])
    // .append(" with a score of ").append(reason.maxScore).append(". For example,
    // by rolling ")
    // .append(helperFunctions.toStringVector(reason.rollToGetMax)).append("\n");
    // continue;
    // }
    // helpMessage.append(" - You can get
    // ").append(Category.CATEGORY_NAMES[reason.pursuedCategory.ordinal()])
    // .append(" with a minimum score of ").append(reason.minScore).append(" by
    // getting ")
    // .append(helperFunctions.toStringVector(reason.rollToGetMin)).append(" and a
    // maximum score of ")
    // .append(reason.maxScore).append(" by rolling
    // ").append(helperFunctions.toStringVector(reason.rollToGetMax)).append("\n");
    // }

    // helpMessage.append("\nConsidering this, your target should be to get ");
    // if (target.isPresent()) {
    // helpMessage.append(Category.CATEGORY_NAMES[target.get().getKey().ordinal()]).append(".
    // A way to do this would be to roll ")
    // .append(ioFunctions.toStringVector(target.get().getValue())).append(" in your
    // subsequent rolls.\n");
    // } else {
    // helpMessage.append("None\n");
    // }

    // if (wantsToStand(scoreCard, keptDice, diceRolls)) {
    // helpMessage.append("You should stand.\n");
    // } else {
    // helpMessage.append("Do not stand. You should keep rolling.\n");
    // }

    // if (diceToKeep.isEmpty()) {
    // helpMessage.append("Do not keep any dice. You should roll all the dice.\n");
    // } else {
    // helpMessage.append("You should keep the following dice before you roll:
    // ").append(ioFunctions.toStringVector(diceToKeep));
    // }

    // return helpMessage.toString();
    // }

    public String getHelp(ScoreCard scoreCard, List<Integer> keptDice, List<Integer> diceRolls) {
        List<Integer> diceToKeep = getDiceToKeep(scoreCard, diceRolls, keptDice);
        List<Integer> helpDice = helperFunctions.concatenate(keptDice, diceToKeep);
        Optional<Map<Category, Reason>> categoryPursuits = getCategoryPursuits(scoreCard, helpDice);
        Optional<Map.Entry<Category, List<Integer>>> target = getTarget(scoreCard, helpDice);

        StringBuilder helpMessage = new StringBuilder(
                "You should keep: " + ioFunctions.toStringVector(diceToKeep) + " because:\n");

        for (Map.Entry<Category, Reason> entry : categoryPursuits.get().entrySet()) {
            Reason reason = entry.getValue();
            
            if (reason.getMinScore() == 0) {
                helpMessage.append(" - You can get ").append(Category.CATEGORY_NAMES.get(reason.getPursuedCategory()))
                        .append(" with a score of ").append(reason.getMaxScore()).append(". For example, by rolling ")
                        .append(ioFunctions.toStringVector(reason.getRollToGetMax())).append("\n");
                continue;
            }
            helpMessage.append(" - You can get ").append(Category.CATEGORY_NAMES.get(reason.getPursuedCategory()))
                    .append(" with a minimum score of ").append(reason.getMinScore()).append(" by getting ")
                    .append(ioFunctions.toStringVector(reason.getRollToGetMin())).append(" and a maximum score of ")
                    .append(reason.getMaxScore()).append(" by rolling ")
                    .append(ioFunctions.toStringVector(reason.getRollToGetMax())).append("\n");
        }

        helpMessage.append("\nConsidering this, your target should be to get ");
        if (target.isPresent()) {
            helpMessage.append(Category.CATEGORY_NAMES.get(target.get().getKey()))
                    .append(". A way to do this would be to roll ")
                    .append(ioFunctions.toStringVector(target.get().getValue())).append(" in your subsequent rolls.\n");
        } else {
            helpMessage.append("None\n");
        }

        if (wantsToStand(scoreCard, keptDice, diceRolls)) {
            helpMessage.append("You should stand.\n");
        } else {
            helpMessage.append("Do not stand. You should keep rolling.\n");
        }

        if (diceToKeep.isEmpty()) {
            helpMessage.append("Do not keep any dice. You should roll all the dice.\n");
        } else {
            helpMessage.append("You should keep the following dice before you roll: ")
                    .append(ioFunctions.toStringVector(diceToKeep));
        }

        return helpMessage.toString();
    }
}