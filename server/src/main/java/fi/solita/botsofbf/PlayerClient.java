package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.GameState;
import fi.solita.botsofbf.dto.Move;
import fi.solita.botsofbf.dto.Player;
import fi.solita.botsofbf.events.GameStateChanged;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Repository
public class PlayerClient {

    private static final int BOT_ANSWER_TIMEOUT_MILLIS = 2500;

    private final RestTemplate restTemplate = new RestTemplateBuilder()
                    .setConnectTimeout(Duration.ofMillis(BOT_ANSWER_TIMEOUT_MILLIS))
                    .setReadTimeout(Duration.ofMillis(BOT_ANSWER_TIMEOUT_MILLIS))
                    .build();

    public Move askMoveFromPlayer(Player player, GameState currentState) {
        final HttpEntity<GameStateChanged> gameStateChangedHttpEntity =
                new HttpEntity<>(GameStateChanged.create("new turn", currentState, player));

        final ResponseEntity<Move> response = restTemplate.postForEntity(
                player.url, gameStateChangedHttpEntity, Move.class);

        return response.getBody();
    }

}
