package com.yahtzee;

import java.util.*;
import java.util.stream.Collectors;

// Class to hold the points, winner, and round of a scorecard entry
class ScoreCardEntry {
    private final int points;
    private final Player winner;
    private final int round;

    public ScoreCardEntry(int points, Player winner, int round) {
        this.points = points;
        this.winner = winner;
        this.round = round;
    }

    public int getPoints() {
        return points;
    }

    public Player getWinner() {
        return winner;
    }

    public int getRound() {
        return round;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ScoreCardEntry that = (ScoreCardEntry) obj;
        return points == that.points && round == that.round && Objects.equals(winner, that.winner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points, winner, round);
    }
}

public class ScoreCard {
    private final Map<Category, Optional<ScoreCardEntry>> scoreCard;

    // Default constructor that initializes the scorecard with empty values for all
    // categories
    public ScoreCard() {
        this.scoreCard = initializeScoreCard();
    }

    // Constructor with scoreCard parameter
    public ScoreCard(Map<Category, Optional<ScoreCardEntry>> scoreCard) {
        this.scoreCard = scoreCard;
    }

    // Static method to deserialize a scorecard from a string representation
    public static ScoreCard deserialize(String serial, Player human, Player computer) {
        List<String> lines = Arrays.asList(serial.split("\n"));

        int categoryIndex = 0;
        Map<Category, Optional<ScoreCardEntry>> scoreCard = new LinkedHashMap<>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.equals("0")) {
                scoreCard.put(Category.values()[categoryIndex], Optional.empty());
                categoryIndex++;
                continue;
            }

            List<String> parts = Arrays.asList(line.split(" "));

            if (parts.size() != 3) {
                continue;
            }

            Category category = Category.values()[categoryIndex];

            int points = Integer.parseInt(parts.get(0));
            Player winner = parts.get(1).equals("Human") ? human : computer;
            int round = Integer.parseInt(parts.get(2));

            scoreCard.put(category, Optional.of(new ScoreCardEntry(points, winner, round)));
            categoryIndex++;
        }

        return new ScoreCard(scoreCard);
    }

    // Method to serialize the scorecard to a string representation
    public String serialize() {
        
        StringBuilder serial = new StringBuilder();
        
        
        for (Map.Entry<Category, Optional<ScoreCardEntry>> entry : scoreCard.entrySet()) {
            if (entry.getValue().isPresent()) {
                ScoreCardEntry scoreCardEntry = entry.getValue().get();
                serial.append(scoreCardEntry.getPoints()).append(" ")
                        .append(scoreCardEntry.getWinner().getName()).append(" ")
                        .append(scoreCardEntry.getRound()).append("\n");
            } else {
                serial.append("0\n");
            }
        }
        
        return serial.toString();
    }
 
    // Copy constructor
    public ScoreCard(ScoreCard other) {
        this.scoreCard = other.scoreCard;
    }

    // Copy assignment operator
    public ScoreCard assign(ScoreCard other) {
        if (this != other) {
            return new ScoreCard(other);
        }
        return this;
    }

    // Equality operator to compare two ScoreCard objects
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ScoreCard scoreCard1 = (ScoreCard) obj;
        return Objects.equals(scoreCard, scoreCard1.scoreCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scoreCard);
    }

    // Adds a score entry to the specified category with points, winner, and round,
    // returns updated scorecard
    // public ScoreCard addEntry(Category category, int points, Player winner, int
    // round) {
    // if (scoreCard.get(category).isPresent()) {
    // throw new IllegalArgumentException("Category already has a score card
    // entry");
    // }

    // Map<Category, Optional<ScoreCardEntry>> newScoreCard = new
    // HashMap<>(scoreCard);
    // newScoreCard.put(category, Optional.of(new ScoreCardEntry(points, winner,
    // round)));
    // return new ScoreCard(newScoreCard);
    // }

    public ScoreCard addEntry(Category category, int points, Player winner, int round) {
        if (scoreCard.get(category).isPresent()) {
            throw new IllegalArgumentException("Category already has a score card entry");
        }

        Map<Category, Optional<ScoreCardEntry>> newScoreCard = new LinkedHashMap<>(scoreCard);
        newScoreCard.put(category, Optional.of(new ScoreCardEntry(points, winner, round)));
        return new ScoreCard(newScoreCard);
    }

    // Adds an entry automatically for the max scoring category based on dice roll
    public ScoreCard addEntry(int round, Player winner, List<Integer> dice) {
        Optional<Category> maxCategory = getMaxScoringCategory(dice);
        if (!maxCategory.isPresent()) {
            return this;
        }
        return addEntry(maxCategory.get(), Category.getScore(dice, maxCategory.get()), winner, round);
    }

    // Finds the best scoring category based on dice roll
    public Optional<Category> getMaxScoringCategory(List<Integer> dice) {
        List<Category> openCategories = getOpenCategories();
        List<Category> applicableCategories = Category.getApplicableCategories(dice);
        List<Category> assignableCategories = helperFunctions.intersection(openCategories, applicableCategories);

        if (assignableCategories.isEmpty()) {
            return Optional.empty();
        }

        Map<Category, Integer> categoryScores = new HashMap<>();
        for (Category category : assignableCategories) {
            int score = Category.getScore(dice, category);
            categoryScores.put(category, score);
        }

        Category maxCategory = Category.ONES;
        int maxScore = 0;
        for (Map.Entry<Category, Integer> entry : categoryScores.entrySet()) {
            if (entry.getValue() >= maxScore) {
                maxScore = entry.getValue();
                maxCategory = entry.getKey();
            }
        }

        return Optional.of(maxCategory);
    }

    // Checks if the scorecard is completely filled
    public boolean isFull() {
        for (Map.Entry<Category, Optional<ScoreCardEntry>> entry : scoreCard.entrySet()) {
            if (!entry.getValue().isPresent()) {
                return false;
            }
        }
        return true;
    }

    // Returns a list of unfilled categories
    public List<Category> getOpenCategories() {
        List<Category> openCategories = new ArrayList<>();
        for (Map.Entry<Category, Optional<ScoreCardEntry>> entry : scoreCard.entrySet()) {
            if (!entry.getValue().isPresent()) {
                openCategories.add(entry.getKey());
            }
        }

        return helperFunctions.reversed(openCategories);
    }

    // Returns a list of possible categories based on dice roll
    public List<Category> getPossibleCategories(List<Integer> dice) {
        List<Category> openCategories = getOpenCategories();

        List<Category> possibleCategories = new ArrayList<>();
        for (Category category : openCategories) {
            if (Category.isPossibleCategory(dice, category)) {
                possibleCategories.add(category);
            }
        }

        return possibleCategories;
    }

    // Gets the total score for a given player by summing their points
    public int getPlayerScore(Player player) {
        int totalScore = 0;
        for (Map.Entry<Category, Optional<ScoreCardEntry>> entry : scoreCard.entrySet()) {
            if (entry.getValue().isPresent() && entry.getValue().get().getWinner().equals(player)) {
                totalScore += entry.getValue().get().getPoints();
            }
        }
        return totalScore;
    }

    // Returns a map of player scores
    public Map<Player, Integer> getPlayerScores(List<Player> players) {
        Map<Player, Integer> playerScores = new HashMap<>();
        for (Player player : players) {
            int playerScore = getPlayerScore(player);
            playerScores.put(player, playerScore);
        }
        return playerScores;
    }

    // Determines the winner by comparing player scores, returns empty if the
    // scorecard is not full
    public Optional<Player> getWinner() {
        if (!isFull()) {
            return Optional.empty();
        }

        List<Player> players = getPlayers();
        Map<Player, Integer> playerScores = getPlayerScores(players);

        int maxScore = 0;
        Player winner = null;
        for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                winner = entry.getKey();
            }
        }

        return Optional.ofNullable(winner);
    }

    // Checks if the game is a draw
    public boolean isDraw() {
        if (!isFull()) {
            return false;
        }

        List<Player> players = getPlayers();
        Map<Player, Integer> playerScores = getPlayerScores(players);

        int maxScore = 0;
        for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
            }
        }

        int count = 0;
        for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
            if (entry.getValue() == maxScore) {
                count++;
            }
        }

        return count > 1;
    }

    // Get a list of all players who have entries in the scorecard
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        for (Map.Entry<Category, Optional<ScoreCardEntry>> entry : scoreCard.entrySet()) {
            if (entry.getValue().isPresent() && !players.contains(entry.getValue().get().getWinner())) {
                players.add(entry.getValue().get().getWinner());
            }
        }
        return players;
    }

    // Generate a formatted string representation of the scorecard
    public String getString() {
        StringBuilder scoreCardString = new StringBuilder();
        scoreCardString.append(String.format("%-20s%-10s%-15s%-10s%n", "Category", "Round", "Winner", "Points"));
        scoreCardString.append("-".repeat(50)).append("\n");
        for (Map.Entry<Category, Optional<ScoreCardEntry>> entry : scoreCard.entrySet()) {
            scoreCardString.append(String.format("%-20s", Category.CATEGORY_NAMES.get(entry.getKey())));
            if (entry.getValue().isPresent()) {
                ScoreCardEntry scoreCardEntry = entry.getValue().get();
                scoreCardString.append(String.format("%-10s%-15s%-10s%n",
                        scoreCardEntry.getRound(),
                        scoreCardEntry.getWinner().getName(),
                        scoreCardEntry.getPoints()));
            } else {
                scoreCardString.append(String.format("%-10s%-15s%-10s%n", "-", "-", "-"));
            }
        }
        return scoreCardString.toString();
    }

    // Initialize the scorecard with all categories set to empty (no entries yet)
    // private static Map<Category, Optional<ScoreCardEntry>> initializeScoreCard()
    // {
    // Map<Category, Optional<ScoreCardEntry>> tempScoreCard = new HashMap<>();
    // for (Category category : Category.values()) {
    // tempScoreCard.put(category, Optional.empty());
    // }
    // return tempScoreCard;
    // }
    private static Map<Category, Optional<ScoreCardEntry>> initializeScoreCard() {
        Map<Category, Optional<ScoreCardEntry>> tempScoreCard = new LinkedHashMap<>();
        for (Category category : Category.values()) {
            tempScoreCard.put(category, Optional.empty());
        }
        return tempScoreCard;
    }

    // Static method to get the score for a given dice roll and category
    public static int getScore(List<Integer> dice, Category category) {
        // Implement the logic to calculate the score based on the dice roll and
        // category
        return Category.getScore(dice, category);
    }

    // Static method to check if a given dice roll is possible for a category
    public static boolean isPossibleCategory(List<Integer> dice, Category category) {
        // Implement the logic to check if the dice roll is possible for the category
        return Category.isPossibleCategory(dice, category);
    }
}
