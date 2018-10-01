package com.acxiom.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.acxiom.bean.DataRequest;
import com.acxiom.bean.SampleStore;
import com.acxiom.bean.SampleUser;
import com.acxiom.bean.UserRequest;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.DataResponse;
import com.acxiom.response.ResponseStatus;
import com.acxiom.response.UserListResponse;
import com.acxiom.response.UserResponse;
import com.acxiom.utils.PropertiesUtils;

@Service
public class UserServiceImpl implements UserService {
	private PropertiesUtils p = new PropertiesUtils("application.properties");
	private String CA_URL = p.getProperty("CA_PEER_URL");
	private String CA_ADMIN = p.getProperty("CA_ADMIN_NAME");
	private String CA_ADMIN_PASSWORD = p.getProperty("CA_ADMIN_PASSWORD");
	File sampleStoreFile = new File(p.getProperty("HFCSAMPLE_PROPERTIES"));

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public BaseResponseBean register(UserRequest user) {
		BaseResponseBean result = new BaseResponseBean();

		try {
			HFCAClient ca = HFCAClient.createNewInstance(CA_URL, null);
			ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

			SampleStore sampleStore = new SampleStore(sampleStoreFile);
			// login with admin
			SampleUser admin = new SampleUser(CA_ADMIN, null, sampleStore);
			admin.setEnrollment(ca.enroll(CA_ADMIN, CA_ADMIN_PASSWORD));

			// register user
			RegistrationRequest rr = new RegistrationRequest(user.getName(), user.getAffiliation());
			rr.setSecret(user.getSecret());
			ca.register(rr, admin);

			// record the info to db
			String sql = "insert into user(name,affiliation,create_dt,create_user) VALUES(?,?,SYSDATE(),?)";
			jdbcTemplate.update(sql, new Object[] { user.getName(), user.getAffiliation(), CA_ADMIN });

			result.setStatus(ResponseStatus.SUCCESS);
		} catch (Exception e) {
			result.setStatus(ResponseStatus.REGISTER_FAILED);
		}

		return result;
	}

	public UserResponse login(UserRequest user) {
		UserResponse result = new UserResponse();

		try {
			HFCAClient ca = HFCAClient.createNewInstance(CA_URL, null);
			ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

			Enrollment e = ca.enroll(user.getName(), user.getSecret());
			result.setCertificate(e.getCert());
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (Exception e) {
			result.setStatus(ResponseStatus.LOGIN_FAILED);
		}

		return result;
	}

	public UserListResponse getUsers() {
		UserListResponse result = new UserListResponse();

		String sql = "select id,name,affiliation from user";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		if (list.size() > 0) {
			result.setUsers(list);
			result.setStatus(ResponseStatus.SUCCESS);
		} else {
			result.setStatus(ResponseStatus.USER_LIST_FAILED);
		}
		return result;
	}

	@Deprecated
	public BaseResponseBean saveData(DataRequest data) {
		BaseResponseBean result = new BaseResponseBean();
		// if (data == null || StringUtils.isEmpty(data.getKey())) {
		// result.setStatus(ResponseStatus.MISS_THE_KEY);
		// return result;
		// }
		//
		// // 1. data to Json
		// JsonConfig jsonConfig = new JsonConfig();
		// PropertyFilter filter = new PropertyFilter() {
		// public boolean apply(Object object, String fieldName, Object fieldValue) {
		// return null == fieldValue;
		// }
		// };
		// jsonConfig.setJsonPropertyFilter(filter);
		//
		// JSONObject json = JSONObject.fromObject(data, jsonConfig);
		//
		// // 2. Json encode
		// String encode_data = "";
		// try {
		// encode_data = URLEncoder.encode(json.toString(), "utf-8");
		// } catch (UnsupportedEncodingException e) {
		// result.setStatus(ResponseStatus.ERROR);
		// return result;
		// }
		//
		// // 3. save data
		// // TODO: need replace by chaincode
		// String sql = "insert into temp(id,value) VALUES(?,?)";
		// jdbcTemplate.update(sql, new Object[] { data.getKey(), encode_data });
		//
		// result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	@Deprecated
	public DataResponse getDataByKey(String key) {
		DataResponse result = new DataResponse();
		// if (StringUtils.isEmpty(key)) {
		// result.setStatus(ResponseStatus.MISS_THE_KEY);
		// return result;
		// }
		//
		// // 1. get data
		// // TODO: need replace by chaincode
		// String sql = "select value from temp where id = ?";
		// String data_s = "";
		// try {
		// data_s = jdbcTemplate.queryForObject(sql, String.class, key);
		// } catch (Exception e) {
		// result.setStatus(ResponseStatus.DO_NOT_FIND_DATA);
		// return result;
		// }
		//
		// // 2. Json decode
		// String decode_data = "";
		// try {
		// decode_data = URLDecoder.decode(data_s, "utf-8");
		// } catch (UnsupportedEncodingException e) {
		// result.setStatus(ResponseStatus.ERROR);
		// return result;
		// }
		//
		// // 3. Json to Data
		// JSONObject jsonObject = JSONObject.fromObject(decode_data);
		// DataRequest data = (DataRequest) JSONObject.toBean(jsonObject,
		// DataRequest.class);
		// result.setData(data);
		// result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}
}
