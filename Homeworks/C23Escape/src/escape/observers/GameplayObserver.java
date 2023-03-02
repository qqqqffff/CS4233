package escape.observers;

import escape.required.GameObserver;
import escape.required.GameStatus;

import java.util.*;

public class GameplayObserver implements GameObserver {
    private GameStatus.MoveResult gameStatus = GameStatus.MoveResult.NONE;
    private final Map<String, Integer> playerScores;
    private final Integer maxScore;

    /**
     * Gameplay Observer:
     * Observer that oversees the general functions and actions of the program
     * Manages the Game finishing and the current score
     * @param players the list of players
     * @param maxScore the max score
     */
    public GameplayObserver(String[] players, Integer maxScore){
        this.maxScore = maxScore;

        playerScores = new HashMap<>();
        Arrays.stream(players).forEach(player -> playerScores.put(player, 0));
    }

    /**
     * Player Getter Method
     * @return the list of players
     */
    public String[] getPlayers(){
        return playerScores.keySet().toArray(String[]::new);
    }

    /**
     * Update Player Score Method:
     * Takes in a selected player and increments their score based on the final score
     * @param player player whose score is being updated
     * @param additionalScore score to be incremented
     */
    public void updatePlayerScore(String player, int additionalScore){
        playerScores.put(player, playerScores.get(player) + additionalScore);
        if(maxScore != null && playerScores.get(player) >= maxScore) notify(player);
    }

    /**
     * Score Getter Method
     * @param player the player whose score is being returned
     * @return the score of the player
     */
    public int getPlayerScore(String player){
        return playerScores.get(player);
    }

    /**
     * Final Game Status Parser:
     * Updates the Game Status when the game Terminates
     * @param player who ends the game
     */
    private void evaluateFinalGameStatus(String player){
        int playerScore = playerScores.get(player);
        gameStatus = GameStatus.MoveResult.WIN;
        for(String analysisPlayer : playerScores.keySet()){
            if(!analysisPlayer.equals(player)) {
                if (playerScore < playerScores.get(analysisPlayer)) {
                    gameStatus = GameStatus.MoveResult.LOSE;
                    break;
                } else if (playerScore == playerScores.get(analysisPlayer)) {
                    gameStatus = GameStatus.MoveResult.DRAW;
                }
            }
        }
    }

    /**
     * Getter Method for the Game Status
     * @return the current Game Status
     */
    public GameStatus.MoveResult getGameStatus(){
        return this.gameStatus;
    }

    /**
     * Notify Method (without error):
     * Method that ends the game when a player is passed
     * @param player that ends the game
     */
    @Override
    public void notify(String player) {
        evaluateFinalGameStatus(player);
        if(gameStatus.equals(GameStatus.MoveResult.WIN)) player += " is the WINNER!";
        else if(gameStatus.equals(GameStatus.MoveResult.LOSE)) player += " has LOST!";
        else if(gameStatus.equals(GameStatus.MoveResult.DRAW)) player += " has DRAWN!";
        System.out.println(player);

        System.out.println("Final Score:");
        StringBuilder finalScore = new StringBuilder();
        int newLineCounter = 0;
        for(String analysisPlayers : playerScores.keySet()){
            finalScore.append(analysisPlayers).append(" - ").append(playerScores.get(analysisPlayers));
            newLineCounter++;
            if(newLineCounter != playerScores.size()) finalScore.append('\n');
        }
        System.out.println(finalScore);
    }

    /**
     * Notify Method (with error):
     * Method that outputs any system errors that may occur
     * @param message that will play
     * @param cause error that is thrown
     */
    @Override
    public void notify(String message, Throwable cause) {
        System.out.println("Error - " + cause.getMessage() + ": " + message);
    }
}
