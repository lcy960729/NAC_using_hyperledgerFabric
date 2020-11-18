import { closeCreateNetworkPopup } from './home';
import { setNetwork } from './getNetwork';

export const createNetwork = () => {
  const networkInputData = {
    methodName: document.getElementById('methodName').value,
    subject: {
      name: document.getElementById('subjectName').value,
      macAddress: document.getElementById('macAddress').value,
    },
  };

  fetch(`http://` + server + `:9322/api/v1/lookUpTables`, {
    method: 'POST',
    headers: {
      'content-Type': 'application/json',
      'orgAffiliation': 'userOrg',
      'orgMspId': 'UserOrgMSP',
    },
    body: JSON.stringify(networkInputData),
  }).then((res) => {
    if (res.status === 200 || res.status === 201) {
      lookUpTable.id = res.headers.get('Location').split('/')[2];
      lookUpTable.methodName = networkInputData.methodName;
      lookUpTable.subject = networkInputData.subject;
      lookUpTable.scName = 'acc';
      lookUpTable.abi = 'accessControl';
      lookUpTable.objects = new Array();
      lookUpTable.index = networkList.length;

      setNetwork();

      networkList.push({
        methodName: lookUpTable.methodName,
        id: lookUpTable.id,
      });
      createSelect(networkList.length - 1);

      closeCreateNetworkPopup();
    } else {
      console.log(res.statusText);
    }
  });
};

function createSelect(index) {
  const selectBox = document.getElementById('selectBox');
  let opt = document.createElement('option');
  opt.value = index;
  opt.innerText = networkList[index].methodName;

  selectBox.appendChild(opt);
  networkList[index].optionElement = opt;

  opt.selected = true;
}

const createNetworkBtn = document.getElementById('createNetworkBtn');
createNetworkBtn.addEventListener('click', createNetwork);
