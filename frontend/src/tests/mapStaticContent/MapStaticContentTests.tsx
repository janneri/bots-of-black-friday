import React from 'react'
import { mapDimensions, renderTest, stageDimensions } from '@src/tests/TestStage'
import { MapStaticContent } from '@src/components/MapStaticContent'
import { MapGrid } from '@src/components/MapGrid'

function surroundWithWall (content: string): string {
  const result = `x${content}x`

  if (result.length > stageDimensions.mapWidth) {
    throw Error(`Map row exceeds map width (${result.length} > ${stageDimensions.mapWidth}).`)
  }

  return result
}

function getFullWidthWall (): string {
  return surroundWithWall(
    'x'.repeat(stageDimensions.mapWidth - 2)
  )
}

function getRows (count: number): string[] {
  return '&'.repeat(count).split('&').map(() => '')
}

const centerContent = '_#_o_#_'
const centerRow = [
  '_'.repeat(stageDimensions.middle.x - Math.floor(centerContent.length / 2) - 1),
  centerContent,
  '_'.repeat(stageDimensions.middle.x - Math.floor(centerContent.length / 2) - 1)
].join('')

renderTest(<>
  <MapStaticContent
    gameMap={{
      tiles: [
        getFullWidthWall(),
        getRows(stageDimensions.middle.y - 2).map(() => {
          return surroundWithWall('_'.repeat(stageDimensions.mapWidth - 2))
        }),
        surroundWithWall(
          centerRow
        ),
        getRows(stageDimensions.middle.y - 2).map(() => {
          return surroundWithWall('_'.repeat(stageDimensions.mapWidth - 2))
        }),
        getFullWidthWall()
      ].flat()
    }}
    mapDimensions={mapDimensions}
  />
  <MapGrid mapDimensions={mapDimensions} />
</>)
