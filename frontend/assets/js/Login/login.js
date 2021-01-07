const server = 'localhost';

const isCreatedChannel = () => {
  fetch(`http://` + server + `:9322/api/v1/NACUser/isCreatedChannel`, {}).then(
    (res) => {
      if (res.status === 200 || res.status === 201) {
        res.text().then((isCreatedChannelResult) => {
          if (isCreatedChannelResult === 'true') {
            document.location.href = '/';
          } else {
            document.location.href = '/no_channel';
          }
        });
      } else {
        console.log(res.statusText);
      }
    }
  );
};

const login = () => {
  var loginInfo = {
    id: document.getElementById('id').value,
    pw: document.getElementById('pw').value,
  };

  fetch(`http://` + server + `:9322/api/v1/NACUser/login`, {
    method: 'POST',
    body: JSON.stringify(loginInfo),
  }).then((res) => {
    if (res.status === 200 || res.status === 201) {
      res.text().then((loginResult) => {
        if (loginResult === 'true') {
          isCreatedChannel();
        } else {
          document.location.href = '/login';
        }
      });
    } else {
      console.log(res.statusText);
    }
  });
};

var loginBtn = document.getElementById('loginBtn');
loginBtn.addEventListener('click', login);
