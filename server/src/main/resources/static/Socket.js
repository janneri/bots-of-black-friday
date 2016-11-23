"use strict";

var SockJS = require('./sockjs-0.3.4.js');
var Stomp = require('stompjs');
var MapWidget = require('./MapWidget.js')

var stompClient = null;
var mapWidget = null;

var Socket = {

  connect: function(map, chat) {
    var socket = new SockJS('/hello');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;
    var self = this;
    stompClient.connect({}, function (frame) {
      stompClient.subscribe('/topic/chat', function (message) {
        chat.handleChatMessage(message.body);
      });

      stompClient.subscribe('/topic/events', function (gameStateChangedEvent) {
        self.handleGameEvent(JSON.parse(gameStateChangedEvent.body));
      });

    });
    mapWidget = map;
  },

  handleGameEvent: function(event) {
    mapWidget.setState(event.gameState);
  }


};
module.exports = Socket;