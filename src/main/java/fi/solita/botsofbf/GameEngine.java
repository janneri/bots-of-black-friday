package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.*;
import fi.solita.botsofbf.events.GameStateChanged;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import fi.solita.botsofbf.dto.Move;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.UUID;

@Component
public final class GameEngine {

    private GameState currentState = new GameState();

    private GameEngine() {
        // todo pois
        //currentState = currentState.addPlayer(Player.create("foo", "localhost:3000/testapi", new Position(100, 100, currentState.map)))
          //      .addPlayer(Player.create("bar", "localhost:3000/testapi", new Position(200, 200, currentState.map)))
            //    .addPlayer(Player.create("plaa", "localhost:3000/testapi", new Position(300, 300, currentState.map)));
    }

    public RegisterResponse registerPlayer(String playerName, String url) {
        Position randomValidPosition = Map.randomPosition(currentState.map); // TODO
        Player player = Player.create(playerName, url, randomValidPosition);
        currentState = currentState.addPlayer(player);

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
        notifyUi(currentState.getPlayer(playerId).name + ": " + message, currentState);
    }

    public void startNewRound() {
        currentState = currentState.newRound();

        notifyPlayers();
        notifyUi("starting new round", currentState);
    }

    @Autowired
    private SimpMessagingTemplate template;

    private void notifyUi(String reason, GameState newGameState) {
        template.convertAndSend("/topic/events", GameStateChanged.create(reason, newGameState));
    }

    private void notifyPlayers() {
        final AsyncRestTemplate rt = new AsyncRestTemplate();
        for (Player p: currentState.players) {
            final HttpEntity<GameStateChanged> he = new HttpEntity<>(GameStateChanged.create("new turn", currentState));
            rt.postForEntity(p.url, he, Move.class);
        }
    }

    private void randomMoves() {
        final Move[] moveArray = {Move.LEFT, Move.RIGHT, Move.UP, Move.DOWN};

        for (Player p: currentState.players) {
            currentState = movePlayer(p.id, moveArray[new Random().nextInt(moveArray.length)]);
        }
    }

}
