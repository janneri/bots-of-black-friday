import { GameMap } from "./types/GameMap";
import { GameState } from "./types/GameState";
import { RegisterResponse } from "./types/RegisterResponse";
import { Move } from "./types/Move";

const LOCAL_URL = "http://localhost:8080";
let PROD_URL = "";

const getUrl = (): string => {
  if (process.env.NODE_ENV === "development") {
    return LOCAL_URL;
  }

  if (PROD_URL === "") {
    console.log(
      "Please set PROD_URL to point at the server of your Code Camp location instance:"
    );
    console.log("Oulu: https://bots-of-black-friday-oulu.azurewebsites.net");
    console.log("Tampere: https://bots-of-black-friday-tampere.azurewebsites.net");
    console.log("Turku: https://bots-of-black-friday-turku.azurewebsites.net");
    console.log("Helsinki https://bots-of-black-friday-helsinki.azurewebsites.net");
    process.exit(1);
  }
  return PROD_URL;
};

const headers = {
  "content-type": "application/json;charset=UTF-8",
};

export const register = (playerName: string): Promise<RegisterResponse> => {
  console.log(`${getUrl()}/register`);
  return fetch(`${getUrl()}/register`, {
    method: "POST",
    headers: headers,
    body: JSON.stringify({ playerName: playerName }),
  }).then((response: Response) => response.json());
};

export const getGameState = (): Promise<GameState> => {
  return fetch(`${getUrl()}/gamestate`).then((response: Response) =>
    response.json()
  );
};

export const move = (playerId: string, move: Move): Promise<Response> => {
  return fetch(`${getUrl()}/${playerId}/move`, {
    method: "PUT",
    headers: headers,
    body: JSON.stringify(move),
  });
};

export const say = (playerId: string, message: string): Promise<Response> => {
  return fetch(`${getUrl()}/${playerId}/say`, {
    method: "POST",
    headers: headers,
    body: JSON.stringify(message),
  });
};

export const getMap = (): Promise<GameMap> => {
  return fetch(`${getUrl()}/map`).then((response) => response.json());
};
