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
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import com.acxiom.bean.SampleOrg;
import com.acxiom.bean.SampleStore;
import com.acxiom.bean.SampleUser;
import com.acxiom.utils.FabricSDKUtils;

public class JasonChainInvoke {

	@Test
	public void invoke() throws Exception {
		System.out.println("-------- start --------");

		HFClient client = HFClient.createNewInstance();
		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

		// ======= init SampleOrg =======
		SampleOrg sampleOrg = initSampleOrg();

		// ======= construct Channel =======
		Channel channel = constructChannel(client, FabricSDKUtils.CHANNEL_NAME, sampleOrg);

		// ======= run ChainCode =======
		runChainCode(client, channel);

		System.out.println("-------- end --------");
	}

	private SampleOrg initSampleOrg() throws Exception {
		SampleOrg sampleOrg = new SampleOrg(FabricSDKUtils.ORG_NAME, FabricSDKUtils.MSPID);

		File sampleStoreFile = new File(FabricSDKUtils.PROPERTIES_PATH);
		if (sampleStoreFile.exists()) {
			sampleStoreFile.delete();
		}
		SampleStore sampleStore = new SampleStore(sampleStoreFile);

		SampleUser peerOrgAdmin = sampleStore.getMember(FabricSDKUtils.ORG_NAME + "Admin", FabricSDKUtils.ORG_NAME,
				FabricSDKUtils.MSPID, findFileSk(Paths.get(FabricSDKUtils.PRIVATEKEYFILE_DIRECTORY).toFile()),
				Paths.get(FabricSDKUtils.CERTIFICATEFILE_PATH).toFile());
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
		File f = new File(FabricSDKUtils.NETWORK_CONFIG_FILE);
		NetworkConfig config = NetworkConfig.fromYamlFile(f);

		client.setUserContext(sampleOrg.getPeerAdmin());
		Channel newChannel = client.loadChannelFromConfig(FabricSDKUtils.CHANNEL_NAME, config);

		byte[] serializedChannelBytes = newChannel.serializeChannel();
		newChannel.shutdown(true);
		return client.deSerializeChannel(serializedChannelBytes).initialize();
	}

	private void runChainCode(HFClient client, Channel channel) throws Exception {

		// Collection<ProposalResponse> responses;
		Collection<ProposalResponse> successful = new LinkedList<>();
		Collection<ProposalResponse> failed = new LinkedList<>();

		ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(FabricSDKUtils.CHAIN_CODE_NAME)
				.setVersion(FabricSDKUtils.CHAIN_CODE_VERSION);
		chaincodeIDBuilder.setPath(FabricSDKUtils.CHAIN_CODE_PATH);
		ChaincodeID chaincodeID = chaincodeIDBuilder.build();

		successful.clear();
		failed.clear();

		///////////////
		/// Send transaction proposal to all peers
		TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
		transactionProposalRequest.setChaincodeID(chaincodeID);
		transactionProposalRequest.setChaincodeLanguage(FabricSDKUtils.CHAIN_CODE_LANG);
		// transactionProposalRequest.setFcn("invoke");
		transactionProposalRequest.setFcn("set");
		transactionProposalRequest.setProposalWaitTime(120000);
		transactionProposalRequest.setArgs("acxiom", "jason002");

		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); // Just some extra junk in
																							// transient map
		tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
		tm2.put("result", ":)".getBytes(UTF_8)); // This should be returned see chaincode why.
		tm2.put(FabricSDKUtils.EXPECTED_EVENT_NAME, FabricSDKUtils.EXPECTED_EVENT_DATA); // This should trigger an event
																							// see chaincode why.

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

		System.out.println("++++++++ sendTransaction xxx +++++++++");

	}
}
