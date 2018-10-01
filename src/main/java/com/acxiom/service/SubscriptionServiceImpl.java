package com.acxiom.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acxiom.bean.Subscription;
import com.acxiom.bean.SubscriptionStatus;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.ConsentResponse;
import com.acxiom.response.IdResponse;
import com.acxiom.response.ResponseStatus;
import com.acxiom.response.SubscriptionListResponse;
import com.acxiom.response.SubscriptionResponse;
import com.acxiom.utils.EmailUtils;
import com.acxiom.utils.FabricSDKUtils;
import com.acxiom.utils.KeyNameUtils;
import com.acxiom.utils.Utils;

import net.sf.json.JSONObject;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	@Autowired
	ConsentService consentService;

	public IdResponse createSubscription(Subscription data) {
		IdResponse result = new IdResponse();

		if (StringUtils.isEmpty(data.getConsentId())) {
			result.setStatus(ResponseStatus.MISS_THE_KEY);
			return result;
		}
		ConsentResponse c = consentService.getConsentById(data.getConsentId());
		if (!"000".equals(c.getReturnCode())) {
			result.setReturnCode(c.getReturnCode());
			result.setReturnMessage(c.getReturnMessage());
			return result;
		}

		// check email
		if (!EmailUtils.checkEmail(data.getEmail())) {
			result.setStatus(ResponseStatus.SUBSCRIPTION_EMAIL_ERROR);
			return result;
		}
		// 1. create Subscription
		String id = Utils.getUUID();
		data.setId(id);
		String value = Utils.objectToJson(data);

		FabricSDKUtils fabric = new FabricSDKUtils();
		try {
			fabric.invoke(id, value);
		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		// 2. add relationship between Entity and Subscription
		try {
			fabric.invoke(KeyNameUtils.getSubscriptionWithEntity(data.getEntity(), id), "\"" + id + "\"");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		// 3. add relationship between consentId and Subscription
		try {
			fabric.invoke(KeyNameUtils.getSubscriptionWithConsentId(data.getConsentId(), id), "\"" + id + "\"");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		result.setId(id);
		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	public SubscriptionListResponse findByEntity(String entity) {
		SubscriptionListResponse result = new SubscriptionListResponse();
		List<Subscription> subscriptions = new ArrayList<Subscription>();
		FabricSDKUtils fabric = new FabricSDKUtils();

		try {
			String a = fabric.getStateByRange(KeyNameUtils.getSubscriptionStartKey(entity),
					KeyNameUtils.getSubscriptionEndKey(entity));

			JSONObject jsonObject = JSONObject.fromObject(a);

			@SuppressWarnings("unchecked")
			List<Map<String, String>> list = (List<Map<String, String>>) jsonObject.get("data");

			for (Map<String, String> m : list) {
				String datatransferId = m.get("collectData");

				SubscriptionResponse r = getSubscriptionById(datatransferId);
				if (!"000".equals(r.getReturnCode())) {
					result.setReturnCode(r.getReturnCode());
					result.setReturnMessage(r.getReturnMessage());
					return result;
				}

				subscriptions.add(r.getSubscription());
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		if (subscriptions.size() == 0) {
			result.setStatus(ResponseStatus.DO_NOT_FIND_DATA);
			return result;
		} else {
			result.setSubscription(subscriptions);
			result.setStatus(ResponseStatus.SUCCESS);
		}
		return result;

	}

	public SubscriptionResponse getSubscriptionById(String subscriptionId) {
		SubscriptionResponse result = new SubscriptionResponse();
		if (StringUtils.isEmpty(subscriptionId)) {
			result.setStatus(ResponseStatus.INVALID_ID_SUPPLIED);
			return result;
		}

		FabricSDKUtils fabric = new FabricSDKUtils();
		String data_s = null;
		try {
			data_s = fabric.query(subscriptionId);
		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		if (StringUtils.isEmpty(data_s)) {
			result.setStatus(ResponseStatus.SUBSCRIPTION_NOT_FOUND);
			return result;
		}

		JSONObject jsonObject = JSONObject.fromObject(data_s);

		Subscription data = (Subscription) JSONObject.toBean(jsonObject, Subscription.class);

		result.setSubscription(data);
		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	public BaseResponseBean deleteSubscriptionById(String subscriptionId) {
		BaseResponseBean result = new BaseResponseBean();

		SubscriptionResponse r = getSubscriptionById(subscriptionId);
		if (!"000".equals(r.getReturnCode())) {
			result.setReturnCode(r.getReturnCode());
			result.setReturnMessage(r.getReturnMessage());
			return result;
		}

		Subscription subscription = r.getSubscription();
		subscription.setStatus(SubscriptionStatus.inactive);
		try {
			FabricSDKUtils fabric = new FabricSDKUtils();
			fabric.invoke(subscription.getId(), Utils.objectToJson(subscription));
		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	public List<Subscription> getSubscriptionsByConsentId(String consentId) {
		List<Subscription> result = new ArrayList<Subscription>();

		FabricSDKUtils fabric = new FabricSDKUtils();

		String a;
		try {
			a = fabric.getStateByRange(KeyNameUtils.getSubAndConStartKey(consentId),
					KeyNameUtils.getSubAndConEndKey(consentId));
			JSONObject jsonObject = JSONObject.fromObject(a);

			@SuppressWarnings("unchecked")
			List<Map<String, String>> list = (List<Map<String, String>>) jsonObject.get("data");

			for (Map<String, String> m : list) {
				SubscriptionResponse r = getSubscriptionById(m.get("collectData"));

				if ("000".equals(r.getReturnCode())) {
					result.add(r.getSubscription());
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
