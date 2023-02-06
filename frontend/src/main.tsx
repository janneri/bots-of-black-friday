import React, { createRef } from 'react'
import ReactDOM from 'react-dom/client'
import { useResizeDetector } from 'react-resize-detector'
import { has } from 'lodash'
import { Client, type IFrame, type Message } from '@stomp/stompjs'
import { type GameState } from './types/GameState'
import { type GameMap } from './types/GameMap'
import { type Player } from './types/Player'
import Chat from './Chat'
import Map from './Map'
import Scoreboard from './Scoreboard'
import beerImage from './assets/beer.png'
import { errorResult, type Result, valueResult } from './Result'
import './index.css'

interface GameStateChanged {
  reason?: string
  gameState?: GameState
  newMap?: GameMap
}

function isGameState (obj: any): obj is GameState {
  return has(obj, ['players']) &&
    has(obj, ['finishedPlayers']) &&
    has(obj, ['items']) &&
    has(obj, ['round']) &&
    has(obj, ['shootingLines'])
}

function isGameMap (obj: any): obj is GameMap {
  return has(obj, ['width']) &&
    has(obj, ['height']) &&
    has(obj, ['maxItemCount']) &&
    has(obj, ['tiles']) &&
    has(obj, ['name']) &&
    has(obj, ['exit'])
}

function ErrorMessage ({ message }: { message: string }): JSX.Element {
  return (
    <p className="font-bots text-sm text-red-500 uppercase py-4">{message}</p>
  )
}

function ContainerWidth ({ containerRef, children }: {
  children: (containerWidth: number) => JSX.Element
  containerRef: React.RefObject<any>
}): JSX.Element {
  const { width: containerWidth } = useResizeDetector({ targetRef: containerRef })

  if (containerWidth === undefined) {
    return (
      <p className="font-bots text-sm text-zinc-50 uppercase py-4">Loading...</p>
    )
  }

  return <>
    {children(containerWidth)}
  </>
}

function Toggle ({ id, label, checked, onChange }: {
  id: string
  label: string | JSX.Element
  checked: boolean
  onChange: (val: boolean) => void
}): JSX.Element {
  /* Original HTML / Tailwind CSS Source: https://tailwindcomponents.com/component/toggle-switch */
  return <div>
    <div
      className="relative inline-block w-10 mr-2 align-middle select-none transition duration-200 ease-in"
    >
      <input
        type="checkbox"
        name={id}
        id={id}
        checked={checked}
        className="toggle-checkbox absolute block w-6 h-6 rounded-full bg-white border-4 appearance-none cursor-pointer"
        onChange={(e) => {
          onChange(e.target.checked)
        }}
      />
      <label
        htmlFor={id}
        className="toggle-label block overflow-hidden h-6 rounded-full bg-gray-300 cursor-pointer"></label>
    </div>
    <label htmlFor={id} className="font-bots text-[0.5rem] text-zinc-50">{label}</label>
  </div>
}

class App extends React.Component<unknown, {
  gameState: Result<GameState>
  gameMap: Result<GameMap>
  chatMessages: string[]
  showBeer: boolean
  showItemLabels: boolean
  showMapGrid: boolean
}> {
  private readonly maxMessages = 10
  private stompClient: Client | undefined
  private readonly containerRef: React.RefObject<any>

  constructor (props: unknown) {
    super(props)
    this.containerRef = createRef()
    this.state = {
      gameMap: errorResult('No game map.'),
      gameState: errorResult('No game state.'),
      chatMessages: [],
      showBeer: false,
      showItemLabels: true,
      showMapGrid: false
    }
  }

  async componentDidMount (): Promise<void> {
    const gameMap = await fetch(import.meta.env.VITE_MAP_ENDPOINT)
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

    const stompClient = new Client({
      brokerURL: import.meta.env.VITE_WEBSOCKET_ENDPOINT,
      connectionTimeout: 10_000,
      reconnectDelay: 10_000
    })

    stompClient.onConnect = () => {
      stompClient.subscribe('/topic/chat', (message: Message) => {
        this.handleChatEvent(message)
      })

      stompClient.subscribe('/topic/events', (message: Message) => {
        this.handleGameEvent(message)
      })
    }

    stompClient.onWebSocketError = (event: any) => {
      this.setState((state) => ({
        ...state,
        // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
        gameMap: errorResult(`WebSocket error: ${event}`),
        // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
        gameState: errorResult(`WebSocket error: ${event}`)
      }))
    }

    stompClient.onStompError = (frame: IFrame) => {
      this.setState((state) => ({
        ...state,
        gameMap: errorResult(`Stomp error: ${frame.command}`),
        gameState: errorResult(`Stomp error: ${frame.command}`)
      }))
    }

    this.stompClient = stompClient
    stompClient.activate()
  }

  handleChatEvent (message: Message): void {
    const newMessage = message.body
    this.setState((state) => ({
      ...state,
      chatMessages: [
        newMessage,
        ...state.chatMessages.slice(0, this.maxMessages - 1)
      ]
    }))
  }

  handleGameEvent (message: Message): void {
    const stateChange = JSON.parse(message.body) as GameStateChanged
    const newMap = stateChange.newMap
    const newState = stateChange.gameState

    if (isGameMap(newMap)) {
      this.setState((state) => ({
        ...state,
        gameMap: valueResult(newMap)
      }))
    } else if (isGameState(newState)) {
      this.setState((state) => ({
        ...state,
        gameState: valueResult(newState)
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
    await this.stompClient?.deactivate()
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

  render (): JSX.Element {
    const {
      gameState,
      gameMap,
      chatMessages,
      showBeer,
      showMapGrid,
      showItemLabels
    } = this.state

    const sortedPlayers = (gameState.ok ? [...gameState.value.players] : [])
      .sort(this.comparePlayersByName)
    const sortedFinishedPlayers = (gameState.ok ? [...gameState.value.finishedPlayers] : [])
      .sort(this.comparePlayersByScore)

    return (
      <>
        <div className="mx-auto min-h-screen bg-zinc-900 tracking-wider">
          <header className="flex flex-row sticky top-0 p-4 bg-zinc-800 border-4 border-zinc-800 border-b-zinc-400">
            <div className="flex-1 self-center">
              <h2 className="font-bots text-xs text-zinc-50">Bots of Black Friday</h2>
            </div>
            <div>
              {gameMap.ok &&
                <h1 className="font-bots text-transparent text-2xl bg-clip-text bg-gradient-to-b from-amber-800 to-amber-100">
                  {gameMap.value.name}
                </h1>
              }
            </div>
            <div className="flex-1"></div>
          </header>
          <div ref={this.containerRef} className="p-4">
            {!gameMap.ok && <ErrorMessage
              message={`Game map error: ${JSON.stringify(gameMap.error, null, '  ')}`}
            />}
            {!gameState.ok && <ErrorMessage
              message={`Game state error: ${JSON.stringify(gameState.error, null, '  ')}`}
            />}
            {gameMap.ok && gameState.ok && <ContainerWidth containerRef={this.containerRef}>
              {(containerWidth) => <Map
                gameMap={gameMap.value}
                gameState={gameState.value}
                containerWidth={containerWidth}
                showBeer={showBeer}
                showMapGrid={showMapGrid}
                showItemLabels={showItemLabels}
              />}
            </ContainerWidth>}
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
                <h3 className="font-bots text-sm text-zinc-50 uppercase pb-4">
                  Active players
                </h3>
                <Scoreboard players={sortedPlayers} />
              </div>
              <div className="flex-1 rounded bg-zinc-700 p-4">
                <h3 className="font-bots text-sm text-zinc-50 uppercase pb-4">
                  Scoreboard
                </h3>
                <Scoreboard players={sortedFinishedPlayers} />
              </div>
              <div className="flex-1 rounded bg-zinc-700 p-4">
                <h3 className="font-bots text-sm text-zinc-50 uppercase pb-4">
                  Chat
                </h3>
                <Chat messages={chatMessages} />
              </div>
            </div>
            <div className="font-bots text-[0.5rem] text-zinc-50 rounded bg-zinc-700 mt-4 p-4">
              <h3 className="uppercase pb-4">
                Asset sources
              </h3>
              <ul>
                <li className="pb-2 underline"><a href="https://pixel-boy.itch.io/ninja-adventure-asset-pack">Pixel-Boy&apos;s ninja adventure asset pack</a></li>
                <li className="pb-2 underline"><a href="https://alifdoll.itch.io/pixel-food-asset">Alifdoll&apos;s pixel food</a></li>
              </ul>
            </div>
          </div>
        </div>
      </>
    )
  }
}

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
)
