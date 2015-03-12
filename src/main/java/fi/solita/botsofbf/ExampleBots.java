package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.Move;
import fi.solita.botsofbf.dto.Registration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@RestController
public class ExampleBots {

    private RegisterResponse_ leftie;
    private RegisterResponse_ zombie;
    private RegisterResponse_ hunter;

    @RequestMapping(value = "/startleftie", method = RequestMethod.POST)
    public void registerLeftie() {
        leftie = reg("leftie", "/leftie");
    }

    @RequestMapping(value = "/startzombie", method = RequestMethod.POST)
    public void registerZombie() {
        zombie = reg("zombie", "/zombie");
    }

    @RequestMapping(value = "/starthunter", method = RequestMethod.POST)
    public void registerHunter() {
        hunter = reg("hunter", "/hunter");
    }

    @RequestMapping(value = "/startrandom", method = RequestMethod.POST)
    public void registerRandomMovers(@RequestBody int num) {
        for (int i = 0; i < num; i++) {
            reg("random " + i, "/random");
        }
    }

    private RegisterResponse_ reg(String playerName, String url) {
        RestTemplate restTemplate = new RestTemplate();
        Registration registration = new Registration(playerName, "http://localhost:8080" + url);
        ResponseEntity<RegisterResponse_> result =
                restTemplate.postForEntity("http://localhost:8080/register", registration, RegisterResponse_.class);

        return result.getBody();
    }


    @RequestMapping(value = "/leftie", method = RequestMethod.POST)
    public @ResponseBody Move moveLeftie(@RequestBody GameStateChanged_ gameStateChanged) {
        System.out.println("leftie responding");
        return Move.LEFT;
    }

    @RequestMapping(value = "/zombie", method = RequestMethod.POST)
    public void moveZombie(@RequestBody GameStateChanged_ gameStateChanged) throws InterruptedException {
        System.out.println("zombie responding");
        Thread.sleep(5000);
        System.out.println("zombie woke up");
    }

    @RequestMapping(value = "/hunter", method = RequestMethod.POST)
    public @ResponseBody Move moveHunter(@RequestBody GameStateChanged_ gameStateChanged) throws InterruptedException {
        Player_ hunterInGame = gameStateChanged.gameState.players.stream().filter(p -> p.name.equals(hunter.player.name)).findFirst().get();
        Optional<Item_> huntedItem = gameStateChanged.gameState.items.stream()
                .sorted(Comparator.comparing((Item_ item) -> item.position.distance(hunterInGame.position))
                        .thenComparing(Comparator.comparing((Item_ item) -> item.position.x + item.position.y)))
                .findFirst();

        System.out.println("hunter is at " + hunterInGame.position + " and hunting " + huntedItem.get().position);

        if (huntedItem.isPresent()) {

            if ( hunterInGame.position.equals(huntedItem.get().position) ) {
                return Move.PICK;
            }
            if (hunterInGame.position.x > huntedItem.get().position.x) {
                return Move.LEFT;
            }
            else if (hunterInGame.position.x < huntedItem.get().position.x) {
                return Move.RIGHT;
            }
            else if (hunterInGame.position.y > huntedItem.get().position.y) {
                return Move.UP;
            }
            else if (hunterInGame.position.y < huntedItem.get().position.y) {
                return Move.DOWN;
            }
        }
        return null;
    }

    @RequestMapping(value = "/random", method = RequestMethod.POST)
    public Move moveRandom(@RequestBody GameState_ gameState) {
        return Move.values()[new Random().nextInt(Move.values().length)];
    }


    public static class RegisterResponse_ {
        public UUID id;
        public Player_ player;
        public GameState_ gameState;
    }

    public static class Player_ {
        public String name;
        public String url;
        public Position_ position;
        public int movedOnRound;
        public int score;
    }

    public static class GameState_ {
        public Map_ map;
        public Set<Player_> players;
        public Set<Item_> items;
        public int round;
    }

    public static class GameStateChanged_ {
        public String reason;
        public GameState_ gameState;
        public Player_ playerState;
    }

    public static class Item_ {
        public int price;
        public Position_ position;
    }

    public static class Map_ {
        public int width;
        public int height;
        public String cells;
    }

    public static class Position_ {
        public int x;
        public int y;

        public int distance(Position_ other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Position_ position_ = (Position_) o;

            if (x != position_.x) return false;
            if (y != position_.y) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }

        @Override
        public String toString() {
            return String.format("(%s,%s)", x, y);
        }
    }
}
