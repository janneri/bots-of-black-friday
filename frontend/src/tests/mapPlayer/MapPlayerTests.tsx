import React from 'react'
import { MapPlayer, playerTints } from '@src/components/MapPlayer'
import { playerTextures } from '@src/utils/textures'
import { type Player } from '@src/types/Player'
import { getCellX, getCellY, mapDimensions, renderTest, stageDimensions } from '@src/tests/TestStage'
import { type Texture } from 'pixi.js'
import { type Position } from '@src/types/Position'

export const playerDefaults: Readonly<Player> = {
  name: 'Player one',
  position: {
    x: 0,
    y: 0
  },
  health: 100,
  money: 100,
  score: 0,
  state: 'MOVE',
  timeInState: 0,
  usableItems: [],
  actionCount: 0
}

export const playerTestCases: ReadonlyArray<Readonly<{
  key: string
  name: string
  position: Position
  texture: Texture
  tint: number
  hasWeapon: boolean
}>> = playerTextures.map((texture, textureIndex) => {
  const tintsMiddleIndex = Math.floor(playerTints.length / 2)

  const variants = playerTints.map((tint, tintIndex) => {
    return {
      key: `player-${tintIndex}-${textureIndex}`,
      name: `Player ${textureIndex} ${tintIndex}`,
      position: {
        x: getCellX(() => {
          const offset = ((tintIndex % tintsMiddleIndex) + 1) * 2
          let x = tintIndex
          if (tintIndex < tintsMiddleIndex) {
            x = stageDimensions.middle.x - offset
          } else if (tintIndex > tintsMiddleIndex) {
            x = stageDimensions.middle.x + offset
          } else {
            x = stageDimensions.middle.x + (playerTints.length % 2 === 0 ? offset : 0)
          }

          return x
        }),
        y: getCellY(() => textureIndex * 2 + 1)
      },
      texture,
      tint,
      hasWeapon: false
    }
  })

  return variants
}).flat().map((testCase) => {
  return [
    testCase,
    {
      ...testCase,
      key: `${testCase.key}-weapon`,
      position: {
        x: testCase.position.x,
        y: getCellY(() => testCase.position.y + 1)
      },
      hasWeapon: true
    }
  ]
}).flat()

renderTest(<>
  {playerTestCases.map(({ key, name, position, texture, tint, hasWeapon }) => {
    return <MapPlayer
      key={key}
      player={{
        ...playerDefaults,
        name,
        position
      }}
      mapDimensions={mapDimensions}
      texture={texture}
      tint={tint}
      hasWeapon={hasWeapon}
    />
  })}
</>)
