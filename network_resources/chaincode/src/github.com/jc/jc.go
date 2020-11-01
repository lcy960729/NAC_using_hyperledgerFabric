package main

import (
	"fmt"
	"math"
	"strconv"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"
)

type Chaincode struct {
}

func (cc *Chaincode) Init(stub shim.ChaincodeStubInterface) sc.Response {
	fcn, params := stub.GetFunctionAndParameters()
	fmt.Println("Init()", fcn, params)
	return shim.Success(nil)
}

// Invoke is called as a result of an application request to run the chaincode.
func (cc *Chaincode) Invoke(stub shim.ChaincodeStubInterface) sc.Response {
	fcn, params := stub.GetFunctionAndParameters()

	switch fcn {
	case "misbehaviorJudge":
		return cc.misbehaviorJudge(stub, params)
	default:
		return shim.Error("error")
	}
}

func (cc *Chaincode) misbehaviorJudge(stub shim.ChaincodeStubInterface, params []string) sc.Response {
	msb := params[0] // misbehavior
	msbLen, err := strconv.ParseFloat(params[1], 64)
	if err != nil {
		return shim.Error(err.Error())
	}

	var base float64
	var litervar float64
	var squared float64
	var penalty string

	switch msb {
	case "FrequentAccess":
		base = 2
		litervar = 1
		squared = msbLen / litervar
		penalty = strconv.Itoa(int(math.Pow(base, squared))) + "m"
		break
	default:
		penalty = "0m"
		break
	}

	response := shim.Success([]byte("success"))
	response.Message = penalty

	return response
}
