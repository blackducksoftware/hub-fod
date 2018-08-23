package com.blackducksoftware.integration.hub.fod.utils;

import java.text.SimpleDateFormat;

import org.springframework.util.StringUtils;

import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectView;
import com.blackducksoftware.integration.hub.fod.common.VulnerabilityReportConstants;
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder;
import com.blackducksoftware.integration.rest.RestConstants;

public class HubFodProjectVersionRequestBuilder extends ProjectRequestBuilder{
	
	public HubFodProjectVersionRequestBuilder(final ProjectView projectView, ProjectVersionView projectVersionView, String fodAppRelease)
	{
		
		setProjectName(projectView.name);
		setDescription(projectView.description);
		setProjectLevelAdjustments(projectView.projectLevelAdjustments);
		setProjectOwner(projectView.projectOwner);
		setProjectTier(projectView.projectTier);
		
		setVersionName(projectVersionView.versionName);
		setDistribution(projectVersionView.distribution);
		setPhase(projectVersionView.phase);
		setVersionNickname(projectVersionView.nickname);
		final SimpleDateFormat sdf = new SimpleDateFormat(RestConstants.JSON_DATE_FORMAT);
        String releasedOn = (projectVersionView.releasedOn==null)?"":sdf.format(projectVersionView.releasedOn);
		setReleasedOn(releasedOn);
		
		final StringBuilder sb = new StringBuilder(StringUtils.isEmpty(projectVersionView.releaseComments)?"":projectVersionView.releaseComments);
        sb.append("(" + VulnerabilityReportConstants.HUB_NOTES_FOD_LABEL + fodAppRelease + ")");
        projectVersionView.releaseComments = sb.toString();
     
        setReleaseComments(projectVersionView.releaseComments);
	}

}
