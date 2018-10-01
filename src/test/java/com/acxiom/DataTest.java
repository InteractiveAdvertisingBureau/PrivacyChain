package com.acxiom;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.acxiom.bean.CollectData;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.DataResponse;
import com.acxiom.service.DataService;
import com.acxiom.utils.Utils;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataTest {
	@Autowired
	private DataService dataService;

	// @Test
	public void collectData() throws Exception {
		CollectData data = new CollectData();
		List<String> optin = new ArrayList<String>();
		optin.add("asd1");
		optin.add("asd2");
		optin.add("asd3");
		data.setOptin(optin);

		System.out.println(dataService.collectData(data).getConsentId());

		assertTrue("000".equals(dataService.collectData(data).getReturnCode()));
		assertNotNull(dataService.collectData(data).getConsentId());
	}

	//  @Test
	public void getDataByConsentId() throws Exception {
		String consentId = "55f3f6df82784cbdb44e49a902e8383d";

		DataResponse data = dataService.getDataByConsentId(consentId);
		CollectData c = data.getData();
		for (String x : c.getOptin()) {
			System.out.println(x);
		}

		System.out.println(Utils.objectToJson(data));
		assertTrue("000".equals(data.getReturnCode()));
		// assertNotNull(dataService.collectData(data).getConsentId());
	}

 	//@Test
	public void updateConsent() throws Exception {
		String consentId = "55f3f6df82784cbdb44e49a902e8383d";

		CollectData data = new CollectData();
		data.setConsentId(consentId);
		List<String> optin = new ArrayList<String>();
		optin.add("asd1");
		optin.add("asd2");
		optin.add("asd3");
		data.setOptin(optin);

		BaseResponseBean result = dataService.updateConsent(data);

		System.out.println(result.getReturnCode());
		System.out.println(result.getReturnMessage());
		assertTrue("000".equals(result.getReturnCode()));
		// assertNotNull(dataService.collectData(data).getConsentId());
	}

}
