# Python template

Template for running simplistic bot in this game.

## Contents

* `bot.py` - contains the implementation of the Bot class
* `client.py` - contains the implementation of the API client
* `main.py` - the main entry point of the application that runs the bot
* `scheduler.py` - contains a scheduler class that updates game state and player state and calls bot's `resolve_move` function
* `requirements.txt` - lists the required dependencies to run the application

## Usage

To run the application, install the required dependencies by running the following command:

```sh
python -m pip install -r requirements.txt
```

Then, run the main file with the following command:

```sh
python main.py
```
