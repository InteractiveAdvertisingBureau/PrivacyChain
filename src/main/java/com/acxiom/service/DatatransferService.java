package com.acxiom.service;

import java.util.List;

import com.acxiom.bean.Datatransfer;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.DatatransferListResponse;
import com.acxiom.response.DatatransferResponse;
import com.acxiom.response.IdResponse;
import com.acxiom.response.IdsResponse;

public interface DatatransferService {

	IdResponse createDatatransfer(Datatransfer data);

	BaseResponseBean updateDatatransfer(Datatransfer data);

	IdsResponse createWithArray(List<Datatransfer> data);

	DatatransferListResponse findByConsentID(String consentID);

	DatatransferResponse getDatatransferById(String datatransferId);

}
