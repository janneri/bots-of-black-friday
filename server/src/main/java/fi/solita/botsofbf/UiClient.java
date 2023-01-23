package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.GameState;
import fi.solita.botsofbf.dto.Map;
import fi.solita.botsofbf.dto.Player;
import fi.solita.botsofbf.events.GameStateChanged;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class UiClient {

    @Autowired
    private SimpMessagingTemplate template;

    public void notifyUi(String reason, GameState newGameState) {
        template.convertAndSend("/topic/events", GameStateChanged.create(reason, newGameState, null));
    }

    public void resetMap(Map map) {
        template.convertAndSend("/topic/events", GameStateChanged.create(null, null, map));
    }

    public void sendChatMessageToUi(Player player, String message) {
        template.convertAndSend("/topic/chat", player.name + ": " + message);
    }

}
