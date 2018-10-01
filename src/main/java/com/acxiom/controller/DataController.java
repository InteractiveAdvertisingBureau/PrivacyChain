package com.acxiom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.acxiom.bean.CollectData;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.CollectDataResponse;
import com.acxiom.response.DataHistoryResponse;
import com.acxiom.response.DataResponse;
import com.acxiom.service.DataService;
import com.acxiom.utils.Utils;

@RestController
@RequestMapping("/data")
public class DataController {
	private static final Logger log = LoggerFactory.getLogger(DataController.class);
	@Autowired
	DataService dataService;

	@RequestMapping(value = "/collectData", method = RequestMethod.POST)
	@ResponseBody
	public CollectDataResponse collectData(@RequestBody CollectData data) {
		log.info("[collectData]" + Utils.objectToJson(data));
		return dataService.collectData(data);
	}

	@RequestMapping(value = "/getDataByConsentId/{consentId}", method = RequestMethod.GET)
	@ResponseBody
	public DataResponse getDataByConsentId(@PathVariable String consentId) {
		log.info("[getDataByConsentId]" + consentId);
		return dataService.getDataByConsentId(consentId);
	}

	@RequestMapping(value = "/getDataHistoryByConsentId/{consentId}", method = RequestMethod.GET)
	@ResponseBody
	public DataHistoryResponse getDataHistoryByConsentId(@PathVariable String consentId) {
		log.info("[getDataHistoryByConsentId]" + consentId);
		return dataService.getDataHistoryByConsentId(consentId);
	}

	@RequestMapping(value = "/updateConsent", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseBean updateConsent(@RequestBody CollectData data) {
		log.info("[updateConsent]" + Utils.objectToJson(data));
		return dataService.updateConsent(data);
	}

	@RequestMapping(value = "/transferData", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseBean transferData(@RequestBody CollectData data) {
		log.info("[transferData]" + Utils.objectToJson(data));
		return dataService.updateConsent(data);
	}

	@RequestMapping(value = "/recieptData", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseBean recieptData(@RequestBody CollectData data) {
		log.info("[recieptData]" + Utils.objectToJson(data));
		return dataService.updateConsent(data);
	}

	@RequestMapping(value = "/revokeData", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseBean revokeData(@RequestBody CollectData data) {
		log.info("[revokeData]" + Utils.objectToJson(data));
		return dataService.revokeData(data);
	}

}
