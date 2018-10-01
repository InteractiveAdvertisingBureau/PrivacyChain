package com.acxiom.response;

public class ResponseStatus {
	// default
	public static final String[] SUCCESS = { "000", "Success" };
	public static final String[] ERROR = { "201", "System Rrror" };

	// UserController
	public static final String[] LOGIN_FAILED = { "101", "Failed to login user" };
	public static final String[] USER_LIST_FAILED = { "102", "Failed to find the users" };
	public static final String[] REGISTER_FAILED = { "103", "Failed to register user" };

	// DataController
	public static final String[] MISS_THE_KEY = { "104", "must input the consentId" };
	public static final String[] DO_NOT_FIND_DATA = { "105", "don't find the data" };
	public static final String[] MISS_SAR_ID = { "111", "must input the sarId" };
	public static final String[] MISS_ENTITY = { "112", "must input the entity" };
	public static final String[] MISS_OPERATION = { "113", "must input the operation" };
	public static final String[] MISS_OPT_IN = { "114", "must input the Attribute" };

	// CompanyController
	public static final String[] MISS_COMPANY_NAME = { "106", "must input the company name" };
	public static final String[] MISS_UPLOAD_FILE = { "107", "must upload a file" };
	public static final String[] MISS_SEGMENT_NAME = { "108", "must input the segment name" };
	public static final String[] MISS_TARGET_COMPANY_NAME = { "109", "must input the Target Company name" };
	public static final String[] MISS_MATCH_KEY = { "110", "must input the matchKey" };

	// DatatransferController
	public static final String[] MISS_DATATRANSFERID = { "400", "Invalid datatransferId supplied" };
	public static final String[] DATATRANSFER_NOT_FOUND = { "404", "datatransfer not found" };
	public static final String[] INVALID_ID_SUPPLIED = { "400", "Invalid ID supplied" };

	// SubscriptionController
	public static final String[] SUBSCRIPTION_NOT_FOUND = { "404", "Subscription not found" };
	public static final String[] SUBSCRIPTION_EMAIL_ERROR = { "401", "Email has a wrong format" };

}
