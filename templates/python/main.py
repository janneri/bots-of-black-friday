import time

from bot import Bot
from client import Client
from scheduler import BotScheduling


def main():
    url = "https://bots-of-black-friday-helsinki.azurewebsites.net"
    bot_name = "<your_bot_name>"

    headers = {"Content-Type": "application/json", "Accept": "application/json"}
    client = Client(url, headers=headers)
    game = client.register_player(bot_name)

    # TODO: Maybe you can use these for something
    player_info = game["player"]
    map_info = game["map"]

    player_id = game["id"]
    bot = Bot(bot_name, player_id)

    scheduling = BotScheduling(client, bot)
    print("Started playing!")
    while True:
        scheduling.play_round()
        time.sleep(0.5)


if __name__ == "__main__":
    main()
