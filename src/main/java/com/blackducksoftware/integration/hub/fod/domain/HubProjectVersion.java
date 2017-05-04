package com.blackducksoftware.integration.hub.fod.domain;

import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;

public class HubProjectVersion {
	
	private String distribution;
	private String phase;
	private String releaseComments;
	private String versionName;
	
	public String getDistribution() {
		return distribution;
	}
	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	public String getReleaseComments() {
		return releaseComments==null?"":releaseComments;
	}
	public void setReleaseComments(String releaseComments) {
		this.releaseComments = releaseComments;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	

	public void convertFromProjectView(ProjectVersionView pvv)
	{
		setVersionName(pvv.getVersionName());
		setReleaseComments(pvv.getReleaseComments());
		setDistribution(pvv.getDistribution().name());
		setPhase(pvv.getPhase().name());
		
	}

}
