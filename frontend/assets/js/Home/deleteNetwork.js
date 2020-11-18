const deleteNetwork = () => {
  if (!confirm('네트워크를 삭제하시겠습니까?')) {
    return;
  }

  fetch(`http://` + server + `:9322/api/v1/lookUpTables/` + lookUpTable.id, {
    method: 'DELETE',
    headers: {
      orgAffiliation: 'userOrg',
      orgMspId: 'UserOrgMSP',
    },
  }).then((res) => {
    if (res.status === 200 || res.status === 201) {
      const opt = networkList[lookUpTable.index].optionElement;
      opt.parentNode.removeChild(opt);

      deleteUserList(lookUpTable.objects);

      document.getElementById('updateUserBtn').style.display = 'none';
      document.getElementById('deleteUserBtn').style.display = 'none';
      document.getElementById('misTable').style.display = 'none';
      document.getElementById('deleteNetworkBtn').style.display = 'none';
      document.getElementById('objectTable').style.display = 'none';
      document.getElementById('addUserBtn').style.display = 'none';

      document.getElementById('lookupTable').innerHTML = '';
      document.getElementById('tbody').innerHTML = '';

      document.getElementById('policyTable').innerHTML = '';
      document.getElementById('misbody').innerHTML = '';

      lookUpTable.id = '';
      lookUpTable.methodName = '';
      lookUpTable.subject = '';
      lookUpTable.scName = '';
      lookUpTable.abi = '';
      lookUpTable.objects = new Array();
      lookUpTable.index = '';
    } else {
      console.log(res.statusText);
    }
  });
};

const deleteUserList = (objects) => {
  for (let object of objects) {
    fetch(`http://` + server + `:9322/api/v1/policyTables/` + object.policyId, {
      method: 'DELETE',
      headers: {
        orgAffiliation: 'userOrg',
        orgMspId: 'UserOrgMSP',
      },
    }).then((res) => {
      if (res.status === 200 || res.status === 201) {
        console.log(object.policyId + ' is DELETED!');
      } else {
        console.log(res.statusText);
      }
    });
  }
};

const deleteNetworkBtn = document.getElementById('deleteNetworkBtn');
deleteNetworkBtn.addEventListener('click', deleteNetwork);
