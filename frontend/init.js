import '@babel/polyfill';
import app from './app';
const PORT = 4000;

const handleListening = () =>
  console.log(`✅  Listening on: http://localhost:${PORT}`);

const http = require('http');

// Create an HTTP service.
http.createServer(app).listen(PORT, handleListening);
