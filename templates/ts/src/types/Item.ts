import { Position } from "./Position";

export interface Item {
  price: number;
  discountPercent: number;
  position: Position;
  type: "JUST_SOME_JUNK" | "WEAPON";
  isUsable: boolean;
}
