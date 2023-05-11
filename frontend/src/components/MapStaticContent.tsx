import React from 'react'
import { Container, Sprite, TilingSprite } from '@pixi/react'
import { type Texture } from 'pixi.js'
import { type GameMap } from '@src/types/GameMap'
import { type MapDimensions } from '@src/utils/MapDimensions'
import { textures } from '@src/utils/textures'
import { toPixelPosition } from '@src/utils/toPixelPosition'

enum TileType {
  WALL = 'x',
  EXIT = 'o',
  MINE = '#',
  FLOOR = '_'
}

export function MapStaticContent ({
  gameMap: {
    tiles
  },
  mapDimensions: {
    tileWidthInPx,
    halfTileWidthInPx,
    stageWidthInPx,
    stageHeightInPx
  }
}: {
  gameMap: Pick<GameMap, 'tiles'>
  mapDimensions: MapDimensions
}): JSX.Element {
  // Note! Floor texture is 1024 px times 1024 px and contains 12 tiles
  // Note! textures.floor.width returns sometimes 1, replaced with a static value.
  const floorTileScale = (tileWidthInPx / (1024 / 12))

  const mapStaticSprites = tiles.map((row, rowIndex): JSX.Element[] => {
    const rowSprites = [...row]
      .map((tileType, colIndex) => {
        let texture: Texture
        if (tileType === TileType.WALL) {
          texture = textures.wall
        } else if (tileType === TileType.MINE) {
          texture = textures.mine
        } else if (tileType === TileType.EXIT) {
          texture = textures.exit
        } else if (tileType === TileType.FLOOR) {
          return null
        } else {
          console.error(`Unknown tile type (${tileType}) at (${rowIndex}, ${colIndex}).`)
          return null
        }

        const { xInPx, yInPx } = toPixelPosition(
          { x: colIndex, y: rowIndex },
          tileWidthInPx,
          halfTileWidthInPx
        )

        // Alternative mine color: 0xF4AFC0
        const tint = tileType === TileType.MINE ? 0xFF6368 : 0xFFFFFF

        return <Sprite
          key={`static-${tileType}-${rowIndex}-${colIndex}`}
          anchor={[0.5, 0.5]}
          x={xInPx}
          y={yInPx}
          width={tileWidthInPx}
          height={tileWidthInPx}
          texture={texture}
          tint={tint}
        />
      }).filter((sprite): sprite is JSX.Element => sprite !== null)

    return rowSprites
  }).filter((sprite) => sprite.length > 0)

  return (
    <>
      <TilingSprite
        key="map-floor"
        texture={textures.floor}
        width={stageWidthInPx}
        height={stageHeightInPx}
        tileScale={[floorTileScale, floorTileScale]}
        tilePosition={{ x: 0, y: 0 }}
      />
      <Container key="map-static" position={[0, 0]}>
        {mapStaticSprites}
      </Container>
    </>
  )
}
