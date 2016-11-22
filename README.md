# Bots of black friday

> Shoppers' favorite sale is always the day after Thanksgiving sale, a.k.a. Black Friday.

![bots shopping to the death](bots.png)

## Running the server

1. `cd server/`
2. Prepare the frontend: `npm install && npm run-script build`
3. Run the backend: `mvn spring-boot:run`

After this, the GUI can be accessed from http://localhost:8080/

## Development

You can develop the frontend with npm run-script watch, but the backend
does not support hot-reloading.

## Architecture

The server and the bots both provide REST API's to each other: the
server, for bots to register and tell where their REST endpoint is; the
bots, to get the current game status from server and tell the server (in
reply) what they want to do.

## Rules of the game

### In English

* The server sends the current game situation to the bots.  Every bot
  has to answer with some kind of action, be it movement (UP, DOWN,
  LEFT, RIGHT), picking up an item (PICK) or using one (USE).
* Faulty responses are penalised by decreasing the bot's health.  Dead
  bots are removed from the shop by guards.  Faulty actions include
  running into a wall, trying to pick nonexistent items, taking too long
  to form a response, protocol errors etc.
* The items in the shop have a price and a discount percentage.  The
  better the discount, the longer it takes to pick up.
* Every bot has a given amount of money, which is spent on shopping.
* The higher the value of items collected, the better will be the bot's
  position on the top list.
* Because it's Black Friday, some items are weapons that can be used to
  harm the nearest shopper.
* When the bot is out of money, it has to exit the shop by the cash
  register.  After this it is safely out of the game.  Stealing will be
  punished.
* Don't go too early to the cash register.  You won't be able to get
  back.
* The shops may have walls.

### Suomeksi

* Serveri lähettää botille pelitilan, johon botin on vastattava omalla siirrollaan, joka on liikkuminen (UP, DOWN, LEFT, RIGHT), esineen nostaminen (PICK) tai esineen käyttäminen (USE).
* Virheellisistä siirroista (seinään juokseminen, olemattoman esineen hamuilu, siirron palauttamisessa viivästely, rajapintavirheet yms) botin kunto vähenee. Kuolleet botit poistetaan kaupasta vartijoiden toimesta.
* Kaupan esineillä on hinta ja alennusprosentti. Mitä kovempi alennus, sitä kauemmin esineen nostamisessa kestää.
* Botilla on tietty määrä rahaa, joka kuluu ostoksia tehdessä.
* Mitä enemmän rahallista arvoa saa koottua pelin aikana, sitä korkeammalle pääsee top-listalla.
* Koska Black Friday, osa esineistä on aseita, joita käyttämällä voi vahingoittaa lähintä kanssashoppailijaa.
* Kun botilla ei ole enää varaa ostaa esineitä, on sen suunnattava pois kaupasta kassan kautta, jolloin peli päättyy sen osalta. Varastaminen on rangaistava teko, josta saa sakinhivutusta.
* Kassalle ei kannata hortoilla kesken pelin, jos ei halua poistua ennenaikaisesti.
* Kaupoissa voi olla seiniä, jotka hankaloittavat kulkua.

## Rajapinta

* Rajapinnan tietosisältö on nähtävissä esimerkkibotissa.  Voit katsoa
  serverin lähettämää [esimerkkiviestiä](./example-message.md)

