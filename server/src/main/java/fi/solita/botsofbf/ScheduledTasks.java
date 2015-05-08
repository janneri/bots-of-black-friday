package fi.solita.botsofbf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private GameEngine gameEngine;

    @Scheduled(fixedDelay = GameEngine.PAUSE_BETWEEN_ROUNDS_MILLIS)
    public void tick() {
        gameEngine.tick();
    }

}