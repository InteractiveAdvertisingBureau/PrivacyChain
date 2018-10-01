package com.acxiom.utils;

public class KeyNameUtils {
	public static String SPLIT_COMMA = ",";

	public static String getEntityWithConsentId(String entity, String consentId) {
		return "entity_[" + entity + "]_consentId_" + consentId;
	}

	public static String getEntityStartKey(String entity) {
		return "entity_[" + entity + "]_c";
	}

	public static String getEntityEndKey(String entity) {
		return "entity_[" + entity + "]_d";
	}

	public static String getSubscriptionWithEntity(String entity, String subscriptionId) {
		return "entity_[" + entity + "]_subscriptionId_" + subscriptionId;
	}

	public static String getSubscriptionStartKey(String entity) {
		return "entity_[" + entity + "]_s";
	}

	public static String getSubscriptionEndKey(String entity) {
		return "entity_[" + entity + "]_t";
	}

	public static String getSubscriptionWithConsentId(String consentId, String subscriptionId) {
		return "consentId_[" + consentId + "]_subscriptionId_" + subscriptionId;
	}

	public static String getSubAndConStartKey(String consentId) {
		return "consentId_[" + consentId + "]_s";
	}

	public static String getSubAndConEndKey(String consentId) {
		return "consentId_[" + consentId + "]_t";
	}
}
