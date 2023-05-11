import React from 'react'
import { Sprite, Stage, useApp } from '@pixi/react'
import ReactDOM from 'react-dom/client'
import '@src/index.css'
import { calculateMapDimensions } from '@src/utils/MapDimensions'
import { MapGrid } from '@src/components/MapGrid'
import { FPS } from 'yy-fps'
import { Texture } from 'pixi.js'
import { type PixelPosition, toPixelPosition } from '@src/utils/toPixelPosition'

const mapWidth = 17
const mapHeight = 13

export const stageDimensions: Readonly<{
  containerWidthInPx: number
  mapWidth: number
  mapHeight: number
  middle: {
    x: number
    y: number
  }
}> = {
  containerWidthInPx: 750,
  mapWidth,
  mapHeight,
  middle: {
    x: Math.floor(mapWidth / 2),
    y: Math.floor(mapHeight / 2)
  }
}

// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
export const mapDimensions = calculateMapDimensions(
  stageDimensions.mapWidth,
  stageDimensions.mapHeight,
  stageDimensions.containerWidthInPx
)!

export function getCellX (thunk: () => number): number {
  const cellIndex = thunk()

  if (cellIndex > mapDimensions.width - 1) {
    throw Error('Cell index exceeds map width.')
  }

  return cellIndex
}

export function getCellY (thunk: () => number): number {
  const cellIndex = thunk()

  if (cellIndex > mapDimensions.height - 1) {
    throw Error('Cell index exceeds map height.')
  }

  return cellIndex
}

function TestStageReady (): JSX.Element {
  const app = useApp()
  app.renderer.on('postrender', () => {
    const el = document.getElementById('test-stage-ready')
    if (el !== null && el.style.display !== 'block') {
      el.textContent = 'TEST RENDERED'
      el.style.display = 'block'
    }
  })

  // NOTE! This is outside the stage on purpose.
  const position = toPixelPosition(
    { x: stageDimensions.mapWidth, y: stageDimensions.mapHeight },
    mapDimensions.tileWidthInPx,
    mapDimensions.halfTileWidthInPx
  )

  return <FilledTile position={position} />
}

export const TestStage: React.FC<React.PropsWithChildren> = function ({
  children
}) {
  return <Stage
    className="mx-auto"
    width={mapDimensions.stageWidthInPx}
    height={mapDimensions.stageHeightInPx}
    options={{ antialias: false, backgroundColor: 0xeef1f5 }}
  >
    <MapGrid mapDimensions={mapDimensions} />
    <TestStageReady />
    {children}
  </Stage>
}

export function FilledTile ({
  position: {
    xInPx,
    yInPx
  }
}: {
  position: PixelPosition
}): JSX.Element {
  return <Sprite
    anchor={[0.5, 0.5]}
    x={xInPx}
    y={yInPx}
    width={mapDimensions.tileWidthInPx}
    height={mapDimensions.tileWidthInPx}
    texture={Texture.WHITE}
    tint={0x991B4B}
  />
}

export const TestContainer: React.FC<React.PropsWithChildren> = function ({
  children
}) {
  return <div id="container" className="container mx-auto">
    {children}
    <div
      id="test-stage-ready"
      data-testid="test-stage-ready"
      className="w-fit mx-auto p-2 bg-green-500 text-center text-zinc-900 hidden"
    ></div>
  </div>
}

export function renderTest (content: JSX.Element): void {
  const fpsCounter = new FPS()

  const animate = (): void => {
    fpsCounter.frame()
    requestAnimationFrame(animate)
  }

  requestAnimationFrame(animate)

  ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
    <React.StrictMode>
      <TestContainer>
        <TestStage>
          {content}
        </TestStage>
      </TestContainer>
    </React.StrictMode>
  )
}
