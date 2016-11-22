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

move_names = ('UP', 'DOWN', 'LEFT', 'RIGHT', 'PICK', 'USE')
moves = dict((move, Response('"%s"' % move, mimetype='application/json'))
        for move in move_names)

@bot.route(move_endpoint, methods=['POST'])
def move():
    print(request.get_json()['playerState']['position'])
    return random.choice(moves.values())

if __name__ == '__main__': bot.run(port=port, debug=True)

