package com.acxiom.service;

import java.util.List;

import com.acxiom.bean.Consent;
import com.acxiom.bean.Consents;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.CollectDataResponse;
import com.acxiom.response.ConsentHistoryResponse;
import com.acxiom.response.IdsResponse;
import com.acxiom.response.ConsentResponse;

public interface ConsentService {

	CollectDataResponse createConsent(Consent data);

	BaseResponseBean updateConsent(Consent data);

	IdsResponse createWithArray(List<Consent> data);

	IdsResponse findIdsByEntity(String entitys);

	ConsentResponse getConsentById(String consentId);

	BaseResponseBean revokeWithArray(Consents consents);

	BaseResponseBean revoke(String consentId);

	ConsentHistoryResponse getConsentHistoryByConsentId(String consentId);

}
