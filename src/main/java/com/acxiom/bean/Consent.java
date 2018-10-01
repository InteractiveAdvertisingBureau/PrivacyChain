package com.acxiom.bean;

import java.util.List;

public class Consent {
	private String id;
	private String consentType;
	private String entity;
	private String expires;
	private List<String> attributes;
	
	private List<Tag> tags;
	
	private ConsentStatus status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConsentType() {
		return consentType;
	}

	public void setConsentType(String consentType) {
		this.consentType = consentType;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public ConsentStatus getStatus() {
		return status;
	}

	public void setStatus(ConsentStatus status) {
		this.status = status;
	}

}
