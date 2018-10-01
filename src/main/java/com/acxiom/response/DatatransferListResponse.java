package com.acxiom.response;

import java.util.List;

import com.acxiom.bean.Datatransfer;

public class DatatransferListResponse extends BaseResponseBean {
	private List<Datatransfer> datatransfers;

	public List<Datatransfer> getDatatransfers() {
		return datatransfers;
	}

	public void setDatatransfers(List<Datatransfer> datatransfers) {
		this.datatransfers = datatransfers;
	}

}
