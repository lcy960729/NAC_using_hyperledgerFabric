package main.java.com.example.nac.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.com.example.nac.DTO.ChainCodeArgsDTO;
import main.java.com.example.nac.DTO.RegisterMethodDTO;
import main.java.com.example.nac.DTO.UpdateMethodDTO;
import main.java.com.example.nac.fabric.FabricNetwork;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/")
public class LookUpTableController {
    FabricNetwork fabricNetwork = new FabricNetwork();

    @PostMapping(path = "/lookUpTable")
    public ResponseEntity<String> createLookUpTable(@RequestHeader(value = "userName") String userName,
                                                    @RequestHeader(value = "secretKey") String secretKey,
                                                    @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                                    @RequestHeader(value = "orgMspId") String orgMspId,
                                                    @RequestBody String argumentsJson) {

        try {
            ArrayList<String> arguments = new ArrayList<>();
            JSONArray jsonArray = (JSONArray) ((JSONObject) new JSONParser().parse(argumentsJson)).get("arguments");
            jsonArray.forEach(jsonItem -> arguments.add((String) jsonItem));

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(userName)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(secretKey)
                    .chaincodeName("rc")
                    .func("methodRegister")
                    .arguments(arguments)
                    .build();

            fabricNetwork.invoke(chainCodeArgsDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.ok().build();
    }

    //api/v1/Login
    @PutMapping(path = "/lookUpTable/{methodName}")
    public ResponseEntity<String> updateLookUpTable(@PathVariable("methodName") String methodName,
                                                    @RequestHeader(value = "userName") String userName,
                                                    @RequestHeader(value = "secretKey") String secretKey,
                                                    @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                                    @RequestHeader(value = "orgMspId") String orgMspId,
                                                    @RequestBody String argumentsJson) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateMethodDTO updateMethodDTO = objectMapper.readValue(argumentsJson, UpdateMethodDTO.class);
            updateMethodDTO.setMethodName(methodName);

            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(updateMethodDTO.getMethodName());
            arguments.add(objectMapper.writeValueAsString(updateMethodDTO.getSubject()));
            arguments.add(objectMapper.writeValueAsString(updateMethodDTO.getObjects()));
            arguments.add(updateMethodDTO.getScName());
            arguments.add(updateMethodDTO.getAbi());

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(userName)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(secretKey)
                    .chaincodeName("rc")
                    .func("methodUpdate")
                    .arguments(arguments)
                    .build();

            fabricNetwork.invoke(chainCodeArgsDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/lookUpTable/{methodName}")
    public ResponseEntity<String> deleteLookUpTable(@PathVariable("methodName") String methodName,
                                                    @RequestHeader(value = "userName") String userName,
                                                    @RequestHeader(value = "secretKey") String secretKey,
                                                    @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                                    @RequestHeader(value = "orgMspId") String orgMspId) {

        try {
            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(methodName);

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(userName)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(secretKey)
                    .chaincodeName("rc")
                    .func("methodDelete")
                    .arguments(arguments)
                    .build();

            fabricNetwork.invoke(chainCodeArgsDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/lookUpTable")
    public ResponseEntity<String> getContract(@RequestParam("methodName") String methodName,
                                              @RequestHeader(value = "userName") String userName,
                                              @RequestHeader(value = "secretKey") String secretKey,
                                              @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                              @RequestHeader(value = "orgMspId") String orgMspId) {

        try {
            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(methodName);

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(userName)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(secretKey)
                    .chaincodeName("rc")
                    .func("getContract")
                    .arguments(arguments)
                    .build();

            String result = fabricNetwork.query(chainCodeArgsDTO, false);

            if (result != null)
                return new ResponseEntity<>(result, HttpStatus.OK);
            else
                return ResponseEntity.unprocessableEntity().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping(path = "/lookUpTables")
    public ResponseEntity<String> getContractList(@RequestHeader(value = "userName") String userName,
                                                  @RequestHeader(value = "secretKey") String secretKey,
                                                  @RequestHeader(value = "orgAffiliation") String orgAffiliation,
                                                  @RequestHeader(value = "orgMspId") String orgMspId) {

        try {
            ArrayList<String> arguments = new ArrayList<>();

            ChainCodeArgsDTO chainCodeArgsDTO = ChainCodeArgsDTO.builder()
                    .userName(userName)
                    .orgAffiliation(orgAffiliation)
                    .orgMspId(orgMspId)
                    .secretKey(secretKey)
                    .chaincodeName("rc")
                    .func("getMethodNameList")
                    .arguments(arguments)
                    .build();

            String result = fabricNetwork.query(chainCodeArgsDTO, true);

            if (result != null)
                return new ResponseEntity<>(result, HttpStatus.OK);
            else
                return ResponseEntity.unprocessableEntity().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
