package main.java.com.example.nac.fabric;

import lombok.SneakyThrows;
import main.java.com.example.nac.DTO.ChainCodeArgsDTO;
import main.java.com.example.nac.Exception.NotExistChainCode;
import main.java.com.example.nac.Exception.NotExistChainCodeFunc;
import main.java.com.example.nac.fabric.client.CAClient;
import main.java.com.example.nac.fabric.client.ChannelClient;
import main.java.com.example.nac.fabric.client.FabricClient;
import main.java.com.example.nac.fabric.config.Config;
import main.java.com.example.nac.fabric.user.UserContext;
import main.java.com.example.nac.fabric.util.Util;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FabricNetwork {
    private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
    private static final String EXPECTED_EVENT_NAME = "event";

    FabricClient fabClient = null;

    SampleOrg serviceOrg = new SampleOrg(Config.ServiceOrg, Config.ServiceOrg_MSP);
    SampleOrg userOrg = new SampleOrg(Config.UserOrg, Config.UserOrg_MSP);

    public FabricNetwork() {
        try {
            serviceOrg.addPeerLocation(Config.ServiceOrg_PEER_0, Config.ServiceOrg_PEER_0_URL);
            serviceOrg.addPeerLocation(Config.ServiceOrg_PEER_0, Config.ServiceOrg_PEER_0_URL);
            serviceOrg.addEventHubLocation(Config.ServiceOrg_EVENT_HUB, Config.ServiceOrg_EVENT_HUB_URL);
            serviceOrg.addOrdererLocation(Config.ORDERER_NAME, Config.ORDERER_URL);

            serviceOrg.setCaClient(new CAClient(Config.CA_ServiceOrg_URL, null));

            UserContext serviceOrgAdmin = new UserContext();
            serviceOrgAdmin.setName(Config.ADMIN);
            serviceOrgAdmin.setAffiliation(Config.ServiceOrg);
            serviceOrgAdmin.setMspId(Config.ServiceOrg_MSP);
            serviceOrg.getCaClient().setAdminUserContext(serviceOrgAdmin);
            serviceOrgAdmin = serviceOrg.getCaClient().enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);
            userOrg.setAdmin(serviceOrgAdmin);

            userOrg.addPeerLocation(Config.UserOrg_PEER_0, Config.UserOrg_PEER_0_URL);
            userOrg.addPeerLocation(Config.UserOrg_PEER_0, Config.UserOrg_PEER_0_URL);
            userOrg.addEventHubLocation(Config.UserOrg_EVENT_HUB, Config.UserOrg_EVENT_HUB_URL);
            userOrg.addOrdererLocation(Config.ORDERER_NAME, Config.ORDERER_URL);
            userOrg.setCaClient(new CAClient(Config.CA_UserOrg_URL, null));

            UserContext userOrgAdmin = new UserContext();
            userOrgAdmin.setName(Config.ADMIN);
            userOrgAdmin.setAffiliation(Config.UserOrg);
            userOrgAdmin.setMspId(Config.UserOrg_MSP);
            userOrg.getCaClient().setAdminUserContext(userOrgAdmin);
            userOrgAdmin = userOrg.getCaClient().enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);
            userOrg.setAdmin(userOrgAdmin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createChannel() {
        try {
            CryptoSuite.Factory.getCryptoSuite();
            Util.cleanUp();
            // Construct Channel
            UserContext org1Admin = new UserContext();
            File pkFolder1 = new File(Config.ServiceOrg_USR_ADMIN_PK);
            File[] pkFiles1 = pkFolder1.listFiles();
            File certFolder1 = new File(Config.ServiceOrg_USR_ADMIN_CERT);
            File[] certFiles1 = certFolder1.listFiles();
            Enrollment enrollOrg1Admin = Util.getEnrollment(Config.ServiceOrg_USR_ADMIN_PK, pkFiles1[0].getName(),
                    Config.ServiceOrg_USR_ADMIN_CERT, certFiles1[0].getName());
            org1Admin.setEnrollment(enrollOrg1Admin);
            org1Admin.setMspId(Config.ServiceOrg_MSP);
            org1Admin.setName(Config.ADMIN);

            UserContext org2Admin = new UserContext();
            File pkFolder2 = new File(Config.UserOrg_USR_ADMIN_PK);
            File[] pkFiles2 = pkFolder2.listFiles();
            File certFolder2 = new File(Config.UserOrg_USR_ADMIN_CERT);
            File[] certFiles2 = certFolder2.listFiles();
            Enrollment enrollOrg2Admin = Util.getEnrollment(Config.UserOrg_USR_ADMIN_PK, pkFiles2[0].getName(),
                    Config.UserOrg_USR_ADMIN_CERT, certFiles2[0].getName());
            org2Admin.setEnrollment(enrollOrg2Admin);
            org2Admin.setMspId(Config.UserOrg_MSP);
            org2Admin.setName(Config.ADMIN);

            FabricClient fabClient = new FabricClient(org1Admin);

            // Create a new channel
            Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
            ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(Config.CHANNEL_CONFIG_PATH));

            byte[] channelConfigurationSignatures = fabClient.getInstance()
                    .getChannelConfigurationSignature(channelConfiguration, org1Admin);

            Channel mychannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME, orderer, channelConfiguration,
                    channelConfigurationSignatures);

            Peer peer0_org1 = fabClient.getInstance().newPeer(Config.ServiceOrg_PEER_0, Config.ServiceOrg_PEER_0_URL);
            Peer peer1_org1 = fabClient.getInstance().newPeer(Config.ServiceOrg_PEER_1, Config.ServiceOrg_PEER_1_URL);
            Peer peer0_org2 = fabClient.getInstance().newPeer(Config.UserOrg_PEER_0, Config.UserOrg_PEER_0_URL);
            Peer peer1_org2 = fabClient.getInstance().newPeer(Config.UserOrg_PEER_1, Config.UserOrg_PEER_1_URL);

            mychannel.joinPeer(peer0_org1);
            mychannel.joinPeer(peer1_org1);

            mychannel.addOrderer(orderer);

            mychannel.initialize();

            fabClient.getInstance().setUserContext(org2Admin);

            mychannel = fabClient.getInstance().getChannel(Config.CHANNEL_NAME);
            mychannel.joinPeer(peer0_org2);
            mychannel.joinPeer(peer1_org2);

            Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO, "Channel created " + mychannel.getName());
            Collection peers = mychannel.getPeers();
            Iterator peerIter = peers.iterator();
            while (peerIter.hasNext()) {
                Peer pr = (Peer) peerIter.next();
                Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO, pr.getName() + " at " + pr.getUrl());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enrollAdmin(String caUrl, String orgAdminName, String orgAffiliation, String orgMsp) {
        try {
            Util.cleanUp();
            CAClient caClient = new CAClient(caUrl, null);
            // Enroll Admin to Org1MSP
            UserContext adminUserContext = new UserContext();
            adminUserContext.setName(orgAdminName);
            adminUserContext.setAffiliation(orgAffiliation);
            adminUserContext.setMspId(orgMsp);

            caClient.setAdminUserContext(adminUserContext);
            adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

            caClient.getInstance().newHFCAAffiliation(Config.UserOrg).create(adminUserContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String registerEnrollUser(String caUrl, String orgAffiliation, String orgMsp, String userName) {
        String eSecret = null;

        try {
            Util.cleanUp();
            CAClient caClient = new CAClient(caUrl, null);
            // Enroll Admin to Org1MSP
            UserContext adminUserContext = new UserContext();
            adminUserContext.setName("admin");
            adminUserContext.setAffiliation(orgAffiliation);
            adminUserContext.setMspId(orgMsp);
            caClient.setAdminUserContext(adminUserContext);
            adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

            // Register and Enroll user to Org1MSP
            UserContext userContext = new UserContext();
            userContext.setName(userName);
            userContext.setAffiliation(orgAffiliation);
            userContext.setMspId(orgMsp);

            eSecret = caClient.registerUser(userName, orgAffiliation);
            userContext = caClient.enrollUser(userContext, eSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eSecret;
    }

    public void deployInstantiateChaincode(String chainCodeName, String chainCodePath, String chainCodeVersion) {
        try {
            CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();

            UserContext org1Admin = new UserContext();
            File pkFolder1 = new File(Config.ServiceOrg_USR_ADMIN_PK);
            File[] pkFiles1 = pkFolder1.listFiles();
            File certFolder = new File(Config.ServiceOrg_USR_ADMIN_CERT);
            File[] certFiles = certFolder.listFiles();
            Enrollment enrollOrg1Admin = Util.getEnrollment(Config.ServiceOrg_USR_ADMIN_PK, pkFiles1[0].getName(),
                    Config.ServiceOrg_USR_ADMIN_CERT, certFiles[0].getName());
            org1Admin.setEnrollment(enrollOrg1Admin);
            org1Admin.setMspId(Config.ServiceOrg_MSP);
            org1Admin.setName(Config.ADMIN);

            UserContext org2Admin = new UserContext();
            File pkFolder2 = new File(Config.UserOrg_USR_ADMIN_PK);
            File[] pkFiles2 = pkFolder2.listFiles();
            File certFolder2 = new File(Config.UserOrg_USR_ADMIN_CERT);
            File[] certFiles2 = certFolder2.listFiles();
            Enrollment enrollOrg2Admin = Util.getEnrollment(Config.UserOrg_USR_ADMIN_PK, pkFiles2[0].getName(),
                    Config.UserOrg_USR_ADMIN_CERT, certFiles2[0].getName());
            org2Admin.setEnrollment(enrollOrg2Admin);
            org2Admin.setMspId(Config.UserOrg_MSP);
            org2Admin.setName(Config.ADMIN);

            FabricClient fabClient = new FabricClient(org1Admin);

            Channel mychannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME);
            Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
            Peer peer0_org1 = fabClient.getInstance().newPeer(Config.ServiceOrg_PEER_0, Config.ServiceOrg_PEER_0_URL);
            Peer peer1_org1 = fabClient.getInstance().newPeer(Config.ServiceOrg_PEER_1, Config.ServiceOrg_PEER_1_URL);
            Peer peer0_org2 = fabClient.getInstance().newPeer(Config.UserOrg_PEER_0, Config.UserOrg_PEER_0_URL);
            Peer peer1_org2 = fabClient.getInstance().newPeer(Config.UserOrg_PEER_1, Config.UserOrg_PEER_1_URL);
            mychannel.addOrderer(orderer);
            mychannel.addPeer(peer0_org1);
            mychannel.addPeer(peer1_org1);
            mychannel.addPeer(peer0_org2);
            mychannel.addPeer(peer1_org2);
            mychannel.initialize();

            List<Peer> org1Peers = new ArrayList<>();
            org1Peers.add(peer0_org1);
            org1Peers.add(peer1_org1);

            List<Peer> org2Peers = new ArrayList<>();
            org2Peers.add(peer0_org2);
            org2Peers.add(peer1_org2);

            Collection<ProposalResponse> response = fabClient.deployChainCode(chainCodeName,
                    chainCodePath, Config.CHAINCODE_ROOT_DIR, TransactionRequest.Type.JAVA.toString(),
                    chainCodeVersion, org1Peers);

            for (ProposalResponse res : response) {
                Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO,
                        chainCodeName + "- Chain code deployment " + res.getStatus());
            }

            fabClient.getInstance().setUserContext(org2Admin);

            response = fabClient.deployChainCode(chainCodeName,
                    chainCodePath, Config.CHAINCODE_ROOT_DIR, TransactionRequest.Type.JAVA.toString(),
                    chainCodeVersion, org2Peers);

            for (ProposalResponse res : response) {
                Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO,
                        chainCodeName + "- Chain code deployment " + res.getStatus());
            }

            ChannelClient channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);

            String[] arguments = {""};
            response = channelClient.instantiateChainCode(chainCodeName, chainCodeVersion,
                    chainCodePath, TransactionRequest.Type.JAVA.toString(), "Init", arguments, null);

            for (ProposalResponse res : response) {
                Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO,
                        chainCodeName + "- Chain code instantiation " + res.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void invoke(ChainCodeArgsDTO chainCodeArgsDTO) {
        Util.cleanUp();
        String caUrl = Config.CA_UserOrg_URL;
        EventHub eventHub = null;
        try {
            CAClient caClient = new CAClient(caUrl, null);

            UserContext adminUserContext = new UserContext();
            adminUserContext.setName(Config.ADMIN);
            adminUserContext.setAffiliation(chainCodeArgsDTO.getOrgAffiliation());
            adminUserContext.setMspId(chainCodeArgsDTO.getOrgMspId());
            caClient.setAdminUserContext(adminUserContext);
            adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

            UserContext userContext = new UserContext();
            userContext.setName(chainCodeArgsDTO.getUserName());
            userContext.setAffiliation(chainCodeArgsDTO.getOrgAffiliation());
            userContext.setMspId(chainCodeArgsDTO.getOrgMspId());
            userContext = caClient.enrollUser(userContext, chainCodeArgsDTO.getSecretKey());

            FabricClient fabClient = new FabricClient(userContext);
            ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
            Channel channel = channelClient.getChannel();
            Peer peer = fabClient.getInstance().newPeer(Config.UserOrg_PEER_0, Config.UserOrg_PEER_0_URL);
            eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
            Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
            channel.addPeer(peer);
            channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.initialize();

            if (chainCodeArgsDTO.getChaincodeName() == null || chainCodeArgsDTO.getChaincodeName().length() == 0) {
                throw new NotExistChainCode();
            } else if (chainCodeArgsDTO.getFunc() == null || chainCodeArgsDTO.getFunc().length() == 0) {
                throw new NotExistChainCodeFunc();
            }

            //String[] arguments = { "CAR2", "CY", "TIVOLI", "Blue", "CY" };
            TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
            ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chainCodeArgsDTO.getChaincodeName()).build();
            request.setChaincodeID(chaincodeID);
            request.setFcn(chainCodeArgsDTO.getFunc());
            request.setArgs(chainCodeArgsDTO.getArguments());
            request.setProposalWaitTime(1000);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
            tm2.put("result", ":)".getBytes(UTF_8));
            tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA);
            request.setTransientMap(tm2);
            Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
            for (ProposalResponse res : responses) {
                ChaincodeResponse.Status status = res.getStatus();
                Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO, "Invoked createCar on " + chaincodeID + ". Status - " + status);
            }

            eventHub.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            assert eventHub != null;
            eventHub.shutdown();
        }
    }

    public String query(ChainCodeArgsDTO chainCodeArgsDTO, boolean isAdmin) throws InvalidArgumentException, ProposalException {
        Util.cleanUp();
        String caUrl = Config.CA_UserOrg_URL;
        EventHub eventHub = null;

        try {
            CAClient caClient = new CAClient(caUrl, null);

            UserContext adminUserContext = new UserContext();
            adminUserContext.setName(chainCodeArgsDTO.getUserName());
            adminUserContext.setAffiliation(chainCodeArgsDTO.getOrgAffiliation());
            adminUserContext.setMspId(chainCodeArgsDTO.getOrgMspId());
            caClient.setAdminUserContext(adminUserContext);
            adminUserContext = caClient.enrollAdminUser(chainCodeArgsDTO.getUserName(), chainCodeArgsDTO.getSecretKey());

            FabricClient fabClient;

            if (!isAdmin) {
                UserContext userContext = new UserContext();
                userContext.setName(chainCodeArgsDTO.getUserName());
                userContext.setAffiliation(chainCodeArgsDTO.getOrgAffiliation());
                userContext.setMspId(chainCodeArgsDTO.getOrgMspId());
                userContext = caClient.enrollUser(userContext, chainCodeArgsDTO.getSecretKey());

                fabClient = new FabricClient(userContext);
            } else {
                fabClient = new FabricClient(adminUserContext);
            }

            ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
            Channel channel = channelClient.getChannel();
            Peer peer = fabClient.getInstance().newPeer(Config.UserOrg_PEER_0, Config.UserOrg_PEER_0_URL);
            eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
            Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
            channel.addPeer(peer);
            channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.initialize();

            if (chainCodeArgsDTO.getChaincodeName() == null || chainCodeArgsDTO.getChaincodeName().length() == 0) {
                throw new NotExistChainCode();
            } else if (chainCodeArgsDTO.getFunc() == null || chainCodeArgsDTO.getFunc().length() == 0) {
                throw new NotExistChainCodeFunc();
            }

            //String[] arguments = { "CAR2", "CY", "TIVOLI", "Blue", "CY" };
            Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO, "Querying ...");

            String[] arguments = new String[chainCodeArgsDTO.getArguments().size()];
            for (int i = 0; i < chainCodeArgsDTO.getArguments().size(); i++) {
                arguments[i] = chainCodeArgsDTO.getArguments().get(i);
            }

            String result = null;

            Collection<ProposalResponse> responsesQuery = channelClient.queryByChainCode(chainCodeArgsDTO.getChaincodeName(), chainCodeArgsDTO.getFunc(), arguments);
            for (ProposalResponse pres : responsesQuery) {
                String stringResponse = new String(pres.getMessage());
                result = pres.getMessage();
                Logger.getLogger(FabricNetwork.class.getName()).log(Level.INFO, stringResponse);
            }
            eventHub.shutdown();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            assert eventHub != null;
            eventHub.shutdown();
            return null;
        }
    }
}
