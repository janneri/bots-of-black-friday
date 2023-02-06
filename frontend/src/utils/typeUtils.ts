import { type GameState } from '@src/types/GameState'
import { type GameMap } from '@src/types/GameMap'

function objectHasRequiredProperties<T extends object> (
  obj: T,
  required: Array<keyof T>
): boolean {
  if (obj === null || obj === undefined) {
    return false
  }

  return required.every(k => k in obj)
}

export function isGameState (v: any): v is GameState {
  return objectHasRequiredProperties<GameState>(
    v,
    [
      'players',
      'finishedPlayers',
      'items',
      'round',
      'shootingLines'
    ]
  )
}

export function isGameMap (v: any): v is GameMap {
  return objectHasRequiredProperties<GameMap>(
    v,
    [
      'width',
      'height',
      'maxItemCount',
      'tiles',
      'name',
      'exit'
    ]
  )
}
