package com.acxiom.response;

import java.util.List;

import com.acxiom.bean.Subscription;

public class SubscriptionListResponse extends BaseResponseBean {
	private List<Subscription> subscription;

	public List<Subscription> getSubscription() {
		return subscription;
	}

	public void setSubscription(List<Subscription> subscription) {
		this.subscription = subscription;
	}

}
