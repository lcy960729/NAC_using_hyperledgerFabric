package main.java.com.example.nac.Fabric;

import main.java.com.example.nac.DTO.ChainCodeArgsDTO;
import main.java.com.example.nac.Exception.NotExistChainCode;
import main.java.com.example.nac.Exception.NotExistChainCodeFunc;
import main.java.com.example.nac.Fabric.DTO.ResultQueryAndInvokeDTO;
import main.java.com.example.nac.Fabric.Client.CAClient;
import main.java.com.example.nac.Fabric.Client.ChannelClient;
import main.java.com.example.nac.Fabric.Client.FabricClient;
import main.java.com.example.nac.Fabric.Config.Config;
import main.java.com.example.nac.Fabric.User.UserContext;
import main.java.com.example.nac.Fabric.Util.Util;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
public class FabricNetwork {
    private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
    private static final String EXPECTED_EVENT_NAME = "event";

    private FabricClient fabClient = new FabricClient();

    private FabricOrg serviceOrg = new FabricOrg(Config.ServiceOrg, Config.ServiceOrg_MSP);
    private FabricOrg userOrg = new FabricOrg(Config.UserOrg, Config.UserOrg_MSP);

    public FabricOrg getServiceOrg() {
        return serviceOrg;
    }

    public FabricOrg getUserOrg() {
        return userOrg;
    }

    private UserContext serviceOrgAdminLocalMSP;
    private UserContext userOrgAdminLocalMSP;

    private List<Peer> serviceOrgPeerList = new ArrayList<>();
    private List<Peer> userOrgPeerList = new ArrayList<>();

    private ChannelClient channelClient = null;

    boolean isCreatedChannel = false;

    public boolean isCreatedChannel() {
        return isCreatedChannel;
    }

    public FabricNetwork() {
        try {
            Util.cleanUp();

            serviceOrgAdminLocalMSP = enrollAdminLocalMSP(serviceOrg);
            userOrgAdminLocalMSP = enrollAdminLocalMSP(userOrg);

            //Set ServiceOrg
            serviceOrg.addPeerLocation(Config.ServiceOrg_PEER_0, Config.ServiceOrg_PEER_0_URL);
            //serviceOrg.addPeerLocation(Config.ServiceOrg_PEER_1, Config.ServiceOrg_PEER_1_URL);
            serviceOrg.addEventHubLocation(Config.EVENT_HUB, Config.EVENT_HUB_URL);

            serviceOrg.setCaClient(new CAClient(Config.CA_ServiceOrg_URL, null));

            enrollAdmin(serviceOrg);

            //Set UserOrg
            userOrg.addPeerLocation(Config.UserOrg_PEER_0, Config.UserOrg_PEER_0_URL);
            //userOrg.addPeerLocation(Config.UserOrg_PEER_1, Config.UserOrg_PEER_1_URL);
            userOrg.addEventHubLocation(Config.UserOrg_EVENT_HUB, Config.UserOrg_EVENT_HUB_URL);

            userOrg.setCaClient(new CAClient(Config.CA_UserOrg_URL, null));

            enrollAdmin(userOrg);

            // Channel Setting
            fabClient.setUserContext(userOrg.getAdmin());


            Peer userOrgPeer = fabClient.getInstance().newPeer(Config.UserOrg_PEER_0, Config.UserOrg_PEER_0_URL);

            for (String queryChannel : fabClient.getInstance().queryChannels(userOrgPeer)) {
                if (queryChannel.equals("usernetwork")) {
                    isCreatedChannel = true;
                    break;
                }
            }

            if (isCreatedChannel) {
                Channel mychannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME);

                Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
                mychannel.addOrderer(orderer);

                EventHub eventHub = fabClient.getInstance().newEventHub(Config.EVENT_HUB, Config.EVENT_HUB_URL);
                mychannel.addEventHub(eventHub);
                for (String peerName : serviceOrg.getPeerNames()) {
                    String peerUrl = serviceOrg.getPeerLocation(peerName);
                    Peer peer = fabClient.getInstance().newPeer(peerName, peerUrl);
                    mychannel.addPeer(peer);
                    serviceOrgPeerList.add(peer);
                }

                fabClient.setUserContext(userOrg.getAdmin());
                for (String peerName : userOrg.getPeerNames()) {
                    String peerUrl = userOrg.getPeerLocation(peerName);
                    Peer peer = fabClient.getInstance().newPeer(peerName, peerUrl);
                    mychannel.addPeer(peer);
                    userOrgPeerList.add(peer);
                }
                mychannel.initialize();

                channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserContext enrollAdminLocalMSP(FabricOrg fabricOrg) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException {
        UserContext userContext = new UserContext();

        String pkPath = fabricOrg.getName().equals(Config.ServiceOrg) ? Config.ServiceOrg_USR_ADMIN_PK : Config.UserOrg_USR_ADMIN_PK;
        String certPath = fabricOrg.getName().equals(Config.ServiceOrg) ? Config.ServiceOrg_USR_ADMIN_CERT : Config.UserOrg_USR_ADMIN_CERT;

        File pkFolder1 = new File(pkPath);
        File[] pkFiles1 = pkFolder1.listFiles();
        File certFolder1 = new File(certPath);
        File[] certFiles1 = certFolder1.listFiles();
        Enrollment enrollServiceOrgAdmin = Util.getEnrollment(pkPath, pkFiles1[0].getName(),
                certPath, certFiles1[0].getName());
        userContext.setEnrollment(enrollServiceOrgAdmin);
        userContext.setName(Config.ADMIN);
        userContext.setMspId(fabricOrg.getMSPID());
        userContext.setAffiliation(fabricOrg.getName());

        return userContext;
    }

    public void enrollAdmin(FabricOrg fabricOrg) {
        try {
            Util.cleanUp();

            UserContext adminUserContext = new UserContext();
            adminUserContext.setName("admin");
            adminUserContext.setAffiliation(fabricOrg.getName());
            adminUserContext.setMspId(fabricOrg.getMSPID());

            CAClient caClient = fabricOrg.getCaClient();

            caClient.setAdminUserContext(adminUserContext);
            adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

            fabricOrg.setAdmin(adminUserContext);

            boolean isExist = false;
            Collection<HFCAAffiliation> hfcaAffiliation = caClient.getInstance().getHFCAAffiliations(adminUserContext).getChildren();
            for (HFCAAffiliation affiliation : hfcaAffiliation) {
                if (affiliation.getName().equals(fabricOrg.getName())) {
                    isExist = true;
                    break;
                }
            }

            if (!isExist)
                caClient.getInstance().newHFCAAffiliation(fabricOrg.getName()).create(adminUserContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String registerEnrollUser(FabricOrg fabricOrg, String userName) {
        String eSecret = null;

        try {
            Util.cleanUp();
            CAClient caClient = fabricOrg.getCaClient();

            // Register and Enroll user to Org1MSP
            UserContext userContext = new UserContext();
            userContext.setName(userName);
            userContext.setAffiliation(fabricOrg.getName());
            userContext.setMspId(fabricOrg.getMSPID());

            eSecret = caClient.registerUser(userName, fabricOrg.getName());
            userContext = caClient.enrollUser(userContext, eSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eSecret;
    }

    public void createChannel() {
        try {
            fabClient.setUserContext(serviceOrgAdminLocalMSP);

            // Create a new channel
            Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
            ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(Config.CHANNEL_CONFIG_PATH));

            byte[] channelConfigurationSignatures = fabClient.getInstance()
                    .getChannelConfigurationSignature(channelConfiguration, serviceOrgAdminLocalMSP);

            Channel mychannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME, orderer, channelConfiguration,
                    channelConfigurationSignatures);

            EventHub eventHub = fabClient.getInstance().newEventHub(Config.EVENT_HUB, Config.EVENT_HUB_URL);
            mychannel.addEventHub(eventHub);

            //Add ServiceOrg peer on myChannel
            for (String peerName : serviceOrg.getPeerNames()) {
                String peerUrl = serviceOrg.getPeerLocation(peerName);
                Peer peer = fabClient.getInstance().newPeer(peerName, peerUrl);
                mychannel.joinPeer(peer);

                serviceOrgPeerList.add(peer);
            }

            fabClient.getInstance().setUserContext(userOrgAdminLocalMSP);
            mychannel = fabClient.getInstance().getChannel(Config.CHANNEL_NAME);

            //Add UserOrg peer on myChannel
            for (String peerName : userOrg.getPeerNames()) {
                String peerUrl = userOrg.getPeerLocation(peerName);
                Peer peer = fabClient.getInstance().newPeer(peerName, peerUrl);
                mychannel.joinPeer(peer);
                userOrgPeerList.add(peer);
            }

            mychannel.initialize();

            channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);

            Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO, "Channel created " + mychannel.getName());
            Collection peers = mychannel.getPeers();
            for (Object peer : peers) {
                Peer pr = (Peer) peer;
                Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO, pr.getName() + " at " + pr.getUrl());
            }

            isCreatedChannel = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<String> deployInstantiateChaincode(String chainCodeName, String chainCodePath, String chainCodeVersion) {
        try {
            fabClient.setUserContext(serviceOrgAdminLocalMSP);

            Collection<ProposalResponse> response = fabClient.deployChainCode(chainCodeName,
                    chainCodePath, Config.CHAINCODE_ROOT_DIR, TransactionRequest.Type.JAVA.toString(),
                    chainCodeVersion, serviceOrgPeerList);

            for (ProposalResponse res : response) {
                Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO,
                        chainCodeName + "- Chain code deployment " + res.getStatus());
            }

            fabClient.setUserContext(userOrgAdminLocalMSP);

            response = fabClient.deployChainCode(chainCodeName,
                    chainCodePath, Config.CHAINCODE_ROOT_DIR, TransactionRequest.Type.JAVA.toString(),
                    chainCodeVersion, userOrgPeerList);

            for (ProposalResponse res : response) {
                Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO,
                        chainCodeName + "- Chain code deployment " + res.getStatus());
            }

            String[] arguments = {""};
            response = channelClient.instantiateChainCode(chainCodeName, chainCodeVersion,
                    chainCodePath, TransactionRequest.Type.JAVA.toString(), "Init", arguments, null);

            for (ProposalResponse res : response) {
                Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO,
                        chainCodeName + "- Chain code instantiation " + res.getStatus());
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } finally {
            channelClient.getChannel().getEventHubs().forEach(EventHub::shutdown);
        }
    }

    public ResultQueryAndInvokeDTO invoke(ChainCodeArgsDTO chainCodeArgsDTO, boolean isAdmin) throws Exception {
        Util.cleanUp();

        FabricOrg currentOrg;
        if (serviceOrg.getMSPID().equals(chainCodeArgsDTO.getOrgMspId())) {
            currentOrg = serviceOrg;
        } else {
            currentOrg = userOrg;
        }

        CAClient caClient = currentOrg.getCaClient();

        if (!isAdmin) {
            UserContext userContext = new UserContext();
            userContext.setName(chainCodeArgsDTO.getUserName());
            userContext.setAffiliation(chainCodeArgsDTO.getOrgAffiliation());
            userContext.setMspId(chainCodeArgsDTO.getOrgMspId());
            userContext = caClient.enrollUser(userContext, chainCodeArgsDTO.getSecretKey());

            fabClient.setUserContext(userContext);
        } else {
            fabClient.setUserContext(currentOrg.getAdmin());
        }

        if (chainCodeArgsDTO.getChaincodeName() == null || chainCodeArgsDTO.getChaincodeName().length() == 0) {
            throw new NotExistChainCode();
        } else if (chainCodeArgsDTO.getFunc() == null || chainCodeArgsDTO.getFunc().length() == 0) {
            throw new NotExistChainCodeFunc();
        }

        TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chainCodeArgsDTO.getChaincodeName()).build();
        request.setChaincodeID(chaincodeID);
        request.setFcn(chainCodeArgsDTO.getFunc());
        request.setArgs(chainCodeArgsDTO.getArguments());
        request.setProposalWaitTime(10000);

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8));
        tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA);
        request.setTransientMap(tm2);

        Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);

        ResultQueryAndInvokeDTO result = new ResultQueryAndInvokeDTO();

        for (ProposalResponse res : responses) {
            ChaincodeResponse.Status status = res.getStatus();
            result.setStatus(status.getStatus());
            result.setMessage(res.getMessage());
            Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO, "Invoked " + chainCodeArgsDTO.getFunc() + " on " + chainCodeArgsDTO.getChaincodeName() + ". Status - " + status);
        }

        return result;
    }

    public ResultQueryAndInvokeDTO query(ChainCodeArgsDTO chainCodeArgsDTO, boolean isAdmin) throws Exception {
        Util.cleanUp();

        FabricOrg currentOrg;
        if (serviceOrg.getMSPID().equals(chainCodeArgsDTO.getOrgMspId())) {
            currentOrg = serviceOrg;
        } else {
            currentOrg = userOrg;
        }

        CAClient caClient = currentOrg.getCaClient();

        if (!isAdmin) {
            UserContext userContext = new UserContext();
            userContext.setName(chainCodeArgsDTO.getUserName());
            userContext.setAffiliation(chainCodeArgsDTO.getOrgAffiliation());
            userContext.setMspId(chainCodeArgsDTO.getOrgMspId());
            userContext = caClient.enrollUser(userContext, chainCodeArgsDTO.getSecretKey());

            fabClient.setUserContext(userContext);
        } else {
            fabClient.setUserContext(currentOrg.getAdmin());
        }

        if (chainCodeArgsDTO.getChaincodeName() == null || chainCodeArgsDTO.getChaincodeName().length() == 0) {
            throw new NotExistChainCode();
        } else if (chainCodeArgsDTO.getFunc() == null || chainCodeArgsDTO.getFunc().length() == 0) {
            throw new NotExistChainCodeFunc();
        }

        Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO, "Querying ...");

        String[] arguments = new String[chainCodeArgsDTO.getArguments().size()];
        for (int i = 0; i < chainCodeArgsDTO.getArguments().size(); i++) {
            arguments[i] = chainCodeArgsDTO.getArguments().get(i);
        }

        ResultQueryAndInvokeDTO result = new ResultQueryAndInvokeDTO();

        Collection<ProposalResponse> responsesQuery = channelClient.queryByChainCode(chainCodeArgsDTO.getChaincodeName(), chainCodeArgsDTO.getFunc(), arguments);
        for (ProposalResponse pres : responsesQuery) {
            ChaincodeResponse.Status status = pres.getStatus();
            result.setStatus(status.getStatus());
            result.setMessage(pres.getMessage());
            Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO, "queried " + chainCodeArgsDTO.getFunc() + " on " + chainCodeArgsDTO.getChaincodeName() + ". status - " + status + ". response - " + pres.getMessage());
        }

        return result;
    }
}
