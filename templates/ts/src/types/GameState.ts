import { Player } from "./Player";
import { Item } from "./Item";
import { ShootingLine } from "./ShootingLine";

export interface GameState {
  players: Player[];
  finishedPlayers: Player[];
  items: Item[];
  round: number;
  shootingLines: ShootingLine[];
}
