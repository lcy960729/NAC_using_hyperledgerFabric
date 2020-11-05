package main.java.com.example.nac.fabric.client;

import main.java.com.example.nac.fabric.config.Config;
import main.java.com.example.nac.fabric.util.Util;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FabricClient {

	private HFClient instance;

	public HFClient getInstance() {
		return instance;
	}

	public FabricClient(User context) throws CryptoException, InvalidArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		// setup the client
		instance = HFClient.createNewInstance();
		instance.setCryptoSuite(cryptoSuite);
		instance.setUserContext(context);
	}

	public ChannelClient createChannelClient(String name) throws InvalidArgumentException {
		Channel channel = instance.newChannel(name);
		ChannelClient client = new ChannelClient(name, channel, this);
		return client;
	}

	public Collection<ProposalResponse> deployChainCode(String chainCodeName, String chaincodePath, String codepath,
			String language, String version, Collection<Peer> peers)
			throws InvalidArgumentException, ProposalException, IOException {
		InstallProposalRequest request = instance.newInstallProposalRequest();
		ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(chainCodeName).setVersion(version)
				.setPath(chaincodePath);
		ChaincodeID chaincodeID = chaincodeIDBuilder.build();
		Logger.getLogger(FabricClient.class.getName()).log(Level.INFO,
				"Deploying chaincode " + chainCodeName + " using Fabric client " + instance.getUserContext().getMspId()
						+ " " + instance.getUserContext().getName());
		request.setChaincodeID(chaincodeID);
		request.setUserContext(instance.getUserContext());
		request.setChaincodeLanguage(TransactionRequest.Type.JAVA);
		request.setChaincodeVersion(version);
		request.setChaincodeInputStream(Util.generateTarGzInputStream(
				(Paths.get(codepath, chaincodePath).toFile()),
				"src"));
		request.setChaincodePath(null);

		Collection<ProposalResponse> responses = instance.sendInstallProposal(request, peers);
		return responses;
	}

}
