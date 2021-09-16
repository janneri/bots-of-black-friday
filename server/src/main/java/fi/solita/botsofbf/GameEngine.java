package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public final class GameEngine {

    public static final int PAUSE_BETWEEN_ROUNDS_MILLIS = 1000;

    private UiClient uiClient;
    private GameState currentState;
    private Queue<Player> registeredPlayers = new ConcurrentLinkedQueue<>();
    private ConcurrentHashMap<UUID, Move> currentMoves = new ConcurrentHashMap<>();

    @Autowired
    public GameEngine(GameState initialGameState, UiClient uiClient) {
        this.currentState = initialGameState;
        this.uiClient = uiClient;
    }

    public RegisterResponse registerPlayer(String playerName, String url) {
        currentState.throwIfNameReserved(playerName);
        Position randomValidPosition = currentState.map.randomFloorPosition();
        Player player = Player.create(playerName, url, randomValidPosition);
        registeredPlayers.add(player);
        System.out.println("Registered " + playerName + " " + url);
        return new RegisterResponse(player, currentState);
    }

    public void say(UUID playerId, String message) {
        uiClient.sendChatMessageToUi(currentState.getPlayer(playerId), message);
    }

    public void tick() {
        for (Player p : registeredPlayers) {
            currentState = currentState.addPlayer(p);
        }
        registeredPlayers = new ConcurrentLinkedQueue<>();
        playRound();
        endRound();
    }

    private void playRound() {
        for ( Player player : currentState.players ) {
            currentState = playTurn(player, currentState);
        }
    }

    public void registerMove(UUID playerId, Move move) {
        currentMoves.put(playerId, move);
    }

    private GameState playTurn(Player player, GameState fromState) {
        GameState newGameState;
        try {
            Move move = currentMoves.get(player.id);
            newGameState = fromState.movePlayer(player.id, move);
            uiClient.notifyUi(player.name + " moved", newGameState);
        } catch (Exception e) {
            System.out.println(player.name + " is fucking up: " + e.getMessage());
            newGameState = fromState.addInvalidMove(player);
            uiClient.notifyUi(player.name + " is fucking up: " + e.getMessage(), currentState);
        }

        return newGameState.removeDeadPlayers();
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

    public GameState getCurrentState() {
        return currentState;
    }

}
