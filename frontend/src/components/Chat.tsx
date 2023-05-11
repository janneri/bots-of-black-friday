import React from 'react'
import { truncate } from 'lodash'

export default function Chat ({ messages }: { messages: string[] }): JSX.Element {
  return (
      <table className="table-fixed text-[0.5rem] text-left w-full">
        <thead>
        <tr>
          <th>&nbsp;</th>
        </tr>
        </thead>
        <tbody>
        {messages.length === 0 && <tr key="message-none">
          <td className="w-full">
            No messages.
          </td>
        </tr>}
        {messages.map((message, index) => {
          return <tr key={`message-${index}`} className="border-b border-zinc-50 border-solid odd:bg-zinc-600">
            <td className={index === 0 ? 'w-full p-3 pl-0' : 'w-full p-3 pl-0'}>
              {truncate(message, { length: 96 })}
            </td>
          </tr>
        })}
        </tbody>
      </table>
  )
}
