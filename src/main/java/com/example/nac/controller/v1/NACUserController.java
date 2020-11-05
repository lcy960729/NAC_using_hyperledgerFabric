package main.java.com.example.nac.controller.v1;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.com.example.nac.DTO.ChainCodeArgsDTO;
import main.java.com.example.nac.DTO.RegisterMethodDTO;
import main.java.com.example.nac.fabric.FabricNetwork;
import main.java.com.example.nac.fabric.config.Config;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/NACUser/")
public class NACUserController {
    FabricNetwork fabricNetwork = new FabricNetwork();

    @GetMapping(path = "/init")
    public ResponseEntity<String> init() {
        try {
            fabricNetwork.createChannel();
            Thread.sleep(1000);
            fabricNetwork.enrollAdmin(Config.CA_UserOrg_URL, Config.ADMIN, Config.UserOrg, Config.UserOrg_MSP);
            Thread.sleep(1000);
            fabricNetwork.deployInstantiateChaincode(Config.CHAINCODE_RC_NAME, Config.CHAINCODE_RC_PATH, Config.CHAINCODE_RC_VERSION);
            Thread.sleep(1000);
            fabricNetwork.deployInstantiateChaincode(Config.CHAINCODE_ACC_NAME, Config.CHAINCODE_ACC_PATH, Config.CHAINCODE_ACC_VERSION);
            Thread.sleep(1000);
            fabricNetwork.deployInstantiateChaincode(Config.CHAINCODE_JC_NAME, Config.CHAINCODE_JC_PATH, Config.CHAINCODE_JC_VERSION);
            Thread.sleep(1000);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping(path = "/test")
    public ResponseEntity<String> test(@RequestParam("version") String version) {
        try {
            fabricNetwork.deployInstantiateChaincode("acc1", Config.CHAINCODE_ACC_PATH, Config.CHAINCODE_ACC_VERSION);
            Thread.sleep(1000);
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/register")
    public ResponseEntity<String> registerUser(@RequestBody String registerSubjectJson,
                                               @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                               @RequestHeader(value = "orgMspId") String orgMspId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RegisterMethodDTO registerMethodDTO = objectMapper.readValue(registerSubjectJson, RegisterMethodDTO.class);

            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(registerMethodDTO.getMethodName());
            arguments.add(objectMapper.writeValueAsString(registerMethodDTO.getSubject()));
            arguments.add("acc");
            arguments.add("accessControl");

            String secretKey = fabricNetwork.registerEnrollUser(Config.CA_UserOrg_URL, Config.UserOrg, Config.UserOrg_MSP, registerMethodDTO.getMethodName());

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(registerMethodDTO.getMethodName())
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(secretKey)
                    .chaincodeName("rc")
                    .func("methodRegister")
                    .arguments(arguments)
                    .build();

            fabricNetwork.invoke(chainCodeArgsDTO);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("secretKey", secretKey);

            String argumentsJson = objectMapper.writeValueAsString(arguments);

            return new ResponseEntity<>(argumentsJson, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
