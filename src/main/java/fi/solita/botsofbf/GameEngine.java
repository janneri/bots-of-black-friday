package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.*;
import fi.solita.botsofbf.events.GameStateChanged;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.UUID;

@Component
public final class GameEngine {

    public static final int ROUND_DURATION_MILLIS = 1000;

    private GameState currentState = new GameState();

    private GameEngine() {}

    public RegisterResponse registerPlayer(String playerName, String url) {
        Position randomValidPosition = currentState.map.randomPosition();
        Player player = Player.create(playerName, url, randomValidPosition, currentState.round);
        currentState = currentState.addPlayer(player);
        System.out.println("Registered " + playerName + " " + url);
        notifyUi(playerName + " registered", currentState);
        return new RegisterResponse(player, currentState);
    }

    public GameState movePlayer(UUID playerId, Move move) {
        // todo vuoro kasite: salli vain yksi siirto per vuoro
        // todo tapa pelaaja, jos se lahettaa liikaa siirtoja per vuoro
        currentState = currentState.movePlayer(playerId, move);

        notifyUi(currentState.getPlayer(playerId).name + " moved", currentState);
        return currentState;
    }

    public void say(UUID playerId, String message) {
        sendChatMessageToUi(currentState.getPlayer(playerId), message);
    }

    public void tick() {
        playRound();
        endRound();
    }

    private void playRound() {
        final RestTemplate restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory rf =
                (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();

        int timeout = ROUND_DURATION_MILLIS / (currentState.players.size() == 0 ? 1 : currentState.players.size());
        rf.setReadTimeout(timeout);
        rf.setConnectTimeout(timeout);

        for ( Player player : currentState.players ) {

            System.out.println("Notify player " + player.name);

            try {
                final HttpEntity<GameStateChanged> he =
                        new HttpEntity<>(GameStateChanged.create("new turn", currentState, player));

                ResponseEntity<Move> response = restTemplate.postForEntity(player.url, he, Move.class);
                System.out.println(player.name + " action: " + response.getBody());

                currentState = currentState.movePlayer(player.id, response.getBody());
                notifyUi(player.name + " moved", currentState);

            } catch (Exception e) {
                System.out.println(player.name + " is fucking up: " + e.getMessage());
                notifyUi(player.name + " is fucking up: " + e.getMessage(), currentState);
            }
        }
    }

    private void endRound() {
        currentState = currentState.newRound();
        currentState = currentState.spawnItems();
        notifyUi("starting new round", currentState);
    }

    public void restart() {
        currentState = new GameState();
    }

    @Autowired
    private SimpMessagingTemplate template;

    private void notifyUi(String reason, GameState newGameState) {
        template.convertAndSend("/topic/events", GameStateChanged.create(reason, newGameState, null));
    }

    private void sendChatMessageToUi(Player player, String message) {
        template.convertAndSend("/topic/chat", player.name + ": " + message);
    }
}
