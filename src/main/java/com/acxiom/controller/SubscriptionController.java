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

import com.acxiom.bean.Subscription;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.IdResponse;
import com.acxiom.response.SubscriptionListResponse;
import com.acxiom.response.SubscriptionResponse;
import com.acxiom.service.SubscriptionService;
import com.acxiom.utils.Utils;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {
	private static final Logger log = LoggerFactory.getLogger(SubscriptionController.class);

	@Autowired
	SubscriptionService subscriptionService;

	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public IdResponse createSubscription(@RequestBody Subscription data) {
		log.info("[createSubscription]" + Utils.objectToJson(data));
		return subscriptionService.createSubscription(data);
	}

	@RequestMapping(value = "/findByEntity", method = RequestMethod.GET)
	@ResponseBody
	public SubscriptionListResponse findByEntity(@RequestParam String entity) {
		log.info("[findByEntity]" + entity);
		return subscriptionService.findByEntity(entity);
	}

	@RequestMapping(value = "/{subscriptionId}", method = RequestMethod.GET)
	@ResponseBody
	public SubscriptionResponse getSubscriptionById(@PathVariable String subscriptionId) {
		log.info("[getSubscriptionById]" + subscriptionId);
		return subscriptionService.getSubscriptionById(subscriptionId);
	}

	@RequestMapping(value = "/{subscriptionId}", method = RequestMethod.DELETE)
	@ResponseBody
	public BaseResponseBean deleteSubscriptionById(@PathVariable String subscriptionId) {
		log.info("[deleteSubscriptionById]" + subscriptionId);
		return subscriptionService.deleteSubscriptionById(subscriptionId);
	}

}
