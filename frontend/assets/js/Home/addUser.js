import { updateNetwork } from './updateNetwork';
import { closeAddUserPopup } from './home';

export const addUser = () => {
  var userData = {
    subjectId: lookUpTable.id,
    object: {
      name: document.getElementById('objectName').value,
      macAddress: document.getElementById('objectMacAddress').value,
    },
    resource: document.getElementById('resource').value,
    action: document.getElementById('action').value,
    permission: document.getElementById('permission').value,
    threshold: document.getElementById('threshold').value,
    minInterval: document.getElementById('minInterval').value,
  };

  fetch(`http://` + server + `:9322/api/v1/policyTables`, {
    method: 'POST',
    headers: {
      'content-Type': 'application/json',
      'orgAffiliation': 'userOrg',
      'orgMspId': 'UserOrgMSP',
    },
    body: JSON.stringify(userData),
  }).then((res) => {
    if (res.status === 200 || res.status === 201) {
      const policyId = res.headers.get('Location').split('/')[2];

      userData.object.policyId = policyId;
      lookUpTable.objects.push(userData.object);

      updateNetwork();

      closeAddUserPopup();
    } else {
      console.log(res.statusText);
    }
  });
};
