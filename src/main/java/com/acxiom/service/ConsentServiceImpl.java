package com.acxiom.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acxiom.bean.Consent;
import com.acxiom.bean.ConsentHistory;
import com.acxiom.bean.ConsentStatus;
import com.acxiom.bean.Consents;
import com.acxiom.bean.Subscription;
import com.acxiom.bean.SubscriptionStatus;
import com.acxiom.bean.Tag;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.CollectDataResponse;
import com.acxiom.response.ConsentHistoryResponse;
import com.acxiom.response.ConsentResponse;
import com.acxiom.response.IdsResponse;
import com.acxiom.response.ResponseStatus;
import com.acxiom.utils.EmailUtils;
import com.acxiom.utils.FabricSDKUtils;
import com.acxiom.utils.KeyNameUtils;
import com.acxiom.utils.PropertiesUtils;
import com.acxiom.utils.Utils;

import net.sf.json.JSONObject;

@Service
public class ConsentServiceImpl implements ConsentService {
	private PropertiesUtils p = new PropertiesUtils("application.properties");
	File sampleStoreFile = new File(p.getProperty("HFCSAMPLE_PROPERTIES"));

	@Autowired
	SubscriptionService subscriptionService;

	public CollectDataResponse createConsent(Consent data) {
		CollectDataResponse result = new CollectDataResponse();
		String consentId = Utils.getUUID();
		if (StringUtils.isEmpty(data.getEntity())) {
			data.setEntity(Utils.getCurrentDate());
		}
		data.setId(consentId);
		String value = Utils.objectToJson(data);

		FabricSDKUtils fabric = new FabricSDKUtils();

		try {
			fabric.invoke(consentId, value);
		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		result.setConsentId(consentId);
		result.setStatus(ResponseStatus.SUCCESS);

		if (StringUtils.isNotEmpty(data.getEntity())) {
			try {
				fabric.invoke(KeyNameUtils.getEntityWithConsentId(data.getEntity(), consentId),
						"\"" + consentId + "\"");
			} catch (Exception e) {
				e.printStackTrace();
				result.setStatus(ResponseStatus.ERROR);
				return result;
			}
		}

		return result;
	}

	public BaseResponseBean updateConsent(Consent data) {
		BaseResponseBean result = new BaseResponseBean();

		ConsentResponse r = getConsentById(data.getId());
		if (!"000".equals(r.getReturnCode())) {
			result.setReturnCode(r.getReturnCode());
			result.setReturnMessage(r.getReturnMessage());
			return result;
		}

		if (StringUtils.isEmpty(data.getExpires())) {
			data.setExpires(r.getConsent().getExpires());
		}

		try {
			FabricSDKUtils fabric = new FabricSDKUtils();
			fabric.invoke(data.getId(), Utils.objectToJson(data));
		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		// send email notification
		List<Subscription> subsList = subscriptionService.getSubscriptionsByConsentId(data.getId());
		sendEmailNotification(data, subsList);

		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	// send email notification
	private void sendEmailNotification(Consent data, List<Subscription> subsList) {

		List<String> addresses = new ArrayList<String>();

		for (Subscription s : subsList) {
			if (SubscriptionStatus.active.equals(s.getStatus())) {
				if (StringUtils.isNotBlank(s.getEmail())) {
					addresses.add(s.getEmail());
				}
			}
		}

		EmailUtils.send(Utils.objectToJson(data), addresses);

	}

	public ConsentResponse getConsentById(String consentId) {
		ConsentResponse result = new ConsentResponse();
		if (StringUtils.isEmpty(consentId)) {
			result.setStatus(ResponseStatus.MISS_THE_KEY);
			return result;
		}

		FabricSDKUtils fabric = new FabricSDKUtils();
		String data_s = null;
		try {
			data_s = fabric.query(consentId);
		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}
		if (StringUtils.isEmpty(data_s)) {
			result.setStatus(ResponseStatus.DO_NOT_FIND_DATA);
			return result;
		}

		JSONObject jsonObject = JSONObject.fromObject(data_s);

		@SuppressWarnings("rawtypes")
		Map<String, Class> classMap = new HashMap<String, Class>();
		classMap.put("consent", Consent.class);
		classMap.put("tags", Tag.class);

		Consent data = (Consent) JSONObject.toBean(jsonObject, Consent.class, classMap);

		result.setConsent(data);
		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	public IdsResponse findIdsByEntity(String entitys) {
		IdsResponse result = new IdsResponse();

		String[] entity = entitys.split(KeyNameUtils.SPLIT_COMMA);

		List<String> consentIds = new ArrayList<String>();
		try {
			FabricSDKUtils fabric = new FabricSDKUtils();

			for (String ent : entity) {
				String a = fabric.getStateByRange(KeyNameUtils.getEntityStartKey(ent),
						KeyNameUtils.getEntityEndKey(ent));
				JSONObject jsonObject = JSONObject.fromObject(a);
				@SuppressWarnings("unchecked")
				List<Map<String, String>> list = (List<Map<String, String>>) jsonObject.get("data");

				for (Map<String, String> m : list) {
					consentIds.add(m.get("collectData"));
				}
			}

			if (consentIds.size() == 0) {
				result.setStatus(ResponseStatus.DO_NOT_FIND_DATA);
				return result;
			}

		} catch (Exception e) {
			e.getStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}
		result.setIds(consentIds);
		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	public BaseResponseBean revoke(String consentId) {
		BaseResponseBean result = new BaseResponseBean();
		ConsentResponse c = getConsentById(consentId);
		if (!"000".equals(c.getReturnCode())) {
			result.setReturnCode(c.getReturnCode());
			result.setReturnMessage(c.getReturnMessage());
			return result;
		}

		Consent consent = c.getConsent();
		consent.setStatus(ConsentStatus.revoked);

		result = updateConsent(consent);
		return result;
	}

	public BaseResponseBean revokeWithArray(Consents consents) {
		BaseResponseBean result = new BaseResponseBean();
		for (String consentId : consents.getConsents()) {
			result = revoke(consentId);
			if (!"000".equals(result.getReturnCode())) {
				return result;
			}
		}
		return result;
	}

	public IdsResponse createWithArray(List<Consent> data) {
		IdsResponse result = new IdsResponse();
		List<String> consentIds = new ArrayList<String>();
		for (Consent consent : data) {
			CollectDataResponse c = createConsent(consent);

			if (!"000".equals(c.getReturnCode())) {
				result.setReturnCode(c.getReturnCode());
				result.setReturnMessage(c.getReturnMessage());
				return result;
			}
			consentIds.add(c.getConsentId());
		}
		result.setIds(consentIds);
		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	public ConsentHistoryResponse getConsentHistoryByConsentId(String consentId) {

		ConsentHistoryResponse result = new ConsentHistoryResponse();
		if (StringUtils.isEmpty(consentId)) {
			result.setStatus(ResponseStatus.MISS_THE_KEY);
			return result;
		}

		FabricSDKUtils fabric = new FabricSDKUtils();
		String data_s = null;
		try {
			data_s = fabric.queryHistory(consentId);
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}
		if (StringUtils.isEmpty(data_s)) {
			result.setStatus(ResponseStatus.DO_NOT_FIND_DATA);
			return result;
		}

		// Json String to Object
		JSONObject jsonObject = JSONObject.fromObject(data_s);

		System.out.println(data_s);

		Map<String, Object> classmap = new HashMap<String, Object>();
		classmap.put("data", ConsentHistory.class);
		classmap.put("collectData", Consent.class);
		classmap.put("status", ConsentStatus.class);

		classmap.put("tags", Tag.class);

		ConsentHistoryResponse data = (ConsentHistoryResponse) JSONObject.toBean(jsonObject,
				ConsentHistoryResponse.class, classmap);

		// result.setData(data);
		data.setStatus(ResponseStatus.SUCCESS);
		return data;

	}
}
