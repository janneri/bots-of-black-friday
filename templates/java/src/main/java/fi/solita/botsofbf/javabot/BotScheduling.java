package fi.solita.botsofbf.javabot;

import fi.solita.botsofbf.javabot.dto.GameState;
import fi.solita.botsofbf.javabot.dto.Move;
import fi.solita.botsofbf.javabot.dto.Player;
import fi.solita.botsofbf.javabot.dto.RegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BotScheduling {
    private static final Logger LOG = LoggerFactory.getLogger(BotScheduling.class);

    @Autowired Client client;

    private Bot myBot;

    @Scheduled(fixedRate = 700)
    private void playRound() {
        if (myBot == null) {
            String botName = "my-java-bot";
            RegisterResponse registerResponse = client.registerPlayer(botName);
            myBot = new Bot(botName, registerResponse.id(), registerResponse.map());
            return;
        }

        GameState gameState = client.getGameState();

        Optional<Player> myPlayer = myBot.findMe(gameState);
        if (myPlayer.isEmpty()) {
            LOG.info("My bot is dead or not there yet");
        }
        else {
            Move nextMove = myBot.resolveNextMove(gameState);
            LOG.info("My bot moving to {}", nextMove);
            client.registerMove(myBot.id, nextMove);
        }
    }

}
