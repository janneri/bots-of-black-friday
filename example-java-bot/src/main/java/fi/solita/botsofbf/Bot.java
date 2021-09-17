package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.GameMap;
import fi.solita.botsofbf.dto.Move;
import fi.solita.botsofbf.dto.Player;
import fi.solita.botsofbf.dto.Position;
import fi.solita.botsofbf.dto.Registration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


/* This is an example bot, when running the game in a mode where bots are clients. */
@RestController
@EnableScheduling
public class Bot {
    private RestClient restClient;
    private Map<UUID, Player> myBots = new HashMap<>();

    public Bot(RestClient restClient) {
        this.restClient = restClient;
    }

    @PostMapping(value = "/register")
    public void registerNewBot() {
        var botVersion = myBots.size();
        var playerName = "testbot " + botVersion;
        var registrationResponse = restClient.registerPlayer(new Registration(playerName));
        myBots.put(registrationResponse.id, registrationResponse.player);
    }

    @Scheduled(fixedDelay = 500)
    public void movePlayers() {
        if (myBots.size() == 0) {
            return;
        }

        var gameState = restClient.getGameState();
        var map = gameState.map;

        myBots.forEach((botId, bot) -> {
            // TODO examine gamestate to make a sensible move
            var currentPos = gameState.players.stream()
                    .filter(p -> p.name.equals(bot.name))
                    .map(p -> p.position)
                    .findFirst();

            if (currentPos.isEmpty()) {
                System.out.println("The server probably killed me because I'm not in the gamestate");
            }
            else {
                var randomMove = randomValidMove(currentPos.get(), map);
                restClient.move(botId, randomMove);
                System.out.println(bot.name + " moved " + randomMove + " from " + currentPos.get());
            }
        });
    }

    private Move randomValidMove(Position fromPos, GameMap map) {
        var move = randomMove();
        while (!map.isValidPosition(fromPos.move(move))) {
            move = randomMove();
        }
        return move;
    }

    private Move randomMove() {
        return Arrays.asList(Move.UP, Move.LEFT, Move.DOWN, Move.RIGHT).get(new Random().nextInt(4));
    }

}
