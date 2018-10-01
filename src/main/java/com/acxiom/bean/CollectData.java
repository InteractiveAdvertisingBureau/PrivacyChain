package com.acxiom.bean;

import java.util.List;

public class CollectData {
	private String consentId;
	private List<String> optin;

	private String entity;
	private String operation;
	private String destination;
	private String source;

	private String consentExpiry;
	private String purpose;
	private String sarId;

	public String getSarId() {
		return sarId;
	}

	public void setSarId(String sarId) {
		this.sarId = sarId;
	}

	public String getConsentExpiry() {
		return consentExpiry;
	}

	public void setConsentExpiry(String consentExpiry) {
		this.consentExpiry = consentExpiry;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getConsentId() {
		return consentId;
	}

	public void setConsentId(String consentId) {
		this.consentId = consentId;
	}

	public List<String> getOptin() {
		return optin;
	}

	public void setOptin(List<String> optin) {
		this.optin = optin;
	}

}
