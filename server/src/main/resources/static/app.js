/** @jsx React.DOM */

"use strict";

var React = require('react');
var MapWidget = require('./MapWidget');
var ChatWidget = require('./ChatWidget');
var Socket = require('./Socket');

function loaded() {
  var map = React.render(<MapWidget />, document.getElementById('map'));
  var chat = React.render(<ChatWidget />, document.getElementById('chat'));

  // TODO tila jotenkin järkevämmin
  Socket.connect(map, chat);

}

window.onload = loaded;