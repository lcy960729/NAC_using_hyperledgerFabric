package main.java.com.example.nac.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.com.example.nac.DTO.*;
import main.java.com.example.nac.fabric.FabricNetwork;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/")
public class PolicyTableController {

    FabricNetwork fabricNetwork = new FabricNetwork();

    @PostMapping(path = "/policyTable")
    public ResponseEntity<String> policyAdd(@RequestHeader(value = "userName") String userName,
                          @RequestHeader(value = "secretKey") String secretKey,
                          @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                          @RequestHeader(value = "orgMspId") String orgMspId,
                          @RequestBody String argumentsJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AddPolicyDTO addPolicyDTO = objectMapper.readValue(argumentsJson, AddPolicyDTO.class);

            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(objectMapper.writeValueAsString(addPolicyDTO.getObject()));
            arguments.add(addPolicyDTO.getResource());
            arguments.add(addPolicyDTO.getAction());
            arguments.add(String.valueOf(addPolicyDTO.getPermission()));
            arguments.add(String.valueOf(addPolicyDTO.getThreshold()));
            arguments.add(String.valueOf(addPolicyDTO.getMinInterval()));

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(userName)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(secretKey)
                    .chaincodeName("acc1")
                    .func("policyAdd")
                    .arguments(arguments)
                    .build();

            fabricNetwork.invoke(chainCodeArgsDTO);

            return new ResponseEntity<>(objectMapper.writeValueAsString(arguments), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/policyTable/{methodName}")
    public ResponseEntity<String> policyUpdate(@PathVariable("methodName") String objectName,
                                               @RequestHeader(value = "userName") String userName,
                                               @RequestHeader(value = "secretKey") String secretKey,
                                               @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                               @RequestHeader(value = "orgMspId") String orgMspId,
                                               @RequestBody String argumentsJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UpdatePolicyDTO updatePolicyDTO = objectMapper.readValue(argumentsJson, UpdatePolicyDTO.class);

            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(objectMapper.writeValueAsString(updatePolicyDTO.getObject()));
            arguments.add(updatePolicyDTO.getResource());
            arguments.add(updatePolicyDTO.getAction());
            arguments.add(String.valueOf(updatePolicyDTO.getPermission()));
            arguments.add(String.valueOf(updatePolicyDTO.getThreshold()));
            arguments.add(String.valueOf(updatePolicyDTO.getMinInterval()));

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(userName)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(secretKey)
                    .chaincodeName("acc1")
                    .func("policyUpdate")
                    .arguments(arguments)
                    .build();

            fabricNetwork.invoke(chainCodeArgsDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/policyTable/{methodName}")
    public ResponseEntity<String> policyDelete(@PathVariable("methodName") String objectName,
                                               @RequestHeader(value = "userName") String userName,
                                               @RequestHeader(value = "secretKey") String secretKey,
                                               @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                               @RequestHeader(value = "orgMspId") String orgMspId,
                                               @RequestBody String argumentsJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            DeletePolicyDTO deletePolicyDTO = objectMapper.readValue(argumentsJson, DeletePolicyDTO.class);

            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(objectMapper.writeValueAsString(deletePolicyDTO.getObject()));
            arguments.add(deletePolicyDTO.getResource());

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(userName)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(secretKey)
                    .chaincodeName("acc")
                    .func("policyDelete")
                    .arguments(arguments)
                    .build();

            fabricNetwork.invoke(chainCodeArgsDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/accessControl")
    public ResponseEntity<String> accessControl(@RequestHeader(value = "userName") String userName,
                                                @RequestHeader(value = "secretKey") String secretKey,
                                                @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                                @RequestHeader(value = "orgMspId") String orgMspId,
                                                @RequestBody String argumentsJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AccessControlDTO accessControlDTO = objectMapper.readValue(argumentsJson, AccessControlDTO.class);

            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(objectMapper.writeValueAsString(accessControlDTO.getObject()));
            arguments.add(accessControlDTO.getResource());
            arguments.add(accessControlDTO.getAction());

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(userName)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(secretKey)
                    .chaincodeName("acc")
                    .func("accessControl")
                    .arguments(arguments)
                    .build();

            fabricNetwork.query(chainCodeArgsDTO, false);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok().build();
    }


}
