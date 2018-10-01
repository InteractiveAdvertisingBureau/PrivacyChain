package com.acxiom.bean;

import java.util.List;

public class MatchInfo {
	private String targetCompanyName;

	private String companyName;
	private String segmentName;

	private List<String> matchKey;

	public String getTargetCompanyName() {
		return targetCompanyName;
	}

	public void setTargetCompanyName(String targetCompanyName) {
		this.targetCompanyName = targetCompanyName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getSegmentName() {
		return segmentName;
	}

	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}

	public List<String> getMatchKey() {
		return matchKey;
	}

	public void setMatchKey(List<String> matchKey) {
		this.matchKey = matchKey;
	}

}
