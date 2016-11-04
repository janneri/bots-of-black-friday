var webpack = require('webpack');

module.exports = {
  context: __dirname,
  entry: './src/main/resources/static/app.js',
  module: {
    loaders: [
      { test: /\.json$/, loader: 'json-loader'},
      { test: /\.js$/, loader: 'babel-loader' }
    ]
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env': {NODE_ENV: JSON.stringify('development')}
    })
  ]
};
