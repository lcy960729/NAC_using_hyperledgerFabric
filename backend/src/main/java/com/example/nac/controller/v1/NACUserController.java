package main.java.com.example.nac.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.com.example.nac.DTO.LoginDTO;
import main.java.com.example.nac.Fabric.FabricNetwork;
import main.java.com.example.nac.Fabric.Config.Config;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/NACUser/")
public class NACUserController {

    final FabricNetwork fabricNetwork;

    public NACUserController(FabricNetwork fabricNetwork) {
        this.fabricNetwork = fabricNetwork;
    }

    @GetMapping(path = "/createChannel")
    public ResponseEntity<String> createChannel() {
        try {
            fabricNetwork.createChannel();
            Thread.sleep(1000);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody String loginData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LoginDTO loginDTO = objectMapper.readValue(loginData, LoginDTO.class);

            if (loginDTO.getId().equals("admin") && loginDTO.getPw().equals("adminpw"))
                return new ResponseEntity("true", HttpStatus.OK);
            else
                return new ResponseEntity("false", HttpStatus.FORBIDDEN);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping(path = "/isCreatedChannel")
    public ResponseEntity<String> isCreatedChannel() {
        if (fabricNetwork.isCreatedChannel())
            return new ResponseEntity("true", HttpStatus.OK);
        else
            return new ResponseEntity("false", HttpStatus.OK);
    }

    @GetMapping(path = "/deployRC")
    public ResponseEntity<String> deployRC() {
        try {
            fabricNetwork.deployInstantiateChaincode(Config.CHAINCODE_RC_NAME, Config.CHAINCODE_RC_PATH, Config.CHAINCODE_RC_VERSION);
            Thread.sleep(1000);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping(path = "/deployACC")
    public ResponseEntity<String> deployAcc() {
        try {
            fabricNetwork.deployInstantiateChaincode(Config.CHAINCODE_ACC_NAME, Config.CHAINCODE_ACC_PATH, Config.CHAINCODE_ACC_VERSION);
            Thread.sleep(1000);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping(path = "/deployJC")
    public ResponseEntity<String> deployJc() {
        try {
            fabricNetwork.deployInstantiateChaincode(Config.CHAINCODE_JC_NAME, Config.CHAINCODE_JC_PATH, Config.CHAINCODE_JC_VERSION);
            Thread.sleep(1000);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
