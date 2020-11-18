import '@babel/polyfill';
import app from './app';
const PORT = 4000;

const handleListening = () =>
  console.log(`✅  Listening on: http://localhost:${PORT}`);

const https = require('https');
const http = require('http');
const fs = require('fs');

const options = {
  key: fs.readFileSync('secret/private.pem'),
  cert: fs.readFileSync('secret/public.pem'),
};

// Create an HTTP service.
http.createServer(app).listen(PORT, handleListening);

//https.createServer(options, app).listen(4001, handleListening);
