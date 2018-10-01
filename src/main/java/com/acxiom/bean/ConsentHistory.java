package com.acxiom.bean;

public class ConsentHistory {
	private Consent collectData;
	private String txId;
	private String timestamp;
	private String isDelete;

	public Consent getCollectData() {
		return collectData;
	}

	public void setCollectData(Consent collectData) {
		this.collectData = collectData;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

}
