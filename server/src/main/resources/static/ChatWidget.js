"use strict";

var React = require('react');
var _ = require('lodash');


var ChatWidget = React.createClass({
  getInitialState: function() {
    return {messages: [{time: this.timeStr(), message: "Admin: You can chat here"}]};
  },

  timeStr: function() {
    var time = new Date();
    var zeroPad = function(num) { return num < 10 ? '0' + num : num; }
    return time.getHours() + ":" + zeroPad(time.getMinutes()) + ":" + zeroPad(time.getSeconds());
  },

  handleChatMessage: function(message) {
    var newMessages = _.take(this.state.messages, 2);
    var time = new Date();
    newMessages.unshift({time: this.timeStr(), message: message});
    this.setState({messages: newMessages});
    console.log("handled " + message)
  },

  render: function() {
      var messageNodes = this.state.messages.map(function(message) {
          return (
              <div>{message.time} {message.message}</div>
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