"use strict";

var React = require('react');
var _ = require('lodash');


var ChatWidget = React.createClass({
  messageIndex: 1,

  getInitialState: function() {
    return {messages: [this.createMessage("Admin: You can chat here")]};
  },

  createMessage: function(messageStr) {
    return {id: ++this.messageIndex, time: this.timeStr(), message: messageStr};
  },

  timeStr: function() {
    var time = new Date();
    var zeroPad = function(num) { return num < 10 ? '0' + num : num; };
    return time.getHours() + ":" + zeroPad(time.getMinutes()) + ":" + zeroPad(time.getSeconds());
  },

  handleChatMessage: function(message) {
    var newMessages = _.take(this.state.messages, 2);
    newMessages.unshift(this.createMessage(message));
    this.setState({messages: newMessages});
    console.log("handled " + message)
  },

  render: function() {
      var messageNodes = this.state.messages.map(function(message) {
          return (
              <div key={message.id}>{message.time} {message.message}</div>
          );
      });

      return (
          <div className="messages">
              {messageNodes}
          </div>
      );
  }

});

module.exports = ChatWidget;