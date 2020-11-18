import routes from '../routes';

export const error = (req, res) => {
  res.render('error', { pageTitle: 'error' });
};

export const no_channel = (req, res) => {
  res.render('no_channel', { pageTitle: 'no_channel' });
};

export const home = (req, res) => {
  res.render('home', { pageTitle: 'home' });
};

export const getJoin = (req, res) => {
  res.render('join', { pageTitle: 'Join' });
};

// export const postJoin = async (req, res, next) => {
//   const {
//     body: { name, email, password, password2 },
//   } = req;
//   if (password !== password2) {
//     req.flash('error', "Passwords don't match");
//     res.status(400);
//     res.render('join', { pageTitle: 'Join' });
//   } else {
//     try {
//       const user = await User({
//         name,
//         email,
//       });
//       await User.register(user, password);
//       next();
//     } catch (error) {
//       console.log(error);
//       res.redirect(routes.home);
//     }
//   }
// };

export const getLogin = (req, res) => res.render('login', { pageTitle: 'Log In' });

// export const postLogin = (req, res) => {
//   res.render('no_channel');
// };

// export const postLogin = passport.authenticate('local', {
//   failureRedirect: routes.login,
//   successRedirect: routes.home,
//   successFlash: 'Welcome',
//   failureFlash: "Can't log in. Check email and/or password",
// });

export const logout = (req, res) => {
  req.flash('info', 'Logged out, see you later');
  req.logout();
  res.redirect(routes.home);
};
