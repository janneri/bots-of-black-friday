from flask import Flask, request, jsonify, Response
import requests, random
from json import dumps, loads
bot = Flask(__name__)

server_url = 'http://localhost:8080' # CHANGE
port = 8774
move_endpoint = '/api/move'
my_address = 'http://localhost:%d%s' % (port, move_endpoint)

@bot.route('/api/ping')
def ping(): return "pong\n"

@bot.route('/api/register/<nick>', methods=['POST'])
def register(nick):
    response = requests.post("%s/register" % server_url,
            json=dict(playerName=nick, url=my_address))
    return jsonify(response.json()['player'])

move_resp = dict((move, Response('"%s"' % move, mimetype='application/json'))
        for move in ('UP', 'DOWN', 'LEFT', 'RIGHT', 'PICK', 'USE'))

@bot.route(move_endpoint, methods=['POST'])
def move():
    state = request.get_json()
    loc = get_player_location(state)
    possible_moves = get_legal_moves(state, loc)
    if 'PICK' in possible_moves: my_move = 'PICK'
    else: my_move = random.choice(possible_moves)
    print("Currently at %s, possible moves are %s, going to do %s" %
            (loc, possible_moves, my_move))
    return move_resp[my_move]

def get_in(state, *args):
    if not args: return state
    return get_in(state[args[0]], *args[1:])

def get_player_location(state):
    pos = get_in(state, 'playerState', 'position')
    return position_to_location(pos)

def position_to_location(pos): return (pos['x'], pos['y'])

def get_tile_at(state, location):
    x, y = location
    map = get_in(state, 'gameState', 'map', 'tiles')
    return map[y][x]

def delta(loc, delta):
    return (loc[0] + delta[0], loc[1] + delta[1])

def get_legal_moves(state, location):
    return get_legal_directions(state, location) + \
            get_legal_actions(state, location)

def get_legal_directions(state, location):
    return [direction[0]
            for direction in (('UP', (0, -1)), ('DOWN', (0, 1)),
                ('LEFT', (-1, 0)), ('RIGHT', (1, 0)))
            if get_tile_at(state, delta(location, direction[1])) != 'x']

def get_legal_actions(state, location):
    item_locs = (position_to_location(item['position'])
            for item in get_in(state, 'gameState', 'items'))
    return ['PICK'] if location in item_locs else []

if __name__ == '__main__': bot.run(port=port, debug=True)

