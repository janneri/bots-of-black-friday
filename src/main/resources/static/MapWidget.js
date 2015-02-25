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

var MapWidget = React.createClass({

  getInitialState: function() {
    return {players: []};
  },

  /**
   * When the component is mounted into the document - this is similar to a
   * constructor, but invoked when the instance is actually mounted into the
   * document. Here's, we'll just set up an animation loop that invokes our
   * method. Binding of `this.onTick` is not needed because all React methods
   * are automatically bound before being mounted.
   */
  componentDidMount: function() {
  },

  componentWillUnmount: function() {
    window.clearInterval(this._interval);
  },



  /**
   * This is the "main" method for any component. The React API allows you to
   * describe the structure of your UI component at *any* point in time.
   */
  render: function() {

    var drawPlayer = function(player) {
      return <Rectangle
        key={player.id}
        x={player.position.x}
        y={player.position.y}
        width="10"
        height="10"
        fill="#ff0000"
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

