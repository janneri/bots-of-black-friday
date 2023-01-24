export interface Player {
  name: string;
  score: number;
  money: number;
  state: "MOVE" | "USE" | "PICK";
  timeInState: number;
  usableItems: string[]; // TODO: tarkista
  actionCount: number;
  health: number;
}
