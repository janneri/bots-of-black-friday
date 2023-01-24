import { Player } from "./types/Player";
import { GameMap } from "./types/GameMap";

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
