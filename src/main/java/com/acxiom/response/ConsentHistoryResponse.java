package com.acxiom.response;

import java.util.List;

import com.acxiom.bean.ConsentHistory;

public class ConsentHistoryResponse extends BaseResponseBean {
	private List<ConsentHistory> data;

	public List<ConsentHistory> getData() {
		return data;
	}

	public void setData(List<ConsentHistory> data) {
		this.data = data;
	}

}
