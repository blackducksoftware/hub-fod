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
import com.blackducksoftware.integration.hub.model.view.ComplexLicenseView;
import com.blackducksoftware.integration.hub.model.view.components.ActivityDataView;
import com.blackducksoftware.integration.hub.model.view.components.ReviewedDetailsView;
import com.blackducksoftware.integration.hub.model.view.components.RiskProfileView;
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
        "componentName",
        "componentVersionName",
        "componentUrl",
        "componentVersionUrl",
        "originName",
        "externalNamespace",
        "externalId",
        //"externalNamespaceDistribution",
        "componentVersionOriginUrl",
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
public class ComponentVersionOriginBom {

    @JsonProperty("componentName")
    private final String componentName;

    @JsonProperty("componentVersionName")
    private final String componentVersionName;

    @JsonProperty("componentUrl")
    private final String componentUrl;

    @JsonProperty("componentVersionUrl")
    private final String componentVersionUrl;

    @JsonProperty("originName")
    private final String originName;

    @JsonProperty("externalNamespace")
    private final String externalNamespace;

    @JsonProperty("externalId")
    private final String externalId;

    /*@JsonProperty("externalNamespaceDistribution")
    private final boolean externalNamespaceDistribution;*/

    @JsonProperty("componentVersionOriginUrl")
    private final String componentVersionOriginUrl;

    @JsonProperty("totalVulnerabilities")
    private final int totalVulnerabilities;

    @JsonProperty("vulnerabilities")
    private final List<TransformedVulnerabilityWithRemediationView> vulnerabilities;

    @JsonProperty("totalMatchedFilesCount")
    private final int totalMatchedFilesCount;

    @JsonProperty("matchedFiles")
    private final List<TransformedMatchedFilesView> matchedFiles;

    @JsonProperty("licenses")
    private final ComplexLicenseView licenses;

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

    public ComponentVersionOriginBom(String componentName, String componentVersionName, String componentUrl, String componentVersionUrl, String originName,
            String externalNamespace, String externalId, /*boolean externalNamespaceDistribution,*/ String componentVersionOriginUrl, int totalVulnerabilities,
            List<TransformedVulnerabilityWithRemediationView> vulnerabilities, int totalMatchedFilesCount, List<TransformedMatchedFilesView> matchedFiles,
            ComplexLicenseView licenses, List<MatchedFileUsageEnum> usages, Date releasedOn, RiskProfileView licenseRiskProfile,
            RiskProfileView securityRiskProfile, RiskProfileView versionRiskProfile, RiskProfileView activityRiskProfile,
            RiskProfileView operationalRiskProfile, ActivityDataView activityData, ReviewStatusEnum reviewStatus, ReviewedDetailsView reviewedDetails,
            String approvalStatus) {
        this.componentName = componentName;
        this.componentVersionName = componentVersionName;
        this.componentUrl = componentUrl;
        this.componentVersionUrl = componentVersionUrl;
        this.originName = originName;
        this.externalNamespace = externalNamespace;
        this.externalId = externalId;
        //this.externalNamespaceDistribution = externalNamespaceDistribution;
        this.componentVersionOriginUrl = componentVersionOriginUrl;
        this.totalVulnerabilities = totalVulnerabilities;
        this.vulnerabilities = vulnerabilities;
        this.totalMatchedFilesCount = totalMatchedFilesCount;
        this.matchedFiles = matchedFiles;
        this.licenses = licenses;
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

    public String getComponentName() {
        return componentName;
    }

    public String getComponentVersionName() {
        return componentVersionName;
    }

    public String getComponentUrl() {
        return componentUrl;
    }

    public String getComponentVersionUrl() {
        return componentVersionUrl;
    }

    public String getOriginName() {
        return originName;
    }

    public String getExternalNamespace() {
        return externalNamespace;
    }

    public String getExternalId() {
        return externalId;
    }

    /*public boolean getExternalNamespaceDistribution() {
        return externalNamespaceDistribution;
    }
*/
    public String getComponentVersionOriginUrl() {
        return componentVersionOriginUrl;
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

    public ComplexLicenseView getLicenses() {
        return licenses;
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
        return "ComponentVersionOriginBom [componentName=" + componentName + ", componentVersionName=" + componentVersionName + ", componentUrl=" + componentUrl
                + ", componentVersionUrl=" + componentVersionUrl + ", originName=" + originName + ", externalNamespace=" + externalNamespace + ", externalId="
                + externalId + ", componentVersionOriginUrl=" + componentVersionOriginUrl + ", totalVulnerabilities=" + totalVulnerabilities
                + ", vulnerabilities=" + vulnerabilities + ", totalMatchedFilesCount=" + totalMatchedFilesCount + ", matchedFiles=" + matchedFiles
                + ", licenses=" + licenses + ", usages=" + usages + ", releasedOn=" + releasedOn + ", licenseRiskProfile=" + licenseRiskProfile
                + ", securityRiskProfile=" + securityRiskProfile + ", versionRiskProfile=" + versionRiskProfile + ", activityRiskProfile=" + activityRiskProfile
                + ", operationalRiskProfile=" + operationalRiskProfile + ", activityData=" + activityData + ", reviewStatus=" + reviewStatus
                + ", reviewedDetails=" + reviewedDetails + ", approvalStatus=" + approvalStatus + "]";
    }

}
