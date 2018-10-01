package com.acxiom;

import static org.junit.Assert.assertNotNull;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.acxiom.bean.UserRequest;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.service.UserService;
import com.acxiom.utils.PropertiesUtils;

import net.sf.json.JSONObject;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest {
	PropertiesUtils p = new PropertiesUtils("application.properties");

	String ca_url = p.getProperty("CA_PEER_URL");
	String admin_name = p.getProperty("CA_ADMIN_NAME");
	String admin_password = p.getProperty("CA_ADMIN_PASSWORD");

	@Autowired
	private UserService userService;

	// @Test
	public void register() throws Exception {
		UserRequest user = new UserRequest();
		user.setName("jason001");
		user.setSecret("123456");
		user.setAffiliation("org1.department1");

		JSONObject json = JSONObject.fromObject(user);
		System.out.println(json);
		BaseResponseBean x = userService.register(user);

		System.out.println(x.getReturnCode());
		System.out.println(x.getReturnMessage());

		assertNotNull(x.getReturnCode());
		assertNotNull(x.getReturnMessage());
	}

	@Test
	public void login() throws Exception {
		HFCAClient ca = HFCAClient.createNewInstance(ca_url, null);
		ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

		Enrollment x = login(admin_name, admin_password, ca);

		assertNotNull(x.getCert());
		assertNotNull(x.getKey());
	}

	private Enrollment login(String name, String password, HFCAClient ca) throws Exception {
		return ca.enroll(name, password);
	}

	// @Test
	public void list() {
		System.out.println(userService.getUsers().getUsers());

		assertNotNull(userService.getUsers().getReturnCode());
		assertNotNull(userService.getUsers().getReturnMessage());
	}

}
