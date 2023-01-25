import { Item } from "./Item";
import { Position } from "./Position";

export interface Player {
  name: string;
  position: Position;
  health: number;
  money: number;
  score: number;
  state: "MOVE" | "USE" | "PICK";
  timeInState: number;
  usableItems: Item[];
  actionCount: number;
}
