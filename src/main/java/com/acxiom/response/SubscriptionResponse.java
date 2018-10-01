package com.acxiom.response;

import com.acxiom.bean.Subscription;

public class SubscriptionResponse extends BaseResponseBean {
	private Subscription subscription;

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

}
