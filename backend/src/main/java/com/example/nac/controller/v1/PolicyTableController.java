package main.java.com.example.nac.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.com.example.nac.DTO.*;
import main.java.com.example.nac.Fabric.DTO.ResultQueryAndInvokeDTO;
import main.java.com.example.nac.Fabric.FabricNetwork;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
public class PolicyTableController {
    final FabricNetwork fabricNetwork;

    public PolicyTableController(FabricNetwork fabricNetwork) {
        this.fabricNetwork = fabricNetwork;
    }

    @PostMapping(path = "/policyTables")
    public ResponseEntity<String> policyAdd(@RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                            @RequestHeader(value = "orgMspId") String orgMspId,
                                            @RequestBody String argumentsJson) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AddPolicyDTO addPolicyDTO = objectMapper.readValue(argumentsJson, AddPolicyDTO.class);

            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(UUID.randomUUID().toString());
            arguments.add(addPolicyDTO.getSubjectId());
            arguments.add(objectMapper.writeValueAsString(addPolicyDTO.getObject()));
            arguments.add(addPolicyDTO.getResource());
            arguments.add(addPolicyDTO.getAction());
            arguments.add(String.valueOf(addPolicyDTO.getPermission()));
            arguments.add(String.valueOf(addPolicyDTO.getThreshold()));
            arguments.add(String.valueOf(addPolicyDTO.getMinInterval()));

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(null)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(null)
                    .chaincodeName("acc")
                    .func("policyAdd")
                    .arguments(arguments)
                    .build();

            ResultQueryAndInvokeDTO result = fabricNetwork.invoke(chainCodeArgsDTO, true);

            if (result.getStatus() == 200) {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Location", "/policyTables/" + result.getMessage());

                return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
            } else {
                return new ResponseEntity(result.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping(path = "/policyTables/{id}")
    public ResponseEntity<String> getPolicy(@PathVariable("id") String id,
                                            @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                            @RequestHeader(value = "orgMspId") String orgMspId) {

        try {
            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(id);

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(null)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .chaincodeName("acc")
                    .func("getPolicy")
                    .arguments(arguments)
                    .build();

            ResultQueryAndInvokeDTO result = fabricNetwork.query(chainCodeArgsDTO, true);

            if (result.getStatus() == 200) {
                return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
            } else {
                return new ResponseEntity(result.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @PutMapping(path = "/policyTables/{id}")
    public ResponseEntity<String> policyUpdate(@PathVariable("id") String id,
                                               @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                               @RequestHeader(value = "orgMspId") String orgMspId,
                                               @RequestBody String argumentsJson) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UpdatePolicyDTO updatePolicyDTO = objectMapper.readValue(argumentsJson, UpdatePolicyDTO.class);

            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(id);
            arguments.add(updatePolicyDTO.getObjectName());
            arguments.add(updatePolicyDTO.getResource());
            arguments.add(updatePolicyDTO.getAction());
            arguments.add(String.valueOf(updatePolicyDTO.getPermission()));
            arguments.add(String.valueOf(updatePolicyDTO.getThreshold()));
            arguments.add(String.valueOf(updatePolicyDTO.getMinInterval()));

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(null)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(null)
                    .chaincodeName("acc")
                    .func("policyUpdate")
                    .arguments(arguments)
                    .build();

            ResultQueryAndInvokeDTO result = fabricNetwork.invoke(chainCodeArgsDTO, true);

            if (result.getStatus() == 200) {
                return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
            } else {
                return new ResponseEntity(result.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @DeleteMapping(path = "/policyTables/{id}")
    public ResponseEntity<String> policyDelete(@PathVariable("id") String id,
                                               @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                               @RequestHeader(value = "orgMspId") String orgMspId) {

        try {
            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(id);

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(null)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(null)
                    .chaincodeName("acc")
                    .func("policyDelete")
                    .arguments(arguments)
                    .build();

            ResultQueryAndInvokeDTO result = fabricNetwork.invoke(chainCodeArgsDTO, true);

            if (result.getStatus() == 200) {
                return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
            } else {
                return new ResponseEntity(result.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping(path = "/accessControl")
    public ResponseEntity<String> accessControl(
            @RequestParam("id") String id,
            @RequestHeader(value = "orgAffiliation") String orgAffiliation,
            @RequestHeader(value = "orgMspId") String orgMspId) {

        try {
            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(id);
            arguments.add(LocalDateTime.now().toString());

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(null)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(null)
                    .chaincodeName("acc")
                    .func("accessControl")
                    .arguments(arguments)
                    .build();

            ResultQueryAndInvokeDTO result = fabricNetwork.invoke(chainCodeArgsDTO, true);

            if (result.getMessage().equals("true")) {
                return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
            } else {
                return new ResponseEntity(result.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
