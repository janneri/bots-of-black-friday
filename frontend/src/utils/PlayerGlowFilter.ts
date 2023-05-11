import type { CLEAR_MODES, FilterSystem, RenderTexture } from '@pixi/core'
import { Filter, utils } from '@pixi/core'
import vertex from '@src/utils/default.vert'
import fragment from '@src/utils/playerGlow.frag'

export interface PlayerGlowFilterOptions {
  distance: number
  outerStrength: number
  color: number
  quality: number
  alpha: number
}

export class PlayerGlowFilter extends Filter {
  /** Default values for options. */
  static readonly defaults: PlayerGlowFilterOptions = {
    distance: 12,
    outerStrength: 4,
    color: 0xffffff,
    quality: 0.5,
    alpha: 1
  }

  public time: number

  /**
   * Modified from @pixi/filter-glow
   */
  constructor (options?: Partial<PlayerGlowFilterOptions>, time = 0) {
    const opts: PlayerGlowFilterOptions = Object.assign({}, PlayerGlowFilter.defaults, options)
    const {
      outerStrength,
      color,
      quality,
      alpha
    } = opts

    const distance = Math.round(opts.distance)

    super(vertex, fragment
      .replace(/__ANGLE_STEP_SIZE__/gi, `${(1 / quality / distance).toFixed(7)}`)
      .replace(/__DIST__/gi, `${distance.toFixed(0)}.0`))

    this.uniforms.glowColor = new Float32Array([0, 0, 0, 1])
    this.uniforms.alpha = 1

    Object.assign(this, {
      color,
      outerStrength,
      padding: distance,
      alpha
    })

    this.time = time
  }

  apply (filterManager: FilterSystem, input: RenderTexture, output: RenderTexture, clear: CLEAR_MODES): void {
    this.uniforms.time = this.time

    filterManager.applyFilter(this, input, output, clear)
  }

  get color (): number {
    return utils.rgb2hex(this.uniforms.glowColor)
  }

  set color (value: number) {
    utils.hex2rgb(value, this.uniforms.glowColor)
  }

  get outerStrength (): number {
    return this.uniforms.outerStrength
  }

  set outerStrength (value: number) {
    this.uniforms.outerStrength = value
  }

  get alpha (): number {
    return this.uniforms.alpha
  }

  set alpha (value: number) {
    this.uniforms.alpha = value
  }
}
