# Bots of Black Friday

> Shoppers' favorite sale is always the day after Thanksgiving sale, a.k.a. Black Friday.

![bots shopping to the death](bots.png)

Bots of Black Friday is a game for code camp events. This repo contains the server code and instructions/templates for bot implementation. The idea is that "a moderator" runs the server in a LAN or Azure. The players (software developers) implement bots that talk to the server.

## Rules of the game

* Bots act as clients. The clients have time until the next tick to register their next move to the server. 
  If they do it more than once, the previous move is overwritten. 
  On the next tick, the server calculates the next game state, which is accessible from another endpoint.
* Available actions are movement (UP, DOWN, LEFT, RIGHT), picking up an item (PICK), or using one (USE).
* Faulty moves are penalized by decreasing the bot's health.  Dead
  bots are removed from the shop by guards.  Faulty actions include
  running into a wall, trying to pick nonexistent items, etc.
* The items in the shop have a price and a discount percentage. The
  better the discount, the longer it takes to pick up.
* Every bot has a given amount of money, which is spent on shopping.
* The higher the value of items collected, the better will be the bot's
  position on the top list.
* The "price" of an item is the price *before* discount, so it is *not*
  the amount of money that will be spent when you pick it up.
* When items have been collected, new ones are added to the map.
* There's no time limit to exploring the shop, but your health decreases because shopping is hard.
* Some items are weapons that can be used to drain energy from the shopper that is the farthest away from the drainer
  (calculated as the manhattan distance between players, and not taking
  into account walls). The bot that used the weapon receives some health back when used.
* Weapons always hit and cannot be dodged.
* A weapon can only be used once.
* When the bot is out of money, it has to exit the shop by the cash
  register.  After this, it is safely out of the game.
* Don't go too early to the cash register.  You won't be able to get
  back.
* The shops may have walls and traps. Traps cause damage but you can walk through them.

## Creating a bot

``templates/`` directory contains project templates that you can use to get started:

* Clojure
* Java
* TypeScript (requires Node.js 18)

* Even if you don't end up using a template, it is recommended to look through the TypeScript template to see all the available API calls, their parameters, and return types.

### Bot implementation basics

Get the server URL from the moderator.

Register your bot (name must be unique and name length between 1 and 20):
```
POST {{url}}/register
Content-Type: application/json

{
    "playerName": "testbot2",
}
```
Note: The response contains the map and a secret player id, which you need to capture and use for the next calls.

Then get the current game state:
```
GET {{url}}/gamestate
```

If your bot is accepted and found in the player list of the game state, calculate and register your next move:
```
PUT {{url}}/{{playerId}}/move
Content-Type: application/json

"LEFT"
```

The game ticks in 1 second, and your bot needs to be in sync with that. 
Because of the 1-second tick rate, you probably want to register your next move every 500ms.
As the quality of the bots improve, the game moderator can shorten the cycle. Network quality matters. 

## Running the server

You can run the server locally for developing your bot. It's not needed though, as you can use the deployed server instance.

### How to start

Requirements:

* Java 11

To package and execute Jar, run in the root directory:

- Build the server: `./mvnw clean package -pl server -P production`
- Run the server: `java -jar server/target/server-1.0-SNAPSHOT.jar`

After this, the GUI can be accessed from http://localhost:8080/

### Admin instructions

The following are only needed for the administrator of the game server.

#### How to create a map?

Copy and paste existing maps, such as the default.map. You need to package and redeploy after adding new maps.

#### How to change the map?

* Changing the map resets the scoreboard and the whole game state.
* Use the moderator_api.http-file. Be sure to use the correct environment (add it to the http-client.env.json-file if necessary).

#### How to increase or decrease game speed

Change the GameEngine.PAUSE_BETWEEN_ROUNDS_MILLIS in [server/src/main/java/fi/solita/botsofbf/GameEngine.java](server/src/main/java/fi/solita/botsofbf/GameEngine.java) and rebuild & redeploy. 

The best value depends on network latency, bot count, and bot quality.

#### Developing the server

Requirements:

* Java 11
* Node.js 18

To start the backend (Spring Boot), run in the root directory:

- Run the backend: `./mvnw spring-boot:run -pl server`

Note! You can also run Maven tasks from your IDE.

To start the frontend (Node.js / Vite), run in the root directory:

- Install packages: `npm ci --prefix="frontend"`
- Check addresses in [frontend/.env.local](frontend/.env.local)
- Start dev server: `npm run dev --prefix="frontend"`
- Access frontend at: http://localhost:5173/
- The frontend should be talking with the server at http://localhost:8080/

Note! You can also run NPM tasks from your IDE.

To create a development build:

- Check addresses in [frontend/.env.production](frontend/.env.production)
- Build the server: `./mvnw clean package -pl server -P production`
- Run the server: `java -jar server/target/server-1.0-SNAPSHOT.jar`
- The server should be available at http://localhost:8080/
