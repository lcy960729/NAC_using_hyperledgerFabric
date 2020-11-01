/*
 * SPDX-License-Identifier: Apache-2.0
 */

package main

import (
	"encoding/json"
	"fmt"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"
)

// Chaincode is the definition of the chaincode structure.
type Chaincode struct {
}

type lookUpTable struct {
	MethodName string   `json:"MethodName"`
	Subject    []string `json:"Subject"` //[name,MAC]
	Object     []string `json:"Object"`  //[name,MAC]
	ScName     string   `json:"ScName"`
	ABI        string   `json:"ABI"`
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
	case "methodRegister":
		return cc.methodRegister(stub, params)
	case "methodUpdate":
		return cc.methodUpdate(stub, params)
	case "methodDelete":
		return cc.methodDelete(stub, params)
	case "getContract":
		return cc.getContract(stub, params)
	case "getLookUpTableList":
		return cc.getLookUpTableList(stub, params)
	case "getAllLookUpTableList":
		return cc.getAllLookUpTableList(stub, params)
	default:
		return shim.Error("error")
	}
}

/*
 when recieves information the new method , register the information into the lookup table
 methodname , subject(name, mac addr), object(name, mac addr), scname, ABI
*/
func (cc *Chaincode) methodRegister(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	if len(params) != 7 {
		return shim.Error("Incorrect number of parameter")
	}
	methodName, subjectname, subjectmac, objectname, objectmac, scName, abi := params[0], params[1], params[2], params[3], params[4], params[5], params[6]

	lookUpTableData := &lookUpTable{MethodName: methodName, Subject: []string{subjectname, subjectmac}, Object: []string{objectname, objectmac}, ScName: scName, ABI: abi}

	lookUpTableDataBytes, err := json.Marshal(lookUpTableData) //json encoding

	if err != nil { //nil = no error
		return shim.Error("failed to Marshal lookUpTableData, error : " + err.Error())
	}

	err = stub.PutState(methodName, lookUpTableDataBytes)

	if err != nil {
		return shim.Error("failed to PutState lookUpTableData, error : " + err.Error())
	}

	return shim.Success([]byte("true"))
}

/*
 receive the information existing method that needs to be updated and update information
*/
func (cc *Chaincode) methodUpdate(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	if len(params) != 7 {
		return shim.Error("Incorrect number of parameter")
	}

	methodName, subjectname, subjectmac, objectname, objectmac, scName, abi := params[0], params[1], params[2], params[3], params[4], params[5], params[6]

	lookUpTableData := lookUpTable{}
	lookUpTableDataBytes, err := stub.GetState(methodName)

	if err != nil {
		return shim.Error("failed to GetState lookUpTableData, error : " + err.Error())
	}

	err = json.Unmarshal(lookUpTableDataBytes, &lookUpTableData)

	if err != nil {
		return shim.Error("failed to UnMarshal lookUpTableData, error : " + err.Error())
	}

	lookUpTableData.Subject = []string{subjectname, subjectmac}
	lookUpTableData.Object = []string{objectname, objectmac}
	lookUpTableData.ScName = scName
	lookUpTableData.ABI = abi

	lookUpTableDataBytes, err = json.Marshal(lookUpTableData)

	if err != nil {
		return shim.Error("failed to Marshal lookUpTableData, error : " + err.Error())
	}

	err = stub.PutState(methodName, lookUpTableDataBytes)

	if err != nil {
		return shim.Error("failed to PutState lookUpTableData, error : " + err.Error())
	}

	return shim.Success([]byte("true"))
}

/*
 receive methodname , Delete the information on lookuptable
*/
func (cc *Chaincode) methodDelete(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	if len(params) != 1 {
		return shim.Error("Incorrect number of parameter")
	}

	methodName := params[0]

	err := stub.DelState(methodName)

	if err != nil {
		return shim.Error("failed to DelState methodName, error : " + err.Error())
	}

	return shim.Success([]byte("true"))
}

/*
 receive methodname,  return Method address, and of the contract (ACC,JC) of the method.
*/

func (cc *Chaincode) getContract(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	if len(params) != 1 {
		return shim.Error("Incorrect number of parameter")
	}

	methodName := params[0]

	lookUpTableData := lookUpTable{}
	lookUpTableDataBytes, err := stub.GetState(methodName)

	if err != nil {
		return shim.Error("failed to GetState lookUpTableData, error : " + err.Error())
	}

	err = json.Unmarshal(lookUpTableDataBytes, &lookUpTableData)

	if err != nil {
		return shim.Error("failed to UnMarshal lookUpTableData, error : " + err.Error())
	}

	fmt.Println(string(lookUpTableDataBytes))

	response := shim.Success([]byte("success"))
	response.Message = string(lookUpTableDataBytes)
	return response
}

func (cc *Chaincode) getLookUpTableList(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	if len(params) != 2 {
		return shim.Error("Incorrect number of parameter")
	}

	subjectName := params[0]
	//subjectMac := params[1]

	queryString :=
		`{
		"selector": {
		   "Subject": 
		   [  
				"` + subjectName + `",
				"` + "00:00:00:00" + `"
		   ]
		}
		}`

	queryResults, err := stub.GetQueryResult(queryString)

	if err != nil {
		return shim.Error("Error")
	}

	lookUpTableData := lookUpTable{}
	var lookUpTableDataList []lookUpTable

	for queryResults.HasNext() {
		lookUpTableDataEntity, _ := queryResults.Next()
		err = json.Unmarshal(lookUpTableDataEntity.GetValue(), &lookUpTableData)

		lookUpTableDataList = append(lookUpTableDataList, lookUpTableData)
	}

	lookUpTableDataBytes, err := json.Marshal(lookUpTableDataList)
	return shim.Success([]byte(lookUpTableDataBytes))
}

func (cc *Chaincode) getAllLookUpTableList(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	queryString := `{
		"selector": {
		   "MethodName": {
			  "$regex": ""
		   }
		}
	 }`

	queryResults, err := stub.GetQueryResult(queryString)

	if err != nil {
		return shim.Success([]byte("Error index is zero"))
	}

	var lookUpTableDataList []lookUpTable

	for queryResults.HasNext() {
		lookUpTableDataEntity, _ := queryResults.Next()

		lookUpTableData := lookUpTable{}
		err = json.Unmarshal(lookUpTableDataEntity.GetValue(), &lookUpTableData)

		lookUpTableDataList = append(lookUpTableDataList, lookUpTableData)
	}

	lookUpTableDataBytes, err := json.Marshal(lookUpTableDataList)
	return shim.Success([]byte(lookUpTableDataBytes))
}

func main() {

	// Create a new Smart Contract
	err := shim.Start(new(Chaincode))
	if err != nil {
		fmt.Printf("Error creating new Smart Contract: %s", err)
	}
}