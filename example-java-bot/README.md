# Java example bot for bobf

Run with:

   mvn spring-boot:run

After the bot is running, you can make it register itself to the server
by POSTing to its /bot REST handler.  This can be done by e.g. curl like
this:

   curl -d x http://localhost:9080/bot

