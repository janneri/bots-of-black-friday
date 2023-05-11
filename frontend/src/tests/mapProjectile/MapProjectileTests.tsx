import React from 'react'
import { FilledTile, mapDimensions, renderTest, stageDimensions } from '@src/tests/TestStage'
import { type Position } from '@src/types/Position'
import { MapProjectile } from '@src/components/MapProjectile'
import { MapLabel } from '@src/components/MapLabel'
import { toPixelPosition } from '@src/utils/toPixelPosition'
import { labelFilters } from '@src/utils/labelFilters'

export const projectileTestCases: ReadonlyArray<Readonly<{
  fromText: string
  toText: string
  from: Position
  to: Position
}>> = [
  {
    fromText: 'C',
    toText: 'T',
    from: {
      x: stageDimensions.middle.x,
      y: stageDimensions.middle.y - 1
    },
    to: {
      x: stageDimensions.middle.x,
      y: 2
    }
  },
  {
    fromText: 'C',
    toText: 'TR',
    from: {
      x: stageDimensions.middle.x + 1,
      y: stageDimensions.middle.y - 1
    },
    to: {
      x: stageDimensions.mapWidth - 3,
      y: 2
    }
  },
  {
    fromText: 'C',
    toText: 'R',
    from: {
      x: stageDimensions.middle.x + 1,
      y: stageDimensions.middle.y
    },
    to: {
      x: stageDimensions.mapWidth - 3,
      y: stageDimensions.middle.y
    }
  },
  {
    fromText: 'C',
    toText: 'BR',
    from: {
      x: stageDimensions.middle.x + 1,
      y: stageDimensions.middle.y + 1
    },
    to: {
      x: stageDimensions.mapWidth - 3,
      y: stageDimensions.mapHeight - 3
    }
  },
  {
    fromText: 'C',
    toText: 'B',
    from: {
      x: stageDimensions.middle.x,
      y: stageDimensions.middle.y + 1
    },
    to: {
      x: stageDimensions.middle.x,
      y: stageDimensions.mapHeight - 3
    }
  },
  {
    fromText: 'C',
    toText: 'BL',
    from: {
      x: stageDimensions.middle.x - 1,
      y: stageDimensions.middle.y + 1
    },
    to: {
      x: 2,
      y: stageDimensions.mapHeight - 3
    }
  },
  {
    fromText: 'C',
    toText: 'L',
    from: {
      x: stageDimensions.middle.x - 1,
      y: stageDimensions.middle.y
    },
    to: {
      x: 2,
      y: stageDimensions.middle.y
    }
  },
  {
    fromText: 'C',
    toText: 'TL',
    from: {
      x: stageDimensions.middle.x - 1,
      y: stageDimensions.middle.y - 1
    },
    to: {
      x: 2,
      y: 2
    }
  }
].map((testCase) => {
  return [
    testCase,
    {
      fromText: testCase.toText,
      toText: testCase.fromText,
      from: testCase.to,
      to: testCase.from
    }
  ]
}).flat()

const showLabel = false

renderTest(<>
  <FilledTile
    key={'center'}
    position={toPixelPosition(
      stageDimensions.middle,
      mapDimensions.tileWidthInPx,
      mapDimensions.halfTileWidthInPx
    )}
  />
  {projectileTestCases.map(({ fromText, toText, from, to }) => {
    const fromPixelPosition = toPixelPosition(
      from,
      mapDimensions.tileWidthInPx,
      mapDimensions.halfTileWidthInPx
    )

    const label = <MapLabel
      key={`label-${fromText}-${toText}`}
      text={`${fromText} -> ${toText}`}
      mapDimensions={mapDimensions}
      itemPosition={fromPixelPosition}
      zIndex={1}
      filters={labelFilters.item}
    />

    return [
      <MapProjectile
        key={`projectile-${fromText}-${toText}`}
        fromPosition={from}
        toPosition={to}
        mapDimensions={mapDimensions}
        animate={false}
      />,
      showLabel ? label : null
    ]
  }).flat()}
</>)
