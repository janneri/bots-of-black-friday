import { type Player } from './Player'
import { type Item } from './Item'
import { type ShootingLine } from './ShootingLine'

export interface GameState {
  players: Player[]
  finishedPlayers: Player[]
  items: Item[]
  round: number
  shootingLines: ShootingLine[]
}
