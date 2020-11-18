import { setNetwork } from './getNetwork';

export const updateNetwork = () => {
  var updateNetworkData = {
    methodName: lookUpTable.methodName,
    subjectName: lookUpTable.subject.name,
    objects: lookUpTable.objects,
    scName: lookUpTable.scName,
    abi: lookUpTable.abi,
  };

  fetch(`http://` + server + `:9322/api/v1/lookUpTables/` + lookUpTable.id, {
    method: 'PUT',
    headers: {
      'content-Type': 'application/json',
      'orgAffiliation': 'userOrg',
      'orgMspId': 'UserOrgMSP',
    },
    body: JSON.stringify(updateNetworkData),
  }).then((res) => {
    if (res.status === 200 || res.status === 201) {
      setNetwork();
    } else {
      console.log(res.statusText);
    }
  });
};
