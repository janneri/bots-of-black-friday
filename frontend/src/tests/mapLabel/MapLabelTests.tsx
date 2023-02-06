import React from 'react'
import { MapLabel } from '@src/components/MapLabel'
import { toPixelPosition } from '@src/utils/toPixelPosition'
import { FilledTile, mapDimensions, renderTest, stageDimensions } from '@src/tests/TestStage'
import { type Position } from '@src/types/Position'

const testCases: Array<{
  position: Position
  text: string
}> = [
  {
    position: { x: stageDimensions.middle.x, y: 1 },
    text: ','.repeat(4).split(',').map((_, i) => {
      return `First lines not visible (${i + 1}/5)`
    }).join('\n')
  },
  {
    position: { x: stageDimensions.middle.x, y: 4 },
    text: 'Single line label'
  },
  {
    position: { x: stageDimensions.middle.x, y: 7 },
    text: 'Multiline label,\nall lines\nare centered'
  }
]

renderTest(<>
  {testCases.map(({ position, text }) => {
    const pixelPosition = toPixelPosition(
      position,
      mapDimensions.tileWidthInPx,
      mapDimensions.halfTileWidthInPx
    )

    return [
      <MapLabel
        key={`label-${position.x}-${position.y}`}
        text={text}
        mapDimensions={mapDimensions}
        itemPosition={pixelPosition}
        zIndex={1}
        filters={[]}
      />,
      <FilledTile
        key={`label-${position.x}-${position.y}-target`}
        position={pixelPosition}
      />
    ]
  }).flat()}
</>)
