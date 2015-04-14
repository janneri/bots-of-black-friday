/**
 *
 * @jsx React.DOM
 */
"use strict";

var React = require('react');
var _ = require('lodash');

// easy to shift 3
var TILE_WIDTH_IN_PIXELS = 8;
var TILE_WIDTH_SHIFT_AMOUNT = 3;

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

var G = React.createClass({
  render: function() {
    return this.transferPropsTo(
      <g>{this.props.children}</g>
    );
  }
});

var Text = React.createClass({
  render: function() {
    return this.transferPropsTo(
      <text>{this.props.children}</text>
    );
  }
});

var Circle = React.createClass({
  render: function() {
    return this.transferPropsTo(
      <circle>{this.props.children}</circle>
    );
  }
});

var MapWidget = React.createClass({

  getInitialState: function() {
    return {players: [], items: [], map: {width: 0, height: 0, walls: []}};
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
      var color = player.state === "MOVE" ? "#ff0000" : "#0000ff";
      return (
      <G key={player.id}>
        <Rectangle
          x={player.position.x << TILE_WIDTH_SHIFT_AMOUNT}
          y={player.position.y << TILE_WIDTH_SHIFT_AMOUNT}
          width={TILE_WIDTH_IN_PIXELS}
          height={TILE_WIDTH_IN_PIXELS}
          fill={color}/>
        <Text
          x={player.position.x << TILE_WIDTH_SHIFT_AMOUNT}
          y={(player.position.y << TILE_WIDTH_SHIFT_AMOUNT) - TILE_WIDTH_IN_PIXELS}
          fill="#000000">
          {player.name + (player.timeInState != 0 ? " (" + player.timeInState + ")" : "")}
        </Text>
      </G>
      );
    };

    var drawWalls = function(row) {
          return (
          <G key={wall.upperLeftCorner.x + "." + wall.upperLeftCorner.y + "." + wall.width + "." + wall.height}>
            <Rectangle
              x={wall.upperLeftCorner.x << TILE_WIDTH_SHIFT_AMOUNT}
              y={wall.upperLeftCorner.y << TILE_WIDTH_SHIFT_AMOUNT}
              width={wall.width << TILE_WIDTH_SHIFT_AMOUNT}
              height={wall.height << TILE_WIDTH_SHIFT_AMOUNT}
              fill="#8C8C8C"/>
          </G>
          );
    };

    var drawTiles = function(tiles) {
        if (!tiles) {
            return [];
        }
        var svgTiles = [];
        for (var y = 0; y < tiles.length; y++) {
            for (var x = 0; x < tiles[y].length; x++) {
                if ( tiles[y][x] === 'x' ) {
                    svgTiles.push(drawWallTile(x, y));
                }
            }
        }
        return svgTiles;
    };

    var drawWallTile = function(x, y) {
        var color = "#494949";
        return (
            <G key={"tile_" + x + "." + y}>
                <Rectangle
                    x={x << TILE_WIDTH_SHIFT_AMOUNT}
                    y={y << TILE_WIDTH_SHIFT_AMOUNT}
                    width={TILE_WIDTH_IN_PIXELS}
                    height={TILE_WIDTH_IN_PIXELS}
                    fill={color} />
            </G>
        );
    };

    var drawMapName = function(name) {
        return (
            <G key="map_name">
                <Text x={TILE_WIDTH_IN_PIXELS} y={TILE_WIDTH_IN_PIXELS} fill="#FFFFFF" fontSize="10px">
                {name}
                </Text>
            </G>
        );
    };
    var drawItem = function(item) {
      return (
        <G key={item.position.x + "." + item.position.y}>
          <Circle
            cx={(item.position.x << TILE_WIDTH_SHIFT_AMOUNT) + (TILE_WIDTH_IN_PIXELS >> 1)}
            cy={(item.position.y << TILE_WIDTH_SHIFT_AMOUNT) + (TILE_WIDTH_IN_PIXELS >> 1)}
            r={TILE_WIDTH_IN_PIXELS >> 1}
            fill="#00ff00"/>
          <Text
            x={item.position.x << TILE_WIDTH_SHIFT_AMOUNT}
            y={(item.position.y << TILE_WIDTH_SHIFT_AMOUNT) - TILE_WIDTH_IN_PIXELS}
            fill="#000000">
          {item.price + '€ - ' + item.discountPercent + '%'}
          </Text>
        </G>
      );
    };

    var printPlayer = function(player) {
      return (
        <tr>
          <td>{player.name + (player.timeInState != 0 ? " (" + player.timeInState + ")" : "")}</td>
          <td>{player.money}&euro;</td>
          <td>{player.score}&euro;</td>
        </tr>
      );
    }

    return (
      <div>
        <SVGComponent height={this.state.map.height << TILE_WIDTH_SHIFT_AMOUNT} width={this.state.map.width << TILE_WIDTH_SHIFT_AMOUNT}>
          <Rectangle
            key="store"
            x="0"
            y="0"
            width={this.state.map.width << TILE_WIDTH_SHIFT_AMOUNT}
            height={this.state.map.height << TILE_WIDTH_SHIFT_AMOUNT}
            fill="none"
            stroke="crimson">
          </Rectangle>

          <G>
          {drawTiles(this.state.map.tiles)}
          {drawMapName(this.state.map.name)}
          {this.state.items.map(drawItem)}
          {this.state.players.map(drawPlayer)}
          </G>
        </SVGComponent>

        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Money left</th>
              <th>Possession</th>
            </tr>
          </thead>
          <tbody>
          {_.sortBy(this.state.players, 'score').reverse().map(printPlayer)}
          </tbody>
        </table>
      </div>

    );
  }

});

module.exports = MapWidget;

