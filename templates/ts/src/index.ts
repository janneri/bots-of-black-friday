import { move, register } from "./api";
import { Move } from "./types/Move";

export const main = async () => {
  // Look in the api.ts file for api calls
  // First you need to register your bot to the game server.
  // When registering, you will receive an id for your bot.
  // This will be used to control your bot.

  // You need to wait one second between each action.

  // Below is example code for a bot that after
  // being registered moves randomly to left and right.
  // You can use this code as a starting point for your own implementation.

  const startingInformation = await register("My cool bot");

  setInterval(() => {
    const moves: Move[] = ["LEFT", "RIGHT"];
    move(
      startingInformation.id,
      moves[Math.floor(Math.random() * moves.length)]
    );
  }, 1000);
};

if (require.main === module) {
  main();
}
