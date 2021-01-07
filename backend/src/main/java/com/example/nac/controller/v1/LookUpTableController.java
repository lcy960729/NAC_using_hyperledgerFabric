package main.java.com.example.nac.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import main.java.com.example.nac.DTO.ChainCodeArgsDTO;
import main.java.com.example.nac.DTO.RegisterMethodDTO;
import main.java.com.example.nac.DTO.UpdateMethodDTO;
import main.java.com.example.nac.Fabric.DTO.ResultQueryAndInvokeDTO;
import main.java.com.example.nac.Fabric.FabricNetwork;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
public class LookUpTableController {

    final FabricNetwork fabricNetwork;

    public LookUpTableController(FabricNetwork fabricNetwork) {
        this.fabricNetwork = fabricNetwork;
    }

    @PostMapping(path = "/lookUpTables")
    public ResponseEntity<String> addLookUpTable(@RequestBody String registerNetworkData,
                                                 @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                                 @RequestHeader(value = "orgMspId") String orgMspId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RegisterMethodDTO registerMethodDTO = objectMapper.readValue(registerNetworkData, RegisterMethodDTO.class);

            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(UUID.randomUUID().toString());
            arguments.add(registerMethodDTO.getMethodName());
            arguments.add(objectMapper.writeValueAsString(registerMethodDTO.getSubject()));
            arguments.add("acc");
            arguments.add("accessControl");

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(null)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(null)
                    .chaincodeName("rc")
                    .func("methodRegister")
                    .arguments(arguments)
                    .build();

            ResultQueryAndInvokeDTO result = fabricNetwork.invoke(chainCodeArgsDTO, true);

            if (result.getStatus() == 200) {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Location", "/lookUpTables/" + result.getMessage());

                return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
            } else {
                return new ResponseEntity(result.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            }


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/lookUpTables/{id}")
    public ResponseEntity<String> updateLookUpTable(@PathVariable("id") String id,
                                                    @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                                    @RequestHeader(value = "orgMspId") String orgMspId,
                                                    @RequestBody String argumentsJson) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateMethodDTO updateMethodDTO = objectMapper.readValue(argumentsJson, UpdateMethodDTO.class);

            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(id);
            arguments.add(updateMethodDTO.getMethodName());
            arguments.add(updateMethodDTO.getSubjectName());
            arguments.add(objectMapper.writeValueAsString(updateMethodDTO.getObjects()));
            arguments.add(updateMethodDTO.getScName());
            arguments.add(updateMethodDTO.getAbi());

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(null)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(null)
                    .chaincodeName("rc")
                    .func("methodUpdate")
                    .arguments(arguments)
                    .build();

            ResultQueryAndInvokeDTO result = fabricNetwork.invoke(chainCodeArgsDTO, true);

            if (result.getStatus() == 200) {
                return new ResponseEntity(result.getMessage(), HttpStatus.OK);
            } else {
                return new ResponseEntity(result.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }

    }


    @DeleteMapping(path = "/lookUpTables/{id}")
    public ResponseEntity<String> deleteLookUpTable(@PathVariable("id") String id,
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
                    .chaincodeName("rc")
                    .func("methodDelete")
                    .arguments(arguments)
                    .build();

            ResultQueryAndInvokeDTO result = fabricNetwork.invoke(chainCodeArgsDTO, true);

            if (result.getStatus() == 200) {
                return new ResponseEntity(result.getMessage(), HttpStatus.OK);
            } else {
                return new ResponseEntity(result.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping(path = "/lookUpTables/{id}")
    public ResponseEntity<String> getContract(@PathVariable("id") String id,
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
                    .chaincodeName("rc")
                    .func("getContract")
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

    @GetMapping(path = "/lookUpTables/object")
    public ResponseEntity<String> getObjList(@RequestParam("macAddress") String macAddress,
                                             @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                             @RequestHeader(value = "orgMspId") String orgMspId) {

        try {
            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(macAddress);

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(null)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(null)
                    .chaincodeName("rc")
                    .func("getObjList")
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

    @GetMapping(path = "/lookUpTables")
    public ResponseEntity<String> getContractList(@RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                                  @RequestHeader(value = "orgMspId") String orgMspId) {

        try {
            ArrayList<String> arguments = new ArrayList<>();

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(null)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(null)
                    .chaincodeName("rc")
                    .func("getMethodNameList")
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
}
