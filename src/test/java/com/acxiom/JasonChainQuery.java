package com.acxiom;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

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
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Test;

import com.acxiom.bean.SampleOrg;
import com.acxiom.bean.SampleStore;
import com.acxiom.bean.SampleUser;
import com.acxiom.utils.FabricSDKUtils;

public class JasonChainQuery {

	@Test
	public void query() throws Exception {
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

		ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(FabricSDKUtils.CHAIN_CODE_NAME)
				.setVersion(FabricSDKUtils.CHAIN_CODE_VERSION).setPath(FabricSDKUtils.CHAIN_CODE_PATH);
		ChaincodeID chaincodeID = chaincodeIDBuilder.build();
		// ChaincodeID(example_cc_go:github.com/example_cc:1)

		System.out.println("++++++++ sendTransaction xxx +++++++++");
		QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
		queryByChaincodeRequest.setArgs(new String[] { "acxiom" });
		queryByChaincodeRequest.setFcn("get");
		queryByChaincodeRequest.setChaincodeID(chaincodeID);

		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
		tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));

		queryByChaincodeRequest.setTransientMap(tm2);

		Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest,
				channel.getPeers());

		for (ProposalResponse proposalResponse : queryProposals) {
			String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
			System.out.println(payload);
		}

		System.out.println("++++++++ sendTransaction xxx +++++++++");

	}
}
