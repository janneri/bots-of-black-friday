import { type Item } from './Item'
import { type Position } from './Position'

export interface Player {
  name: string
  position: Position
  health: number
  money: number
  score: number
  state: 'MOVE' | 'USE' | 'PICK'
  timeInState: number
  usableItems: Item[]
  actionCount: number
}
