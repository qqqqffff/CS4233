package escape.observers;

import escape.required.GameObserver;
import escape.required.GameStatus;

import java.util.*;

public class GameplayObserver implements GameObserver {
    private GameStatus.MoveResult gameStatus = GameStatus.MoveResult.NONE;
    private final Map<String, Integer> playerScores;
    private final Integer maxScore;
    public GameplayObserver(String[] players, Integer maxScore){
        this.maxScore = maxScore;

        playerScores = new HashMap<>();
        Arrays.stream(players).forEach(player -> playerScores.put(player, 0));
    }
    public String[] getPlayers(){
        return playerScores.keySet().toArray(String[]::new);
    }
    public void updatePlayerScore(String player, int additionalScore){
        playerScores.put(player, playerScores.get(player) + additionalScore);
        if(maxScore != null && playerScores.get(player) >= maxScore) notify(player);
    }
    public int getPlayerScore(String player){
        return playerScores.get(player);
    }
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
    public GameStatus.MoveResult getGameStatus(){
        return this.gameStatus;
    }
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

    @Override
    public void notify(String message, Throwable cause) {
        System.out.println("Error - " + cause.getMessage() + ": " + message);
    }
}
