import { SCALE_MODES, Texture } from 'pixi.js'
import { pickByString } from '@src/utils/pickByString'
import wallImage from '@src/assets/wall.png'
import floorImage from '@src/assets/floor.jpg'
import mineImage from '@src/assets/mine.png'
import exitImage from '@src/assets/exit.png'
import junkOneImage from '@src/assets/junk-001.png'
import junkTwoImage from '@src/assets/junk-002.png'
import junkThreeImage from '@src/assets/junk-003.png'
import potionImage from '@src/assets/potion.png'
import weaponImage from '@src/assets/weapon.png'
import labelImage from '@src/assets/label.png'
import playerOneImage from '@src/assets/player-001.png'
import playerTwoImage from '@src/assets/player-002.png'
import playerThreeImage from '@src/assets/player-003.png'
import playerFourImage from '@src/assets/player-004.png'
import playerFiveImage from '@src/assets/player-005.png'
import energyBallImage from '@src/assets/energyball.png'
import beerImage from '@src/assets/beer.png'
import { type Item } from '@src/types/Item'

const textureOptions = {
  scaleMode: SCALE_MODES.NEAREST
}

export const textures: Readonly<{
  wall: Texture
  floor: Texture
  mine: Texture
  exit: Texture
  potion: Texture
  weapon: Texture
  label: Texture
  energyBall: Texture
  beer: Texture
}> = {
  wall: Texture.from(wallImage, textureOptions),
  floor: Texture.from(floorImage, textureOptions),
  mine: Texture.from(mineImage, textureOptions),
  exit: Texture.from(exitImage, textureOptions),
  potion: Texture.from(potionImage, textureOptions),
  weapon: Texture.from(weaponImage, textureOptions),
  label: Texture.from(labelImage, textureOptions),
  energyBall: Texture.from(energyBallImage, textureOptions),
  beer: Texture.from(beerImage, textureOptions)
}

export const junkTextures: readonly Texture[] = [
  Texture.from(junkOneImage, textureOptions),
  Texture.from(junkTwoImage, textureOptions),
  Texture.from(junkThreeImage, textureOptions)
]

export function getJunkTexture (x: number, y: number): Texture {
  return pickByString(`${x}${y}`, junkTextures)
}

export function getItemTexture ({ type, position }: Item, showBeer: boolean): Texture {
  if (type === 'WEAPON') {
    return textures.weapon
  }

  if (type === 'POTION') {
    return showBeer ? textures.beer : textures.potion
  }

  return getJunkTexture(position.x, position.y)
}

export const playerTextures: readonly Texture[] = [
  Texture.from(playerOneImage, textureOptions),
  Texture.from(playerTwoImage, textureOptions),
  Texture.from(playerThreeImage, textureOptions),
  Texture.from(playerFourImage, textureOptions),
  Texture.from(playerFiveImage, textureOptions)
]

export function getPlayerTexture (name: string): Texture {
  return pickByString(name, playerTextures)
}
