import React from 'react'
import { Container } from '@pixi/react'
import { isNull } from 'lodash'
import { type GameState } from '@src/types/GameState'
import { type Item } from '@src/types/Item'
import { type MapDimensions } from '@src/utils/MapDimensions'
import { getItemTexture, getPlayerTexture } from '@src/utils/textures'
import { toPixelPosition } from '@src/utils/toPixelPosition'
import { zIndex } from '@src/utils/zIndex'
import { MapItem } from '@src/components/MapItem'
import { getPlayerTint, MapPlayer } from '@src/components/MapPlayer'
import { labelFilters } from '@src/utils/labelFilters'
import { MapProjectile } from '@src/components/MapProjectile'
import { MapLabel } from '@src/components/MapLabel'

function getItemLabelText ({ type, price, discountPercent }: Item): string | null {
  if (type === 'JUST_SOME_JUNK') {
    return discountPercent > 0 ? `${price} €\n-${discountPercent} %` : `${price} €`
  }

  if (type === 'WEAPON') {
    return `${price} €`
  }

  return null
}

function itemIsWeapon (item: Item): boolean {
  return item.isUsable && item.type === 'WEAPON'
}

export function MapDynamicContent ({
  gameState: {
    items,
    players,
    shootingLines
  },
  mapDimensions,
  showBeer,
  showItemLabels
}: {
  gameState: GameState
  mapDimensions: MapDimensions
  showBeer: boolean
  showItemLabels: boolean
}): JSX.Element {
  const itemSprites = items.map((item) => {
    const texture = getItemTexture(item, showBeer)

    return <MapItem
      key={`item-${item.type}-${item.position.x}-${item.position.y}`}
      item={item}
      mapDimensions={mapDimensions}
      texture={texture}
    />
  })

  const itemLabelSprites = (showItemLabels ? items : [])
    .map((item) => ([item, getItemLabelText(item)]))
    .filter((pair): pair is [Item, string] => !isNull(pair[1]))
    .map(([item, labelText]) => {
      const itemPosition = toPixelPosition(
        item.position,
        mapDimensions.tileWidthInPx,
        mapDimensions.halfTileWidthInPx
      )

      return <MapLabel
        key={`item-${item.type}-${item.position.x}-${item.position.y}-label`}
        text={labelText}
        mapDimensions={mapDimensions}
        itemPosition={itemPosition}
        filters={labelFilters.item}
        zIndex={zIndex.itemLabel}
      />
    })

  const playerSprites = players.map((player) => {
    const hasWeapon = player.usableItems.find(itemIsWeapon) !== undefined

    return <MapPlayer
      key={`player-${player.name}`}
      player={player}
      mapDimensions={mapDimensions}
      texture={getPlayerTexture(player.name)}
      tint={getPlayerTint(player.name)}
      hasWeapon={hasWeapon}
    />
  })

  const playerLabelSprites = players.map(({ name, position, timeInState }) => {
    const playerPosition = toPixelPosition(
      position,
      mapDimensions.tileWidthInPx,
      mapDimensions.halfTileWidthInPx
    )

    const label = timeInState > 0 ? `${name}\n(${timeInState})` : name

    return <MapLabel
      key={`player-${name}-label`}
      text={label}
      mapDimensions={mapDimensions}
      itemPosition={playerPosition}
      filters={labelFilters.player}
      zIndex={zIndex.playerLabel}
    />
  })

  const projectiles = shootingLines
    .filter(({ age }) => age < 2)
    .map(({ fromPosition, toPosition }) => {
      return <MapProjectile
        key={`projectile-${fromPosition.x}-${fromPosition.y}-to-${toPosition.x}-${toPosition.y}`}
        fromPosition={fromPosition}
        toPosition={toPosition}
        mapDimensions={mapDimensions}
        animate={true}
      />
    })

  return (
    <Container position={[0, 0]} sortableChildren={true}>
      {itemSprites}
      {playerSprites}
      {projectiles}
      {itemLabelSprites}
      {playerLabelSprites}
    </Container>
  )
}
