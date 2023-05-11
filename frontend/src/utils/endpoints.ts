function getProtocol (url: string): string {
  const protocol = url.split('//')
  if (Array.isArray(protocol) && protocol.length > 0) {
    return protocol[0]
  }

  return 'http:'
}

function getRelativeUrl (relativePath: string, currentLocation: string, endpointProtocol: string): string {
  const url = new URL(relativePath, currentLocation)
  url.protocol = endpointProtocol

  return url.toString()
}

export function getMapEndpoint (currentLocation: string, isDev: boolean): string {
  if (isDev) {
    return 'http://localhost:8080/map'
  }

  return getRelativeUrl('/map', currentLocation, getProtocol(currentLocation))
}

export function getWebsocketEndpoint (currentLocation: string, isDev: boolean): string {
  if (isDev) {
    return 'ws://localhost:8080/hello'
  }

  const endpointProtocol = getProtocol(currentLocation) === 'https:' ? 'wss' : 'ws'
  return getRelativeUrl('/hello', currentLocation, endpointProtocol)
}
