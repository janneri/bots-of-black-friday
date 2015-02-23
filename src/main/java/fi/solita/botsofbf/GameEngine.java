package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.*;
import fi.solita.botsofbf.events.GameStateChanged;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class GameEngine {

    private GameState currentState = new GameState();

    private GameEngine() {}

    public RegisterResponse registerPlayer(String playerName) {
        Position randomValidPosition = Position.ORIGIN; // TODO
        Player player = Player.create(playerName, randomValidPosition);
        currentState.addPlayer(player);

        notifyUi(playerName + " registered", currentState);
        return new RegisterResponse(player, currentState);
    }

    public GameState movePlayer(UUID playerId, Move move) {
        // todo vuoro kasite: salli vain yksi siirto per vuoro
        // todo tapa pelaaja, jos se lahettaa liikaa siirtoja per vuoro
        currentState.movePlayer(playerId, move);

        notifyUi(currentState.getPlayer(playerId).name + " moved", currentState);
        return currentState;
    }

    public void say(UUID playerId, String message) {
        notifyUi(currentState.getPlayer(playerId).name + ": " + message, currentState);
    }

    public void startNewRound() {
        currentState.newRound();
        notifyUi("starting new round", currentState);
    }

    @Autowired
    private SimpMessagingTemplate template;

    private void notifyUi(String reason, GameState newGameState) {
        template.convertAndSend("/topic/events", GameStateChanged.create(reason, newGameState));
    }

}
