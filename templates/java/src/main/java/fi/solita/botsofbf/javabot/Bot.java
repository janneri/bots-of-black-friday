package fi.solita.botsofbf.javabot;

import fi.solita.botsofbf.javabot.dto.*;

import java.util.Optional;

public class Bot {
    public final String botName;
    public final String id;
    public final Map map;

    public Bot(String botName, String id, Map map) {
        this.botName = botName;
        this.id = id;
        this.map = map;
    }

    public Optional<Player> findMe(GameState gameState) {
        return gameState.players().stream().filter(p -> p.name().equals(botName)).findFirst();
    }

    public Move resolveNextMove(GameState gameState) {
        // todo, calculate next move
        Position myCurrentPos = findMe(gameState).map(Player::position).orElseThrow();

        if (Math.random() > 0.5) {
            return Move.LEFT;
        }
        else {
            return Move.RIGHT;
        }
    }
}
