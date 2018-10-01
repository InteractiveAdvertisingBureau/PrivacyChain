package com.acxiom.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseResponseBean {

	private static final Logger log = LoggerFactory.getLogger(BaseResponseBean.class);

	private String returnCode;
	private String returnMessage;

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	public void setStatus(String[] status_arr) {
		if (status_arr.length != 2) {
			log.error("Array length !=2");
			return;
		}
		returnCode = status_arr[0];
		returnMessage = status_arr[1];

	}
}
