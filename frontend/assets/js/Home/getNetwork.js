import { getUser } from './getUser';

const lookup_table = document.getElementById('lookupTable');

export const getNetwork = (selectedIndex) => {
  let networkId = networkList[selectedIndex].id;
  fetch(`http://` + server + `:9322/api/v1/lookUpTables/` + networkId, {
    headers: {
      orgAffiliation: 'userOrg',
      orgMspId: 'UserOrgMSP',
    },
  }).then((res) => {
    if (res.status === 200 || res.status === 201) {
      res.text().then((text) => {
        lookUpTable = JSON.parse(text);
        lookUpTable.id = networkId;
        lookUpTable.index = selectedIndex;

        setNetwork();
      });
    } else {
      console.log(res.statusText);
    }
  });
};

export function setNetwork() {
  init();
  printNetwork();

  printObjectList(lookUpTable.objects);
}

function init() {
  document.getElementById('addUserBtn').style.display = 'block';
  document.getElementById('deleteNetworkBtn').style.display = 'block';

  document.getElementById('lookupTable').innerHTML = '';

  document.getElementById('objectTable').style.display = 'block';
  document.getElementById('tbody').innerHTML = '';

  document.getElementById('updateUserBtn').style.display = 'none';
  document.getElementById('deleteUserBtn').style.display = 'none';

  document.getElementById('policyTable').innerHTML = '';

  document.getElementById('misTable').style.display = 'none';
  document.getElementById('misbody').innerHTML = '';
}

function printNetwork() {
  const lookUpTableColumn = [
    'Network Name',
    'Subject Name',
    'Subject MacAddress',
    'scName',
    'abi',
  ];
  const keys = ['methodName', 'subject', 'scName', 'abi'];

  for (let i = 0, columnIndex = 0; i < keys.length; i++) {
    let key = keys[i];

    if (key == 'subject') {
      for (let subKey in lookUpTable[key]) {
        createDataList(
          lookUpTableColumn[columnIndex++],
          lookUpTable[key][subKey]
        );
      }
    } else {
      createDataList(lookUpTableColumn[columnIndex++], lookUpTable[key]);
    }
  }
}

function createDataList(columnName, data) {
  const dl = document.createElement('dl');

  const dt = document.createElement('dt');
  dt.innerText = columnName;
  dl.appendChild(dt);

  const dd = document.createElement('dd');
  dd.innerText = data;
  dl.appendChild(dd);
  lookup_table.appendChild(dl);
}

function printObjectList(objects) {
  const tbody = document.getElementById('tbody');

  for (var i = 0; i < objects.length; i++) {
    let tr = document.createElement('tr');
    tr.id = `tr_${i}`;

    let td = new Array(3);
    td[0] = document.createElement('td');
    td[0].innerText = i + 1;
    tr.appendChild(td[0]);

    td[1] = document.createElement('td');
    td[1].innerText = objects[i].name;
    tr.appendChild(td[1]);

    td[2] = document.createElement('td');
    td[2].innerText = objects[i].macAddress;
    tr.appendChild(td[2]);

    tr.addEventListener('click', (event) => {
      getUser(event.target.parentElement.id.split('_')[1]);
    });

    tbody.appendChild(tr);
  }
}
