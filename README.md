# Bots of black friday

> Shoppers' favorite sale is always the day after Thanksgiving sale, a.k.a. Black Friday.

![bots shopping to the death](bots.png)

## Rules of the game

* Bots act as clients. The clients have time until the next tick to register their next move to the server. 
  If they do it more than once, the previous move is overwritten. 
  On next tick the server calculates the next game state, which is accessible from another endpoint.
* Available actions are movement (UP, DOWN, LEFT, RIGHT), picking up an item (PICK) or using one (USE).
* Faulty responses are penalised by decreasing the bot's health.  Dead
  bots are removed from the shop by guards.  Faulty actions include
  running into a wall, trying to pick nonexistent items, etc.
* The items in the shop have a price and a discount percentage.  The
  better the discount, the longer it takes to pick up.
* Every bot has a given amount of money, which is spent on shopping.
* The higher the value of items collected, the better will be the bot's
  position on the top list.
* The "price" of an item is the price *before* discount, so it is *not*
  the amount of money that will actually be spent when you pick it up.
* When items have been collected, new ones are added on the map.
* There's no time limit to exploring the shop.
* Because it's Black Friday, some items are weapons that can be used to
  harm the shopper that is the farthest away from the shooter
  (calculated as the manhattan distance between players, and not taking
  into account walls).
* Weapons always hit and cannot be dodged.
* A weapon can only be used once.
* When the bot is out of money, it has to exit the shop by the cash
  register.  After this it is safely out of the game.  Stealing will be
  punished.
* Don't go too early to the cash register.  You won't be able to get
  back.
* The shops may have walls, and traps. Traps cause damage but you can walk through them.

## Architecture

Bots act as clients.
The clients have time until the next tick to register their next move to the server.
If they do it more than once, the previous move is overwritten.
On next tick the server calculates the next game state, which is accessible from another endpoint.

## Running the server

You can run the server locally for developing your bot. It's not needed though, as you can use the deployed server instance.

### How to start

1. `cd server/`
2. Prepare the frontend: `npm install && npm run-script build`
3. Run the backend: `mvn spring-boot:run`

After this, the GUI can be accessed from http://localhost:8080/

### How to create a bot?

First, register your bot:
```json
POST {{url}}/register
Content-Type: application/json

{
    "playerName": "testbot2",
}

Note: The response contains an id, which you need to capture and use for the next calls. 
```

Then get the current gamestate:
```
GET {{url}}/gamestate
```

And register your next move:
```
PUT {{url}}/{{playerId}}/move
Content-Type: application/json

"LEFT"
```

The game ticks in about 1 second. If you  have about 1 second to

### Admin instructions

The following are only needed for the administrator of the game server.

#### How to create a new map

```
sudo mkdir -m 777 -p /bobf-maps
touch /bobf-maps/example-map
echo "map name" >> /bobf-maps/example-map
-- limit the number of items on map to 3
echo 3 >> /bobf-maps/example-map
echo "xxxxxxxxx" >> /bobf-maps/example-map
-- x is wall, o is exit # is a trap which decreases bots health
echo "x_o____#x" >> /bobf-maps/example-map
echo "xxxxxxxxx" >> /bobf-maps/example-map
```

#### How to change the map

`./server/cli changemap example-map`

also works for the predefined maps (see server/src/main/resources/maps)

`./server/cli changemap split_trap.map`

#### How to increase or decrease game speed

Change the GameEngine.PAUSE_BETWEEN_ROUNDS_MILLIS and reboot. The best
value depends on network latency, bot count and bot quality.

#### Developing the server

You can develop the frontend with `npm run watch`.
The backend does not support live reloading, but you can run the app with your IDE, and most IDEs support hot deployment of static resources and Java code.
