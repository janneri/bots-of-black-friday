package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.GameState;
import fi.solita.botsofbf.dto.Move;
import fi.solita.botsofbf.dto.RegisterResponse;
import fi.solita.botsofbf.dto.Registration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class RestClient {

    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .rootUri("https://bots-of-black-friday.azurewebsites.net")
            .build();

    public GameState getGameState() {
        final ResponseEntity<GameState> response = restTemplate.getForEntity(
                "/gamestate", GameState.class);

        return response.getBody();
    }

    public void move(UUID playerId, Move move) {
        restTemplate.put("/{playerId}/move", move, playerId);
    }

    public RegisterResponse registerPlayer(Registration registration) {
        ResponseEntity<RegisterResponse> result =
                restTemplate.postForEntity("/register", registration, RegisterResponse.class);

        return result.getBody();
    }

}
