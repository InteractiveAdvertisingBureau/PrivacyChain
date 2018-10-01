package com.acxiom.response;

import java.util.List;

import com.acxiom.bean.SegmentSummary;

public class SegmentListResponse extends BaseResponseBean {

	private List<SegmentSummary> summary;

	public List<SegmentSummary> getSummary() {
		return summary;
	}

	public void setSummary(List<SegmentSummary> summary) {
		this.summary = summary;
	}

}
