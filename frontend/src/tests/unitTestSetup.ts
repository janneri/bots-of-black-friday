import { afterEach, expect } from 'vitest'
import { cleanup, render } from '@testing-library/react'
import matchers from '@testing-library/jest-dom/matchers'

expect.extend(matchers)

afterEach(() => {
  cleanup()
})

function customRender (ui: JSX.Element, options = {}): ReturnType<typeof render> {
  return render(ui, {
    // wrap provider(s) here if needed
    wrapper: ({ children }) => children,
    ...options
  })
}

// eslint-disable-next-line import/export
export * from '@testing-library/react'
export { default as userEvent } from '@testing-library/user-event'

// override render export
// eslint-disable-next-line import/export
export { customRender as render }
