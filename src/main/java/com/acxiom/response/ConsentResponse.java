package com.acxiom.response;

import com.acxiom.bean.Consent;

public class ConsentResponse extends BaseResponseBean {

	private Consent consent;

	public Consent getConsent() {
		return consent;
	}

	public void setConsent(Consent consent) {
		this.consent = consent;
	}

}
