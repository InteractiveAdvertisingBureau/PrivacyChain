package com.acxiom.response;

import java.util.List;

import com.acxiom.bean.HistoryData;

public class DataHistoryResponse extends BaseResponseBean {
	private List<HistoryData> data;

	public List<HistoryData> getData() {
		return data;
	}

	public void setData(List<HistoryData> data) {
		this.data = data;
	}

}
