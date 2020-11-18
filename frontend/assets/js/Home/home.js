import { getNetworkList } from './getNetworkList';
import { addUser } from './addUser';
import { updateUser } from './updateUser';
import './createNetwork';
import './deleteNetwork';
import './deleteUser';

global.server = '180.189.90.200';
global.networkList = new Array();
global.lookUpTable = new Object();
global.policyTable = new Object();

const addUserPopUpForm = new Array();

const title = document.getElementById('addUser_popup_title');
const methodNameAtAddUser = document.getElementById('methodNameAtAddUser');
const objectName = document.getElementById('objectName');
const objectMacAddress = document.getElementById('objectMacAddress');
const resource = document.getElementById('resource');
const action = document.getElementById('action');
const permission = document.getElementById('permission');
const threshold = document.getElementById('threshold');
const minInterval = document.getElementById('minInterval');
const submitUserBtn = document.getElementById('submitUserBtn');

addUserPopUpForm.push(title);
addUserPopUpForm.push(methodNameAtAddUser);
addUserPopUpForm.push(objectName);
addUserPopUpForm.push(objectMacAddress);
addUserPopUpForm.push(resource);
addUserPopUpForm.push(action);
addUserPopUpForm.push(permission);
addUserPopUpForm.push(threshold);
addUserPopUpForm.push(minInterval);
addUserPopUpForm.push(submitUserBtn);

const createNetwork_popup = document.getElementById('network-popup');
const createNetwork_popup_closeBtn = document.getElementById('closeBtn');
const createNetwork_popup_openBtn = document.getElementById('addNetworkBtn');

const addUser_popup = document.getElementById('addUser-popup');
const addUser_popup_closeBtn = document.getElementById('usp-closeBtn');
const addUserBtn = document.getElementById('addUserBtn');
const updateUserBtn = document.getElementById('updateUserBtn');

export const closeCreateNetworkPopup = () => {
  createNetwork_popup.style.visibility = 'hidden';
};
createNetwork_popup_closeBtn.addEventListener('click', closeCreateNetworkPopup);

const viewCreateNetworkPopup = () => {
  document.getElementById('methodName').value = '';
  document.getElementById('subjectName').value = '';
  document.getElementById('macAddress').value = '';
  createNetwork_popup.style.visibility = 'visible';
};
createNetwork_popup_openBtn.addEventListener('click', viewCreateNetworkPopup);

export const closeAddUserPopup = () => {
  addUser_popup.style.visibility = 'hidden';
};
addUser_popup_closeBtn.addEventListener('click', closeAddUserPopup);

const viewAddUserPopup = (mode) => {
  addUser_popup.style.visibility = 'visible';

  for (const componant of addUserPopUpForm) {
    componant.innerHTML = '';
    componant.value = '';
  }

  resource.disabled = true;
  action.disabled = true;
  if (mode == '사용자 추가') {
    title.innerText = '사용자 추가';
    methodNameAtAddUser.value = lookUpTable.methodName;
    objectMacAddress.disabled = false;
    resource.value = 'Network';
    action.value = 'connect';
    submitUserBtn.innerText = 'Add User';
  } else {
    title.innerText = '사용자 수정';
    methodNameAtAddUser.value = lookUpTable.methodName;
    objectName.value = policyTable.object.name;
    objectMacAddress.value = policyTable.object.macAddress;
    objectMacAddress.disabled = true;
    resource.value = policyTable.resource;
    action.value = policyTable.action;
    permission.value = policyTable.permission;
    threshold.value = policyTable.threshold;
    minInterval.value = policyTable.minInterval;
    submitUserBtn.innerText = 'Update User';
  }
};

addUserBtn.addEventListener('click', (event) => {
  viewAddUserPopup(event.target.innerText);
});

updateUserBtn.addEventListener('click', (event) => {
  viewAddUserPopup(event.target.innerText);
});

submitUserBtn.addEventListener('click', (event) => {
  if (event.target.innerText == 'Add User') {
    addUser();
  } else {
    updateUser();
  }
});

function init() {
  getNetworkList();
}

init();
