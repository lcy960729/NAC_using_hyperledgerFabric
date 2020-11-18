export const getUser = (index) => {
  console.log(lookUpTable.objects[index].policyId);
  fetch(
    `http://` +
      server +
      `:9322/api/v1/policyTables/` +
      lookUpTable.objects[index].policyId,
    {
      method: 'GET',
      headers: {
        'content-Type': 'application/json',
        'orgAffiliation': 'userOrg',
        'orgMspId': 'UserOrgMSP',
      },
    }
  ).then((res) => {
    if (res.status === 200 || res.status === 201) {
      res.text().then((text) => {
        policyTable = JSON.parse(text);
        policyTable.index = index;
        policyTable.id = lookUpTable.objects[index].policyId;
        policyTable.methodName = lookUpTable.methodName;

        setUser();
      });
    } else {
      console.log(res.statusText);
    }
  });
};

const policy_table = document.getElementById('policyTable');

export function setUser() {
  init();
  printUser();

  printMisbehaviorList(policyTable.misbehaviorTables);
}

function init() {
  document.getElementById('updateUserBtn').style.display = 'block';
  document.getElementById('deleteUserBtn').style.display = 'block';

  document.getElementById('policyTable').innerHTML = '';

  document.getElementById('misTable').style.display = 'block';
  document.getElementById('misbody').innerHTML = '';
}

function printUser() {
  const userTableColumn = [
    'Network Name',
    'Object Name',
    'Object MacAddress',
    'Resource',
    'Action',
    'Permission',
    'Last Request Time',
    'Time of Unblock',
    'Min Interval Time (m)',
    'Number of Requests during Min Interval Time',
    'Threshold',
  ];

  const keys = [
    'methodName',
    'object',
    'resource',
    'action',
    'permission',
    'toLR',
    'timeOfUnblock',
    'minInterval',
    'noFR',
    'threshold',
  ];

  const objKeys = ['name', 'macAddress'];
  for (let i = 0, columnIndex = 0; i < keys.length; i++) {
    let key = keys[i];

    if (key == 'object') {
      for (let subKey of objKeys) {
        createDataList(
          userTableColumn[columnIndex++],
          policyTable[key][subKey]
        );
      }
    } else {
      createDataList(userTableColumn[columnIndex++], policyTable[key]);
    }
  }
}

function createDataList(columnName, data) {
  const dl = document.createElement('dl');

  const dt = document.createElement('dt');
  dt.innerText = columnName;
  dl.appendChild(dt);

  const dd = document.createElement('dd');
  if (columnName == 'Time of Unblock' || columnName == 'Last Request Time') {
    data = data.replace('T', ' ');
  }
  dd.innerText = data;
  dl.appendChild(dd);
  policy_table.appendChild(dl);
}

function printMisbehaviorList(misbehaviorTables) {
  const misbody = document.getElementById('misbody');

  for (var i = 0; i < misbehaviorTables.length; i++) {
    let tr = document.createElement('tr');

    let td = new Array(4);
    td[0] = document.createElement('td');
    td[0].innerText = i + 1;
    tr.appendChild(td[0]);

    td[1] = document.createElement('td');
    td[1].innerText = misbehaviorTables[i].reason;
    tr.appendChild(td[1]);

    td[2] = document.createElement('td');
    td[2].innerText = misbehaviorTables[i].penalty;
    tr.appendChild(td[2]);

    td[3] = document.createElement('td');
    td[3].innerText = misbehaviorTables[i].time.replace('T', ' ');
    tr.appendChild(td[3]);

    misbody.appendChild(tr);
  }
}
