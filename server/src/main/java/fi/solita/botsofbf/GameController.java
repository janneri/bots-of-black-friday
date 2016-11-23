package fi.solita.botsofbf;

import java.io.IOException;
import java.util.UUID;

import fi.solita.botsofbf.dto.RegisterResponse;
import fi.solita.botsofbf.dto.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GameController {

    @Autowired
    private GameEngine gameEngine;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public RegisterResponse register(@RequestBody Registration registration) {
        return gameEngine.registerPlayer(registration.playerName, registration.url);
    }

    @RequestMapping(value = "/{playerId}/say", method = RequestMethod.POST)
    public void say(@PathVariable String playerId, @RequestBody String message) {
        gameEngine.say(UUID.fromString(playerId), message);
    }

    @RequestMapping(value = "/restart", method = RequestMethod.POST)
    public void restart() {
        gameEngine.restart();
    }

    @RequestMapping(value = "/changemap", method = RequestMethod.POST)
    public void changeMap(@RequestBody String map) throws IOException {
        gameEngine.changeMap(map);
    }

}
