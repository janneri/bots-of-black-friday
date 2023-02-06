export interface MapDimensions {
  width: number
  height: number
  containerWidthInPx: number
  tileWidthInPx: number
  halfTileWidthInPx: number
  stageWidthInPx: number
  stageHeightInPx: number
}

export function calculateMapDimensions (
  width: number | undefined,
  height: number | undefined,
  containerWidthInPx: number | undefined
): MapDimensions | undefined {
  if (width === undefined || height === undefined) {
    return
  }

  if (containerWidthInPx === undefined) {
    return
  }

  const tileWidthInPx = Math.floor(containerWidthInPx / width)

  return {
    width,
    height,
    containerWidthInPx,
    tileWidthInPx,
    halfTileWidthInPx: Math.round(tileWidthInPx / 2),
    stageWidthInPx: width * tileWidthInPx,
    stageHeightInPx: height * tileWidthInPx
  }
}
