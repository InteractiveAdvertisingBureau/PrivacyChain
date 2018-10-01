package com.acxiom.response;

import java.util.List;

import com.acxiom.bean.CompanySummary;

public class CompanyListResponse extends BaseResponseBean {

	private List<CompanySummary> summary;

	public List<CompanySummary> getSummary() {
		return summary;
	}

	public void setSummary(List<CompanySummary> summary) {
		this.summary = summary;
	}

}
