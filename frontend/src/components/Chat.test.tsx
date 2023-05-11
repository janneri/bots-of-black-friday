import React from 'react'
import { render, screen } from '@src/tests/unitTestSetup'
import Chat from '@src/components/Chat'

describe('Chat', () => {
  it('should render without messages', () => {
    render(
      <Chat messages={[]} />
    )

    expect(screen.getByText('No messages.')).toBeInTheDocument()
  })
})
