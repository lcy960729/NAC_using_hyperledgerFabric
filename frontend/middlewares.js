import routes from './routes';

export const localsMiddleware = (req, res, next) => {
  res.locals.siteName = 'NAS Service';
  res.locals.routes = routes;
  res.locals.loggedUser = req.user || null;
  next();
};
