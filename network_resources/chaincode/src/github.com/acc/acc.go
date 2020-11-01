/*
 * SPDX-License-Identifier: Apache-2.0
 */

 package main

 import (
	 "encoding/json"
	 "fmt"
	 "strconv"
	 "time"
 
	 "github.com/hyperledger/fabric/core/chaincode/shim"
	 sc "github.com/hyperledger/fabric/protos/peer"
 )
 
 // Chaincode is the definition of the chaincode structure.
 type Chaincode struct {
 }
 
 type policyTable struct {
	 MethodName    string        `json:"MethodName"`
	 Resource      string        `json:"Resource"`
	 Action        string        `json:"Action"`
	 Permission    bool          `json:"Permission"`
	 ToLR          time.Time     `json:"ToLR"`
	 TimeOfUnblock time.Time     `json:"TimeOfUnblock"`
	 MinInterval   time.Duration `json:"MinInterval"`
	 NoFR          int           `json:"NoFR"`
	 Threshold     int           `json:"Threshold"`
 
	 MisbehaviorTables []misbehaviorTable `json:"Misbehaviors"`
 }
 
 type misbehaviorTable struct {
	 Misbehavior string        `json:"Misbehavior"`
	 Time        time.Time     `json:"Time"`
	 Penalty     time.Duration `json:"Penalty"`
 }
 
 // Init is called when the chaincode is instantiated by the blockchain network.
 func (cc *Chaincode) Init(stub shim.ChaincodeStubInterface) sc.Response {
	 fcn, params := stub.GetFunctionAndParameters()
	 fmt.Println("Init()", fcn, params)
	 return shim.Success(nil)
 }
 
 // Invoke is called as a result of an application request to run the chaincode.
 func (cc *Chaincode) Invoke(stub shim.ChaincodeStubInterface) sc.Response {
	 fcn, params := stub.GetFunctionAndParameters()
 
	 switch fcn {
	 case "policyAdd":
		 return cc.policyAdd(stub, params)
	 case "policyUpdate":
		 return cc.policyUpdate(stub, params)
	 case "policyDelete":
		 return cc.policyDelete(stub, params)
	 case "accessControl":
		 return cc.accessControl(stub, params)
	 case "setJC":
		 return cc.setJC(stub, params)
	 case "deleteACC":
		 return cc.deleteACC(stub, params)
	 default:
		 return shim.Error("error")
	 }
 }
 
 // resource, action, permission, threshold, minerval
 func (cc *Chaincode) policyAdd(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	 if len(params) != 6 {
		 return shim.Error("Incorrect number of parameter")
	 }
 
	 methodName, resource, action := params[0], params[1], params[2]
	 permission, _ := strconv.ParseBool(params[3])
	 threshold, _ := strconv.Atoi(params[4])
	 minInterval, _ := time.ParseDuration(params[5])
	 toLR, _ := time.Parse("1996-07-28 00:00:00", "0")
 
	 policyTableData := &policyTable{MethodName: methodName, Resource: resource, Action: action, Permission: permission, ToLR: toLR, MisbehaviorTables: nil, Threshold: threshold, TimeOfUnblock: toLR, MinInterval: minInterval}
 
	 penalty, _ := time.ParseDuration("0")
	 newMisbehaviorData := misbehaviorTable{Misbehavior: "init", Penalty: penalty, Time: toLR}
 
	 policyTableData.MisbehaviorTables = append(policyTableData.MisbehaviorTables, newMisbehaviorData)
 
	 policyTableDataBytes, err := json.Marshal(policyTableData)
	 fmt.Println(string(policyTableDataBytes))
 
	 if err != nil {
		 return shim.Error("failed to Marshal lookUpTableData, error : " + err.Error())
	 }
 
	 id := ""
	 id += methodName
	 id += resource
	 err = stub.PutState(id, policyTableDataBytes)
 
	 if err != nil {
		 return shim.Error("failed to PutState lookUpTableData, error : " + err.Error())
	 }
 
	 return shim.Success(nil)
 }
 
 func (cc *Chaincode) policyUpdate(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	 if len(params) != 6 {
		 return shim.Error("Incorrect number of parameter")
	 }
 
	 methodName, resource, action := params[0], params[1], params[2]
	 permission, _ := strconv.ParseBool(params[3])
	 threshold, _ := strconv.Atoi(params[4])
	 minInterval, _ := time.ParseDuration(params[5])
 
	 policyTableData := policyTable{}
 
	 id := ""
	 id += methodName
	 id += resource
 
	 policyTableDataBytes, err := stub.GetState(id)
 
	 if err != nil {
		 return shim.Error("failed to GetState lookUpTableData, error : " + err.Error())
	 }
 
	 err = json.Unmarshal(policyTableDataBytes, &policyTableData)
 
	 if err != nil {
		 return shim.Error("failed to UnMarshal lookUpTableData, error : " + err.Error())
	 }
 
	 policyTableData.Action = action
	 policyTableData.Permission = permission
	 policyTableData.Threshold = threshold
	 policyTableData.MinInterval = minInterval
 
	 policyTableDataBytes, err = json.Marshal(policyTableData)
 
	 if err != nil {
		 return shim.Error("failed to Marshal lookUpTableData, error : " + err.Error())
	 }
 
	 err = stub.PutState(id, policyTableDataBytes)
 
	 if err != nil {
		 return shim.Error("failed to PutState lookUpTableData, error : " + err.Error())
	 }
 
	 return shim.Success(nil)
 }
 
 func (cc *Chaincode) policyDelete(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	 if len(params) != 2 {
		 return shim.Error("Incorrect number of parameter")
	 }
 
	 methodName, resource := params[0], params[1]
	 id := ""
	 id += methodName
	 id += resource
 
	 err := stub.DelState(id)
 
	 if err != nil {
		 return shim.Error("failed to DelState methodName, error : " + err.Error())
	 }
 
	 return shim.Success(nil)
 }
 
 //resource, action
 func (cc *Chaincode) accessControl(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	 if len(params) != 3 {
		 return shim.Error("Incorrect number of parameter")
	 }
 
	 methodName, resource, action := params[0], params[1], params[2]
	 id := ""
	 id += methodName
	 id += resource
 
	 requestTime := time.Now() //time.Parse("1996-07-28 00:00:00", params[2]) //
 
	 behaviorCheck := true
	 policyCheck := false
 
	 queryString :=
		 `{
			 "selector": {
				 "MethodName": "` + methodName + `",
				 "Resource": "` + resource + `", 
				 "Action": "` + action + `"
			 }
	 }`
 
	 queryResults, err := stub.GetQueryResult(queryString)
 
	 if err != nil || !queryResults.HasNext() {
		 return shim.Error("You don't have permission for that resource")
	 }
 
	 policyTableDataEntity, err := queryResults.Next()
 
	 policyTableData := policyTable{}
	 policyTableDataBytes := policyTableDataEntity.GetValue()
 
	 err = json.Unmarshal(policyTableDataBytes, &policyTableData)
 
	 if policyTableData.TimeOfUnblock.Before(requestTime) {
		 if !policyTableData.TimeOfUnblock.IsZero() {
			 policyTableData.NoFR = 0
			 policyTableData.ToLR, _ = time.Parse("1996-07-28 00:00:00", "0")
			 policyTableData.TimeOfUnblock, _ = time.Parse("1996-07-28 00:00:00", "0")
		 }
 
		 policyCheck = policyTableData.Permission
 
		 if requestTime.Sub(policyTableData.ToLR) <= policyTableData.MinInterval {
			 policyTableData.NoFR++
 
			 if policyTableData.NoFR >= policyTableData.Threshold {
				 behaviorCheck = false
 
				 msb := "msb"
				 penalty := cc.misbehaviorJudge(stub, "subject", "msb")
 
				 policyTableData.TimeOfUnblock = requestTime.Add(penalty)
 
				 newMisbehaviorData := misbehaviorTable{Misbehavior: msb, Penalty: penalty, Time: requestTime}
				 policyTableData.MisbehaviorTables = append(policyTableData.MisbehaviorTables, newMisbehaviorData)
			 }
		 } else {
			 policyTableData.NoFR = 0
		 }
		 policyTableData.ToLR = requestTime
	 }
 
	 policyTableDataBytes, err = json.Marshal(policyTableData)
 
	 if err != nil {
		 return shim.Error("failed to Marshal lookUpTableData, error : " + err.Error())
	 }
 
	 err = stub.PutState(id, policyTableDataBytes)
 
	 if err != nil {
		 return shim.Error("failed to PutState lookUpTableData, error : " + err.Error())
	 }
 
	 output := shim.Success(nil)
 
	 if policyCheck && behaviorCheck {
		 output.Message = "true"
	 } else {
		 output.Message = "false"
	 }
 
	 return output
 }
 
 func (cc *Chaincode) misbehaviorJudge(stub shim.ChaincodeStubInterface, subject string, msb string) time.Duration {
	 jcChainCodeParamas := [][]byte{[]byte("misbehaviorJudge"), []byte(subject), []byte(msb)}
	 jcResponse := stub.InvokeChaincode("JC", jcChainCodeParamas, "mychannel")
 
	 penalty, _ := time.ParseDuration(jcResponse.GetMessage())
	 return penalty
 }
 
 func (cc *Chaincode) setJC(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	 return shim.Success([]byte("success"))
 }
 
 func (cc *Chaincode) deleteACC(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	 return shim.Success([]byte("success"))
 }
 