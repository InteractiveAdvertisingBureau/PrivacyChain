package com.acxiom;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import com.acxiom.bean.SampleOrg;
import com.acxiom.bean.SampleStore;
import com.acxiom.bean.SampleUser;
import com.acxiom.utils.Utils;

public class HistoryTest {

	String CHAIN_CODE_FILEPATH = "sdkintegration/gocc/sample002";
	String CHAIN_CODE_NAME = "example_cc_go002";
	String CHAIN_CODE_PATH = "github.com/example_cc";
	String CHAIN_CODE_VERSION = "1";
	Type CHAIN_CODE_LANG = Type.GO_LANG;

	private String PROPERTIES_PATH = "/tmp/HFCSampletest.properties";
	private String NETWORK_CONFIG_FILE = "src/test/fixture/sdkintegration/network_configs/network-config.yaml";
	private String ORG_NAME = "peerOrg1";
	private String MSPID = "Org1MSP";

	private String CHANNEL_NAME = "foo";

	private String PRIVATEKEYFILE_DIRECTORY = "src/test/fixture/sdkintegration/e2e-2Orgs/v1.1/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/";
	private String CERTIFICATEFILE_PATH = "src/test/fixture/sdkintegration/e2e-2Orgs/v1.1/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem";

	public static void main(String[] args) throws Exception {
		HistoryTest t = new HistoryTest();
		t.query("jason");
	}

	public void query(String consentId) throws Exception {
		System.out.println("-------- start --------");

		HFClient client = HFClient.createNewInstance();
		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

		// ======= init SampleOrg =======
		SampleOrg sampleOrg = initSampleOrg();

		// ======= construct Channel =======
		Channel channel = constructChannel(client, CHANNEL_NAME, sampleOrg);

		// ======= run ChainCode =======
		queryChainCode(client, channel, consentId);
	}

	private SampleOrg initSampleOrg() throws Exception {
		SampleOrg sampleOrg = new SampleOrg(ORG_NAME, MSPID);

		File sampleStoreFile = new File(PROPERTIES_PATH);
		if (sampleStoreFile.exists()) {
			sampleStoreFile.delete();
		}
		SampleStore sampleStore = new SampleStore(sampleStoreFile);

		SampleUser peerOrgAdmin = sampleStore.getMember(ORG_NAME + "Admin", ORG_NAME, MSPID,
				Utils.findFileSk(Paths.get(PRIVATEKEYFILE_DIRECTORY).toFile()),
				Paths.get(CERTIFICATEFILE_PATH).toFile());
		sampleOrg.setPeerAdmin(peerOrgAdmin);

		return sampleOrg;
	}

	private Channel constructChannel(HFClient client, String channelName, SampleOrg sampleOrg) throws Exception {
		File f = new File(NETWORK_CONFIG_FILE);
		NetworkConfig config = NetworkConfig.fromYamlFile(f);

		client.setUserContext(sampleOrg.getPeerAdmin());
		Channel newChannel = client.loadChannelFromConfig(channelName, config);

		byte[] serializedChannelBytes = newChannel.serializeChannel();
		newChannel.shutdown(true);
		return client.deSerializeChannel(serializedChannelBytes).initialize();
	}

	private void queryChainCode(HFClient client, Channel channel, String key) throws Exception {
		ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME)
				.setVersion(CHAIN_CODE_VERSION).setPath(CHAIN_CODE_PATH);
		ChaincodeID chaincodeID = chaincodeIDBuilder.build();

		QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
		queryByChaincodeRequest.setArgs(new String[] { key });
		queryByChaincodeRequest.setFcn("query");
		queryByChaincodeRequest.setChaincodeID(chaincodeID);

		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
		tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));

		queryByChaincodeRequest.setTransientMap(tm2);

		Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest,
				channel.getPeers());

		for (ProposalResponse proposalResponse : queryProposals) {
			if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
				fail("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: "
						+ proposalResponse.getStatus() + ". Messages: " + proposalResponse.getMessage()
						+ ". Was verified : " + proposalResponse.isVerified());
			} else {
				String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
				System.out.println(proposalResponse.getTransactionID());
				System.out.println("Query payload of b from peer " + proposalResponse.getPeer().getName() + " returned "
						+ payload);
			}
		}

	}

}
