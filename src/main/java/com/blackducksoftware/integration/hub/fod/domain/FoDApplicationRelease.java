package com.blackducksoftware.integration.hub.fod.domain;

import java.io.Serializable;

public class FoDApplicationRelease implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String releaseId;
	private String releaseName;
	
	public String getReleaseId() {
		return releaseId;
	}
	public void setReleaseId(String releaseId) {
		this.releaseId = releaseId;
	}
	public String getReleaseName() {
		return releaseName;
	}
	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}
	
	

}
