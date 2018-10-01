package com.acxiom.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acxiom.bean.Datatransfer;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.ConsentResponse;
import com.acxiom.response.DatatransferListResponse;
import com.acxiom.response.DatatransferResponse;
import com.acxiom.response.IdResponse;
import com.acxiom.response.IdsResponse;
import com.acxiom.response.ResponseStatus;
import com.acxiom.utils.FabricSDKUtils;
import com.acxiom.utils.Utils;

import net.sf.json.JSONObject;

@Service
public class DatatransferServiceImpl implements DatatransferService {

	@Autowired
	ConsentService consentService;

	private String getDatatransferIdWithConsentId(String consentId, String id) {
		return "ConsentId_[" + consentId + "]_DatatransferId_" + id;
	}

	private String getConsentIDStartKey(String consentId) {
		return "ConsentId_[" + consentId + "]_Da";
	}

	private String getConsentIDEndKey(String consentId) {
		return "ConsentId_[" + consentId + "]_Db";
	}

	public IdResponse createDatatransfer(Datatransfer data) {
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

		// 1. create Datatransfer
		String id = Utils.getUUID();
		data.setId(id);
		data.setTimestamp(Utils.getCurrentDate());
		String value = Utils.objectToJson(data);

		FabricSDKUtils fabric = new FabricSDKUtils();
		try {
			fabric.invoke(id, value);
		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		// 2. add relationship between Consent and Datatransfer
		try {
			fabric.invoke(getDatatransferIdWithConsentId(data.getConsentId(), id), "\"" + id + "\"");
		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		result.setId(id);
		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	public BaseResponseBean updateDatatransfer(Datatransfer data) {
		BaseResponseBean result = new BaseResponseBean();

		DatatransferResponse r = getDatatransferById(data.getId());
		if (!"000".equals(r.getReturnCode())) {
			result.setReturnCode(r.getReturnCode());
			result.setReturnMessage(r.getReturnMessage());
			return result;
		}

		if (!r.getDatatransfer().getConsentId().equals(data.getConsentId())) {
			result.setStatus(ResponseStatus.INVALID_ID_SUPPLIED);
			return result;
		}

		try {
			FabricSDKUtils fabric = new FabricSDKUtils();
			fabric.invoke(data.getId(), Utils.objectToJson(data));
		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	public DatatransferResponse getDatatransferById(String datatransferId) {
		DatatransferResponse result = new DatatransferResponse();
		if (StringUtils.isEmpty(datatransferId)) {
			result.setStatus(ResponseStatus.INVALID_ID_SUPPLIED);
			return result;
		}

		FabricSDKUtils fabric = new FabricSDKUtils();
		String data_s = null;
		try {
			data_s = fabric.query(datatransferId);
		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		if (StringUtils.isEmpty(data_s)) {
			result.setStatus(ResponseStatus.DATATRANSFER_NOT_FOUND);
			return result;
		}

		JSONObject jsonObject = JSONObject.fromObject(data_s);

		Datatransfer data = (Datatransfer) JSONObject.toBean(jsonObject, Datatransfer.class);

		result.setDatatransfer(data);
		result.setStatus(ResponseStatus.SUCCESS);
		return result;

	}

	public DatatransferListResponse findByConsentID(String consentID) {
		DatatransferListResponse result = new DatatransferListResponse();
		List<Datatransfer> datatransfers = new ArrayList<Datatransfer>();
		FabricSDKUtils fabric = new FabricSDKUtils();

		try {
			String a = fabric.getStateByRange(getConsentIDStartKey(consentID), getConsentIDEndKey(consentID));

			JSONObject jsonObject = JSONObject.fromObject(a);

			@SuppressWarnings("unchecked")
			List<Map<String, String>> list = (List<Map<String, String>>) jsonObject.get("data");

			for (Map<String, String> m : list) {
				String datatransferId = m.get("collectData");

				DatatransferResponse r = getDatatransferById(datatransferId);
				if (!"000".equals(r.getReturnCode())) {
					result.setReturnCode(r.getReturnCode());
					result.setReturnMessage(r.getReturnMessage());
					return result;
				}

				datatransfers.add(r.getDatatransfer());
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		if (datatransfers.size() == 0) {
			result.setStatus(ResponseStatus.DO_NOT_FIND_DATA);
			return result;
		} else {
			result.setDatatransfers(datatransfers);
			result.setStatus(ResponseStatus.SUCCESS);
		}
		return result;

	}

	public IdsResponse createWithArray(List<Datatransfer> data) {
		IdsResponse result = new IdsResponse();
		List<String> ids = new ArrayList<String>();
		for (Datatransfer datatransfer : data) {
			IdResponse c = createDatatransfer(datatransfer);
			if (!"000".equals(c.getReturnCode())) {
				result.setReturnCode(c.getReturnCode());
				result.setReturnMessage(c.getReturnMessage());
				return result;
			}
			ids.add(c.getId());
		}
		result.setIds(ids);
		result.setStatus(ResponseStatus.SUCCESS);
		return result;

	}

}
