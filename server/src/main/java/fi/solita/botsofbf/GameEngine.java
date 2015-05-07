package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class GameEngine {

    public static final int ROUND_DURATION_MILLIS = 500;

    private PlayerClient playerClient;
    private UiClient uiClient;
    private GameState currentState;

    @Autowired
    public GameEngine(GameState initialGameState, UiClient uiClient, PlayerClient playerClient) {
        this.currentState = initialGameState;
        this.playerClient = playerClient;
        this.uiClient = uiClient;
    }

    public RegisterResponse registerPlayer(String playerName, String url) {
        Position randomValidPosition = currentState.map.randomFloorPosition();
        Player player = Player.create(playerName, url, randomValidPosition);
        currentState = currentState.addPlayer(player);
        System.out.println("Registered " + playerName + " " + url);
        uiClient.notifyUi(playerName + " registered", currentState);
        return new RegisterResponse(player, currentState);
    }

    public void say(UUID playerId, String message) {
        uiClient.sendChatMessageToUi(currentState.getPlayer(playerId), message);
    }

    /**
     * Voipi olla, että tämä kannattaisi kirjoittaa sellaiseksi, että
     * vuoro kestää vähintään n millisekuntia, mutta voi kestää enemmänkin, jos joku botti on hidas.
     * Botille pitää kuitenkin joku timeout olla.
     * Saattaapi tulla ongelmaksi bottien tai verkon hitaus ja socket timeoutit.
     * todo parempi systeemi
     */
    public void tick() {
        int timeout = ROUND_DURATION_MILLIS / (currentState.players.size() == 0 ? 1 : currentState.players.size());
        playRound(timeout);
        endRound();
    }

    private void playRound(int timeout) {
        for ( Player player : currentState.players ) {
            currentState = playTurn(player, currentState, timeout);
        }
    }

    private GameState playTurn(Player player, GameState fromState, int timeout) {
        GameState newGameState = null;
        try {
            Move move = playerClient.askMoveFromPlayer(player, fromState, timeout);
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
