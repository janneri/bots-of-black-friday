#### Example message from server to bot

The situation when the bot registers.

```

{
  "id": "d011658d-0d8e-4d37-820b-166f8959647d",
  "player": {
    "name": "ED-209",
    "url": "http://192.168.2.200:3002/round",
    "position": {
      "x": 88,
      "y": 25
    },
    "score": 0,
    "money": 5000,
    "state": "MOVE",
    "timeInState": 0,
    "usableItems": [],
    "actionCount": 0,
    "health": 100
  },
  "gameState": {
    "map": {
      "width": 92,
      "height": 28,
      "maxItemCount": 5,
      "tiles": [
        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
        "xx________________________________________________________________________________________xx",
        "xx________________________________________________________________________________________xx",
        "xx_______o_____________######################xx######################_____________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx_____________________######################xx######################_____________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx___________________________________________xx___________________________________________xx",
        "xx_____________________######################xx######################_____________________xx",
        "xx________________________________________________________________________________________xx",
        "xx________________________________________________________________________________________xx",
        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
      ],
      "name": "citymarket",
      "exit": {
        "x": 9,
        "y": 4
      }
    },
    "players": [
      {
        "name": "ED-209",
        "url": "http://192.168.2.200:3002/round",
        "position": {
          "x": 88,
          "y": 25
        },
        "score": 0,
        "money": 5000,
        "state": "MOVE",
        "timeInState": 0,
        "usableItems": [],
        "actionCount": 0,
        "health": 100
      }
    ],
    "finishedPlayers": [
      {
        "name": "ED-209",
        "url": "http://localhost:3002/round",
        "position": {
          "x": 9,
          "y": 4
        },
        "score": 10370,
        "money": 773,
        "state": "MOVE",
        "timeInState": 0,
        "usableItems": [
          {
            "price": 2305,
            "discountPercent": 83,
            "position": {
              "x": 89,
              "y": 18
            },
            "type": "WEAPON",
            "isUsable": true
          },
          {
            "price": 3865,
            "discountPercent": 73,
            "position": {
              "x": 33,
              "y": 12
            },
            "type": "WEAPON",
            "isUsable": true
          }
        ],
        "actionCount": 181,
        "health": 80
      }
    ],
    "items": [
      {
        "price": 3864,
        "discountPercent": 35,
        "position": {
          "x": 27,
          "y": 10
        },
        "type": "JUST_SOME_JUNK",
        "isUsable": false
      },
      {
        "price": 2300,
        "discountPercent": 34,
        "position": {
          "x": 2,
          "y": 24
        },
        "type": "JUST_SOME_JUNK",
        "isUsable": false
      },
      {
        "price": 2994,
        "discountPercent": 20,
        "position": {
          "x": 80,
          "y": 17
        },
        "type": "JUST_SOME_JUNK",
        "isUsable": false
      },
      {
        "price": 1917,
        "discountPercent": 23,
        "position": {
          "x": 35,
          "y": 22
        },
        "type": "JUST_SOME_JUNK",
        "isUsable": false
      },
      {
        "price": 3886,
        "discountPercent": 26,
        "position": {
          "x": 36,
          "y": 22
        },
        "type": "JUST_SOME_JUNK",
        "isUsable": false
      }
    ],
    "round": 111825,
    "shootingLines": []
  }
}

```
