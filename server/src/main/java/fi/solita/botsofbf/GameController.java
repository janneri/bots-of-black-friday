package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.GameState;
import fi.solita.botsofbf.dto.Move;
import fi.solita.botsofbf.dto.RegisterResponse;
import fi.solita.botsofbf.dto.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;


@RestController
public class GameController {

    @Autowired
    private GameEngine gameEngine;

    @PostMapping(value = "/register")
    public RegisterResponse register(@RequestBody Registration registration) {
        return gameEngine.registerPlayer(registration.playerName, registration.url);
    }

    @PostMapping(value = "/{playerId}/say")
    public void say(@PathVariable String playerId, @RequestBody String message) {
        gameEngine.say(UUID.fromString(playerId), message);
    }

    @PutMapping(value = "/{playerId}/move")
    public void registerMove(@PathVariable String playerId, @RequestBody Move move) {
        gameEngine.registerMove(UUID.fromString(playerId), move);
    }

    @GetMapping(value = "/gamestate")
    public GameState getCurrentGameState() {
        return gameEngine.getCurrentState();
    }

    @PostMapping(value = "/restart")
    public void restart() {
        gameEngine.restart();
    }

    @PostMapping(value = "/changemap")
    public void changeMap(@RequestBody String map) throws IOException {
        gameEngine.changeMap(map);
    }

}
