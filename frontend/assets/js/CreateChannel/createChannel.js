const server = '180.189.90.200';
const createChannelBtn = document.getElementById('createBtn');
const errorText = document.getElementById('error_text');

const createChannel = () => {
  fetch(`http://` + server + `:9322/api/v1/NACUser/createChannel`, {}).then(
    (res) => {
      if (res.status === 200 || res.status === 201) {
        res.text().then((text) => console.log(text));

        document.getElementById('noChannel').style.display = 'none';
        document.getElementById('loding').style.display = 'inline-block';
        createChannelBtn.style.display = 'none';
        errorText.style.fontSize = '20px';
        errorText.innerHTML = '';

        const lodingDiv = document.createElement('div');
        lodingDiv.classList.add('loading');

        for (let i = 0; i < 3; i++) {
          lodingDiv.appendChild(document.createElement('span'));
        }

        errorText.appendChild(lodingDiv);
        errorText.appendChild(document.createElement('span'));

        deployRC();
      } else {
        console.log(res.statusText);
      }
    }
  );
};
createChannelBtn.addEventListener('click', createChannel);

const deployRC = () => {
  errorText.lastChild.innerText = '체인코드 업로드 중..RC';

  fetch(`http://` + server + `:9322/api/v1/NACUser/deployRC`, {}).then(
    (res) => {
      if (res.status === 200 || res.status === 201) {
        res.text().then((text) => console.log(text));
        deployACC();
      } else {
        console.log(res.statusText);
      }
    }
  );
};

const deployACC = () => {
  errorText.lastChild.innerText = '체인코드 업로드 중..ACC';

  fetch(`http://` + server + `:9322/api/v1/NACUser/deployACC`, {}).then(
    (res) => {
      if (res.status === 200 || res.status === 201) {
        res.text().then((text) => console.log(text));
        deployJC();
      } else {
        console.log(res.statusText);
      }
    }
  );
};

const deployJC = () => {
  errorText.lastChild.innerText = '체인코드 업로드 중..JC';

  fetch(`http://` + server + `:9322/api/v1/NACUser/deployJC`, {}).then(
    (res) => {
      if (res.status === 200 || res.status === 201) {
        document.location.href = '/';
      } else {
        console.log(res.statusText);
      }
    }
  );
};
