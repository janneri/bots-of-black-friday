import { AlphaFilter } from 'pixi.js'

export const labelFilters: Readonly<{
  item: AlphaFilter[]
  player: AlphaFilter[]
}> = {
  item: [new AlphaFilter(0.6)],
  player: [new AlphaFilter(1)]
}
