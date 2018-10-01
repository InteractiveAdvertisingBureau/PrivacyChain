package com.acxiom.bean;

import java.util.List;

public class Datatransfer {
	private String id;
	private String consentId;
	private String source;
	private String destination;
	private List<String> attributes;
	private DatatransferStatus status;
	private String timestamp;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConsentId() {
		return consentId;
	}

	public void setConsentId(String consentId) {
		this.consentId = consentId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public DatatransferStatus getStatus() {
		return status;
	}

	public void setStatus(DatatransferStatus status) {
		this.status = status;
	}

}
