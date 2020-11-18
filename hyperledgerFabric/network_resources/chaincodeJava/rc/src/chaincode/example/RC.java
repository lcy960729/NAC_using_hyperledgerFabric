package chaincode.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;

public class RC extends ChaincodeBase {

    private static Log _logger = LogFactory.getLog(RC.class);
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
                case "methodRegister":
                    return methodRegister(stub, params);
                case "methodUpdate":
                    return methodUpdate(stub, params);
                case "methodDelete":
                    return methodDelete(stub, params);
                case "getContract":
                    return getContract(stub, params);
                case "getMethodNameList":
                    return getMethodNameList(stub, params);
                case "getObjList":
                    return getObjList(stub, params);
                default:
                    return newErrorResponse("Invalid invoke function name");
            }
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    private Response methodRegister(ChaincodeStub stub, List<String> args) {
        try {
            final String id = args.get(0);
            final String methodName = args.get(1);
            final Identity subject = objectMapper.readValue(args.get(2), Identity.class);
            final String scName = args.get(3);
            final String abi = args.get(4);

            LookUpTable lookUpTable = LookUpTable.builder()
                    .methodName(methodName)
                    .subject(subject)
                    .objects(new ArrayList<>())
                    .scName(scName)
                    .abi(abi)
                    .build();

            String lookUpTableBytes = objectMapper.writeValueAsString(lookUpTable);
            stub.putStringState(id, lookUpTableBytes);

            return newSuccessResponse(id);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return newErrorResponse("invoke finished successfully");
        }
    }

    private Response getContract(ChaincodeStub stub, List<String> args) {
        final String id = args.get(0);
        String lookUpTableString = stub.getStringState(id);

        if (lookUpTableString != null)
            return newSuccessResponse(lookUpTableString);
        else
            return newErrorResponse("Not Found LookUpTable Entity");
    }

    private Response methodUpdate(ChaincodeStub stub, List<String> args) {
        try {
            final String id = args.get(0);
            final String methodName = args.get(1);
            final String subjectName = args.get(2);
            final ArrayList<ObjectIdentity> objects = objectMapper.readValue(args.get(3), ArrayList.class);
            final String scName = args.get(4);
            final String abi = args.get(5);

            LookUpTable lookUpTable = objectMapper.readValue(stub.getStringState(id), LookUpTable.class);

            lookUpTable.setMethodName(methodName);
            lookUpTable.getSubject().setName(subjectName);
            lookUpTable.setObjects(objects);
            lookUpTable.setScName(scName);
            lookUpTable.setAbi(abi);

            String lookUpTableBytes = objectMapper.writeValueAsString(lookUpTable);

            stub.putStringState(id, lookUpTableBytes);
            return newSuccessResponse("invoke finished successfully");

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return newErrorResponse("invoke finished successfully");
        }
    }

    // query callback representing the query of a chaincode
    private Response methodDelete(ChaincodeStub stub, List<String> args) {
        try {
            final String id = args.get(0);
            stub.delState(id);
        } catch (Exception e) {
            return newErrorResponse();
        }
        return newSuccessResponse();
    }

    private Response getMethodNameList(ChaincodeStub stub, List<String> args) {
        try {
            final String queryString = "{\n" +
                    "   \"selector\": {\n" +
                    "      \"_id\": {\n" +
                    "         \"$regex\": \"\"\n" +
                    "      }\n" +
                    "   },\n" +
                    "   \"fields\": [\n" +
                    "      \"methodName\"\n" +
                    "   ]\n" +
                    "}";

            final Iterator<KeyValue> queryResultsIterator = stub.getQueryResult(queryString).iterator();

            ArrayList<Method> methodNameList = new ArrayList<>();
            while (queryResultsIterator.hasNext()) {
                KeyValue queryResult = queryResultsIterator.next();

                String id = queryResult.getKey();
                JsonObject jsonObject = new JsonParser().parse(queryResult.getStringValue()).getAsJsonObject();

                String methodName = jsonObject.get("methodName").getAsString();

                methodNameList.add(new Method(id, methodName));
            }

            String methodNameListJson = objectMapper.writeValueAsString(methodNameList);

            return newSuccessResponse(methodNameListJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return newErrorResponse();
        }
    }

    private Response getObjList(ChaincodeStub stub, List<String> args) {
        final String macAddress = args.get(0);

        final String queryString = "{\n" +
                "   \"selector\": {\n" +
                "      \"subject\": {\n" +
                "         \"macAddress\": {\n" +
                "            \"$regex\": \"" + macAddress + "\"\n" +
                "         }\n" +
                "      }\n" +
                "   },\n" +
                "   \"fields\": [\n" +
                "      \"objects\"\n" +
                "   ]\n" +
                "}";

        final Iterator<KeyValue> queryResultsIterator = stub.getQueryResult(queryString).iterator();

        if (queryResultsIterator.hasNext()) {
            KeyValue queryResult = queryResultsIterator.next();

            String objList = queryResult.getStringValue();
            return newSuccessResponse(objList);
        }else{
            return newErrorResponse();
        }
    }

    public static void main(String[] args) {
        new RC().start(args);
    }
}
