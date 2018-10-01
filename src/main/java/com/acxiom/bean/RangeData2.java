package com.acxiom.bean;

public class RangeData2<T> {
	private String key;
	private T collectData;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public T getCollectData() {
		return collectData;
	}

	public void setCollectData(T collectData) {
		this.collectData = collectData;
	}

}
