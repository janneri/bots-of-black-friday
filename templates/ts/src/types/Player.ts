import { Item } from "./Item";

export interface Player {
  name: string;
  score: number;
  money: number;
  state: "MOVE" | "USE" | "PICK";
  timeInState: number;
  usableItems: Item[];
  actionCount: number;
  health: number;
}
