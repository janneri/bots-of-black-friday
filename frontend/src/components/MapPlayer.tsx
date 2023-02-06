import React, { useMemo } from 'react'
import { Sprite, useTick } from '@pixi/react'
import { type Texture } from 'pixi.js'
import { pickByString } from '@src/utils/pickByString'
import { toPixelPosition } from '@src/utils/toPixelPosition'
import { zIndex } from '@src/utils/zIndex'
import { type Player } from '@src/types/Player'
import { type MapDimensions } from '@src/utils/MapDimensions'
import { PlayerGlowFilter } from '@src/utils/PlayerGlowFilter'

// https://www.pixilart.com/palettes/tropical-1333
export const playerTints: readonly number[] = [
  0x991B4B,
  0xE15365,
  0xFFA472,
  0xFFDC8A,
  0xFEFFF0,
  0xAFE06E,
  0x21DB81
]

export function getPlayerTint (name: string): number {
  return pickByString(name, playerTints)
}

export function MapPlayer ({
  player,
  mapDimensions: {
    tileWidthInPx,
    halfTileWidthInPx
  },
  texture,
  tint,
  hasWeapon
}: {
  player: Player
  mapDimensions: MapDimensions
  texture: Texture
  tint: number
  hasWeapon: boolean
}): JSX.Element {
  const playerPosition = toPixelPosition(player.position, tileWidthInPx, halfTileWidthInPx)
  const filters = useMemo(() => {
    if (hasWeapon) {
      return [new PlayerGlowFilter({
        color: 0x8FEFF1
      })]
    } else {
      return []
    }
  }, [player, hasWeapon])

  useTick(delta => {
    if (filters.length > 0) {
      filters[0].time += delta
    }
  })

  return <Sprite
    anchor={[0.5, 0.5]}
    x={playerPosition.xInPx}
    y={playerPosition.yInPx}
    width={tileWidthInPx}
    height={tileWidthInPx}
    texture={texture}
    tint={tint}
    zIndex={zIndex.player}
    filters={filters}
  />
}
