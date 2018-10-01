package com.acxiom.bean;

import java.util.List;

public class RangeData<T> {
	private List<RangeData2<T>> data;

	public List<RangeData2<T>> getData() {
		return data;
	}

	public void setData(List<RangeData2<T>> data) {
		this.data = data;
	}

}
