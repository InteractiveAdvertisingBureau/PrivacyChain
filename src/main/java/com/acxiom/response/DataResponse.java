package com.acxiom.response;

import com.acxiom.bean.CollectData;

public class DataResponse extends BaseResponseBean {

	private CollectData data;

	public CollectData getData() {
		return data;
	}

	public void setData(CollectData data) {
		this.data = data;
	}

}
