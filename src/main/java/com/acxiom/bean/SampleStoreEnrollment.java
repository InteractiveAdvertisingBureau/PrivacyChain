package com.acxiom.bean;

import org.hyperledger.fabric.sdk.Enrollment;

import java.io.Serializable;
import java.security.PrivateKey;

public final class SampleStoreEnrollment implements Enrollment, Serializable {

	private static final long serialVersionUID = 3209821520165040863L;
	private final PrivateKey privateKey;
	private final String certificate;

	public SampleStoreEnrollment(PrivateKey privateKey, String certificate) {

		this.certificate = certificate;

		this.privateKey = privateKey;
	}

	@Override
	public PrivateKey getKey() {

		return privateKey;
	}

	@Override
	public String getCert() {
		return certificate;
	}

}
