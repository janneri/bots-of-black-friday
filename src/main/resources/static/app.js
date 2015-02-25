/** @jsx React.DOM */

"use strict";

var React = require('react');
var MapWidget = require('./MapWidget');
var Socket = require('./Socket');

function loaded() {
  var map = React.render(<MapWidget />, document.getElementById('map'));
  // TODO tila jotenkin järkevämmin
  Socket.connect(map);
}

window.onload = loaded;