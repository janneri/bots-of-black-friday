import React from 'react'

export default function Chat ({ messages }: { messages: string[] }): JSX.Element {
  return (
      <table className="font-bots table-fixed text-[0.5rem] text-zinc-50 text-left w-full">
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
          return <tr key={`message-${index}`} className="border-b border-zinc-50 border-dashed odd:bg-zinc-600">
            <td className={index === 0 ? 'w-full pb-2' : 'w-full py-2'}>
              {message}
            </td>
          </tr>
        })}
        </tbody>
      </table>
  )
}
