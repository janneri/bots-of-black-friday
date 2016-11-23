"use strict";

var React = require('react');
var ReactDOM = require('react-dom');
var MapWidget = require('./MapWidget');
var ChatWidget = require('./ChatWidget');
var Socket = require('./Socket');

function loaded() {
  var map = ReactDOM.render(<MapWidget />, document.getElementById('map'));
  var chat = ReactDOM.render(<ChatWidget />, document.getElementById('chat'));

  Socket.connect(map, chat);

}

window.onload = loaded;
