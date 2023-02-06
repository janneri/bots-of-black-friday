import React from 'react'
import { type Player } from './types/Player'

export default function Scoreboard ({ players }: { players: Player[] }): JSX.Element {
  return (
      <table className="font-bots table-fixed text-[0.5rem] text-zinc-50 text-left w-full">
        <thead>
        <tr>
          <th>Name</th>
          <th>Moves</th>
          <th>Health %</th>
          <th>Money left</th>
          <th>Items value</th>
        </tr>
        </thead>
        <tbody>
        {players.length === 0 && <tr key="message-none">
          <td className="w-full py-2" colSpan={5}>
            No players.
          </td>
        </tr>}
        {players.map(({ name, actionCount, health, money, score }) => {
          return <tr key={`scoreboard-${name}`} className="odd:bg-zinc-600">
            <td className="w-1/3 py-2">
              {name}
            </td>
            <td>{actionCount}</td>
            <td>{health}</td>
            <td>{money} €</td>
            <td>{score} €</td>
          </tr>
        })}
        </tbody>
      </table>
  )
}
