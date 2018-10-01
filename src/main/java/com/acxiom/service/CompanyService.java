package com.acxiom.service;

import org.springframework.web.multipart.MultipartFile;

import com.acxiom.bean.MatchInfo;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.CompanyListResponse;
import com.acxiom.response.DownloadMatchResultResponse;
import com.acxiom.response.MatchResultResponse;
import com.acxiom.response.SegmentListResponse;

public interface CompanyService {

	public BaseResponseBean uploadCompanyData(MultipartFile file, String companyName);

	public CompanyListResponse getCompanyList();

	public BaseResponseBean uploadSegmentData(MultipartFile file, String companyName, String segmentName);

	public SegmentListResponse getSegmentList(String companyName);

	public BaseResponseBean matchData(MatchInfo info);

	public MatchResultResponse getMatchResult(String companyName);

	public DownloadMatchResultResponse downloadMatchResult(String dataKey);

}
