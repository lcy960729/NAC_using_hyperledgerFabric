package main.java.com.example.nac.fabric.network;

import main.java.com.example.nac.fabric.chaincode.InvokeQueryChaincode;
import main.java.com.example.nac.fabric.client.ChannelClient;
import main.java.com.example.nac.fabric.client.FabricClient;
import main.java.com.example.nac.fabric.config.Config;
import main.java.com.example.nac.fabric.user.UserContext;
import main.java.com.example.nac.fabric.util.Util;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeployInstantiateChaincode {
    private static final DeployInstantiateChaincode deployInstantiateChaincode = new DeployInstantiateChaincode();

    public static DeployInstantiateChaincode getInstance() {
        return deployInstantiateChaincode;
    }

    private FabricClient fabClient = null;
    private List<Peer> org1Peers = null;
    private List<Peer> org2Peers = null;
    private UserContext org1Admin = null;
    private UserContext org2Admin = null;
    private Channel mychannel = null;

    public DeployInstantiateChaincode() {
        try {
            CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();

            org1Admin = new UserContext();
            File pkFolder1 = new File(Config.ORG1_USR_ADMIN_PK);
            File[] pkFiles1 = pkFolder1.listFiles();
            File certFolder = new File(Config.ORG1_USR_ADMIN_CERT);
            File[] certFiles = certFolder.listFiles();
            Enrollment enrollOrg1Admin = Util.getEnrollment(Config.ORG1_USR_ADMIN_PK, pkFiles1[0].getName(),
                    Config.ORG1_USR_ADMIN_CERT, certFiles[0].getName());
            org1Admin.setEnrollment(enrollOrg1Admin);
            org1Admin.setMspId("Org1MSP");
            org1Admin.setName("admin");

            org2Admin = new UserContext();
            File pkFolder2 = new File(Config.ORG2_USR_ADMIN_PK);
            File[] pkFiles2 = pkFolder2.listFiles();
            File certFolder2 = new File(Config.ORG2_USR_ADMIN_CERT);
            File[] certFiles2 = certFolder2.listFiles();
            Enrollment enrollOrg2Admin = Util.getEnrollment(Config.ORG2_USR_ADMIN_PK, pkFiles2[0].getName(),
                    Config.ORG2_USR_ADMIN_CERT, certFiles2[0].getName());
            org2Admin.setEnrollment(enrollOrg2Admin);
            org2Admin.setMspId(Config.ORG2_MSP);
            org2Admin.setName(Config.ADMIN);

            fabClient = new FabricClient(org1Admin);

            mychannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME);
            Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
            Peer peer0_org1 = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
            Peer peer1_org1 = fabClient.getInstance().newPeer(Config.ORG1_PEER_1, Config.ORG1_PEER_1_URL);
            Peer peer0_org2 = fabClient.getInstance().newPeer(Config.ORG2_PEER_0, Config.ORG2_PEER_0_URL);
            Peer peer1_org2 = fabClient.getInstance().newPeer(Config.ORG2_PEER_1, Config.ORG2_PEER_1_URL);
            mychannel.addOrderer(orderer);
            mychannel.addPeer(peer0_org1);
            mychannel.addPeer(peer1_org1);
            mychannel.addPeer(peer0_org2);
            mychannel.addPeer(peer1_org2);
            mychannel.initialize();

            org1Peers = new ArrayList<>();
            org1Peers.add(peer0_org1);
            org1Peers.add(peer1_org1);

            org2Peers = new ArrayList<>();
            org2Peers.add(peer0_org2);
            org2Peers.add(peer1_org2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//String ccName, String ccPath, String ccVersion
    public void run() throws ProposalException, IOException, InvalidArgumentException, ChaincodeEndorsementPolicyParseException {
        Collection<ProposalResponse> response = fabClient.deployChainCode(Config.CHAINCODE_1_NAME,
                Config.CHAINCODE_1_PATH, Config.CHAINCODE_ROOT_DIR, Type.GO_LANG.toString(),
                Config.CHAINCODE_1_VERSION, org1Peers);

        for (ProposalResponse res : response) {
            Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                    Config.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
        }

        fabClient.getInstance().setUserContext(org2Admin);

        response = fabClient.deployChainCode(Config.CHAINCODE_1_NAME,
                Config.CHAINCODE_1_PATH, Config.CHAINCODE_ROOT_DIR, Type.GO_LANG.toString(),
                Config.CHAINCODE_1_VERSION, org2Peers);

        for (ProposalResponse res : response) {
            Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                    Config.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
        }

        ChannelClient channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);

        String[] arguments = {""};
        response = channelClient.instantiateChainCode(Config.CHAINCODE_1_NAME, Config.CHAINCODE_1_VERSION,
                Config.CHAINCODE_1_PATH, Type.GO_LANG.toString(), "Init", arguments, null);

        for (ProposalResponse res : response) {
            Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                    Config.CHAINCODE_1_NAME + "- Chain code instantiation " + res.getStatus());
        }
    }

    public void runACC() throws ProposalException, IOException, InvalidArgumentException, ChaincodeEndorsementPolicyParseException {
        Collection<ProposalResponse> response = fabClient.deployChainCode(Config.CHAINCODE_ACC_NAME,
                Config.CHAINCODE_ACC_PATH, Config.CHAINCODE_ROOT_DIR, Type.GO_LANG.toString(),
                Config.CHAINCODE_ACC_VERSION, org1Peers);

        for (ProposalResponse res : response) {
            Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                    Config.CHAINCODE_ACC_NAME + "- Chain code deployment " + res.getStatus());
        }

        fabClient.getInstance().setUserContext(org2Admin);

        response = fabClient.deployChainCode(Config.CHAINCODE_ACC_NAME,
                Config.CHAINCODE_ACC_PATH, Config.CHAINCODE_ROOT_DIR, Type.GO_LANG.toString(),
                Config.CHAINCODE_ACC_VERSION, org2Peers);

        for (ProposalResponse res : response) {
            Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                    Config.CHAINCODE_ACC_NAME + "- Chain code deployment " + res.getStatus());
        }

        ChannelClient channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);

        String[] arguments = {""};
        response = channelClient.instantiateChainCode(Config.CHAINCODE_ACC_NAME, Config.CHAINCODE_ACC_VERSION,
                Config.CHAINCODE_ACC_PATH, Type.GO_LANG.toString(), "Init", arguments, null);

        for (ProposalResponse res : response) {
            Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                    Config.CHAINCODE_ACC_NAME + "- Chain code instantiation " + res.getStatus());
        }
    }
    
    public void runRC() throws ProposalException, IOException, InvalidArgumentException, ChaincodeEndorsementPolicyParseException {
        Collection<ProposalResponse> response = fabClient.deployChainCode(Config.CHAINCODE_RC_NAME,
                Config.CHAINCODE_RC_PATH, Config.CHAINCODE_ROOT_DIR, Type.GO_LANG.toString(),
                Config.CHAINCODE_RC_VERSION, org1Peers);

        for (ProposalResponse res : response) {
            Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                    Config.CHAINCODE_RC_NAME + " - Chain code deployment " + res.getStatus());
        }

        fabClient.getInstance().setUserContext(org2Admin);

        response = fabClient.deployChainCode(Config.CHAINCODE_RC_NAME,
                Config.CHAINCODE_RC_PATH, Config.CHAINCODE_ROOT_DIR, Type.GO_LANG.toString(),
                Config.CHAINCODE_RC_VERSION, org2Peers);

        for (ProposalResponse res : response) {
            Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                    Config.CHAINCODE_RC_NAME + " - Chain code deployment " + res.getStatus());
        }

        ChannelClient channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);

        String[] arguments = {""};
        response = channelClient.instantiateChainCode(Config.CHAINCODE_RC_NAME, Config.CHAINCODE_RC_VERSION,
                Config.CHAINCODE_RC_PATH, Type.GO_LANG.toString(), "init", arguments, null);

        for (ProposalResponse res : response) {
            Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                    Config.CHAINCODE_RC_NAME + " - Chain code instantiation " + res.getStatus());
        }
    }

    public void runJC() throws ProposalException, IOException, InvalidArgumentException, ChaincodeEndorsementPolicyParseException {
        Collection<ProposalResponse> response = fabClient.deployChainCode(Config.CHAINCODE_JC_NAME,
                Config.CHAINCODE_JC_PATH, Config.CHAINCODE_ROOT_DIR, Type.GO_LANG.toString(),
                Config.CHAINCODE_JC_VERSION, org1Peers);

        for (ProposalResponse res : response) {
            Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                    Config.CHAINCODE_JC_NAME + "- Chain code deployment " + res.getStatus());
        }

        fabClient.getInstance().setUserContext(org2Admin);

        response = fabClient.deployChainCode(Config.CHAINCODE_JC_NAME,
                Config.CHAINCODE_JC_PATH, Config.CHAINCODE_ROOT_DIR, Type.GO_LANG.toString(),
                Config.CHAINCODE_JC_VERSION, org2Peers);

        for (ProposalResponse res : response) {
            Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                    Config.CHAINCODE_JC_NAME + "- Chain code deployment " + res.getStatus());
        }

        ChannelClient channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);

        String[] arguments = {""};
        response = channelClient.instantiateChainCode(Config.CHAINCODE_JC_NAME, Config.CHAINCODE_JC_VERSION,
                Config.CHAINCODE_JC_PATH, Type.GO_LANG.toString(), "init", arguments, null);

        for (ProposalResponse res : response) {
            Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                    Config.CHAINCODE_JC_NAME + "- Chain code instantiation " + res.getStatus());
        }
    }
}
