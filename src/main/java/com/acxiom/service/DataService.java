package com.acxiom.service;

import com.acxiom.bean.CollectData;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.CollectDataResponse;
import com.acxiom.response.DataHistoryResponse;
import com.acxiom.response.DataResponse;

public interface DataService {

	public CollectDataResponse collectData(CollectData data);

	public BaseResponseBean updateConsent(CollectData data);

	public DataResponse getDataByConsentId(String consentId);

	public DataHistoryResponse getDataHistoryByConsentId(String consentId);

	public BaseResponseBean revokeData(CollectData data);

}
