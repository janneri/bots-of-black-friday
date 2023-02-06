import { type Position } from '@src/types/Position'

export interface PixelPosition {
  xInPx: number
  yInPx: number
}

export function toPixelPosition (
  { x, y }: Position,
  tileWidthInPx: number,
  halfTileWidthInPx: number
): PixelPosition {
  return {
    xInPx: (x * tileWidthInPx) + halfTileWidthInPx,
    yInPx: (y * tileWidthInPx) + halfTileWidthInPx
  }
}
