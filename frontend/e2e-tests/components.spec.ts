import { test, expect } from '@playwright/test'

test.describe('Components', () => {
  const testCases: Array<{
    name: string
    path: string
  }> = [
    {
      name: 'MapItem',
      path: 'mapItem'
    },
    {
      name: 'MapLabel',
      path: 'mapLabel'
    },
    {
      name: 'MapPlayer',
      path: 'mapPlayer'
    },
    {
      name: 'MapProjectile',
      path: 'mapProjectile'
    },
    {
      name: 'MapStaticContent',
      path: 'mapStaticContent'
    }
  ]

  testCases.forEach(({ name, path }) => {
    test(name, async ({ page }) => {
      await page.goto(`/tests/${path}/index.html`, { waitUntil: 'networkidle' })

      // Note! Fonts are not rendered correctly.
      // await page.evaluate(async () => await document.fonts.ready)
      // await page.waitForFunction(async () => await document.fonts.ready)
      // await page.waitForFunction(() => document.fonts.check('12px "Press Start 2P", Courier'))
      // await page.waitForFunction(() => {
      //   return [...document.fonts].find(({ family }) => family === '"Press Start 2P"') !== undefined
      // })

      await page.getByTestId('test-stage-ready').waitFor()
      await expect(page.getByTestId('test-stage-ready')).toHaveText('TEST RENDERED')

      await expect(page.locator('#container canvas')).toHaveScreenshot()
    })
  })
})
