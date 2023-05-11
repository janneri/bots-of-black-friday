import React from 'react'
import { render, screen } from '@src/tests/unitTestSetup'
import Scoreboard from '@src/components/Scoreboard'

describe('Scoreboard', () => {
  it('should render without players', () => {
    render(
      <Scoreboard players={[]} />
    )

    expect(screen.getByText('No players.')).toBeInTheDocument()
  })
})
