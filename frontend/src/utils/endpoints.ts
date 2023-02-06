import { isEmpty, isString } from 'lodash'

function getRelativeUrl (relativePath: string, currentLocation: string, endpointProtocol: string): string {
  const url = new URL(relativePath, currentLocation)
  url.protocol = endpointProtocol

  return url.toString()
}

export function getMapEndpoint (currentLocation: string, endpointProtocol: string): string {
  if (import.meta.env.DEV) {
    const configured = import.meta.env.VITE_MAP_ENDPOINT
    if (isString(configured) && !isEmpty(configured)) {
      return configured
    }

    return 'http://localhost:8080/map'
  }

  return getRelativeUrl('/map', currentLocation, endpointProtocol)
}

export function getWebsocketEndpoint (currentLocation: string, endpointProtocol: string): string {
  if (import.meta.env.DEV) {
    const configured = import.meta.env.VITE_WEBSOCKET_ENDPOINT
    if (isString(configured) && !isEmpty(configured)) {
      return configured
    }

    return 'ws://localhost:8080/hello'
  }

  return getRelativeUrl('/hello', currentLocation, endpointProtocol)
}
