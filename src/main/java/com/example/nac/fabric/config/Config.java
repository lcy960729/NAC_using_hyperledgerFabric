package main.java.com.example.nac.fabric.config;

import java.io.File;

public class Config {
	
	public static final String ServiceOrg_MSP = "ServiceOrgMSP";

	public static final String ServiceOrg = "serviceOrg";

	public static final String UserOrg_MSP = "UserOrgMSP";

	public static final String UserOrg = "userOrg";

	public static final String ADMIN = "admin";
	public static final String ADMIN_PASSWORD = "adminpw";

	public static final String CHANNEL_CONFIG_PATH = "network_resources/config/channel.tx";

	public static final String ServiceOrg_USR_BASE_PATH = "network_resources/crypto-config" + File.separator + "peerOrganizations" + File.separator
			+ "ServiceOrg.example.com" + File.separator + "users" + File.separator + "Admin@ServiceOrg.example.com"
			+ File.separator + "msp";

	public static final String UserOrg_USR_BASE_PATH = "network_resources/crypto-config" + File.separator + "peerOrganizations" + File.separator
			+ "UserOrg.example.com" + File.separator + "users" + File.separator + "Admin@UserOrg.example.com"
			+ File.separator + "msp";

	public static final String ServiceOrg_USR_ADMIN_PK = ServiceOrg_USR_BASE_PATH + File.separator + "keystore";
	public static final String ServiceOrg_USR_ADMIN_CERT = ServiceOrg_USR_BASE_PATH + File.separator + "admincerts";

	public static final String UserOrg_USR_ADMIN_PK = UserOrg_USR_BASE_PATH + File.separator + "keystore";
	public static final String UserOrg_USR_ADMIN_CERT = UserOrg_USR_BASE_PATH + File.separator + "admincerts";

	public static final String CA_ServiceOrg_URL = "http://localhost:7054";

	public static final String CA_UserOrg_URL = "http://localhost:8054";

	public static final String ORDERER_URL = "grpc://localhost:7050";

	public static final String ORDERER_NAME = "orderer.example.com";

	public static final String CHANNEL_NAME = "usernetwork";

	public static final String ServiceOrg_PEER_0 = "peer0.ServiceOrg.example.com";

	public static final String ServiceOrg_PEER_0_URL = "grpc://localhost:7051";

	public static final String ServiceOrg_PEER_1 = "peer1.ServiceOrg.example.com";

	public static final String ServiceOrg_PEER_1_URL = "grpc://localhost:7056";
	
    public static final String UserOrg_PEER_0 = "peer0.UserOrg.example.com";
	
	public static final String UserOrg_PEER_0_URL = "grpc://localhost:8051";
	
	public static final String UserOrg_PEER_1 = "peer1.UserOrg.example.com";
	
	public static final String UserOrg_PEER_1_URL = "grpc://localhost:8056";
	
	public static final String CHAINCODE_ROOT_DIR = "network_resources/chaincodeJava";

	public static final String ServiceOrg_EVENT_HUB = "eventhub01";
	public static final String ServiceOrg_EVENT_HUB_URL = "grpc://localhost:7053";

	public static final String UserOrg_EVENT_HUB = "eventhub02";
	public static final String UserOrg_EVENT_HUB_URL = "grpc://localhost:8053";
	
	public static final String CHAINCODE_ACC_NAME = "acc";
	public static final String CHAINCODE_ACC_PATH = "acc";
	public static final String CHAINCODE_ACC_VERSION = "1";

	public static final String CHAINCODE_RC_NAME = "rc";
	public static final String CHAINCODE_RC_PATH = "rc";
	public static final String CHAINCODE_RC_VERSION = "1";

	public static final String CHAINCODE_JC_NAME = "jc";
	public static final String CHAINCODE_JC_PATH = "jc";
	public static final String CHAINCODE_JC_VERSION = "1";
}
