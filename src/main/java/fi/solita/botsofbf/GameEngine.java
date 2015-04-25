package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class GameEngine {

    public static final int ROUND_DURATION_MILLIS = 500;

    @Autowired
    private PlayerClient playerClient;

    @Autowired
    private UiClient uiClient;

    private GameState currentState;

    public GameEngine() {
        currentState = new GameState(Map.createIkea());
    }

    public RegisterResponse registerPlayer(String playerName, String url) {
        Position randomValidPosition = currentState.map.randomPosition();
        Player player = Player.create(playerName, url, randomValidPosition);
        currentState = currentState.addPlayer(player);
        System.out.println("Registered " + playerName + " " + url);
        uiClient.notifyUi(playerName + " registered", currentState);
        return new RegisterResponse(player, currentState);
    }

    public void say(UUID playerId, String message) {
        uiClient.sendChatMessageToUi(currentState.getPlayer(playerId), message);
    }

    public void tick() {
        playRound();
        endRound();
    }

    private void playRound() {
        int timeout = ROUND_DURATION_MILLIS / (currentState.players.size() == 0 ? 1 : currentState.players.size());

        for ( Player player : currentState.players ) {
            System.out.println("Notify player " + player.name);

            try {
                Move move = playerClient.askMoveFromPlayer(player, currentState, timeout);
                currentState = currentState.movePlayer(player.id, move);
                uiClient.notifyUi(player.name + " moved", currentState);
            } catch (Exception e) {
                System.out.println(player.name + " is fucking up: " + e.getMessage());
                currentState = currentState.addInvalidMove(player);
                uiClient.notifyUi(player.name + " is fucking up: " + e.getMessage(), currentState);
            }

            currentState = currentState.removeDeadPlayers();
        }
    }

    private void endRound() {
        currentState = currentState.newRound();
        currentState = currentState.spawnItems();
        uiClient.notifyUi("starting new round", currentState);
    }

    public void restart() {
        currentState = new GameState(currentState.map);
    }

    public void changeMap(String map) {
        currentState = new GameState(Map.readMapFromFile(map));
    }
}
