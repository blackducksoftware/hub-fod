/**
 * hub-fod
 *
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.
 *
 * The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.fod.batch.model;

import java.util.Date;
import java.util.List;

import com.blackducksoftware.integration.hub.model.enumeration.MatchedFileUsageEnum;
import com.blackducksoftware.integration.hub.model.enumeration.ReviewStatusEnum;
import com.blackducksoftware.integration.hub.model.view.components.ActivityDataView;
import com.blackducksoftware.integration.hub.model.view.components.ReviewedDetailsView;
import com.blackducksoftware.integration.hub.model.view.components.RiskProfileView;
import com.blackducksoftware.integration.hub.model.view.components.VersionBomLicenseView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * This class will be used to store the component version bom details
 *
 * @author smanikantan
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "projectName",
        "projectVersionName",
        "projectVersionUrl",
        "componentID",
        "componentVersionID",
        "componentName",
        "componentVersionName",
        "component",
        "componentVersion",
        "totalVulnerabilities",
        "vulnerabilities",
        "totalMatchedFilesCount",
        "matchedFiles",
        "licenses",
        "origins",
        "usages",
        "releasedOn",
        "licenseRiskProfile",
        "securityRiskProfile",
        "versionRiskProfile",
        "activityRiskProfile",
        "operationalRiskProfile",
        "activityData",
        "reviewStatus",
        "reviewedDetails",
        "approvalStatus"
})
public class ComponentVersionBom {

    @JsonProperty("projectName")
    private final String projectName;

    @JsonProperty("projectVersionName")
    private final String projectVersionName;

    @JsonProperty("projectVersionUrl")
    private final String projectVersionUrl;

    @JsonProperty("componentID")
    private final String componentID;

    @JsonProperty("componentVersionID")
    private final String componentVersionID;

    @JsonProperty("componentName")
    private final String componentName;

    @JsonProperty("componentVersionName")
    private final String componentVersionName;

    @JsonProperty("component")
    private final String component;

    @JsonProperty("componentVersion")
    private final String componentVersion;

    @JsonProperty("totalVulnerabilities")
    private final int totalVulnerabilities;

    @JsonProperty("vulnerabilities")
    private final List<TransformedVulnerabilityWithRemediationView> vulnerabilities;

    @JsonProperty("totalMatchedFilesCount")
    private final int totalMatchedFilesCount;

    @JsonProperty("matchedFiles")
    private final List<TransformedMatchedFilesView> matchedFiles;

    @JsonProperty("licenses")
    private final List<VersionBomLicenseView> licenses;

    @JsonProperty("origins")
    private final List<TransformedOriginView> origins;

    @JsonProperty("usages")
    private final List<MatchedFileUsageEnum> usages;

    @JsonProperty("releasedOn")
    private final Date releasedOn;

    @JsonProperty("licenseRiskProfile")
    private final RiskProfileView licenseRiskProfile;

    @JsonProperty("securityRiskProfile")
    private final RiskProfileView securityRiskProfile;

    @JsonProperty("versionRiskProfile")
    private final RiskProfileView versionRiskProfile;

    @JsonProperty("activityRiskProfile")
    private final RiskProfileView activityRiskProfile;

    @JsonProperty("operationalRiskProfile")
    private final RiskProfileView operationalRiskProfile;

    @JsonProperty("activityData")
    private final ActivityDataView activityData;

    @JsonProperty("reviewStatus")
    private final ReviewStatusEnum reviewStatus;

    @JsonProperty("reviewedDetails")
    private final ReviewedDetailsView reviewedDetails;

    @JsonProperty("approvalStatus")
    private final String approvalStatus;

    public ComponentVersionBom(String projectName, String projectVersionName, String hubProjectVersionUrl, String componentID, String componentVersionID,
            String componentName, String componentVersionName, String component, String componentVersion, int totalVulnerabilities,
            List<TransformedVulnerabilityWithRemediationView> vulnerabilities, int totalMatchedFilesCount, List<TransformedMatchedFilesView> matchedFiles,
            List<VersionBomLicenseView> licenses, List<TransformedOriginView> origins, List<MatchedFileUsageEnum> usages, Date releasedOn,
            RiskProfileView licenseRiskProfile, RiskProfileView securityRiskProfile, RiskProfileView versionRiskProfile, RiskProfileView activityRiskProfile,
            RiskProfileView operationalRiskProfile, ActivityDataView activityData, ReviewStatusEnum reviewStatus, ReviewedDetailsView reviewedDetails,
            String approvalStatus) {
        this.projectName = projectName;
        this.projectVersionName = projectVersionName;
        this.projectVersionUrl = hubProjectVersionUrl;
        this.componentID = componentID;
        this.componentVersionID = componentVersionID;
        this.componentName = componentName;
        this.componentVersionName = componentVersionName;
        this.component = component;
        this.componentVersion = componentVersion;
        this.totalVulnerabilities = totalVulnerabilities;
        this.vulnerabilities = vulnerabilities;
        this.totalMatchedFilesCount = totalMatchedFilesCount;
        this.matchedFiles = matchedFiles;
        this.licenses = licenses;
        this.origins = origins;
        this.usages = usages;
        this.releasedOn = releasedOn;
        this.licenseRiskProfile = licenseRiskProfile;
        this.securityRiskProfile = securityRiskProfile;
        this.versionRiskProfile = versionRiskProfile;
        this.activityRiskProfile = activityRiskProfile;
        this.operationalRiskProfile = operationalRiskProfile;
        this.activityData = activityData;
        this.reviewStatus = reviewStatus;
        this.reviewedDetails = reviewedDetails;
        this.approvalStatus = approvalStatus;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersionName() {
        return projectVersionName;
    }

    public String getHubProjectVersionUrl() {
        return projectVersionUrl;
    }

    public String getComponentID() {
        return componentID;
    }

    public String getComponentVersionID() {
        return componentVersionID;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getComponentVersionName() {
        return componentVersionName;
    }

    public String getComponent() {
        return component;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public int getTotalVulnerabilities() {
        return totalVulnerabilities;
    }

    public List<TransformedVulnerabilityWithRemediationView> getVulnerabilities() {
        return vulnerabilities;
    }

    public int getTotalMatchedFilesCount() {
        return totalMatchedFilesCount;
    }

    public List<TransformedMatchedFilesView> getMatchedFiles() {
        return matchedFiles;
    }

    public List<VersionBomLicenseView> getLicenses() {
        return licenses;
    }

    public List<TransformedOriginView> getOrigins() {
        return origins;
    }

    public List<MatchedFileUsageEnum> getUsages() {
        return usages;
    }

    public Date getReleasedOn() {
        return releasedOn;
    }

    public RiskProfileView getLicenseRiskProfile() {
        return licenseRiskProfile;
    }

    public RiskProfileView getSecurityRiskProfile() {
        return securityRiskProfile;
    }

    public RiskProfileView getVersionRiskProfile() {
        return versionRiskProfile;
    }

    public RiskProfileView getActivityRiskProfile() {
        return activityRiskProfile;
    }

    public RiskProfileView getOperationalRiskProfile() {
        return operationalRiskProfile;
    }

    public ActivityDataView getActivityData() {
        return activityData;
    }

    public ReviewStatusEnum getReviewStatus() {
        return reviewStatus;
    }

    public ReviewedDetailsView getReviewedDetails() {
        return reviewedDetails;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    @Override
    public String toString() {
        return "ComponentVersionBom [projectName=" + projectName + ", projectVersionName=" + projectVersionName + ", hubProjectVersionUrl="
                + projectVersionUrl + ", componentID=" + componentID + ", componentVersionID=" + componentVersionID + ", componentName=" + componentName
                + ", componentVersionName=" + componentVersionName + ", component=" + component + ", componentVersion=" + componentVersion
                + ", totalVulnerabilities=" + totalVulnerabilities + ", vulnerabilities=" + vulnerabilities + ", totalMatchedFilesCount="
                + totalMatchedFilesCount + ", matchedFiles=" + matchedFiles + ", licenses=" + licenses + ", origins=" + origins + ", usages=" + usages
                + ", releasedOn=" + releasedOn + ", licenseRiskProfile=" + licenseRiskProfile + ", securityRiskProfile=" + securityRiskProfile
                + ", versionRiskProfile=" + versionRiskProfile + ", activityRiskProfile=" + activityRiskProfile + ", operationalRiskProfile="
                + operationalRiskProfile + ", activityData=" + activityData + ", reviewStatus=" + reviewStatus + ", reviewedDetails=" + reviewedDetails
                + ", approvalStatus=" + approvalStatus + "]";
    }

}
