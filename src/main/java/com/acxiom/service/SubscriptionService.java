package com.acxiom.service;

import java.util.List;

import com.acxiom.bean.Subscription;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.IdResponse;
import com.acxiom.response.SubscriptionListResponse;
import com.acxiom.response.SubscriptionResponse;

public interface SubscriptionService {

	IdResponse createSubscription(Subscription data);

	SubscriptionListResponse findByEntity(String entity);

	SubscriptionResponse getSubscriptionById(String subscriptionId);

	BaseResponseBean deleteSubscriptionById(String subscriptionId);

	List<Subscription> getSubscriptionsByConsentId(String consentId);
}
