"use strict";

var React = require('react');
var _ = require('lodash');

// easy to shift 4
var TILE_WIDTH_IN_PIXELS = 16;
var TILE_WIDTH_SHIFT_AMOUNT = 4;

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
    return {players: [], items: [], shootingLines: [], map: {width: 0, height: 0, tiles: []}};
  },

  componentDidMount: function() {
      fetch('/map')
          .then((response) => {
              if (!response.ok) {
                  throw new Error(`HTTP error! Status: ${response.status}`);
              }
              return response.json()
          })
          .then(data => this.setState({map: data}));
  },

  componentWillUnmount: function() {
    window.clearInterval(this._interval);
  },

  render: function() {

    var drawPlayer = function(player) {
      var color = player.state === "MOVE" ? "#ff0000" : "#0000ff";
      var text = player.name + (player.timeInState != 0 ? " (" + player.timeInState + ")" : "");
      return (
      <G key={player.id}>
        <Rectangle
          x={player.position.x << TILE_WIDTH_SHIFT_AMOUNT}
          y={player.position.y << TILE_WIDTH_SHIFT_AMOUNT}
          width={TILE_WIDTH_IN_PIXELS - 1}
          height={TILE_WIDTH_IN_PIXELS - 1}
          style={{fill: color, filter: 'url(#shadow)'}}/>
        <Text
          x={(player.position.x << TILE_WIDTH_SHIFT_AMOUNT) + 1}
          y={(player.position.y << TILE_WIDTH_SHIFT_AMOUNT) - TILE_WIDTH_IN_PIXELS + 1}
          fill="white">
          {text}
        </Text>
        <Text
          x={player.position.x << TILE_WIDTH_SHIFT_AMOUNT}
          y={(player.position.y << TILE_WIDTH_SHIFT_AMOUNT) - TILE_WIDTH_IN_PIXELS}
          fill="black">
          {text}
        </Text>
      </G>
      );
    };

    var selectRounding = function(x, y, tiles) {
      var topSame = y > 0 && tiles[y - 1][x] === tiles[y][x];
      var bottomSame = y < tiles.length - 1 && tiles[y + 1][x] === tiles[y][x];

      var leftSame = x > 0 && tiles[y][x - 1] === tiles[y][x];
      var rightSame = x < tiles[y].length - 1 && tiles[y][x + 1] === tiles[y][x];
      return {tl:!(topSame || leftSame),
              tr:!(topSame || rightSame),
              bl:!(bottomSame || leftSame),
              br:!(bottomSame || rightSame)};
    };

    var drawTiles = function(tiles) {
        if (!tiles) {
            return [];
        }
        var svgTiles = [];
        for (var y = 0; y < tiles.length; y++) {
            for (var x = 0; x < tiles[y].length; x++) {
                if ( tiles[y][x] === 'x' ) {
                  var rounding = selectRounding(x, y, tiles);

                  svgTiles.push(drawTile(x, y, "url(#wall-pattern)", rounding));
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

    var drawTile = function(x, y, color, rounding) {
        var {tl=false, tr=false, bl=false, br=false} = rounding || {};

        return (
            <G key={"tile_" + x + "." + y}>
              <path d={rounded_rect(
                  x << TILE_WIDTH_SHIFT_AMOUNT,
                  y << TILE_WIDTH_SHIFT_AMOUNT,
                  TILE_WIDTH_IN_PIXELS,
                  TILE_WIDTH_IN_PIXELS,
                  6,
                  tl,
                  tr,
                  bl,
                  br
                )} stroke="none" fill={color}/>
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
      var fontSize = 20;
      var textXpos = TILE_WIDTH_IN_PIXELS * 2;
      var textYpos = TILE_WIDTH_IN_PIXELS * 2 - fontSize / 2;
        return (
            <G key="map_name">
                <Text x={textXpos} y={textYpos} fill="white" fontSize={fontSize + 'px'}>
                {name}
                </Text>
            </G>
        );
    };

    var drawShootingLine = function(line) {
      return (
            <G key={"line_" + line.fromPosition.x  + "." + line.toPosition.x + line.fromPosition.y  + "." + line.toPosition.y}>
                <Line x1={line.fromPosition.x << TILE_WIDTH_SHIFT_AMOUNT}
                      y1={line.fromPosition.y << TILE_WIDTH_SHIFT_AMOUNT}
                      x2={line.toPosition.x << TILE_WIDTH_SHIFT_AMOUNT}
                      y2={line.toPosition.y << TILE_WIDTH_SHIFT_AMOUNT}
                      stroke="orange" strokeWidth="2" />
            </G>
        );
    };

    var getItemEmoji = function(item) {
        switch (item.type) {
            case "WEAPON":
                // 🪄
                return "\u{1FA84}";
            case "POTION":
                // 🍺
                return "\u{1F37A}";
            default:
                // 🎁
                return  "\u{1F381}";
        }
    }

    var drawItem = function(item) {
      var text = item.type === "WEAPON" || item.type === "POTION" ? item.price + ' \u20ac' : item.price + ' \u20ac -' + item.discountPercent + '%';
      return (
        <G key={"item_" + item.position.x + "." + item.position.y}>
          <Text
            x={(item.position.x << TILE_WIDTH_SHIFT_AMOUNT)}
            y={(item.position.y << TILE_WIDTH_SHIFT_AMOUNT) + TILE_WIDTH_IN_PIXELS}
            r={(TILE_WIDTH_IN_PIXELS >> 1) - 1}>
              {getItemEmoji(item)}
          </Text>
          <Text
            x={(item.position.x << TILE_WIDTH_SHIFT_AMOUNT) + 1}
            y={((item.position.y << TILE_WIDTH_SHIFT_AMOUNT) - TILE_WIDTH_IN_PIXELS) + 1}
            style={{fill: 'white'}}>
            {text}
          </Text>
          <Text
            x={item.position.x << TILE_WIDTH_SHIFT_AMOUNT}
            y={(item.position.y << TILE_WIDTH_SHIFT_AMOUNT) - TILE_WIDTH_IN_PIXELS}
            style={{fill: 'black'}}>
            {text}
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
          <td>{player.money} &euro;</td>
          <td>{player.score} &euro;</td>
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
        return retval;
    }

    return (
      <div>
        <SVGComponent height={this.state.map.height << TILE_WIDTH_SHIFT_AMOUNT} width={this.state.map.width << TILE_WIDTH_SHIFT_AMOUNT}>
            <defs>
              <filter id="shadow">
                <feGaussianBlur in="SourceAlpha" stdDeviation="1" />
                <feOffset dx="1" dy="1" />
                <feMerge>
                    <feMergeNode />
                    <feMergeNode in="SourceGraphic" />
                </feMerge>
              </filter>
              <pattern id="floor-pattern" x="0" y="0" width={TILE_WIDTH_IN_PIXELS * 12} height={TILE_WIDTH_IN_PIXELS * 12} patternUnits="userSpaceOnUse">
                <image xlinkHref="floor.jpg" x="0" y="0" width={TILE_WIDTH_IN_PIXELS * 12} height={TILE_WIDTH_IN_PIXELS * 12}></image>
              </pattern>
              <pattern id="wall-pattern" x="0" y="0" width={TILE_WIDTH_IN_PIXELS * 12} height={TILE_WIDTH_IN_PIXELS * 12} patternUnits="userSpaceOnUse">
                <image xlinkHref="wall.jpg" x="0" y="0" width={TILE_WIDTH_IN_PIXELS * 12} height={TILE_WIDTH_IN_PIXELS * 12}></image>
              </pattern>
              <linearGradient id="Gradient3" x1="0" x2="0" y1="0" y2="1">
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
            style={{fill:'url(#floor-pattern)'}}>
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
