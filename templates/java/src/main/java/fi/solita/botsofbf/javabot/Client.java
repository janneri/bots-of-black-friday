package fi.solita.botsofbf.javabot;

import fi.solita.botsofbf.javabot.dto.GameState;
import fi.solita.botsofbf.javabot.dto.Move;
import fi.solita.botsofbf.javabot.dto.RegisterRequest;
import fi.solita.botsofbf.javabot.dto.RegisterResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class Client {

    final RestTemplate restTemplate;

    public Client(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GameState getGameState() {
        return restTemplate.getForEntity("/gamestate", GameState.class).getBody();
    }

    public RegisterResponse registerPlayer(String botName) {
        return restTemplate.postForEntity("/register", new RegisterRequest(botName), RegisterResponse.class).getBody();
    }

    public void registerMove(String botId, Move move) {
        restTemplate.put("/{botId}/move", move, botId);
    }
}



