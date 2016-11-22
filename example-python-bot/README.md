# Python example bot

## Running

1. install flask and requests
2. python bot.py

## Starting the game

curl -X POST http://localhost:8774/api/register/BotNicknameHere

Note that you can (at least as long as you keep the bot stateless)
register however many times you wish.  All registrations will cause new
bot instances in the server.  The server also handles the bookkeeping
about which answer is for which bot.

