import React, { useCallback } from 'react'
import { Graphics } from '@pixi/react'
import { type Graphics as PixiGraphics } from '@pixi/graphics'
import { type MapDimensions } from '@src/utils/MapDimensions'

export function MapGrid ({
  mapDimensions: {
    width,
    height,
    containerWidthInPx,
    tileWidthInPx
  }
}: {
  mapDimensions: MapDimensions
}): JSX.Element {
  const drawGrid = useCallback((g: PixiGraphics) => {
    g.clear()
    g.lineStyle(1, 0xD97706, 0.6)

    for (let y = 1; y < height; y++) {
      g.moveTo(0, y * tileWidthInPx)
      g.lineTo(width * tileWidthInPx, y * tileWidthInPx)
    }

    for (let x = 1; x < width; x++) {
      g.moveTo(x * tileWidthInPx, 0)
      g.lineTo(x * tileWidthInPx, height * tileWidthInPx)
    }
  }, [containerWidthInPx, width, height])

  return (<Graphics key="map-grid" draw={drawGrid} />)
}
