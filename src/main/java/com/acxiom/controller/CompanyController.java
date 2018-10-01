package com.acxiom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.acxiom.bean.MatchInfo;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.CompanyListResponse;
import com.acxiom.response.DownloadMatchResultResponse;
import com.acxiom.response.MatchResultResponse;
import com.acxiom.response.SegmentListResponse;
import com.acxiom.service.CompanyService;
import com.acxiom.utils.Utils;

@RestController
@RequestMapping("/company")
public class CompanyController {
	private static final Logger log = LoggerFactory.getLogger(CompanyController.class);

	@Autowired
	CompanyService companyService;

	@RequestMapping(value = "/uploadCompanyData/{companyName}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseBean uploadCompanyData(@RequestParam("file") MultipartFile file,
			@PathVariable String companyName) {
		log.info("[uploadCompanyData]" + companyName);
		return companyService.uploadCompanyData(file, companyName);
	}

	@RequestMapping(value = "/getCompanyList", method = RequestMethod.GET)
	@ResponseBody
	public CompanyListResponse getCompanyList() {
		log.info("[getCompanyList]");
		return companyService.getCompanyList();
	}

	@RequestMapping(value = "/uploadSegmentData/{companyName}/{segmentName}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseBean uploadSegmentData(@RequestParam("file") MultipartFile file,
			@PathVariable String companyName, @PathVariable String segmentName) {
		log.info("[uploadSegmentData]" + companyName + "|" + segmentName);
		return companyService.uploadSegmentData(file, companyName, segmentName);
	}

	@RequestMapping(value = "/getSegmentList/{companyName}", method = RequestMethod.GET)
	@ResponseBody
	public SegmentListResponse getSegmentList(@PathVariable String companyName) {
		log.info("[getSegmentList]" + companyName);
		return companyService.getSegmentList(companyName);
	}

	@RequestMapping(value = "/matchData", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseBean matchData(@RequestBody MatchInfo info) {
		log.info("[matchData]" + Utils.objectToJson(info));
		return companyService.matchData(info);
	}

	@RequestMapping(value = "/getMatchResult/{companyName}", method = RequestMethod.GET)
	@ResponseBody
	public MatchResultResponse getMatchResult(@PathVariable String companyName) {
		log.info("[getMatchResult]" + companyName);
		return companyService.getMatchResult(companyName);
	}

	@RequestMapping(value = "/downloadMatchResult/{dataKey}", method = RequestMethod.GET)
	@ResponseBody
	public DownloadMatchResultResponse downloadMatchResult(@PathVariable String dataKey) {
		log.info("[downloadMatchResult]" + dataKey);
		return companyService.downloadMatchResult(dataKey);
	}

}
