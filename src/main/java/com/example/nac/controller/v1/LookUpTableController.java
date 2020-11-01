package main.java.com.example.nac.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.com.example.nac.Enum.ChanCode;
import main.java.com.example.nac.fabric.chaincode.InvokeQueryChaincode;
import main.java.com.example.nac.fabric.network.DeployInstantiateChaincode;
import org.hyperledger.fabric.sdk.exception.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class LookUpTableController {

    private final DeployInstantiateChaincode deployInstantiateChaincode = new DeployInstantiateChaincode();
    private final InvokeQueryChaincode invokeQueryChaincode = null;

    @PostMapping(path = "/lookUpTable")
    public void createLookUpTable(@RequestBody String lookUpTableJson) throws ProposalException, InvalidArgumentException, ParseException {
        invokeQueryChaincode.setChaincodeName(ChanCode.CREATE_LookUpTable.getName());
        invokeQueryChaincode.setFunc(ChanCode.CREATE_LookUpTable.getFunc());

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(lookUpTableJson);

        List<String> arguments = new LinkedList<>();
        //arguments.add(methodName);
        invokeQueryChaincode.setArguments(arguments);

        invokeQueryChaincode.invoke();
    }

    @GetMapping(path = "/lookUpTable/{methodName}")
    public void getContract(@PathVariable("methodName") String methodName) throws ProposalException, InvalidArgumentException {
        invokeQueryChaincode.setChaincodeName(ChanCode.GET_LookUpTable.getName());
        invokeQueryChaincode.setFunc(ChanCode.GET_LookUpTable.getFunc());

        List<String> arguments = new LinkedList<>();
        arguments.add(methodName);
        invokeQueryChaincode.setArguments(arguments);

        invokeQueryChaincode.invoke();
    }

    @PutMapping(path = "/lookUpTable/{methodName}")
    public void updateLookUpTable(@PathVariable("methodName") String methodName, @RequestBody String requestJson) throws ChaincodeEndorsementPolicyParseException, InvalidArgumentException, ProposalException, IOException {
        invokeQueryChaincode.setChaincodeName(ChanCode.UPDATE_LookUpTable.getName());
        invokeQueryChaincode.setFunc(ChanCode.UPDATE_LookUpTable.getFunc());
        //invokeQueryChaincode.setArguments();
    }

    @DeleteMapping(path = "/lookUpTable/{methodName}")
    public void deleteLookUpTable(@PathVariable("methodName") String methodName) throws ProposalException, InvalidArgumentException {
        invokeQueryChaincode.setChaincodeName(ChanCode.DELETE_LookUpTable.getName());
        invokeQueryChaincode.setFunc(ChanCode.DELETE_LookUpTable.getFunc());

        List<String> arguments = new LinkedList<>();
        arguments.add(methodName);
        invokeQueryChaincode.setArguments(arguments);

        invokeQueryChaincode.invoke();
    }

    @GetMapping(path = "/lookUpTable")
    public void getContractList() throws ProposalException, InvalidArgumentException, ParseException {
        invokeQueryChaincode.setChaincodeName(ChanCode.GET_LookUpTableList.getName());
        invokeQueryChaincode.setFunc(ChanCode.GET_LookUpTableList.getFunc());

        List<String> arguments = new LinkedList<>();
        arguments.add("");
        invokeQueryChaincode.setArguments(arguments);

        invokeQueryChaincode.invoke();
    }

    @GetMapping(path = "/init")
    public void init() throws ChaincodeEndorsementPolicyParseException, InvalidArgumentException, ProposalException, IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, InvalidKeySpecException, IllegalAccessException, NoSuchAlgorithmException, CryptoException, ClassNotFoundException, TransactionException, InterruptedException {
        //deployInstantiateChaincode.run();
        //Thread.sleep(1000);
        deployInstantiateChaincode.runRC();
        //Thread.sleep(1000);
        //deployInstantiateChaincode.runJC();
    }

}
