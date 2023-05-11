import React from 'react'
import { Container, NineSlicePlane, Text } from '@pixi/react'
import { type Filter, TextMetrics, TextStyle } from 'pixi.js'
import { textures } from '@src/utils/textures'
import { type PixelPosition } from '@src/utils/toPixelPosition'
import { type MapDimensions } from '@src/utils/MapDimensions'

export function MapLabel ({
  text,
  mapDimensions: {
    halfTileWidthInPx
  },
  itemPosition,
  zIndex,
  filters
}: {
  text: string
  mapDimensions: MapDimensions
  itemPosition: PixelPosition
  zIndex: number
  filters: Filter[]
}): JSX.Element {
  const labelTextStyle = new TextStyle({
    align: 'center',
    fontFamily: '"Press Start 2P", cursive',
    fontSize: 8,
    letterSpacing: 1.4,
    lineHeight: 10,
    fill: '#000000'
  })
  const labelMetrics = TextMetrics.measureText(text, labelTextStyle)

  const labelPadding = 8

  const labelWidth = labelMetrics.maxLineWidth + labelPadding + labelPadding
  const labelHeight = labelMetrics.height + labelPadding + labelPadding
  const labelX = itemPosition.xInPx - Math.round(labelWidth * 0.5)
  const labelY = itemPosition.yInPx - labelHeight - Math.round(halfTileWidthInPx * 1.3)

  const labelContentX = labelPadding
  const labelContentY = labelPadding

  return (
    <Container
      position={[labelX, labelY]}
      filters={filters}
      zIndex={zIndex}
    >
      <NineSlicePlane
        leftWidth={7}
        topHeight={6}
        rightWidth={7}
        bottomHeight={6}
        width={labelWidth}
        height={labelHeight}
        x={0}
        y={0}
        texture={textures.label}
      />
      <Text
        text={text}
        x={labelContentX}
        y={labelContentY}
        style={labelTextStyle}
      />
    </Container>
  )
}
