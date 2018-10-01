package com.acxiom;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import com.acxiom.bean.SampleOrg;
import com.acxiom.bean.SampleStore;
import com.acxiom.bean.SampleUser;

public class JasonChainInvoke_history {
	String CHAIN_CODE_FILEPATH = "sdkintegration/gocc/acxiom";
	String CHAIN_CODE_NAME = "acxiom_cc_go001";
	String CHAIN_CODE_PATH = "github.com/example_cc";
	String CHAIN_CODE_VERSION = "1";
	Type CHAIN_CODE_LANG = Type.GO_LANG;

	private String PROPERTIES_PATH = "/tmp/HFCSampletest.properties";
	private String NETWORK_CONFIG_FILE = "src/test/fixture/sdkintegration/network_configs/network-config.yaml";
	private String ORG_NAME = "peerOrg1";
	private String MSPID = "Org1MSP";

	private String FOO_CHANNEL_NAME = "foo";

	// private String CHAIN_CODE_NAME = "example_cc_go";
	// private String CHAIN_CODE_PATH = "github.com/example_cc";
	// private String CHAIN_CODE_VERSION = "1";
	// private Type CHAIN_CODE_LANG = Type.GO_LANG;

	private String PRIVATEKEYFILE_DIRECTORY = "src/test/fixture/sdkintegration/e2e-2Orgs/v1.1/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/";
	private String CERTIFICATEFILE_PATH = "src/test/fixture/sdkintegration/e2e-2Orgs/v1.1/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem";

	private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	private static final String EXPECTED_EVENT_NAME = "event";

	//@Test
	public void invoke() throws Exception {
		System.out.println("-------- start --------");

		HFClient client = HFClient.createNewInstance();
		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

		// ======= init SampleOrg =======
		SampleOrg sampleOrg = initSampleOrg();

		// ======= construct Channel =======
		Channel channel = constructChannel(client, FOO_CHANNEL_NAME, sampleOrg);

		// ======= run ChainCode =======
		runChainCode(client, channel);

		System.out.println("-------- end --------");
	}

	private SampleOrg initSampleOrg() throws Exception {
		SampleOrg sampleOrg = new SampleOrg(ORG_NAME, MSPID);

		File sampleStoreFile = new File(PROPERTIES_PATH);
		if (sampleStoreFile.exists()) {
			sampleStoreFile.delete();
		}
		SampleStore sampleStore = new SampleStore(sampleStoreFile);

		SampleUser peerOrgAdmin = sampleStore.getMember(ORG_NAME + "Admin", ORG_NAME, MSPID,
				findFileSk(Paths.get(PRIVATEKEYFILE_DIRECTORY).toFile()), Paths.get(CERTIFICATEFILE_PATH).toFile());
		sampleOrg.setPeerAdmin(peerOrgAdmin);

		return sampleOrg;
	}

	public static File findFileSk(File directory) {
		System.out.println(directory);
		File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));
		if (null == matches) {
			throw new RuntimeException(
					format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
		}
		if (matches.length != 1) {
			throw new RuntimeException(format("Expected in %s only 1 sk file but found %d",
					directory.getAbsoluteFile().getName(), matches.length));
		}
		return matches[0];
	}

	private Channel constructChannel(HFClient client, String fOO_CHANNEL_NAME2, SampleOrg sampleOrg) throws Exception {
		File f = new File(NETWORK_CONFIG_FILE);
		NetworkConfig config = NetworkConfig.fromYamlFile(f);

		client.setUserContext(sampleOrg.getPeerAdmin());
		Channel newChannel = client.loadChannelFromConfig(FOO_CHANNEL_NAME, config);

		byte[] serializedChannelBytes = newChannel.serializeChannel();
		newChannel.shutdown(true);
		return client.deSerializeChannel(serializedChannelBytes).initialize();
	}

	private void runChainCode(HFClient client, Channel channel) throws Exception {

		// Collection<ProposalResponse> responses;
		Collection<ProposalResponse> successful = new LinkedList<>();
		Collection<ProposalResponse> failed = new LinkedList<>();

		ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME)
				.setVersion(CHAIN_CODE_VERSION);
		chaincodeIDBuilder.setPath(CHAIN_CODE_PATH);
		ChaincodeID chaincodeID = chaincodeIDBuilder.build();

		successful.clear();
		failed.clear();

		///////////////
		/// Send transaction proposal to all peers
		TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
		transactionProposalRequest.setChaincodeID(chaincodeID);
		transactionProposalRequest.setChaincodeLanguage(CHAIN_CODE_LANG);
		// transactionProposalRequest.setFcn("invoke");
		transactionProposalRequest.setFcn("sdsds");
		transactionProposalRequest.setProposalWaitTime(120000);
		transactionProposalRequest.setArgs("acxiom");

		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); // Just some extra junk in
																							// transient map
		tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
		tm2.put("result", ":)".getBytes(UTF_8)); // This should be returned see chaincode why.
		tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA); // This should trigger an event see chaincode why.

		transactionProposalRequest.setTransientMap(tm2);

		Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest,
				channel.getPeers());
		for (ProposalResponse response : transactionPropResp) {
			if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
				successful.add(response);
				
				System.out.println("----------------------");
//				System.out.println(response.getProposal().getPayload().toStringUtf8());
				System.out.println("----------------------");
				String payload =  response.getProposalResponse().getResponse().getPayload().toStringUtf8();
				System.out.println(payload);

			} else {
				failed.add(response);
			}
		}

		////////////////////////////
		// Send Transaction Transaction to orderer
		channel.sendTransaction(successful).get(120000, TimeUnit.SECONDS);

		System.out.println("++++++++ sendTransaction xxx +++++++++");

	}
}
