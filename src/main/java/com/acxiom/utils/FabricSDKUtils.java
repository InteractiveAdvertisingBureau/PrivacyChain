package com.acxiom.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import com.acxiom.bean.SampleOrg;
import com.acxiom.bean.SampleStore;
import com.acxiom.bean.SampleUser;

public class FabricSDKUtils {

	public static String CHAIN_CODE_FILEPATH = "sdkintegration/gocc/acxiom";
	public static String CHAIN_CODE_NAME = "mycc";
	public static String CHAIN_CODE_PATH = "github.com/acxiom_cc";
	public static String CHAIN_CODE_VERSION = "1.0";
	public static Type CHAIN_CODE_LANG = Type.GO_LANG;

	public static String PROPERTIES_PATH = "/tmp/HFCSampletest.properties";
	public static String NETWORK_CONFIG_FILE = "docker/network_configs/network-config-tls.yaml";
	public static String ORG_NAME = "peerOrg1";
	public static String MSPID = "Org1MSP";

	public static String CHANNEL_NAME = "foo";

	public static String PRIVATEKEYFILE_DIRECTORY = "docker/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/";
	public static String CERTIFICATEFILE_PATH = "docker/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem";

	public static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	public static final String EXPECTED_EVENT_NAME = "event";
	public static final String FUNCTION_QUERY = "get";
	public static final String FUNCTION_SET = "set";
	public static final String FUNCTION_MATCH = "match";
	public static final String FUNCTION_QUERY_HISTORY = "history";
	public static final String FUNCTION_QUERY_BY_RANGE = "range";

	public static final String EVENTHUB_CONNECTION_WAIT_TIME = "org.hyperledger.fabric.sdk.eventhub_connection.wait_time";

	static {
		System.setProperty(EVENTHUB_CONNECTION_WAIT_TIME, "15000");

	}

	public String query(String consentId) throws Exception {
		return query_common(new String[] { consentId }, FUNCTION_QUERY);
	}

	public String queryHistory(String consentId) throws Exception {
		return query_common(new String[] { consentId }, FUNCTION_QUERY_HISTORY);
	}

	public String getStateByRange(String startKey, String endKey) throws Exception {
		return query_common(new String[] { startKey, endKey }, FUNCTION_QUERY_BY_RANGE);
	}

	private String query_common(String[] key, String func) throws Exception {
		System.out.println("-------- query --------");

		HFClient client = HFClient.createNewInstance();
		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

		SampleOrg sampleOrg = initSampleOrg();

		Channel channel = constructChannel(client, CHANNEL_NAME, sampleOrg);

		String result = queryChainCode(client, channel, key, func);

		channel.shutdown(true);

		return result;
	}

	public void invoke(String key, String value) throws Exception {
		invoke(new String[] { key, value }, FUNCTION_SET);
	}

	public void invoke(String[] key, String func) throws Exception {
		System.out.println("-------- start --------");

		HFClient client = HFClient.createNewInstance();
		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

		// ======= init SampleOrg =======
		SampleOrg sampleOrg = initSampleOrg();

		// ======= construct Channel =======
		Channel channel = constructChannel(client, CHANNEL_NAME, sampleOrg);

		// ======= invoke ChainCode =======
		invokeChainCode(client, channel, key, func);

		channel.shutdown(true);
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

	private String queryChainCode(HFClient client, Channel channel, String[] key, String func) throws Exception {
		ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME)
				.setVersion(CHAIN_CODE_VERSION).setPath(CHAIN_CODE_PATH);
		ChaincodeID chaincodeID = chaincodeIDBuilder.build();

		QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
		queryByChaincodeRequest.setArgs(key);
		queryByChaincodeRequest.setFcn(func);
		queryByChaincodeRequest.setChaincodeID(chaincodeID);

		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
		tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));

		queryByChaincodeRequest.setTransientMap(tm2);

		Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest,
				channel.getPeers());

		for (ProposalResponse proposalResponse : queryProposals) {
			String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
			if (StringUtils.isNotBlank(payload)) {
				return payload;
			}
		}
		return null;
	}

	private void invokeChainCode(HFClient client, Channel channel, String[] key, String func) throws Exception {

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
		transactionProposalRequest.setFcn(func);
		transactionProposalRequest.setProposalWaitTime(120000);
		transactionProposalRequest.setArgs(key);

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
			} else {
				failed.add(response);
			}
		}

		// Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils
		// .getProposalConsistencySets(transactionPropResp);
		// if (proposalConsistencySets.size() != 1) {
		// fail(format("Expected only one set of consistent proposal responses but got
		// %d",
		// proposalConsistencySets.size()));
		// }
		//
		// if (failed.size() > 0) {
		// ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
		//// fail("Not enough endorsers for invoke(move a,b,100):" + failed.size() + "
		// endorser error: "
		//// + firstTransactionProposalResponse.getMessage() + ". Was verified: "
		//// + firstTransactionProposalResponse.isVerified());
		// }

		// ProposalResponse resp = successful.iterator().next();
		// byte[] x = resp.getChaincodeActionResponsePayload(); // This is the data
		// returned by the chaincode.
		// String resultAsString = null;
		// if (x != null) {
		// resultAsString = new String(x, "UTF-8");
		// }

		// TxReadWriteSetInfo readWriteSetInfo =
		// resp.getChaincodeActionResponseReadWriteSetInfo();
		// See blockwalker below how to transverse this

		// ChaincodeID cid = resp.getChaincodeID();

		////////////////////////////
		// Send Transaction Transaction to orderer
		channel.sendTransaction(successful).get(120000, TimeUnit.SECONDS);

	}

}
