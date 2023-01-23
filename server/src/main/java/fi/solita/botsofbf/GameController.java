package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
public class GameController {

    @Autowired
    private GameEngine gameEngine;

    @PostMapping(value = "/register")
    public RegisterResponse register(@RequestBody Registration registration) {
        return gameEngine.registerPlayer(registration.playerName);
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

    @GetMapping(value = "/map")
    public Map getMap() {
        return gameEngine.getCurrentState().map;
    }

    @PostMapping(value = "/restart")
    public void restart() {
        gameEngine.restart();
    }

    @PostMapping(value = "/changemap")
    public void changeMap(@RequestBody ChangeMap map) {
        gameEngine.changeMap(map.mapFileName);
    }

}
