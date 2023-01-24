import { Player } from "./types/Player";
import { GameMap } from "./types/GameMap";
import { Item } from "./types/Item";

const LOCAL_URL = "http://localhost:8080";
const PROD_URL = "https://bots-of-black-friday.azurewebsites.net";

const getUrl = (): string => {
  if (process.env.NODE_ENV === "development") {
    return LOCAL_URL;
  }
  return PROD_URL;
};

const headers = {
  "content-type": "application/json;charset=UTF-8",
};

interface RegisterResponse {
  id: string;
  player: Player;
  map: GameMap;
}

export const register = (playerName: string): Promise<RegisterResponse> => {
  console.log(`${getUrl()}/register`);
  return fetch(`${getUrl()}/register`, {
    method: "POST",
    headers: headers,
    body: JSON.stringify({ playerName: playerName }),
  }).then((response: Response) => response.json());
};

interface GameStateResponse {
  players: Player[];
  finishedPlayers: Player[];
  items: Item[];
  round: number;
  shootingLines: any[];
}

export const getGameState = (): Promise<GameStateResponse> => {
  return fetch(`${getUrl()}/gamestate`).then((response: Response) =>
    response.json()
  );
};

type Move = "UP" | "DOWN" | "RIGHT" | "LEFT" | "PICK" | "USE" | "randomMove";

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
