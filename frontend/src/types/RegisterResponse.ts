import { type Player } from './Player'
import { type GameMap } from './GameMap'

export interface RegisterResponse {
  id: string
  player: Player
  map: GameMap
}
