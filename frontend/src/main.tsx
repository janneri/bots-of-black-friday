import React from 'react'
import '@src/index.css'
import { Stage } from '@pixi/react'
import ReactDOM from 'react-dom/client'
import { useResizeDetector } from 'react-resize-detector'
import { Client, type IFrame, type Message } from '@stomp/stompjs'
import { type GameState } from '@src/types/GameState'
import { type GameMap } from '@src/types/GameMap'
import { type Player } from '@src/types/Player'
import { errorResult, type Result, valueResult } from '@src/utils/Result'
import { isGameMap, isGameState } from '@src/utils/typeUtils'
import { calculateMapDimensions } from '@src/utils/MapDimensions'
import Chat from '@src/components/Chat'
import Scoreboard from '@src/components/Scoreboard'
import { Toggle } from '@src/components/Toggle'
import { MapGrid } from '@src/components/MapGrid'
import { MapStaticContent } from '@src/components/MapStaticContent'
import { MapDynamicContent } from '@src/components/MapDynamicContent'
import { getMapEndpoint, getWebsocketEndpoint } from '@src/utils/endpoints'
import beerImage from '@src/assets/beer.png'

interface GameEvent {
  reason?: string
  gameState?: GameState
  newMap?: GameMap
}

function ErrorMessage ({ message }: { message: string }): JSX.Element {
  return (
    <p className="text-sm text-red-500 uppercase py-4">{message}</p>
  )
}

function MapContainer ({
  gameMap,
  gameState,
  showBeer,
  showMapGrid,
  showItemLabels
}: {
  gameMap: Result<GameMap>
  gameState: Result<GameState>
  showBeer: boolean
  showMapGrid: boolean
  showItemLabels: boolean
}): JSX.Element {
  const { width: containerWidth, ref } = useResizeDetector()

  const mapDimensions = calculateMapDimensions(
    gameMap.ok ? gameMap.value.width : undefined,
    gameMap.ok ? gameMap.value.height : undefined,
    containerWidth
  )

  return <div ref={ref}>
    {!gameMap.ok && <ErrorMessage
      message={`Game map error: ${JSON.stringify(gameMap.error, null, '  ')}`}
    />}
    {!gameState.ok && <ErrorMessage
      message={`Game state error: ${JSON.stringify(gameState.error, null, '  ')}`}
    />}
    {mapDimensions !== undefined && gameMap.ok && gameState.ok &&
      <Stage
        className="mx-auto"
        width={mapDimensions.stageWidthInPx}
        height={mapDimensions.stageHeightInPx}
        options={{ antialias: false, backgroundColor: 0xeef1f5 }}
      >
        <MapStaticContent gameMap={gameMap.value} mapDimensions={mapDimensions} />
        {showMapGrid && <MapGrid mapDimensions={mapDimensions} />}
        <MapDynamicContent
          gameState={gameState.value}
          mapDimensions={mapDimensions}
          showBeer={showBeer}
          showItemLabels={showItemLabels}
        />
      </Stage>}
  </div>
}

class App extends React.Component<unknown, {
  gameState: Result<GameState>
  gameMap: Result<GameMap>
  players: Player[]
  finishedPlayers: Player[]
  chatMessages: string[]
  showBeer: boolean
  showItemLabels: boolean
  showMapGrid: boolean
}> {
  private readonly chatTopic = '/topic/chat'
  private readonly eventTopic = '/topic/events'
  private readonly maxMessages = 10
  private stompClient: Client | undefined

  constructor (props: unknown) {
    super(props)
    this.state = {
      gameMap: errorResult('No game map.'),
      gameState: errorResult('No game state.'),
      players: [],
      finishedPlayers: [],
      chatMessages: [],
      showBeer: false,
      showItemLabels: true,
      showMapGrid: false
    }
  }

  comparePlayersByName (a: Player, b: Player): number {
    const nameA = a.name.toUpperCase()
    const nameB = b.name.toUpperCase()

    if (nameA < nameB) {
      return -1
    }
    if (nameA > nameB) {
      return 1
    }

    return 0
  }

  comparePlayersByScore (a: Player, b: Player): number {
    const scoreA = a.money + a.score
    const scoreB = b.money + b.score

    return scoreA - scoreB
  }

  derivedGameState (gameState: Result<GameState>): ({
    gameState: Result<GameState>
    players: Player[]
    finishedPlayers: Player[]
  }) {
    const players = (gameState.ok ? [...gameState.value.players] : [])
      .sort(this.comparePlayersByName)
    const finishedPlayers = (gameState.ok ? [...gameState.value.finishedPlayers] : [])
      .sort(this.comparePlayersByScore)

    return {
      gameState,
      players,
      finishedPlayers
    }
  }

  async componentDidMount (): Promise<void> {
    const gameMap = await fetch(getMapEndpoint(location.href, location.protocol))
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(`Fetching map failed: ${response.status}`)
        }

        return await response.json()
      })
      .catch((err) => err)

    this.setState((state) => ({
      ...state,
      gameMap: isGameMap(gameMap) ? valueResult(gameMap) : errorResult(gameMap)
    }))

    // Note! Using React.StrictMode causes App to mount twice in development mode.
    //       The async componentDidMount and componentWillUnmount are interleaved, and not handled
    //       correctly, this causes stomp events being handled twice.
    //       * A complex fix could be a cancellable promise.
    //       * A simpler fix could be extracting a function component and separate stomp client to a hook.
    const stompClient = new Client({
      brokerURL: getWebsocketEndpoint(location.href, location.protocol === 'https' ? 'wss' : 'ws'),
      connectionTimeout: 10_000,
      reconnectDelay: 10_000
    })

    stompClient.onConnect = () => {
      stompClient.subscribe(this.chatTopic, (message: Message) => {
        this.handleChatEvent(message)
      })

      stompClient.subscribe(this.eventTopic, (message: Message) => {
        this.handleGameEvent(message)
      })
    }

    stompClient.onDisconnect = () => {
      stompClient.unsubscribe(this.chatTopic)
      stompClient.unsubscribe(this.eventTopic)
    }

    stompClient.onWebSocketError = (event: any) => {
      this.setState((state) => ({
        ...state,
        // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
        gameMap: errorResult(`WebSocket error: ${event}`),
        // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
        ...this.derivedGameState(errorResult(`WebSocket error: ${event}`))
      }))
    }

    stompClient.onStompError = (frame: IFrame) => {
      this.setState((state) => ({
        ...state,
        gameMap: errorResult(`Stomp error: ${frame.command}`),
        ...this.derivedGameState(errorResult(`Stomp error: ${frame.command}`))
      }))
    }

    this.stompClient = stompClient
    stompClient.activate()
  }

  handleChatEvent (message: Message): void {
    try {
      const newMessage = message.body
      this.setState((state) => ({
        ...state,
        chatMessages: [
          newMessage,
          ...state.chatMessages
        ].slice(0, this.maxMessages)
      }))
    } catch (err) {
      this.setState((state) => ({
        ...state,
        chatMessages: []
      }))
    }
  }

  handleGameEvent (message: Message): void {
    try {
      const eventPayload = JSON.parse(message.body) as GameEvent
      const newMap = eventPayload.newMap
      const newState = eventPayload.gameState

      if (isGameMap(newMap)) {
        this.setState((state) => ({
          ...state,
          gameMap: valueResult(newMap)
        }))
      } else if (isGameState(newState)) {
        this.setState((state) => ({
          ...state,
          ...this.derivedGameState(valueResult(newState))
        }))
      }
    } catch (err) {
      this.setState((state) => ({
        ...state,
        gameMap: errorResult('handleGameEvent error.'),
        ...this.derivedGameState(errorResult('handleGameEvent error.'))
      }))
    }
  }

  handleToggle (key: 'showBeer' | 'showItemLabels' | 'showMapGrid', value: boolean): void {
    this.setState((state) => ({
      ...state,
      [key]: value
    }))
  }

  async componentWillUnmount (): Promise<void> {
    const clientToBeDestroyed = this.stompClient
    this.stompClient = undefined

    if (clientToBeDestroyed !== undefined) {
      await clientToBeDestroyed.deactivate()
      clientToBeDestroyed.forceDisconnect()
    }
  }

  render (): JSX.Element {
    const {
      gameState,
      gameMap,
      players,
      finishedPlayers,
      chatMessages,
      showBeer,
      showMapGrid,
      showItemLabels
    } = this.state

    return (
      <div className="mx-auto min-h-screen bg-zinc-900">
        <header className="flex flex-row sticky top-0 p-4 bg-zinc-800 border-4 border-zinc-800 border-b-zinc-400">
          <div className="flex-1 self-center">
            <h2 className="text-xs">Bots of Black Friday</h2>
          </div>
          <div>
            {gameMap.ok &&
              <h1 className="text-transparent text-2xl bg-clip-text bg-gradient-to-b from-amber-800 to-amber-100">
                {gameMap.value.name}
              </h1>
            }
          </div>
          <div className="flex-1"></div>
        </header>
        <div className="p-4">
          <MapContainer
            gameMap={gameMap}
            gameState={gameState}
            showBeer={showBeer}
            showMapGrid={showMapGrid}
            showItemLabels={showItemLabels}
          />
          <div className="flex flex-row gap-4 pt-4">
            <div className="flex-1">
              <Toggle
                id="show-map-grid"
                label="Map grid"
                checked={showMapGrid}
                onChange={(v) => { this.handleToggle('showMapGrid', v) }}
              />
            </div>
            <div className="flex-1">
              <Toggle
                id="show-item-labels"
                label="Item labels"
                checked={showItemLabels}
                onChange={(v) => { this.handleToggle('showItemLabels', v) }}
              />
            </div>
            <div className="flex-1">
              <Toggle
                id="show-beer"
                label={<img className="inline" src={beerImage} alt="🍺" />}
                checked={showBeer}
                onChange={(v) => { this.handleToggle('showBeer', v) }}
              />
            </div>
          </div>
          <div className="flex flex-row gap-4 pt-4">
            <div className="flex-1 rounded bg-zinc-700 p-4">
              <h3 className="text-sm uppercase pb-4">
                Active players
              </h3>
              <Scoreboard players={players} />
            </div>
            <div className="flex-1 rounded bg-zinc-700 p-4">
              <h3 className="text-sm uppercase pb-4">
                Scoreboard
              </h3>
              <Scoreboard players={finishedPlayers} />
            </div>
            <div className="flex-1 rounded bg-zinc-700 p-4">
              <h3 className="text-sm uppercase pb-4">
                Chat
              </h3>
              <Chat messages={chatMessages} />
            </div>
          </div>
        </div>
      </div>
    )
  }
}

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
)
