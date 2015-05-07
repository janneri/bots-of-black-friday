# bots-of-black-friday

## Pelin kulku

* Serveri lähettää botille pelitilan, johon botin on vastattava omalla siirrollaan, joka on liikkuminen (UP, DOWN, LEFT, RIGHT), esineen nostaminen (PICK) tai esineen käyttäminen (USE).
* Virheellisistä siirroista (seinään juokseminen, olemattoman esineen hamuilu, siirron palauttamisessa viivästely, rajapintavirheet yms) botin kunto vähenee. Kuolleet botit poistetaan kaupasta vartijoiden toimesta.
* Kaupan esineillä on hinta ja alennusprosentti. Mitä kovempi alennus, sitä kauemmin esineen nostamisessa kestää.
* Botilla on tietty määrä rahaa, joka kuluu ostoksia tehdessä.
* Jos botti on kertonut nostavansa esinettä siirrolla PICK, lähettää serveri tilaa (PlayerState) PICK niin kauan kuin esineen nostamista pitää jatkaa kunnes se on kokonaan hallussa. Botin on silloin vastattava siirrolla PICK, kunnes serveri
alkaa taas antamaan tilaa MOVE. Jos nostoa ei suoriteta loppuun asti, jää esine kauppaan.
* Mitä enemmän rahallista arvoa saa koottua pelin aikana, sitä korkeammalle pääsee top-listalla.
* Koska Black Friday, osa esineistä on aseita, joita käyttämällä voi vahingoittaa lähintä kanssashoppailijaa.
* Kun botilla ei ole enää varaa ostaa esineitä, on sen suunnattava pois kaupasta kassan kautta, jolloin peli päättyy sen osalta. Varastaminen on rangaistava teko, josta saa sakinhivutusta.
* Kassalle ei kannata hortoilla kesken pelin, jos ei halua poistua ennenaikaisesti.
* Kaupoissa voi olla seiniä, jotka hankaloittavat kulkua.

## Rajapinta

* Rajapinnan tietosisältö on nähtävissä esimerkkibotissa.

### Frontti

* npm install
* npm run-script watch