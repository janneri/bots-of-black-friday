/**
 *
 * @jsx React.DOM
 */
"use strict";

var React = require('react');

var MAP_WIDTH = 800;
var MAP_HEIGHT = 800;

var SVGComponent = React.createClass({
  render: function() {
    return this.transferPropsTo(
      <svg>{this.props.children}</svg>
    );
  }
});

var Rectangle = React.createClass({
  render: function() {
    return this.transferPropsTo(
      <rect>{this.props.children}</rect>
    );
  }
});

var bound = function(number, min, max) {
  return Math.max(Math.min(number, max), min);
}


var MapWidget = React.createClass({

  getInitialState: function() {
    return {players: [{id: 1, x: 100, y: 100, color: "#ff0000"},
      {id: 2, x: 50, y: 600, color: "#00ff00"},
      {id: 3, x: 700, y: 400, color: "#0000ff"}]};
  },

  updatePlayer: function(player) {
    var dX = Math.random() < 0.5 ? -10 : 10;
    var dY = Math.random() < 0.5 ? -10 : 10;
    player.x = bound(player.x + dX, 0, MAP_WIDTH);
    player.y = bound(player.y + dY, 0, MAP_HEIGHT);
    return player;
  },

  /**
   * When the component is mounted into the document - this is similar to a
   * constructor, but invoked when the instance is actually mounted into the
   * document. Here's, we'll just set up an animation loop that invokes our
   * method. Binding of `this.onTick` is not needed because all React methods
   * are automatically bound before being mounted.
   */
  componentDidMount: function() {
    this._interval = window.setInterval(this.onTick, 500);
  },

  componentWillUnmount: function() {
    window.clearInterval(this._interval);
  },

  onTick: function() {
    var updatedPlayers = this.state.players.map(this.updatePlayer);
    this.setState({players: updatedPlayers});
  },

  /**
   * This is the "main" method for any component. The React API allows you to
   * describe the structure of your UI component at *any* point in time.
   */
  render: function() {

    var drawPlayer = function(player) {
      return <Rectangle
        key={player.id}
        x={player.x}
        y={player.y}
        width="10"
        height="10"
        fill={player.color}
        />
    };

    return (
      <SVGComponent height="800" width="800">
        <Rectangle
          key="store"
          x="0"
          y="0"
          width={MAP_WIDTH}
          height={MAP_HEIGHT}
          fill="none"
          stroke="crimson">



        </Rectangle>

        {this.state.players.map(drawPlayer)}

        </SVGComponent>
    );
  }

});

module.exports = MapWidget;

