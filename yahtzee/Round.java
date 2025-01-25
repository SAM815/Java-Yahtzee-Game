package com.yahtzee;

import java.util.*;

public class Round {
    // Function to play a round of Yahtzee
    public static ScoreCard playRound(int roundNumber, ScoreCard scoreCard, List<Player> players) {
        Map<Player, Integer> playerScores = scoreCard.getPlayerScores(players);
        Queue<Player> playerQueue = getPlayerQueue(playerScores);
        System.out.println("Player Queue: " + playerQueue);
        

        ScoreCard currentScoreCard = scoreCard;

        boolean roundOver = playerQueue.isEmpty() || scoreCard.isFull();
        while (!roundOver) {
            Player player = playerQueue.poll();

            System.out.println(currentScoreCard.getString());

            System.out.println("It's " + player.getName() + "'s turn.");

            List<Integer> dice = Turn.playTurn(player, scoreCard);

            Optional<Category> scoredCategory = scoreCard.getMaxScoringCategory(dice);

            // if (scoredCategory.isPresent()) {
            //     System.out.println(player.getName() + " scored " + ScoreCard.getScore(dice, scoredCategory.get()) +
            //             " points in the " + Category.CATEGORY_NAMES[scoredCategory.get().ordinal()] + " category.\n\n");
            // }
            if (scoredCategory.isPresent()) {
                String categoryName = Category.CATEGORY_NAMES.get(scoredCategory.get());
                int score = ScoreCard.getScore(dice, scoredCategory.get());
                
                System.out.println(player.getName() + " scored " + score + 
                        " points in the " + categoryName + " category.\n\n");
            }
            

            currentScoreCard = currentScoreCard.addEntry(roundNumber, player, dice);

            roundOver = playerQueue.isEmpty() || currentScoreCard.isFull();
        }

        System.out.println("Round ends");

        return currentScoreCard;
    }

    // // Function to get a queue of players, where the player with the lowest score goes first
    // private static Queue<Player> getPlayerQueue(Map<Player, Integer> playerScores) {
    //     //Queue<Player> playerQueue;
    //     Queue<Player> playerQueue = new LinkedList<>();

    //     Player player1 = playerScores.entrySet().iterator().next().getKey();
    //     Player player2 = playerScores.entrySet().iterator().next().getKey();

    //     int player1Score = playerScores.get(player1);
    //     int player2Score = playerScores.get(player2);

    //     if (player1Score == player2Score) {
    //         if (player1Score == 0) {
    //             System.out.println("Determining who goes first by rolling a die.");
    //         } else {
    //             System.out.println("Both players have a score of " + player1Score + ". Conducting a tie breaker.");
    //         }
    //         return queueFromTieBreaker(player1, player2);
    //     }

    //     return enqueuePlayersByScore(playerScores);
    // }
    private static Queue<Player> getPlayerQueue(Map<Player, Integer> playerScores) {
        Queue<Player> playerQueue = new LinkedList<>();
    
        Iterator<Map.Entry<Player, Integer>> iterator = playerScores.entrySet().iterator();
        Player player1 = iterator.next().getKey();
        Player player2 = iterator.next().getKey();
    
        int player1Score = playerScores.get(player1);
        int player2Score = playerScores.get(player2);
    
        if (player1Score == player2Score) {
            if (player1Score == 0) {
                System.out.println("Determining who goes first by rolling a die.");
            } else {
                System.out.println("Both players have a score of " + player1Score + ". Conducting a tie breaker.");
            }
            return queueFromTieBreaker(player1, player2);
        }
    
        return enqueuePlayersByScore(playerScores);
    }

    // Function to resolve a tie-breaker by rolling a die or other tie-breaking logic
    private static Queue<Player> queueFromTieBreaker(Player player1, Player player2) {
        Queue<Player> playerQueue = new LinkedList<>();

        Player humanPlayer = player1.getName().equals("Human") ? player1 : player2;
        Player computerPlayer = player1.getName().equals("Computer") ? player1 : player2;

        if (ioFunctions.humanWonTieBreaker()) {
            playerQueue.add(humanPlayer);
            playerQueue.add(computerPlayer);
        } else {
            playerQueue.add(computerPlayer);
            playerQueue.add(humanPlayer);
        }

        return playerQueue;
    }

    // Function to enqueue players based on their scores in ascending order
    private static Queue<Player> enqueuePlayersByScore(Map<Player, Integer> playerScores) {
        List<Map.Entry<Player, Integer>> playerScoreList = new ArrayList<>(playerScores.entrySet());

        playerScoreList.sort(Map.Entry.comparingByValue());

        Queue<Player> playerQueue = new LinkedList<>();
        for (Map.Entry<Player, Integer> entry : playerScoreList) {
            playerQueue.add(entry.getKey());
        }

        return playerQueue;
    }
}