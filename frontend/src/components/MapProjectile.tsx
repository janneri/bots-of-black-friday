import React, { useState } from 'react'
import { useSpring } from '@react-spring/web'
import { Sprite as AnimatedSprite } from '@pixi/react-animated'
import { textures } from '@src/utils/textures'
import { type PixelPosition, toPixelPosition } from '@src/utils/toPixelPosition'
import { type Position } from '@src/types/Position'
import { zIndex } from '@src/utils/zIndex'
import { type MapDimensions } from '@src/utils/MapDimensions'

/**
 * Sprite must be oriented originally upwards.
 *
 * @param originX
 * @param originY
 * @param targetX
 * @param targetY
 */
function rotateSpriteToTarget (
  { xInPx: originX, yInPx: originY }: PixelPosition,
  { xInPx: targetX, yInPx: targetY }: PixelPosition
): number {
  const shiftedOriginX = 0
  const shiftedOriginY = -15
  const shiftedTargetX = targetX - originX
  const shiftedTargetY = targetY - originY

  const angle = Math.atan2(
    (shiftedTargetY * shiftedOriginX) - (shiftedTargetX * shiftedOriginY),
    (shiftedTargetX * shiftedOriginX) + (shiftedTargetY * shiftedOriginY)
  )
  return angle
}

interface ProjectileProperties {
  fromPosition: Position
  toPosition: Position
  mapDimensions: MapDimensions
  animate: boolean
}

export const MapProjectile: React.FunctionComponent<ProjectileProperties> = function ({
  fromPosition,
  toPosition,
  mapDimensions: {
    tileWidthInPx,
    halfTileWidthInPx
  },
  animate
}) {
  const from = toPixelPosition(fromPosition, tileWidthInPx, halfTileWidthInPx)
  const to = toPixelPosition(toPosition, tileWidthInPx, halfTileWidthInPx)
  const [rotation] = useState(rotateSpriteToTarget(from, to))

  const props = useSpring({
    from: { x: from.xInPx, y: from.yInPx },
    to: { x: to.xInPx, y: to.yInPx },
    config: {
      clamp: true
    }
  })

  return (<AnimatedSprite
    texture={textures.energyBall}
    anchor={[0.5, 0.5]}
    width={tileWidthInPx}
    height={tileWidthInPx}
    rotation={rotation}
    zIndex={zIndex.projectile}
    {...(animate ? props : { x: from.xInPx, y: from.yInPx })}
  />)
}
