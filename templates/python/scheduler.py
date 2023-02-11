
class BotScheduling:
    def __init__(self, client, bot):
        self.client = client
        self.my_bot = bot

    def play_round(self):
        game_state = self.client.get_game_state()
        player = self.my_bot.find_me(game_state)
        if player is None:
            print("My bot is dead or not there yet")
        else:
            next_move = self.my_bot.resolve_next_move(game_state)
            print(f"My bot moving to {next_move}")
            self.client.register_move(self.my_bot.id_, next_move)
