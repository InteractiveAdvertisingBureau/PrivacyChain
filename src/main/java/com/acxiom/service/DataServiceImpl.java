package com.acxiom.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.acxiom.bean.CollectData;
import com.acxiom.bean.HistoryData;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.CollectDataResponse;
import com.acxiom.response.DataHistoryResponse;
import com.acxiom.response.DataResponse;
import com.acxiom.response.ResponseStatus;
import com.acxiom.utils.FabricSDKUtils;
import com.acxiom.utils.PropertiesUtils;
import com.acxiom.utils.Utils;

import net.sf.json.JSONObject;

@Service
public class DataServiceImpl implements DataService {
	private PropertiesUtils p = new PropertiesUtils("application.properties");
	File sampleStoreFile = new File(p.getProperty("HFCSAMPLE_PROPERTIES"));

	private static String OPERATION_REVOKE = "Revoke Consent";
	private static String OPERATION_UPDATE = "Update Consent";

	@Override
	public DataResponse getDataByConsentId(String consentId) {
		DataResponse result = new DataResponse();
		if (StringUtils.isEmpty(consentId)) {
			result.setStatus(ResponseStatus.MISS_THE_KEY);
			return result;
		}

		FabricSDKUtils fabric = new FabricSDKUtils();
		String data_s = null;
		try {
			data_s = fabric.query(consentId);
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
		CollectData data = (CollectData) JSONObject.toBean(jsonObject, CollectData.class);

		result.setData(data);
		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	@Override
	public CollectDataResponse collectData(CollectData data) {
		CollectDataResponse result = new CollectDataResponse();
		String consentId = Utils.getUUID();
		if (StringUtils.isEmpty(data.getConsentExpiry())) {
			data.setConsentExpiry(Utils.getCurrentDate());
		}
		String value = Utils.objectToJson(data);

		FabricSDKUtils fabric = new FabricSDKUtils();

		try {
			fabric.invoke(consentId, value);
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		result.setConsentId(consentId);
		result.setStatus(ResponseStatus.SUCCESS);

		return result;
	}

	@Override
	public BaseResponseBean updateConsent(CollectData data) {
		BaseResponseBean result = new BaseResponseBean();

		DataResponse r = getDataByConsentId(data.getConsentId());
		if (!"000".equals(r.getReturnCode())) {
			result.setReturnCode(r.getReturnCode());
			result.setReturnMessage(r.getReturnMessage());
			return result;
		}

		if (StringUtils.isEmpty(data.getConsentExpiry())) {
			data.setConsentExpiry(r.getData().getConsentExpiry());
		}
		if (StringUtils.isEmpty(data.getSarId())) {
			data.setSarId(r.getData().getSarId());
		}
		if (StringUtils.isEmpty(data.getPurpose())) {
			data.setPurpose(r.getData().getPurpose());
		}

		try {
			FabricSDKUtils fabric = new FabricSDKUtils();
			fabric.invoke(data.getConsentId(), Utils.objectToJson(data));
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	@Override
	public DataHistoryResponse getDataHistoryByConsentId(String consentId) {
		DataHistoryResponse result = new DataHistoryResponse();
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

		Map<String, Object> classmap = new HashMap<String, Object>();
		classmap.put("data", HistoryData.class);
		classmap.put("collectData", CollectData.class);

		DataHistoryResponse data = (DataHistoryResponse) JSONObject.toBean(jsonObject, DataHistoryResponse.class,
				classmap);

		// result.setData(data);
		data.setStatus(ResponseStatus.SUCCESS);
		return data;

	}

	@Override
	public BaseResponseBean revokeData(CollectData data) {
		BaseResponseBean result = new BaseResponseBean();

		if (StringUtils.isEmpty(data.getSarId())) {
			result.setStatus(ResponseStatus.MISS_SAR_ID);
			return result;
		}
		if (StringUtils.isEmpty(data.getEntity())) {
			result.setStatus(ResponseStatus.MISS_ENTITY);
			return result;
		}
		if (StringUtils.isEmpty(data.getOperation())) {
			result.setStatus(ResponseStatus.MISS_OPERATION);
			return result;
		}
		if (data.getOptin() == null || data.getOptin().size() == 0) {
			result.setStatus(ResponseStatus.MISS_OPT_IN);
			return result;
		}

		DataResponse r = getDataByConsentId(data.getConsentId());
		if (!"000".equals(r.getReturnCode())) {
			result.setReturnCode(r.getReturnCode());
			result.setReturnMessage(r.getReturnMessage());
			return result;
		}
		FabricSDKUtils fabric = new FabricSDKUtils();

		List<String> optin = new ArrayList<String>();

		// 1. do SAR
		CollectData c = r.getData();

		for (String opt : c.getOptin()) {
			optin.add(opt);
		}

		c.setSarId(data.getSarId());
		c.setEntity(data.getEntity());
		c.setOperation(data.getOperation());
		try {
			fabric.invoke(data.getConsentId(), Utils.objectToJson(c));
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		// 2. do Revoke
		c.setOperation(OPERATION_REVOKE);
		c.setOptin(data.getOptin());
		try {
			fabric.invoke(data.getConsentId(), Utils.objectToJson(c));
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		// 3. update consent
		c.setOperation(OPERATION_UPDATE);
		for (String opt : data.getOptin()) {
			optin.remove(opt);
		}
		c.setOptin(optin);
		try {
			fabric.invoke(data.getConsentId(), Utils.objectToJson(c));
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		result.setStatus(ResponseStatus.SUCCESS);
		return result;

	}

}
