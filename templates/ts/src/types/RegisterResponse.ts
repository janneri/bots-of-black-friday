import { Player } from "./Player";
import { GameMap } from "./GameMap";

export interface RegisterResponse {
  id: string;
  player: Player;
  map: GameMap;
}
