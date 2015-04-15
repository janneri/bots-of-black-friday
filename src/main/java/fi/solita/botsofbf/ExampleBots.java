package fi.solita.botsofbf;

import fi.solita.botsofbf.dto.Move;
import fi.solita.botsofbf.dto.Registration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@RestController
public class ExampleBots {

    private RegisterResponse_ leftie;
    private RegisterResponse_ zombie;

    @RequestMapping(value = "/startleftie", method = RequestMethod.POST)
    public void registerLeftie() {
        leftie = reg("leftie", "/leftie");
    }

    @RequestMapping(value = "/startzombie", method = RequestMethod.POST)
    public void registerZombie() {
        zombie = reg("zombie", "/zombie");
    }

    @RequestMapping(value = "/starthunters", method = RequestMethod.POST)
    public Set<RegisterResponse_> registerHunter(@RequestBody int num) {
        return IntStream.rangeClosed(1, num)
                .mapToObj(n -> reg("hunter " + n, "/hunter"))
                .collect(Collectors.toSet());

        /*
        for (int i = 0; i < num; i++) {
            RegisterResponse_ registerResponse = reg("hunter " + i, "/hunter");
            System.out.println("hunter " + i + " registered and got id " + registerResponse.id);
        }
        */
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
        Player_ hunterInGame = gameStateChanged.playerState;

        Optional<Item_> huntedItem = gameStateChanged.gameState.items.stream()
                .filter(i -> hunterInGame.money > i.price)
                .sorted(Comparator.comparing((Item_ item) -> item.position.distance(hunterInGame.position))
                        .thenComparing(Comparator.comparing((Item_ item) -> item.position.x + item.position.y)))
                .findFirst();

        System.out.println("hunter is at " + hunterInGame.position
                + " and hunting " + (huntedItem.isPresent() ? huntedItem.get().position : null));

        if (huntedItem.isPresent()) {
            Move nextMove = nextMoveToItem(hunterInGame.position, huntedItem.get().position, gameStateChanged.gameState.map);
            if ( nextMove != null ) {
                return nextMove;
            }
        }
        else {
            System.out.println(hunterInGame.name + " is running to exit.");
            return nextMoveToItem(hunterInGame.position, gameStateChanged.gameState.map.findExit(), gameStateChanged.gameState.map);
        }

        new RestTemplate().postForEntity(
                String.format("http://localhost:8080/%s/say", gameStateChanged.playerId),
                "Don't know what to do", Void.class);

        return Move.UP;
    }

    private Move nextMoveToItem(Position_ fromPos, Position_ toPos, Map_ map) {
        if ( fromPos.equals(toPos) ) {
            return Move.PICK;
        }
        if (fromPos.x > toPos.x &&
                map.isValidPosition(fromPos.left())) {
            return Move.LEFT;
        }
        else if (fromPos.x < toPos.x &&
                map.isValidPosition(fromPos.right()))  {
            return Move.RIGHT;
        }
        else if (fromPos.y > toPos.y &&
                map.isValidPosition(fromPos.up())) {
            return Move.UP;
        }
        else if (fromPos.y < toPos.y &&
                map.isValidPosition(fromPos.down())) {
            return Move.DOWN;
        }

        return null;
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
        public int money;
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
        public UUID playerId;
    }

    public static class Item_ {
        public int price;
        public Position_ position;
    }


    public static class Map_ {
        public int width;
        public int height;
        public List<String> tiles;

        public boolean isValidPosition(Position_ pos) {
            return pos.x >= 0 && pos.x <= width && pos.y >= 0 && pos.y <= height && !isWall(pos);
        }

        public boolean isWall(Position_ pos) {
            char tile = tiles.get(pos.y).charAt(pos.x);
            return tile == 'x';
        }

        public Position_ findExit() {
            for (int y = 0; y < tiles.size(); y++) {
                for (int x = 0; x < tiles.get(y).length(); x++) {
                    if (tiles.get(y).charAt(x) == 'o') {
                        return Position_.of(x, y);
                    }
                }
            }
            return null;
        }
    }

    public static final int step = 1;

    public static class Position_ {

        public int x;
        public int y;

        public static Position_ of(int x, int y) {
            Position_ pos = new Position_();
            pos.x = x;
            pos.y = y;
            return pos;
        }

        public Position_ left() {
            return Position_.of(x - step, y);
        }
        public Position_ right() {
            return Position_.of(x + step, y);
        }
        public Position_ up() {
            return Position_.of(x, y - step);
        }
        public Position_ down() {
            return Position_.of(x, y + step);
        }

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
