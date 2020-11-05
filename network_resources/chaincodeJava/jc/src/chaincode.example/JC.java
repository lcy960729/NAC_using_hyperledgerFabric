package chaincode.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.pow;

public class JC extends ChaincodeBase {

    private static Log _logger = LogFactory.getLog(JC.class);
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
                case "misbehaviorJudge":
                    return misbehaviorJudge(stub, params);
                default:
                    return newErrorResponse("Invalid invoke function name");
            }
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    private Response misbehaviorJudge(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Incorrect number of arguments. Expecting 3");
        }

        final String msb = args.get(0);
        final double msbLen = Double.parseDouble(args.get(1));

        double base;
        double litervar;
        double squared;
        int penalty;

        switch (msb) {
            case "FrequentAccess":
                base = 2.0;
                litervar = 1.0;
                squared = msbLen / litervar;
                penalty = (int) pow(base, squared);
                break;
            default:
                penalty = 0;
                break;
        }

        return newSuccessResponse(String.valueOf(penalty));
    }

    public static void main(String[] args) {
        new JC().start(args);
    }
}
