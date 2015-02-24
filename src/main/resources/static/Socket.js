"use strict";

var SockJS = require('./sockjs-0.3.4.js');
var Stomp = require('stompjs');

var stompClient = null;

var Socket = {

  setConnected: function(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('conversation').style.visibility = connected ? 'visible' : 'hidden';
    document.getElementById('events').innerHTML = '';
  },

  connect: function() {
    var socket = new SockJS('/hello');
    stompClient = Stomp.over(socket);
    var self = this;
    stompClient.connect({}, function (frame) {
      self.setConnected(true);
      console.log('Connected: ' + frame);
      stompClient.subscribe('/topic/events', function (greeting) {
        //JSON.parse(greeting.body);
        self.handleGameEvent(greeting.body);
      });
    });
  },

  disconnect: function() {
    stompClient.disconnect();
    this.setConnected(false);
    console.log("Disconnected");
  },

  handleGameEvent: function(message) {
    var response = document.getElementById('events');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(message));
    //response.appendChild(p);
  }

};
module.exports = Socket;