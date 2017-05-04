package com.blackducksoftware.integration.hub.fod.domain;

import java.io.Serializable;

public class FoDApplication implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String applicationId;
	
	private String applicationName;

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	

}
