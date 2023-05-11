import { getMapEndpoint, getWebsocketEndpoint } from '@src/utils/endpoints'

describe('Endpoints', () => {
  interface TestCase {
    readonly description: string
    readonly currentLocation: string
    readonly expected: string
  }

  describe('getMapEndpoint', () => {
    describe('in development', () => {
      const expectedValue = 'http://localhost:8080/map'

      const devTestCases: readonly TestCase[] = [
        {
          description: 'should return static value for http protocol on localhost',
          currentLocation: 'http://localhost:8080',
          expected: expectedValue
        },
        {
          description: 'should return static value for https protocol on localhost',
          currentLocation: 'https://localhost:8080',
          expected: expectedValue
        },
        {
          description: 'should return static value for http protocol on public domain',
          currentLocation: 'http://bots-of-black-friday.azurewebsites.net',
          expected: expectedValue
        },
        {
          description: 'should return static value for https protocol on public domain',
          currentLocation: 'https://bots-of-black-friday.azurewebsites.net',
          expected: expectedValue
        }
      ]

      devTestCases.forEach(({ description, currentLocation, expected }) => {
        it(description, () => {
          const endpoint = getMapEndpoint(
            currentLocation,
            true
          )

          expect(endpoint).toEqual(expected)
        })
      })
    })

    describe('in production', () => {
      const prodTestCases: readonly TestCase[] = [
        {
          description: 'should return derived value for http protocol on localhost',
          currentLocation: 'http://localhost:8080',
          expected: 'http://localhost:8080/map'
        },
        {
          description: 'should return derived value for https protocol on localhost',
          currentLocation: 'https://localhost:8080',
          expected: 'https://localhost:8080/map'
        },
        {
          description: 'should return derived value for http protocol on public domain',
          currentLocation: 'http://bots-of-black-friday.azurewebsites.net',
          expected: 'http://bots-of-black-friday.azurewebsites.net/map'
        },
        {
          description: 'should return derived value for https protocol on public domain',
          currentLocation: 'https://bots-of-black-friday.azurewebsites.net',
          expected: 'https://bots-of-black-friday.azurewebsites.net/map'
        }
      ]

      prodTestCases.forEach(({ description, currentLocation, expected }) => {
        it(description, () => {
          import.meta.env.DEV = false
          const endpoint = getMapEndpoint(
            currentLocation,
            false
          )

          expect(endpoint).toEqual(expected)
        })
      })
    })
  })

  describe('getWebsocketEndpoint', () => {
    describe('in development', () => {
      const expectedValue = 'ws://localhost:8080/hello'

      const devTestCases: readonly TestCase[] = [
        {
          description: 'should return static value for http protocol on localhost',
          currentLocation: 'http://localhost:8080',
          expected: expectedValue
        },
        {
          description: 'should return static value for https protocol on localhost',
          currentLocation: 'https://localhost:8080',
          expected: expectedValue
        },
        {
          description: 'should return static value for http protocol on public domain',
          currentLocation: 'http://bots-of-black-friday.azurewebsites.net',
          expected: expectedValue
        },
        {
          description: 'should return static value for https protocol on public domain',
          currentLocation: 'https://bots-of-black-friday.azurewebsites.net',
          expected: expectedValue
        }
      ]

      devTestCases.forEach(({ description, currentLocation, expected }) => {
        it(description, () => {
          const endpoint = getWebsocketEndpoint(
            currentLocation,
            true
          )

          expect(endpoint).toEqual(expected)
        })
      })
    })

    describe('in production', () => {
      const prodTestCases: readonly TestCase[] = [
        {
          description: 'should return derived value for http protocol on localhost',
          currentLocation: 'http://localhost:8080',
          expected: 'ws://localhost:8080/hello'
        },
        {
          description: 'should return derived value for https protocol on localhost',
          currentLocation: 'https://localhost:8080',
          expected: 'wss://localhost:8080/hello'
        },
        {
          description: 'should return derived value for http protocol on public domain',
          currentLocation: 'http://bots-of-black-friday.azurewebsites.net',
          expected: 'ws://bots-of-black-friday.azurewebsites.net/hello'
        },
        {
          description: 'should return derived value for https protocol on public domain',
          currentLocation: 'https://bots-of-black-friday.azurewebsites.net',
          expected: 'wss://bots-of-black-friday.azurewebsites.net/hello'
        }
      ]

      prodTestCases.forEach(({ description, currentLocation, expected }) => {
        it(description, () => {
          import.meta.env.DEV = false
          const endpoint = getWebsocketEndpoint(
            currentLocation,
            false
          )

          expect(endpoint).toEqual(expected)
        })
      })
    })
  })
})
