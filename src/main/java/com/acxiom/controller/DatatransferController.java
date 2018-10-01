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

import com.acxiom.bean.Datatransfer;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.DatatransferListResponse;
import com.acxiom.response.DatatransferResponse;
import com.acxiom.response.IdResponse;
import com.acxiom.response.IdsResponse;
import com.acxiom.service.DatatransferService;
import com.acxiom.utils.Utils;

@RestController
@RequestMapping("/datatransfer")
public class DatatransferController {
	private static final Logger log = LoggerFactory.getLogger(DatatransferController.class);

	@Autowired
	DatatransferService datatransferService;

	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public IdResponse createDatatransfer(@RequestBody Datatransfer data) {
		log.info("[createDatatransfer]" + Utils.objectToJson(data));
		return datatransferService.createDatatransfer(data);
	}

	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public BaseResponseBean updateDatatransfer(@RequestBody Datatransfer data) {
		log.info("[updateDatatransfer]" + Utils.objectToJson(data));
		return datatransferService.updateDatatransfer(data);
	}

	@RequestMapping(value = "/createWithArray", method = RequestMethod.POST)
	@ResponseBody
	public IdsResponse createWithArray(@RequestBody List<Datatransfer> data) {
		log.info("[createWithArray]" + Utils.listToJson(data));

		return datatransferService.createWithArray(data);
	}

	@RequestMapping(value = "/createWithList", method = RequestMethod.POST)
	@ResponseBody
	public IdsResponse createWithList(@RequestBody List<Datatransfer> data) {
		log.info("[createWithList]" + Utils.listToJson(data));

		return datatransferService.createWithArray(data);
	}

	@RequestMapping(value = "/findByConsentID", method = RequestMethod.GET)
	@ResponseBody
	public DatatransferListResponse findByConsentID(@RequestParam String consentID) {
		log.info("[findByConsentID]" + consentID);
		return datatransferService.findByConsentID(consentID);
	}

	@RequestMapping(value = "/{datatransferId}", method = RequestMethod.GET)
	@ResponseBody
	public DatatransferResponse getDatatransferById(@PathVariable String datatransferId) {
		log.info("[getDatatransferById]" + datatransferId);
		return datatransferService.getDatatransferById(datatransferId);
	}
}
