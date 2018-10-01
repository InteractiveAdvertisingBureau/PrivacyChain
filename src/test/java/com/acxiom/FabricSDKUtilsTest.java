package com.acxiom;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.acxiom.bean.CollectData;
import com.acxiom.bean.HistoryData;
import com.acxiom.response.DataHistoryResponse;
import com.acxiom.service.DataService;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FabricSDKUtilsTest {

	@Autowired
	private DataService dataService;

	//@Test
	public void test() throws Exception {
		String consentId = "b9e43ec270424beeb8738b4b795e9647";

		System.out.println(dataService);
		DataHistoryResponse x = dataService.getDataHistoryByConsentId(consentId);
		List<HistoryData> l = x.getData();

		for (HistoryData h : l) {
			CollectData c = h.getCollectData();
			System.out.println(c.getOptin());
		}

	}

}
