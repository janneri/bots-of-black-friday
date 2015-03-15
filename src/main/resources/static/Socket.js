"use strict";

var SockJS = require('./sockjs-0.3.4.js');
var Stomp = require('stompjs');
var MapWidget = require('./MapWidget.js')

var stompClient = null;
var mapWidget = null;

var Socket = {

  setConnected: function(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
  },

  connect: function(map, chat) {
    var socket = new SockJS('/hello');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;
    var self = this;
    stompClient.connect({}, function (frame) {
      self.setConnected(true);
      //console.log('Connected: ' + frame);
      stompClient.subscribe('/topic/chat', function (message) {
        chat.handleChatMessage(message.body);
      });

      stompClient.subscribe('/topic/events', function (gameStateChangedEvent) {
        self.handleGameEvent(JSON.parse(gameStateChangedEvent.body));
      });

    });
    mapWidget = map;
  },

  disconnect: function() {
    stompClient.disconnect();
    this.setConnected(false);
    console.log("Disconnected");
  },

  handleGameEvent: function(event) {
    mapWidget.setState(event.gameState);
  },


};
module.exports = Socket;