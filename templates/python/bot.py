import random


class Bot:
    def __init__(self, name: str, id_: str):
        self.name = name
        self.id_ = id_

    def find_me(self, game_state):
        for player in game_state["players"]:
            if player["name"] == self.name:
                return player
        return None

    def resolve_next_move(self, game_state):
        player = self.find_me(game_state)
        if player is None:
            raise Exception("Player not found")
        current_pos = player["position"]

        if random.random() > 0.5:
            return "LEFT"
        else:
            return "RIGHT"
