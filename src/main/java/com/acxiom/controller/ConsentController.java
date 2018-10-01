package com.acxiom.controller;

import java.util.List;

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

import com.acxiom.bean.Consent;
import com.acxiom.bean.Consents;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.CollectDataResponse;
import com.acxiom.response.ConsentHistoryResponse;
import com.acxiom.response.ConsentResponse;
import com.acxiom.response.IdsResponse;
import com.acxiom.service.ConsentService;
import com.acxiom.utils.Utils;

@RestController
@RequestMapping("/consent")
public class ConsentController {
	private static final Logger log = LoggerFactory.getLogger(ConsentController.class);

	@Autowired
	ConsentService consentService;

	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public CollectDataResponse createConsent(@RequestBody Consent data) {
		log.info("[createConsent]" + Utils.objectToJson(data));
		return consentService.createConsent(data);
	}

	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public BaseResponseBean updateConsent(@RequestBody Consent data) {
		log.info("[updateConsent]" + Utils.objectToJson(data));
		return consentService.updateConsent(data);
	}

	@RequestMapping(value = "/createWithArray", method = RequestMethod.POST)
	@ResponseBody
	public IdsResponse createWithArray(@RequestBody List<Consent> data) {
		log.info("[createWithArray]" + Utils.listToJson(data));

		return consentService.createWithArray(data);
	}

	@RequestMapping(value = "/createWithList", method = RequestMethod.POST)
	@ResponseBody
	public IdsResponse createWithList(@RequestBody List<Consent> data) {
		log.info("[createWithList]" + Utils.listToJson(data));

		return consentService.createWithArray(data);
	}

	@RequestMapping(value = "/findIdsByEntity", method = RequestMethod.GET)
	@ResponseBody
	public IdsResponse findIdsByEntity(@RequestParam String entity) {
		log.info("[findIdsByEntity]" + entity);
		return consentService.findIdsByEntity(entity);
	}

	@RequestMapping(value = "/{consentId}", method = RequestMethod.GET)
	@ResponseBody
	public ConsentResponse getConsentById(@PathVariable String consentId) {
		log.info("[getConsentById]" + consentId);
		return consentService.getConsentById(consentId);
	}

	@RequestMapping(value = "/revokeWithArray", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseBean revokeWithArray(@RequestBody Consents consents) {
		log.info("[revokeWithArray]" + Utils.objectToJson(consents));
		return consentService.revokeWithArray(consents);
	}

	@RequestMapping(value = "/revoke/{consentId}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseBean revoke(@PathVariable String consentId) {
		log.info("[revoke]" + consentId);
		return consentService.revoke(consentId);
	}

	@RequestMapping(value = "/getConsentHistoryByConsentId/{consentId}", method = RequestMethod.GET)
	@ResponseBody
	public ConsentHistoryResponse getConsentHistoryByConsentId(@PathVariable String consentId) {
		log.info("[getConsentHistoryByConsentId]" + consentId);
		return consentService.getConsentHistoryByConsentId(consentId);
	}
}
