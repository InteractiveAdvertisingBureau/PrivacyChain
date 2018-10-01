package com.acxiom.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.acxiom.bean.CompanySummary;
import com.acxiom.bean.MatchInfo;
import com.acxiom.bean.MatchResult;
import com.acxiom.bean.RangeData;
import com.acxiom.bean.RangeData2;
import com.acxiom.bean.SegmentSummary;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.CompanyListResponse;
import com.acxiom.response.DownloadMatchResultResponse;
import com.acxiom.response.MatchResultResponse;
import com.acxiom.response.ResponseStatus;
import com.acxiom.response.SegmentListResponse;
import com.acxiom.utils.FabricSDKUtils;
import com.acxiom.utils.Utils;

import net.sf.json.JSONObject;

@Service
public class CompanyServiceImpl implements CompanyService {
	private String COMPANY_SUMMARY_SEARCH_START_KEY = "company_summary";
	private String COMPANY_SUMMARY_SEARCH_END_KEY = "company_summarz";
	private String SPLIT_ROW = "\n";
	private String MATCH_STATUS_PROCESS = "processing";

	@Override
	public BaseResponseBean uploadCompanyData(MultipartFile file, String companyName) {
		BaseResponseBean result = new BaseResponseBean();
		if (StringUtils.isEmpty(companyName)) {
			result.setStatus(ResponseStatus.MISS_COMPANY_NAME);
			return result;
		}
		if (file.isEmpty()) {
			result.setStatus(ResponseStatus.MISS_UPLOAD_FILE);
			return result;
		}

		StringBuffer sb = new StringBuffer();
		boolean comma_flag = false;
		int count = -1;// 跳过第一行
		try {
			InputStream inputStream = file.getInputStream();
			InputStreamReader is = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(is);
			String s = "";
			while ((s = br.readLine()) != null) {
				if (comma_flag) {
					sb.append(SPLIT_ROW);
				}
				sb.append(s);
				count++;
				comma_flag = true;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		FabricSDKUtils fabric = new FabricSDKUtils();
		try {
			// save all data
			fabric.invoke(getCompanyKey(companyName), sb.toString());
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		// record the summary
		CompanySummary summary = new CompanySummary();
		summary.setName(companyName);
		summary.setFileName(file.getOriginalFilename());
		summary.setTotalCount(count);
		try {
			fabric.invoke(getCompanySummaryKey(companyName), Utils.objectToJson(summary));
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompanyListResponse getCompanyList() {
		CompanyListResponse result = new CompanyListResponse();
		List<CompanySummary> summaryResult = new ArrayList<CompanySummary>();

		try {
			FabricSDKUtils fabric = new FabricSDKUtils();
			String a = fabric.getStateByRange(COMPANY_SUMMARY_SEARCH_START_KEY, COMPANY_SUMMARY_SEARCH_END_KEY);

			JSONObject jsonObject = JSONObject.fromObject(a);

			Map<String, Object> classmap = new HashMap<String, Object>();
			classmap.put("data", RangeData2.class);
			classmap.put("collectData", CompanySummary.class);

			RangeData<CompanySummary> data = (RangeData<CompanySummary>) JSONObject.toBean(jsonObject, RangeData.class,
					classmap);

			List<RangeData2<CompanySummary>> s = data.getData();
			for (RangeData2<CompanySummary> summary : s) {
				summaryResult.add(summary.getCollectData());
			}
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}
		if (summaryResult.size() == 0) {
			result.setStatus(ResponseStatus.DO_NOT_FIND_DATA);
			return result;
		}

		result.setSummary(summaryResult);
		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	@Override
	public BaseResponseBean uploadSegmentData(MultipartFile file, String companyName, String segmentName) {
		BaseResponseBean result = new BaseResponseBean();
		if (StringUtils.isEmpty(companyName)) {
			result.setStatus(ResponseStatus.MISS_COMPANY_NAME);
			return result;
		}
		if (StringUtils.isEmpty(segmentName)) {
			result.setStatus(ResponseStatus.MISS_SEGMENT_NAME);
			return result;
		}
		if (file.isEmpty()) {
			result.setStatus(ResponseStatus.MISS_UPLOAD_FILE);
			return result;
		}

		StringBuffer sb = new StringBuffer();
		boolean comma_flag = false;
		int count = -1;// 跳过第一行
		try {
			InputStream inputStream = file.getInputStream();
			InputStreamReader is = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(is);
			String s = "";
			while ((s = br.readLine()) != null) {
				if (comma_flag) {
					sb.append(SPLIT_ROW);
				}
				sb.append(s);
				count++;
				comma_flag = true;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		FabricSDKUtils fabric = new FabricSDKUtils();
		try {
			// save all data
			fabric.invoke(getSegmentKey(companyName, segmentName), sb.toString());
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		// record the summary
		SegmentSummary summary = new SegmentSummary();
		summary.setCompanyName(companyName);
		summary.setSegmentName(segmentName);
		summary.setFileName(file.getOriginalFilename());
		summary.setTotalCount(count);
		try {
			fabric.invoke(getSegmentSummaryKey(companyName, segmentName), Utils.objectToJson(summary));
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SegmentListResponse getSegmentList(String companyName) {
		SegmentListResponse result = new SegmentListResponse();
		List<SegmentSummary> summaryResult = new ArrayList<SegmentSummary>();

		try {
			FabricSDKUtils fabric = new FabricSDKUtils();
			String a = fabric.getStateByRange(getSegmentStartKey(companyName), getSegmentEndKey(companyName));

			JSONObject jsonObject = JSONObject.fromObject(a);

			Map<String, Object> classmap = new HashMap<String, Object>();
			classmap.put("data", RangeData2.class);
			classmap.put("collectData", SegmentSummary.class);

			RangeData<SegmentSummary> data = (RangeData<SegmentSummary>) JSONObject.toBean(jsonObject, RangeData.class,
					classmap);

			List<RangeData2<SegmentSummary>> s = data.getData();
			for (RangeData2<SegmentSummary> summary : s) {
				summaryResult.add(summary.getCollectData());
			}
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}
		if (summaryResult.size() == 0) {
			result.setStatus(ResponseStatus.DO_NOT_FIND_DATA);
			return result;
		}

		result.setSummary(summaryResult);
		result.setStatus(ResponseStatus.SUCCESS);

		return result;
	}

	private String getSegmentStartKey(String companyName) {
		return "segment_" + companyName + "_summary";
	}

	private String getSegmentEndKey(String companyName) {
		return "segment_" + companyName + "_summarz";
	}

	private String getSegmentKey(String companyName, String segmentName) {
		return "segment_" + companyName + "_data_" + segmentName;
	}

	private String getSegmentSummaryKey(String companyName, String segmentName) {
		return "segment_" + companyName + "_summary_" + segmentName;
	}

	private String getCompanyKey(String companyName) {
		return "company_data_" + companyName;
	}

	private String getCompanySummaryKey(String companyName) {
		return "company_summary_" + companyName;
	}

	@Override
	public BaseResponseBean matchData(MatchInfo info) {
		BaseResponseBean result = new BaseResponseBean();
		if (StringUtils.isEmpty(info.getTargetCompanyName())) {
			result.setStatus(ResponseStatus.MISS_TARGET_COMPANY_NAME);
			return result;
		}
		if (StringUtils.isEmpty(info.getCompanyName())) {
			result.setStatus(ResponseStatus.MISS_COMPANY_NAME);
			return result;
		}
		if (StringUtils.isEmpty(info.getSegmentName())) {
			result.setStatus(ResponseStatus.MISS_SEGMENT_NAME);
			return result;
		}
		if (info.getMatchKey() == null || info.getMatchKey().size() == 0) {
			result.setStatus(ResponseStatus.MISS_MATCH_KEY);
			return result;
		}

		MatchResult m = new MatchResult();
		m.setSegmentName(info.getSegmentName());
		m.setStatus(MATCH_STATUS_PROCESS);
		m.setTargetCompanyName(info.getTargetCompanyName());
		m.setMatchKey(info.getMatchKey());
		try {
			FabricSDKUtils fabric = new FabricSDKUtils();
			fabric.invoke(getMatchKey(info.getCompanyName(), info.getSegmentName(), info.getTargetCompanyName()),
					Utils.objectToJson(m));

			String[] key = new String[info.getMatchKey().size() + 3];
			key[0] = info.getCompanyName();
			key[1] = info.getSegmentName();
			key[2] = info.getTargetCompanyName();
			int id = 3;
			for (String matchKey : info.getMatchKey()) {
				key[id] = matchKey;
				id++;
			}
			fabric.invoke(key, FabricSDKUtils.FUNCTION_MATCH);
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	private String getMatchKey(String companyName, String segmentName, String targetCompanyName) {
		return "match_" + companyName + "_result_" + segmentName + "_with_" + targetCompanyName;
	}

	private String getMatchStartKey(String companyName) {
		return "match_" + companyName + "_result";
	}

	private String getMatchEndKey(String companyName) {
		return "match_" + companyName + "_resulu";
	}

	@SuppressWarnings("unchecked")
	@Override
	public MatchResultResponse getMatchResult(String companyName) {
		MatchResultResponse result = new MatchResultResponse();

		List<MatchResult> summaryResult = new ArrayList<MatchResult>();

		try {
			FabricSDKUtils fabric = new FabricSDKUtils();
			String a = fabric.getStateByRange(getMatchStartKey(companyName), getMatchEndKey(companyName));

			JSONObject jsonObject = JSONObject.fromObject(a);

			Map<String, Object> classmap = new HashMap<String, Object>();
			classmap.put("data", RangeData2.class);
			classmap.put("collectData", MatchResult.class);

			RangeData<MatchResult> data = (RangeData<MatchResult>) JSONObject.toBean(jsonObject, RangeData.class,
					classmap);

			List<RangeData2<MatchResult>> s = data.getData();
			for (RangeData2<MatchResult> summary : s) {
				summaryResult.add(summary.getCollectData());
			}
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}
		if (summaryResult.size() == 0) {
			result.setStatus(ResponseStatus.DO_NOT_FIND_DATA);
			return result;
		}

		result.setSummary(summaryResult);
		result.setStatus(ResponseStatus.SUCCESS);
		return result;
	}

	@Override
	public DownloadMatchResultResponse downloadMatchResult(String dataKey) {

		DownloadMatchResultResponse result = new DownloadMatchResultResponse();

		try {
			FabricSDKUtils fabric = new FabricSDKUtils();
			String detail = fabric.query(dataKey);

			result.setDetail(detail);
		} catch (Exception e) {
			result.setStatus(ResponseStatus.ERROR);
			return result;
		}

		result.setStatus(ResponseStatus.SUCCESS);
		return result;

	}
}
