package chaincode.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ACC  extends ChaincodeBase{

    private static Log _logger = LogFactory.getLog(ACC.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Response init(ChaincodeStub stub) {
        try {
            _logger.info("Init");
            return newSuccessResponse();
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            _logger.info("Invoke java simple chaincode");

            String func = stub.getFunction();
            List<String> params = stub.getParameters();

            switch (func) {
                case "policyAdd":
                    return policyAdd(stub, params);
                case "policyUpdate":
                    return policyUpdate(stub, params);
                case "policyDelete":
                    return policyDelete(stub, params);
                case "accessControl":
                    return accessControl(stub, params);
                case "setJC":
                    return setJc(stub, params);
                default:
                    return newErrorResponse("Invalid invoke function name");
            }
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    private Response policyAdd(ChaincodeStub stub, List<String> args) {
        if (args.size() != 6) {
            return newErrorResponse("Incorrect number of arguments. Expecting 3");
        }

        try {
            final Identity object = objectMapper.readValue(args.get(0), Identity.class);
            final String resource = args.get(1);
            final String action = args.get(2);
            final Boolean permission = Boolean.parseBoolean(args.get(3));
            final int threshold = Integer.parseInt(args.get(4));
            final Long minInterval = Long.parseLong(args.get(5));
            final String toLR = LocalDateTime.MIN.toString();

            PolicyTable policyTable = PolicyTable.builder()
                    .object(object)
                    .resource(resource)
                    .action(action)
                    .permission(permission)
                    .threshold(threshold)
                    .minInterval(minInterval)
                    .toLR(toLR)
                    .build();

            String policyTableBytes = objectMapper.writeValueAsString(policyTable);

            String key = String.valueOf(((object.getName().hashCode() + object.getMacAddress()).hashCode() + resource).hashCode());

            stub.putStringState(key, policyTableBytes);
            return newSuccessResponse("invoke finished successfully");

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return newErrorResponse("invoke finished successfully");
        }
    }

    private Response policyUpdate(ChaincodeStub stub, List<String> args) {
        if (args.size() != 6) {
            return newErrorResponse("Incorrect number of arguments. Expecting 3");
        }

        try {
            final Identity object = objectMapper.readValue(args.get(0), Identity.class);
            final String resource = args.get(1);
            final String action = args.get(2);
            final Boolean permission = Boolean.parseBoolean(args.get(3));
            final int threshold = Integer.parseInt(args.get(4));
            final Long minInterval = Long.parseLong(args.get(5));

            String key = String.valueOf(((object.getName().hashCode() + object.getMacAddress()).hashCode() + resource).hashCode());

            String policyTableJson = stub.getStringState(key);
            PolicyTable policyTable = objectMapper.readValue(policyTableJson, PolicyTable.class);

            policyTable.setResource(resource);
            policyTable.setAction(action);
            policyTable.setPermission(permission);
            policyTable.setThreshold(threshold);
            policyTable.setMinInterval(minInterval);

            String policyTableBytes = objectMapper.writeValueAsString(policyTable);

            stub.putStringState(key, policyTableBytes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return newErrorResponse("invoke finished successfully");
        }
        return newSuccessResponse("invoke finished successfully");
    }

    // query callback representing the query of a chaincode
    private Response policyDelete(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Incorrect number of arguments. Expecting name of the person to query");
        }

        try {
            final Identity object = objectMapper.readValue(args.get(0), Identity.class);
            final String resource = args.get(1);

            String key = String.valueOf(((object.getName().hashCode() + object.getMacAddress()).hashCode() + resource).hashCode());

            stub.delState(key);
        } catch (Exception e) {
            return newErrorResponse();
        }
        return newSuccessResponse();
    }

    private Response accessControl(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3) {
            return newErrorResponse("Incorrect number of arguments. Expecting name of the person to query");
        }

        try {
            final Identity object = objectMapper.readValue(args.get(0), Identity.class);
            final String resource = args.get(1);
            final String action = args.get(2);

            final LocalDateTime requestTime = LocalDateTime.now();

            boolean behaviorCheck = true;
            boolean policyCheck = false;

            String key = String.valueOf(((object.getName().hashCode() + object.getMacAddress()).hashCode() + resource).hashCode());

            String policyTableJson = stub.getStringState(key);
            PolicyTable policyTable = objectMapper.readValue(policyTableJson, PolicyTable.class);

            LocalDateTime timeOfUnblock = LocalDateTime.parse(policyTable.getTimeOfUnblock());
            LocalDateTime toLR = LocalDateTime.parse(policyTable.getToLR());

            if (timeOfUnblock.isBefore(requestTime)){
                if (!timeOfUnblock.isEqual(LocalDateTime.MIN)){
                    policyTable.setNoFR(0);
                    policyTable.setToLR(LocalDateTime.MIN.toString());
                    policyTable.setTimeOfUnblock(LocalDateTime.MIN.toString());
                }

                policyCheck = policyTable.getPermission();

                if (requestTime.until(toLR, ChronoUnit.MINUTES) <= policyTable.getMinInterval()){
                    policyTable.setNoFR(policyTable.getNoFR()+1);

                    if (policyTable.getNoFR() >= policyTable.getThreshold()){
                        behaviorCheck = false;

                        String msb = "FrequentAccess";
                        Long penalty = misbehaviorJudge(stub, msb, String.valueOf(policyTable.getMisbehaviorTables().size()+1));
                        policyTable.setTimeOfUnblock(requestTime.plusMinutes(penalty).toString());

                        MisbehaviorTable misbehaviorTable = new MisbehaviorTable();
                        misbehaviorTable.setPenalty(penalty);
                        misbehaviorTable.setReason(msb);
                        misbehaviorTable.setTime(requestTime);

                        policyTable.getMisbehaviorTables().add(misbehaviorTable);
                    }
                }else {
                    policyTable.setNoFR(0);
                }
                policyTable.setToLR(requestTime.toString());
            }

            String policyTableBytes = objectMapper.writeValueAsString(policyTable);
            stub.putStringState(key, policyTableBytes);

            if (policyCheck && behaviorCheck){
                return newSuccessResponse("true");
            }else{
                return newSuccessResponse("false");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return newErrorResponse();
        }
    }

    private Long misbehaviorJudge(ChaincodeStub stub, String msb, String msbLen)  {
        List<byte[]> args = new ArrayList<>();
        args.add(msb.getBytes());
        args.add(msbLen.getBytes());

        Response jsResponse = stub.invokeChaincode("JC", args, "usernetwork");
        String penalty = jsResponse.getMessage();

        return Long.parseLong(penalty);
    }

    private Response setJc(ChaincodeStub stub, List<String> args) {
        return newSuccessResponse();
    }

    public static void main(String[] args) {
        new ACC().start(args);
    }
}
