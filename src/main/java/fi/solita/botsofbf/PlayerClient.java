package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.GameState;
import fi.solita.botsofbf.dto.Move;
import fi.solita.botsofbf.dto.Player;
import fi.solita.botsofbf.events.GameStateChanged;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class PlayerClient {

    final RestTemplate restTemplate = new RestTemplate();

    public Move askMoveFromPlayer(Player player, GameState currentState, int timeout) {
        final HttpEntity<GameStateChanged> gameStateChangedHttpEntity =
                new HttpEntity<>(GameStateChanged.create("new turn", currentState, player));

        final ResponseEntity<Move> response = getRestTemplate(timeout)
                .postForEntity(player.url, gameStateChangedHttpEntity, Move.class);

        return response.getBody();
    }

    private RestTemplate getRestTemplate(int timeout) {
        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        rf.setReadTimeout(timeout);
        rf.setConnectTimeout(timeout);
        return restTemplate;
    }


}
