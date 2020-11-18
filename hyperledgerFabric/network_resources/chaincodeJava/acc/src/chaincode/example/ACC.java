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
import java.util.UUID;

public class ACC extends ChaincodeBase {

    private static final Log _logger = LogFactory.getLog(ACC.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
                case "getPolicy":
                    return getPolicy(stub, params);
                case "policyAdd":
                    return policyAdd(stub, params);
                case "policyUpdate":
                    return policyUpdate(stub, params);
                case "policyDelete":
                    return policyDelete(stub, params);
                case "accessControl":
                    return accessControl(stub, params);
                default:
                    return newErrorResponse("Invalid invoke function name");
            }
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    private Response policyAdd(ChaincodeStub stub, List<String> args) {
        try {
            final String id = args.get(0);
            final String subjectId = args.get(1);
            final Identity object = objectMapper.readValue(args.get(2), Identity.class);
            final String resource = args.get(3);
            final String action = args.get(4);
            final Boolean permission = Boolean.parseBoolean(args.get(5));
            final int threshold = Integer.parseInt(args.get(6));
            final Long minInterval = Long.parseLong(args.get(7));

            PolicyTable policyTable = PolicyTable.builder()
                    .subjectId(subjectId)
                    .object(object)
                    .resource(resource)
                    .action(action)
                    .permission(permission)
                    .threshold(threshold)
                    .minInterval(minInterval)
                    .misbehaviorTables(new ArrayList<>())
                    .toLR(LocalDateTime.MIN.toString())
                    .noFR(0)
                    .timeOfUnblock(LocalDateTime.MIN.toString())
                    .build();

            String policyTableBytes = objectMapper.writeValueAsString(policyTable);

            stub.putStringState(id, policyTableBytes);

            return newSuccessResponse(id);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return newErrorResponse("invoke finished successfully");
        }
    }

    private Response getPolicy(ChaincodeStub stub, List<String> args) {
        try {
            final String id = args.get(0);
            String policyTableString = stub.getStringState(id);

            return newSuccessResponse(policyTableString);
        } catch (Exception e) {
            return newErrorResponse();
        }
    }

    private Response policyUpdate(ChaincodeStub stub, List<String> args) {
        try {
            final String id = args.get(0);
            final String objectName = args.get(1);
            final String resource = args.get(2);
            final String action = args.get(3);
            final Boolean permission = Boolean.parseBoolean(args.get(4));
            final int threshold = Integer.parseInt(args.get(5));
            final Long minInterval = Long.parseLong(args.get(6));

            String policyTableJson = stub.getStringState(id);
            PolicyTable policyTable = objectMapper.readValue(policyTableJson, PolicyTable.class);
            policyTable.getObject().setName(objectName);
            policyTable.setResource(resource);
            policyTable.setAction(action);
            policyTable.setPermission(permission);
            policyTable.setThreshold(threshold);
            policyTable.setMinInterval(minInterval);

            String policyTableBytes = objectMapper.writeValueAsString(policyTable);

            stub.putStringState(id, policyTableBytes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return newErrorResponse("invoke finished successfully");
        }
        return newSuccessResponse("invoke finished successfully");
    }

    // query callback representing the query of a chaincode
    private Response policyDelete(ChaincodeStub stub, List<String> args) {
        try {
            final String id = args.get(0);
            stub.delState(id);
        } catch (Exception e) {
            return newErrorResponse();
        }
        return newSuccessResponse();
    }

    private Response accessControl(ChaincodeStub stub, List<String> args) {
        boolean behaviorCheck = true;
        boolean policyCheck = false;

        try {
            final String key = args.get(0);
            final LocalDateTime requestTime = LocalDateTime.parse(args.get(1));

            String policyTableJson = stub.getStringState(key);

            if (policyTableJson == null){
                return newErrorResponse("Not Found User");
            }

            PolicyTable policyTable = objectMapper.readValue(policyTableJson, PolicyTable.class);

            LocalDateTime timeOfUnblock = LocalDateTime.parse(policyTable.getTimeOfUnblock());
            LocalDateTime toLR = LocalDateTime.parse(policyTable.getToLR());

            if (timeOfUnblock.isBefore(requestTime)) {
                if (!timeOfUnblock.isEqual(LocalDateTime.MIN)) {
                    policyTable.setNoFR(0);
                    policyTable.setToLR(LocalDateTime.MIN.toString());
                    policyTable.setTimeOfUnblock(LocalDateTime.MIN.toString());
                }

                policyCheck = policyTable.getPermission();

                long untilRequestTimeToToLR = requestTime.until(toLR, ChronoUnit.MINUTES);

                if (untilRequestTimeToToLR <= policyTable.getMinInterval() && untilRequestTimeToToLR >= 0) {
                    policyTable.setNoFR(policyTable.getNoFR() + 1);

                    if (policyTable.getNoFR() >= policyTable.getThreshold()) {
                        behaviorCheck = false;

                        String msb = "FrequentAccess";
                        String msbLen = String.valueOf(policyTable.getMisbehaviorTables().size() + 1);

                        List<String> jcArgs = new ArrayList<>();
                        jcArgs.add("misbehaviorJudge");
                        jcArgs.add(msb);
                        jcArgs.add(msbLen);

                        Response jsResponse = stub.invokeChaincodeWithStringArgs("jc", jcArgs, "usernetwork");
                        Long penalty = Long.parseLong(jsResponse.getMessage());

                        policyTable.setTimeOfUnblock(requestTime.plusMinutes(penalty).toString());

                        MisbehaviorTable misbehaviorTable = new MisbehaviorTable();
                        misbehaviorTable.setPenalty(penalty);
                        misbehaviorTable.setReason(msb);
                        misbehaviorTable.setTime(requestTime.toString());

                        policyTable.getMisbehaviorTables().add(misbehaviorTable);
                    }
                } else {
                    policyTable.setNoFR(0);
                }
                policyTable.setToLR(requestTime.toString());
            }

            String policyTableBytes = objectMapper.writeValueAsString(policyTable);
            stub.putStringState(key, policyTableBytes);

            _logger.info("TEST : " + policyTableBytes);

            if (policyCheck && behaviorCheck){
                return newSuccessResponse("true");
            }else{
                return newSuccessResponse("false");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return newErrorResponse("Not Found User");
        }
    }

    public static void main(String[] args) {
        new ACC().start(args);
    }
}
