import { updateNetwork } from './updateNetwork';
import { closeAddUserPopup } from './home';
import { setUser } from './getUser';

export const updateUser = () => {
  var userData = {
    objectName: document.getElementById('objectName').value,
    resource: document.getElementById('resource').value,
    action: document.getElementById('action').value,
    permission: document.getElementById('permission').value,
    threshold: document.getElementById('threshold').value,
    minInterval: document.getElementById('minInterval').value,
  };

  fetch(`http://` + server + `:9322/api/v1/policyTables/` + policyTable.id, {
    method: 'PUT',
    headers: {
      'content-Type': 'application/json',
      'orgAffiliation': 'userOrg',
      'orgMspId': 'UserOrgMSP',
    },
    body: JSON.stringify(userData),
  }).then((res) => {
    if (res.status === 200 || res.status === 201) {
      lookUpTable.objects[policyTable.index].name = userData.objectName;

      policyTable.object.name = userData.objectName;
      policyTable.resource = userData.resource;
      policyTable.action = userData.action;
      policyTable.permission = userData.permission;
      policyTable.threshold = userData.threshold;
      policyTable.minInterval = userData.minInterval;

      updateNetwork();
      setTimeout(setUser, 300);

      closeAddUserPopup();
    } else {
      console.log(res.statusText);
    }
  });
};
