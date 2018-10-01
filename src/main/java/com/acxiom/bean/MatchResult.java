package com.acxiom.bean;

import java.util.List;

public class MatchResult {
	private String segmentName;
	private String targetCompanyName;
	private int totalMatched;
	private int totalSegCount;
	private String status;
	private String finishTime;
	private String dataKey;
	private String error;
	private List<String> matchKey;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public int getTotalSegCount() {
		return totalSegCount;
	}

	public void setTotalSegCount(int totalSegCount) {
		this.totalSegCount = totalSegCount;
	}

	public List<String> getMatchKey() {
		return matchKey;
	}

	public void setMatchKey(List<String> matchKey) {
		this.matchKey = matchKey;
	}

	public String getDataKey() {
		return dataKey;
	}

	public void setDataKey(String dataKey) {
		this.dataKey = dataKey;
	}

	public String getSegmentName() {
		return segmentName;
	}

	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}

	public String getTargetCompanyName() {
		return targetCompanyName;
	}

	public void setTargetCompanyName(String targetCompanyName) {
		this.targetCompanyName = targetCompanyName;
	}

	public int getTotalMatched() {
		return totalMatched;
	}

	public void setTotalMatched(int totalMatched) {
		this.totalMatched = totalMatched;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

}
