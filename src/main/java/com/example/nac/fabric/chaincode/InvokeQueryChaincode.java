package main.java.com.example.nac.fabric.chaincode;

import main.java.com.example.nac.Exception.NotExistChainCode;
import main.java.com.example.nac.Exception.NotExistChainCodeFunc;
import main.java.com.example.nac.fabric.client.CAClient;
import main.java.com.example.nac.fabric.client.ChannelClient;
import main.java.com.example.nac.fabric.client.FabricClient;
import main.java.com.example.nac.fabric.config.Config;
import main.java.com.example.nac.fabric.user.UserContext;
import main.java.com.example.nac.fabric.util.Util;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class InvokeQueryChaincode {
    private static final InvokeQueryChaincode invokeQueryChaincode = new InvokeQueryChaincode();

    public static InvokeQueryChaincode getInstance() {
        return invokeQueryChaincode;
    }

    private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
    private static final String EXPECTED_EVENT_NAME = "event";

    private ChaincodeID chaincodeID = null;
    private String operator = null;
    private String[] arguments = null;
    private FabricClient fabClient = null;
    private ChannelClient channelClient = null;

    public InvokeQueryChaincode() {
        Util.cleanUp();
        String caUrl = Config.CA_ORG1_URL;
        CAClient caClient = null;
        try {
            caClient = new CAClient(caUrl, null);


            // Enroll Admin to Org1MSP
            UserContext adminUserContext = new UserContext();
            adminUserContext.setName(Config.ADMIN);
            adminUserContext.setAffiliation(Config.ORG1);
            adminUserContext.setMspId(Config.ORG1_MSP);
            caClient.setAdminUserContext(adminUserContext);
            adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

            FabricClient fabClient = new FabricClient(adminUserContext);
            ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
            Channel channel = channelClient.getChannel();
            Peer peer = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
            EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
            Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
            channel.addPeer(peer);
            channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void invoke() throws InvalidArgumentException, ProposalException {
        if (chaincodeID == null) {
            throw new NotExistChainCode();
        } else if (operator == null) {
            throw new NotExistChainCodeFunc();
        }

        //String[] arguments = { "CAR2", "CY", "TIVOLI", "Blue", "CY" };
        TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
        request.setChaincodeID(chaincodeID);
        request.setFcn(operator);
        request.setArgs(arguments);
        request.setProposalWaitTime(1000);

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8));
        tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA);
        request.setTransientMap(tm2);
        Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
        for (ProposalResponse res : responses) {
            Status status = res.getStatus();
            Logger.getLogger(InvokeQueryChaincode.class.getName()).log(Level.INFO, "Invoked createCar on " + chaincodeID + ". Status - " + status);
        }
    }

    public void query() throws InvalidArgumentException, ProposalException, InterruptedException {
        if (chaincodeID == null) {
            throw new NotExistChainCode();
        } else if (operator == null) {
            throw new NotExistChainCodeFunc();
        }

        Logger.getLogger(InvokeQueryChaincode.class.getName()).log(Level.INFO, "Querying for all cars ...");
        Collection<ProposalResponse> responsesQuery = channelClient.queryByChainCode(chaincodeID.getName(), operator, arguments);
        for (ProposalResponse pres : responsesQuery) {
            String stringResponse = new String(pres.getChaincodeActionResponsePayload());
            Logger.getLogger(InvokeQueryChaincode.class.getName()).log(Level.INFO, stringResponse);
        }
    }

    public void setChaincodeName(String chaincodeName) {
        this.chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).build();
    }

    public void setFunc(String operator) {
        this.operator = operator;
    }

    public void setArguments(List<String> argumentlist) {
        arguments = new String[argumentlist.size()];

        for (int i = 0; i < argumentlist.size(); i++) {
            arguments[i] = argumentlist.get(i);
        }
    }

}
