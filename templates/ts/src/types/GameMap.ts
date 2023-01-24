import { Position } from "./Position";

export interface GameMap {
  width: number;
  height: number;
  maxItemCount: number;
  tiles: string[];
  name: string;
  exit: Position;
}
