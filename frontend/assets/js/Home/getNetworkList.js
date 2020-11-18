import { getNetwork } from './getNetwork';

const selectBox = document.getElementById('selectBox');

export const getNetworkList = () => {
  fetch(`http://` + server + `:9322/api/v1/lookUpTables`, {
    headers: {
      orgAffiliation: 'userOrg',
      orgMspId: 'UserOrgMSP',
    },
  }).then((res) => {
    if (res.status === 200 || res.status === 201) {
      res.text().then((text) => {
        networkList = JSON.parse(text);
        createSelect();
      });
    } else {
      console.log(res.statusText);
    }
  });
};

function createSelect() {
  for (var i = 0; i < networkList.length; i++) {
    let opt = document.createElement('option');
    opt.value = i;
    opt.innerText = networkList[i].methodName;

    selectBox.appendChild(opt);
    networkList[i].optionElement = opt;
  }

  selectBox.addEventListener('change', (event) => {
    getNetwork(event.target.value);
  });
}
