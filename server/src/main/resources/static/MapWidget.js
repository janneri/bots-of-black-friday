"use strict";

var React = require('react');
var _ = require('lodash');

// easy to shift 3
var TILE_WIDTH_IN_PIXELS = 8;
var TILE_WIDTH_SHIFT_AMOUNT = 3;

var SVGComponent = React.createClass({
  render: function() {
    return <svg {...this.props}>{this.props.children}</svg>;
  }
});

var Rectangle = React.createClass({
  render: function() {
    return <rect {...this.props}>{this.props.children}</rect>;
  }
});

var Image = React.createClass({
  render: function() {
    return <image {...this.props}>{this.props.children}</image>;
  }
});

var Line = React.createClass({
  render: function() {
    return <line {...this.props}>{this.props.children}</line>
  }
});

var G = React.createClass({
  render: function() {
    return <g {...this.props}>{this.props.children}</g>
  }
});

var Text = React.createClass({
  render: function() {
    return <text {...this.props}>{this.props.children}</text>
  }
});

var Circle = React.createClass({
  render: function() {
    return <circle {...this.props}>{this.props.children}</circle>
  }
});

var MapWidget = React.createClass({

  getInitialState: function() {
    return {players: [], items: [], shootingLines: [{fromPosition: {x: 1, y: 1}, toPosition: {x: 50, y: 1}}], map: {width: 0, height: 0, walls: []}};
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

    var drawTiles = function(tiles) {
        if (!tiles) {
            return [];
        }
        var svgTiles = [];
        for (var y = 0; y < tiles.length; y++) {
            for (var x = 0; x < tiles[y].length; x++) {
                if ( tiles[y][x] === 'x' ) {
                  svgTiles.push(drawTile(x, y, "url(#Gradient3)"));
                  //svgTiles.push(drawTile(x, y, "#494949"));
                }
                else if ( tiles[y][x] === 'o' ) {
                    svgTiles.push(drawTile(x, y, "#CC00FF"));
                }
                else if ( tiles[y][x] === '#' ) {
                  svgTiles.push(drawTrap(x, y));
                }
            }
        }
        return svgTiles;
    };

    var drawTile = function(x, y, color) {
        return (
            <G key={"tile_" + x + "." + y}>

                <Rectangle
                    x={x << TILE_WIDTH_SHIFT_AMOUNT}
                    y={y << TILE_WIDTH_SHIFT_AMOUNT}
                    width={TILE_WIDTH_IN_PIXELS}
                    height={TILE_WIDTH_IN_PIXELS}
                    rx={TILE_WIDTH_IN_PIXELS / 2}
                    ry={TILE_WIDTH_IN_PIXELS / 2}
                    style={{fill: color}} />
            </G>
        );
    };

    var drawTrap = function(x, y) {
      return (
        <G key={"trap_" + x + "." + y}>
          <Circle
            cx={(x << TILE_WIDTH_SHIFT_AMOUNT) + (TILE_WIDTH_IN_PIXELS >> 1)}
            cy={(y << TILE_WIDTH_SHIFT_AMOUNT) + (TILE_WIDTH_IN_PIXELS >> 1)}
            r={TILE_WIDTH_IN_PIXELS >> 1}
            fill="white"
            stroke="red"/>
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

    var drawShootingLine = function(line) {
      return (
            <G key={"line." + line.fromPosition.x  + "." + line.toPosition.x + line.fromPosition.y  + "." + line.toPosition.y}>
                <Line x1={line.fromPosition.x << TILE_WIDTH_SHIFT_AMOUNT}
                      y1={line.fromPosition.y << TILE_WIDTH_SHIFT_AMOUNT}
                      x2={line.toPosition.x << TILE_WIDTH_SHIFT_AMOUNT}
                      y2={line.toPosition.y << TILE_WIDTH_SHIFT_AMOUNT}
                      stroke="orange" strokeWidth="2" />
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
          {item.type === "WEAPON" ? "W " + item.price + '€' : item.price + '€ - ' + item.discountPercent + '%'}
          </Text>
        </G>
      );
    };

    var printPlayer = function(player) {
      return (
        <tr>
          <td>{player.name}</td>
          <td>{player.actionCount + (player.timeInState != 0 ? " (" + player.timeInState + ")" : "") }</td>
          <td>{player.health}</td>
          <td>{player.money}&euro;</td>
          <td>{player.score}&euro;</td>
        </tr>
      );
    }

    /*
    x: x-coordinate
    y: y-coordinate
    w: width
    h: height
    r: corner radius
    tl: top_left rounded?
    tr: top_right rounded?
    bl: bottom_left rounded?
    br: bottom_right rounded?
    */
    var rounded_rect = function(x, y, w, h, r, tl, tr, bl, br) {
        var retval;
        retval  = "M" + (x + r) + "," + y;
        retval += "h" + (w - 2*r);
        if (tr) { retval += "a" + r + "," + r + " 0 0 1 " + r + "," + r; }
        else { retval += "h" + r; retval += "v" + r; }
        retval += "v" + (h - 2*r);
        if (br) { retval += "a" + r + "," + r + " 0 0 1 " + -r + "," + r; }
        else { retval += "v" + r; retval += "h" + -r; }
        retval += "h" + (2*r - w);
        if (bl) { retval += "a" + r + "," + r + " 0 0 1 " + -r + "," + -r; }
        else { retval += "h" + -r; retval += "v" + -r; }
        retval += "v" + (2*r - h);
        if (tl) { retval += "a" + r + "," + r + " 0 0 1 " + r + "," + -r; }
        else { retval += "v" + -r; retval += "h" + r; }
        retval += "z";
        console.log(retval);
        return retval;
    }

    return (
      <div>
        <SVGComponent height={this.state.map.height << TILE_WIDTH_SHIFT_AMOUNT} width={this.state.map.width << TILE_WIDTH_SHIFT_AMOUNT}>
            <defs>
              <filter id="blur-effect-1">
                <feGaussianBlur stdDeviation="2" />
              </filter>
              <filter id="shadow" width="1.5" height="1.5" x="-.25" y="-.25">
                <feGaussianBlur in="SourceAlpha" stdDeviation="2.5" result="blur"/>
                <feColorMatrix result="bluralpha" type="matrix" values=
                        "1 0 0 0   0
                         0 1 0 0   0
                         0 0 1 0   0
                         0 0 0 0.4 0 "/>
                <feOffset in="bluralpha" dx="3" dy="3" result="offsetBlur"/>
                <feMerge>
                    <feMergeNode in="offsetBlur"/>
                    <feMergeNode in="SourceGraphic"/>
                </feMerge>
            </filter>
              <pattern id="floor-pattern" x="0" y="0" width={TILE_WIDTH_IN_PIXELS * 12} height={TILE_WIDTH_IN_PIXELS * 12} patternUnits="userSpaceOnUse">
                <image xlinkHref="floor.jpg" x="0" y="0" width={TILE_WIDTH_IN_PIXELS * 12} height={TILE_WIDTH_IN_PIXELS * 12}></image>
              </pattern>
              <linearGradient id="Gradient3" x1="0" x2="1" y1="0" y2="1">
                <stop offset="0%" stopColor="dark-gray"/>
                <stop offset="50%" stopColor="HotPink"/>
                <stop offset="100%" stopColor="dark-gray"/>
              </linearGradient>
            </defs>
          <Rectangle
            key="store"
            x="0"
            y="0"
            width={this.state.map.width << TILE_WIDTH_SHIFT_AMOUNT}
            height={this.state.map.height << TILE_WIDTH_SHIFT_AMOUNT}
            style={{fill:'url(#floor-pattern)', filter: 'url(#blur-effect-1)'}}>
          </Rectangle>

          <G>
            {drawTiles(this.state.map.tiles)}
            {drawMapName(this.state.map.name)}
            {this.state.items.map(drawItem)}
            {this.state.players.map(drawPlayer)}
            {this.state.shootingLines.map(drawShootingLine)}
          </G>
        </SVGComponent>

        <div className="score-board">
            <div className="finished-player-section">
                <h3 className="title">Scoreboard</h3>
                <table className="player-list">
                    <thead>
                        <tr className="player">
                            <th className="name">Name</th>
                            <th className="moves">Moves</th>
                            <th className="health">Health %</th>
                            <th className="money">Money left</th>
                            <th className="possession">Possession</th>
                        </tr>
                    </thead>
                    <tbody>
                    {_.sortBy(this.state.finishedPlayers, 'score').reverse().map(printPlayer)}
                    </tbody>
                </table>
            </div>

            <div className="active-player-section">
                <h3 className="title">Active players</h3>
                <table className="player-list">
                  <thead>
                    <tr className="player">
                      <th className="name">Name</th>
                      <th className="moves">Moves</th>
                      <th className="health">Health %</th>
                      <th className="money">Money left</th>
                      <th className="possession">Possession</th>
                    </tr>
                  </thead>
                  <tbody>
                  {_.sortBy(this.state.players, 'score').reverse().map(printPlayer)}
                  </tbody>
                </table>
            </div>
        </div>
      </div>

    );
  }

});

module.exports = MapWidget;
