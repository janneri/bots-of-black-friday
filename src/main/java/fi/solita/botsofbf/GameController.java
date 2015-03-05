package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;


@RestController
public class GameController {

    @Autowired
    private GameEngine gameEngine;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public RegisterResponse register(@RequestBody Registration registration) {
        return gameEngine.registerPlayer(registration.playerName, registration.url);
    }

    @RequestMapping("/map")
    public @ResponseBody Map getMap() throws IOException {
       return Map.fromFile("classpath://small_map.txt");
        // classpath?
    }

    @RequestMapping(value = "/{playerId}/say", method = RequestMethod.POST)
    public void say(@PathVariable String playerId, @RequestBody String message) {
        gameEngine.say(UUID.fromString(playerId), message);
    }

    @RequestMapping(value = "/{playerId}/move", method = RequestMethod.PUT)
    public @ResponseBody GameState move(@PathVariable String playerId, @RequestBody Move move) {
        return gameEngine.movePlayer(UUID.fromString(playerId), move);
    }

}
