import React from 'react'
import { MapLabel } from '@src/components/MapLabel'
import { toPixelPosition } from '@src/utils/toPixelPosition'
import { getCellX, getCellY, mapDimensions, renderTest, stageDimensions } from '@src/tests/TestStage'
import { type Position } from '@src/types/Position'
import { MapItem } from '@src/components/MapItem'
import { type Texture } from 'pixi.js'
import { junkTextures, textures } from '@src/utils/textures'

interface ItemTestCase {
  text: string
  position: Position
  texture: Texture
}

const itemTestData: Array<Omit<ItemTestCase, 'position'>> = [
  {
    text: 'Potion',
    texture: textures.potion
  },
  {
    text: 'Weapon',
    texture: textures.weapon
  },
  {
    text: 'Beer',
    texture: textures.beer
  },
  junkTextures.map((texture, textureIndex) => {
    return {
      text: `Junk ${textureIndex}`,
      texture
    }
  })
].flat()

const itemTestCases: ItemTestCase[] = itemTestData.map(({ text, texture }, itemIndex) => {
  return {
    text,
    texture,
    position: {
      x: getCellX(() => {
        const middle = Math.floor(itemTestData.length / 2)
        const offset = ((itemIndex % middle) + 1) * 2
        let x = itemIndex
        if (itemIndex < middle) {
          x = stageDimensions.middle.x - offset
        } else if (itemIndex > middle) {
          x = stageDimensions.middle.x + offset
        } else {
          x = stageDimensions.middle.x + (itemTestData.length % 2 === 0 ? offset : 0)
        }

        return x
      }),
      y: getCellY(() => stageDimensions.middle.y)
    }
  }
})

renderTest(<>
  {itemTestCases.map(({ text, position, texture }) => {
    const pixelPosition = toPixelPosition(
      position,
      mapDimensions.tileWidthInPx,
      mapDimensions.halfTileWidthInPx
    )

    return [
      <MapLabel
        key={`item-${position.x}-${position.y}-label`}
        text={text}
        mapDimensions={mapDimensions}
        itemPosition={pixelPosition}
        zIndex={1}
        filters={[]}
      />,
      <MapItem
        key={`item-${position.x}-${position.y}`}
        item={{
          position
        }}
        texture={texture}
        mapDimensions={mapDimensions}
      />
    ]
  }).flat()}
</>)
