package com.yahtzee;

import java.util.*;
import java.util.stream.Collectors;

class Game {
    private final ScoreCard scoreCard;
    private final int currentRound;
    private final List<Player> players;

    // Constructor: Initializes a Game object with a scorecard, round number, and list of players
    public Game(ScoreCard scoreCard, int currentRound, List<Player> players) {
        this.scoreCard = scoreCard;
        this.currentRound = currentRound;
        this.players = players;
    }

    // Copy constructor: Creates a copy of another Game object
    public Game(Game other) {
        this.scoreCard = other.scoreCard;
        this.currentRound = other.currentRound;
        this.players = other.players;
    }

    public ScoreCard getScoreCard() {
        return scoreCard;
    }
    
    public static Game deserialize(String serial) {
        System.out.println("Inside the Game deserialize \n");
        Human human = new Human();
        Computer computer = new Computer();
        List<Player> players = Arrays.asList(human, computer);
    
        List<String> lines = Arrays.asList(serial.split("\n"));
        System.out.println("Inside the Game deserialize now here \n");
    
        int roundNumber = 1;
        for (String line : lines) {
            if (line.startsWith("Round: ")) {
                roundNumber = Integer.parseInt(line.substring(7).trim());
                System.out.println("Round number: " + roundNumber);
                break;
            }
        }
    
        int scorecardStart = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("Scorecard:")) {
                scorecardStart = i + 1;
                break;
            }
        }
    
        List<String> scorecardLines = lines.subList(scorecardStart, lines.size());
        String scorecardSerial = String.join("\n", scorecardLines);
        System.out.println("Scorecard serial: " + scorecardSerial);
        ScoreCard scoreCard = ScoreCard.deserialize(scorecardSerial, human, computer);
    
        return new Game(scoreCard, roundNumber, players);
    }

    // Serialize: Converts the current game state into a string for saving
    public String serialize() {
        StringBuilder serial = new StringBuilder();
        serial.append("Round: ").append(currentRound).append("\n");
        
        serial.append("Scorecard:\n");
       
        serial.append(scoreCard.serialize()).append("\n");
        
        
        return serial.toString();
    }

    // Check if the game is over by checking if the scorecard is full
    public boolean isOver() {
        return scoreCard.isFull();
    }

    // Check if the game resulted in a draw (tie score)
    public boolean isDraw() {
        return scoreCard.isDraw();
    }

    // Play a round of the game and return the updated Game object
    public Game playRound() {
        if (isOver()) {
            System.out.println("The game is over!");
            return this;
        }

        System.out.println("Round " + currentRound);
        showScores();

        ScoreCard newScoreCard = Round.playRound(currentRound, scoreCard, players);
        

        System.out.println(newScoreCard.getString());

       
        Game result = new Game(newScoreCard, currentRound + 1, players);

        result.showScores();

        return result;
    }

    // Get the scores of all players from the scorecard
    public Map<Player, Integer> getPlayerScores() {
        return scoreCard.getPlayerScores(players);
    }

    // Show the scores of all players
    public void showScores() {
        System.out.println("Scores:");

        Map<Player, Integer> playerScores = getPlayerScores();

        for (Map.Entry<Player, Integer> playerScore : playerScores.entrySet()) {
            System.out.println(playerScore.getKey().getName() + ": " + playerScore.getValue());
        }
        System.out.println();
    }
}