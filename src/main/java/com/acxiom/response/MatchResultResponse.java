package com.acxiom.response;

import java.util.List;

import com.acxiom.bean.MatchResult;

public class MatchResultResponse extends BaseResponseBean {

	private List<MatchResult> summary;

	public List<MatchResult> getSummary() {
		return summary;
	}

	public void setSummary(List<MatchResult> summary) {
		this.summary = summary;
	}

}
