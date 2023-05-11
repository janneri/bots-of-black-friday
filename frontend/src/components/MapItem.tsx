import React from 'react'
import { Sprite } from '@pixi/react'
import { type Texture } from 'pixi.js'
import { type Item } from '@src/types/Item'
import { toPixelPosition } from '@src/utils/toPixelPosition'
import { zIndex } from '@src/utils/zIndex'
import { type MapDimensions } from '@src/utils/MapDimensions'

export function MapItem ({
  item,
  mapDimensions: {
    tileWidthInPx,
    halfTileWidthInPx
  },
  texture
}: {
  item: Pick<Item, 'position'>
  mapDimensions: MapDimensions
  texture: Texture
}): JSX.Element {
  const itemPosition = toPixelPosition(item.position, tileWidthInPx, halfTileWidthInPx)

  return <Sprite
    anchor={[0.5, 0.5]}
    x={itemPosition.xInPx}
    y={itemPosition.yInPx}
    width={tileWidthInPx}
    height={tileWidthInPx}
    texture={texture}
    zIndex={zIndex.item}
  />
}
