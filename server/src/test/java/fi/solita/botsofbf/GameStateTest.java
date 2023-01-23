package fi.solita.botsofbf;


import fi.solita.botsofbf.dto.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class GameStateTest {

    private List<String> mapLines = Arrays.asList(
            "halpahalli",
            "5",
            "xxxxxxx",
            "x__o__x ",
            "xxxxxxx");


    @Test
    public void invalidMoveThrows() {
        Player player = createPlayerToPos(1, 1);
        GameState state = new GameState(Map.createMapFromLines(mapLines))
                .addPlayer(player);

        assertMoveThrows(state, player, Move.UP);
        assertMoveThrows(state, player, Move.LEFT);
        assertMoveThrows(state, player, Move.DOWN);
    }

    @Test
    public void movingAroundDecreasesHealth() {
        Player player = createPlayerToPos(0, 0);
        GameState state = new GameState(Map.createMapFromLines(Arrays.asList("nimi", "5", "__o")))
                .addPlayer(player);

        final int healthDecreasedWhenAging = GameState.HEALTH_LOST_WHEN_AGED;

        assertEquals(100, state.getPlayer(player.id).health);

        Move nextMove = Move.RIGHT;
        for (int i = 0; i < GameState.AGING_MOVE_COUNT; i++) {
            state = state.movePlayer(player.id, nextMove);
            nextMove = nextMove == Move.RIGHT ? Move.LEFT : Move.RIGHT;
        }

        assertEquals(100 - healthDecreasedWhenAging, state.getPlayer(player.id).health);

        for (int i = 0; i < GameState.AGING_MOVE_COUNT; i++) {
            state = state.movePlayer(player.id, nextMove);
            nextMove = nextMove == Move.RIGHT ? Move.LEFT : Move.RIGHT;
        }

        assertEquals(100 - 2 * healthDecreasedWhenAging, state.getPlayer(player.id).health);

    }

    @Test
    public void invalidPickThrows() {
        Map map = Map.createMapFromLines(mapLines);
        Player player = createPlayerToMap(map);
        GameState state = new GameState(map).addPlayer(player);

        assertMoveThrows(state, player, Move.PICK); // no items to pick
    }

    @Test
    public void invalidUseThrows() {
        Map map = Map.createMapFromLines(mapLines);
        Player player = createPlayerToMap(map);
        GameState state = new GameState(map).addPlayer(player);

        assertMoveThrows(state, player, Move.USE); // no items to use
    }

    @Test
    public void validMoveMoves() {
        Player player = createPlayerToPos(1, 1);
        GameState state = new GameState(Map.createMapFromLines(mapLines))
                .addPlayer(player)
                .movePlayer(player.id, Move.RIGHT);

        assertEquals(Position.of(2, 1), state.getPlayer(player.id).position);
    }

    @Test
    public void moveToExitMovesActivePlayerToFinishedPlayers() {
        Player player = createPlayerToPos(2, 1);
        GameState state = new GameState(Map.createMapFromLines(mapLines))
                .addPlayer(player)
                .movePlayer(player.id, Move.RIGHT);

        assertEquals(0, state.players.size());
        assertEquals(1, state.finishedPlayers.size());
    }

    @Test
    public void finishedPlayerListContainsOneRowPerPlayerName() {
        Player p1 = Player.create("p1", Position.of(2, 1));
        Player p2 = Player.create("p1", Position.of(2, 1));
        Player p3 = Player.create("p2", Position.of(2, 1));
        Item item = Item.create(100, 1, Position.of(2, 1));

        GameState state = new GameState(Map.createMapFromLines(mapLines))
                .addItem(item)
                .addPlayer(p1)
                .movePlayer(p1.id, Move.RIGHT)
                .addPlayer(p2)
                .movePlayer(p2.id, Move.PICK)
                .movePlayer(p2.id, Move.RIGHT)
                .addPlayer(p3)
                .movePlayer(p3.id, Move.RIGHT);

        assertEquals(0, state.players.size());
        assertEquals(2, state.finishedPlayers.size());
        System.out.println(state.finishedPlayers);
        assertEquals("p1", state.finishedPlayers.get(0).name);
        assertEquals(100, state.finishedPlayers.get(0).score);
        assertEquals("p2", state.finishedPlayers.get(1).name);
        assertEquals(0, state.finishedPlayers.get(1).score);
    }

    @Test
    public void pickItemIncreasesPlayerScoreAndDecreasesMoneyLeft() {
        Map map = Map.createMapFromLines(mapLines);
        Player player = createPlayerToMap(map);
        Item item = Item.create(100, 1, player.position);

        GameState state = new GameState(map)
                .addPlayer(player)
                .addItem(item)
                .movePlayer(player.id, Move.PICK);

        Player playerAfterMove = state.getPlayer(player.id);
        assertEquals(0, state.items.size());
        assertEquals(player.money - item.discountedPrice, playerAfterMove.money);
        assertEquals(0, playerAfterMove.timeInState);
        assertEquals(1, playerAfterMove.actionCount);
        assertEquals(player.position, playerAfterMove.position);
        assertFalse(playerAfterMove.hasUnusedWeapon());
        assertEquals(100, playerAfterMove.score); // at the moment same as price
    }

    @Test
    public void pickItemRequiresDiscountPercentPer10Picks() {
        Map map = Map.createMapFromLines(mapLines);
        Player player = createPlayerToMap(map);
        int discountPercent = 50;
        Item item = Item.create(100, discountPercent, player.position);

        GameState state = new GameState(map)
                .addPlayer(player)
                .addItem(item);

        int picksRequired = discountPercent/10;
        for (int i = 0; i < picksRequired - 1; i++) {
            state = state.movePlayer(player.id, Move.PICK);
            assertEquals(1, state.items.size());
        }

        state = state.movePlayer(player.id, Move.PICK);
        assertEquals(0, state.items.size());
    }

    @Test
    public void shootingDecreasesTargetPlayersHealth() {
        Map map = Map.createMapFromLines(mapLines);
        Player shooter = createPlayerToMap(map);
        Player targetPlayer = createPlayerToMap(map);

        GameState state = new GameState(map)
                .addPlayer(shooter)
                .addPlayer(targetPlayer)
                .addItem(Item.createWeapon(1, 1, shooter.position))
                .movePlayer(shooter.id, Move.PICK)
                .movePlayer(shooter.id, Move.USE);

        assertEquals(targetPlayer.health - GameState.HEALTH_LOST_WHEN_ATTACKED, state.getPlayer(targetPlayer.id).health);
    }

    @Test
    public void shootingAWeaponRecordsAShootingLineToBeDisplayedAtMap() {
        Map map = Map.createMapFromLines(mapLines);
        Player shooter = createPlayerToMap(map);
        Player targetPlayer = createPlayerToMap(map);

        GameState state = new GameState(map)
                .addPlayer(shooter)
                .addPlayer(targetPlayer)
                .addItem(Item.createWeapon(1, 1, shooter.position))
                .movePlayer(shooter.id, Move.PICK)
                .movePlayer(shooter.id, Move.USE);

        assertEquals(1, state.shootingLines.size());
        ShootingLine shootingLine = state.shootingLines.get(0);
        assertEquals(shooter.position, shootingLine.fromPosition);
        assertEquals(targetPlayer.position, shootingLine.toPosition);
    }

    @Test
    public void usingAnItemMakesItUnusable() {
        Map map = Map.createMapFromLines(mapLines);
        Player shooter = createPlayerToMap(map);
        Player targetPlayer = createPlayerToMap(map);

        GameState state = new GameState(map)
                .addPlayer(shooter)
                .addPlayer(targetPlayer)
                .addItem(Item.createWeapon(1, 1, shooter.position))
                .movePlayer(shooter.id, Move.PICK);

        assertTrue(state.getPlayer(shooter.id).hasUnusedWeapon());

        state = state.movePlayer(shooter.id, Move.USE);

        assertFalse(state.getPlayer(shooter.id).hasUnusedWeapon());
    }

    private void assertMoveThrows(GameState state, Player player, Move move) {
        try {
            state.movePlayer(player.id, move);
        }
        catch (Exception e) {
            return;
        }
        fail("Exception expected");
    }


    private Player createPlayerToPos(int x, int y) {
        return Player.create(UUID.randomUUID().toString(), Position.of(x, y));
    }

    private Player createPlayerToMap(Map map) {
        Position pos = map.randomFloorPosition();
        return createPlayerToPos(pos.x, pos.y);
    }


}