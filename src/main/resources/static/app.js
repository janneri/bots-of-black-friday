/** @jsx React.DOM */

"use strict";

var React = require('react');
var MapWidget = require('./MapWidget');
var Socket = require('./Socket');

function loaded() {
  React.render(<MapWidget />, document.getElementById('map'));
  Socket.connect();
}

window.onload = loaded;