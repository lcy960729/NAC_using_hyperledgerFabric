import express from 'express';
import routes from '../routes';
import fs from 'fs';
import {
  home,
  getJoin,
  getLogin,
  logout,
  error,
  no_channel,
} from '../controllers/userController';

const globalRouter = express.Router();

globalRouter.get(routes.join, getJoin);

globalRouter.get(routes.login, getLogin);

globalRouter.get(routes.home, home);

globalRouter.get(routes.logout, logout);

globalRouter.get(routes.error, error);

globalRouter.get(routes.no_channel, no_channel);

globalRouter.get('/imgs2', function (req, res) {
  fs.readFile('views/img/no_channel.jpg', function (error, data) {
    res.writeHead(200, { 'Content-Type': 'text/html' });
    res.end(data);
  });
});

globalRouter.get('/imgs', function (req, res) {
  fs.readFile('views/img/error_img.jpg', function (error, data) {
    res.writeHead(200, { 'Content-Type': 'text/html' });
    res.end(data);
  });
});

globalRouter.get('/lodingSvg', function (req, res) {
  fs.readFile('views/img/loding.svg', function (error, data) {
    res.writeHead(200, { 'Content-Type': 'image/svg+xml' });
    res.end(data);
  });
});

export default globalRouter;
