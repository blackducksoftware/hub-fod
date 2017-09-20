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
package com.blackducksoftware.integration.hub.fod.batch.step;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.fod.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.hub.fod.batch.model.ComponentVersionBom;
import com.blackducksoftware.integration.hub.fod.batch.model.FortifyUploadRequest;
import com.blackducksoftware.integration.hub.fod.batch.model.HubProjectVersion;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedMatchedFilesView;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedOriginView;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedVulnerabilityWithRemediationView;
import com.blackducksoftware.integration.hub.fod.service.HubServices;
import com.blackducksoftware.integration.hub.fod.utils.PropertyConstants;
import com.blackducksoftware.integration.hub.fod.utils.TransformViewsUtil;
import com.blackducksoftware.integration.hub.fod.utils.VulnerabilityUtil;
import com.blackducksoftware.integration.hub.model.view.MatchedFilesView;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.VersionBomComponentView;
import com.blackducksoftware.integration.hub.model.view.VulnerabilityView;
import com.blackducksoftware.integration.hub.model.view.VulnerabilityWithRemediationView;
import com.blackducksoftware.integration.hub.model.view.VulnerableComponentView;
import com.blackducksoftware.integration.hub.model.view.components.OriginView;
import com.blackducksoftware.integration.hub.model.view.components.VersionBomLicenseView;
import com.google.gson.Gson;

/**
 * This class will be used as Thread and it will perform the following tasks in parallel for each Hub-Fortify mapper
 * 1) Get the Hub project version information
 * 2) Get the Maximum BOM updated date and Last successful runtime of the job
 * 3) Compare the dates, if the last BOM updated date is lesser than last successful runtime of the job, do nothing
 * else perform the following the task
 * i) Get the Vulnerabilities and merged it to single list
 * ii) Upload the list to Fortify
 *
 * @author smanikantan
 *
 */
public class BlackDuckFortifyPushThread implements Callable<Boolean> {

    private BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup;

    private Date maxBomUpdatedDate;

    private final static Logger logger = Logger.getLogger(BlackDuckFortifyPushThread.class);

    private final HubServices hubServices;

    private final PropertyConstants propertyConstants;

    public BlackDuckFortifyPushThread(final BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup, final HubServices hubServices,
            PropertyConstants propertyConstants) {
        this.blackDuckFortifyMapperGroup = blackDuckFortifyMapperGroup;
        this.hubServices = hubServices;
        this.propertyConstants = propertyConstants;
    }

    @Override
    public Boolean call() throws DateTimeParseException, IntegrationException, IllegalArgumentException,
            FileNotFoundException, UnsupportedEncodingException, IOException {

        logger.info("blackDuckFortifyMapper::" + blackDuckFortifyMapperGroup.toString());
        final List<HubProjectVersion> hubProjectVersions = blackDuckFortifyMapperGroup.getHubProjectVersion();

        // Get the last successful runtime of the job
        final Date getLastSuccessfulJobRunTime = getLastSuccessfulJobRunTime(propertyConstants.getBatchJobStatusFilePath());

        // Get the project version view from Hub and calculate the max BOM updated date
        final List<ProjectVersionView> projectVersionItems = getProjectVersionItemsAndMaxBomUpdatedDate(hubProjectVersions);
        logger.info("Compare Dates: "
                + ((getLastSuccessfulJobRunTime != null && maxBomUpdatedDate.after(getLastSuccessfulJobRunTime)) || (getLastSuccessfulJobRunTime == null)
                        || (propertyConstants.isBatchJobStatusCheck())));
        logger.debug("getLastSuccessfulJobRunTime::" + getLastSuccessfulJobRunTime);
        logger.debug("maxBomUpdatedDate:: " + maxBomUpdatedDate);

        if ((getLastSuccessfulJobRunTime != null && maxBomUpdatedDate.after(getLastSuccessfulJobRunTime)) || (getLastSuccessfulJobRunTime == null)
                || (!propertyConstants.isBatchJobStatusCheck())) {
            // Get the vulnerabilities for all Hub project versions and merge it
            FortifyUploadRequest fortifyUploadRequest = mergeVulnerabilities(projectVersionItems, hubProjectVersions);
            Gson gson = new Gson();
            // 2. Java object to JSON, and assign to a String
            String jsonInString = gson.toJson(fortifyUploadRequest);
            logger.debug("fortifyUploadRequest::" + jsonInString);
        }
        return true;

    }

    /**
     * Get the last successful job run time of the job by reading the batch_job_status.txt file
     *
     * @param fileName
     * @return
     * @throws IOException
     * @throws DateTimeParseException
     */
    private Date getLastSuccessfulJobRunTime(String fileName) throws IOException, DateTimeParseException {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String sCurrentLine;
            br = new BufferedReader(new FileReader(fileName));
            while ((sCurrentLine = br.readLine()) != null) {
                final LocalDateTime localDateTime = LocalDateTime.parse(sCurrentLine, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS"));
                return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new IOException("Unable to find the batch_job_status.txt file", e);
        } catch (DateTimeParseException e) {
            logger.error(e.getMessage(), e);
            throw new DateTimeParseException("Error while parsing the date. Please make sure date time format is yyyy/MM/dd HH:mm:ss.SSS", e.getParsedString(),
                    e.getErrorIndex(), e);
        } finally {
            if (br != null) {
                br.close();
            }
            if (fr != null) {
                fr.close();
            }
        }
        return null;
    }

    /**
     * Iterate the hub project versions mapper and get the project version view for each item and calculate the max BOM
     * updated date
     *
     * @param hubProjectVersions
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    private List<ProjectVersionView> getProjectVersionItemsAndMaxBomUpdatedDate(final List<HubProjectVersion> hubProjectVersions)
            throws IllegalArgumentException, IntegrationException {
        List<ProjectVersionView> projectVersionItems = new ArrayList<>();
        for (HubProjectVersion hubProjectVersion : hubProjectVersions) {
            String projectName = hubProjectVersion.getHubProject();
            String projectVersion = hubProjectVersion.getHubProjectVersion();

            // Get the project version
            final ProjectVersionView projectVersionItem = hubServices.getProjectVersion(projectName, projectVersion);
            projectVersionItems.add(projectVersionItem);
            Date bomUpdatedValueAt = hubServices.getBomLastUpdatedAt(projectVersionItem);

            if (maxBomUpdatedDate == null || bomUpdatedValueAt.after(maxBomUpdatedDate)) {
                maxBomUpdatedDate = bomUpdatedValueAt;
            }
            logger.debug("bomUpdatedValueAt::" + bomUpdatedValueAt);
        }
        return projectVersionItems;
    }

    /**
     * Iterate the hub project versions and find the vulnerabilities for Hub project version and transform the
     * vulnerability component view to CSV vulnerability view and merge all the vulnerabilities
     *
     * @param projectVersionItems
     * @param hubProjectVersions
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    private FortifyUploadRequest mergeVulnerabilities(final List<ProjectVersionView> projectVersionItems, final List<HubProjectVersion> hubProjectVersions)
            throws IllegalArgumentException, IntegrationException {
        Map<String, ComponentVersionBom> componentVersionBoms = new HashMap<>();
        // For each Hub project version, get the component version bom and add it to the fortify request
        for (ProjectVersionView projectVersionItem : projectVersionItems) {
            logger.info("Getting Hub Aggregated Bom info for project::" + hubProjectVersions.get(0).getHubProject() + ", version::"
                    + hubProjectVersions.get(0).getHubProjectVersion());
            final List<VulnerableComponentView> vulnerabilities = hubServices.getVulnerabilityComponentViews(projectVersionItem);
            final Map<String, List<VulnerabilityWithRemediationView>> groupByVulnerabilityComponents = VulnerabilityUtil
                    .groupByVulnerabilityByComponent(vulnerabilities);

            // Get all the components and its risk information for the project version
            final List<VersionBomComponentView> allComponents = hubServices.getAggregatedComponentLists(projectVersionItem);

            // Remove the duplicate components across the Hub project version.
            for (VersionBomComponentView component : allComponents) {
                ComponentVersionBom componentVersionBom = getComponentVersionBom(component, groupByVulnerabilityComponents);
                if (hubProjectVersions.size() > 1 && componentVersionBoms.containsKey(component.componentVersion)) {
                    componentVersionBom = new ComponentVersionBom(componentVersionBom.getComponentName(),
                            componentVersionBom.getComponentVersionName(), componentVersionBom.getComponentUrl(), componentVersionBom.getComponentVersionUrl(),
                            componentVersionBom.getTotalVulnerabilities(), componentVersionBom.getVulnerabilities(),
                            componentVersionBom.getTotalMatchedFilesCount(), componentVersionBom.getMatchedFiles(), componentVersionBom.getLicenses(),
                            componentVersionBom.getOrigins(), componentVersionBom.getUsages(), componentVersionBom.getReleasedOn(),
                            componentVersionBom.getLicenseRiskProfile(), componentVersionBom.getSecurityRiskProfile(),
                            componentVersionBom.getVersionRiskProfile(), componentVersionBom.getActivityRiskProfile(),
                            componentVersionBom.getOperationalRiskProfile(), componentVersionBom.getActivityData(), componentVersionBom.getReviewStatus(),
                            componentVersionBom.getReviewedDetails(), componentVersionBom.getApprovalStatus());
                }
                componentVersionBoms.put(component.componentVersion, componentVersionBom);
            }
        }

        // Return the Fortify upload request
        return new FortifyUploadRequest(componentVersionBoms.size(), propertyConstants.getHubServerUrl(), hubProjectVersions.get(0).getHubProject(),
                hubProjectVersions.get(0).getHubProjectVersion(), projectVersionItems.get(0).meta.href.replaceAll(propertyConstants.getHubServerUrl(), ""),
                maxBomUpdatedDate, new ArrayList<>(componentVersionBoms.values()));
    }

    /**
     * Get the Component version Bom for the given component
     *
     * @param component
     * @param hubProjectVersion
     * @param groupByVulnerabilityComponents
     * @param projectReleaseUrl
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    private ComponentVersionBom getComponentVersionBom(final VersionBomComponentView component,
            final Map<String, List<VulnerabilityWithRemediationView>> groupByVulnerabilityComponents)
            throws IllegalArgumentException, IntegrationException {
        logger.debug("Getting Component Version Bom for component::" + component.componentName + ", version::" + component.componentVersionName);
        List<MatchedFilesView> consolidatedMatchedFiles = new ArrayList<>();
        // For each origin, get the matched files view
        for (OriginView origin : component.origins) {
            consolidatedMatchedFiles.addAll(hubServices.getMatchedFiles(origin));
        }
        final List<TransformedMatchedFilesView> matchedFiles = TransformViewsUtil.transformMatchedFilesView(consolidatedMatchedFiles);

        final List<TransformedOriginView> origins = TransformViewsUtil.transformOriginView(component.origins, propertyConstants);

        // Get the Component Version Vulnerability url for the component
        String componentVersionVulnerabilityUrl = hubServices.getComponentVersionVulnerabilityUrl(component.componentVersion);

        // Get the Vulnerabilities for the component
        final List<VulnerabilityView> vulnerableComponentView = hubServices.getVulnerabilities(componentVersionVulnerabilityUrl);
        final List<TransformedVulnerabilityWithRemediationView> vulnerabilityWithRemediationViews = TransformViewsUtil.transformVulnerabilityRemediationView(
                vulnerableComponentView, groupByVulnerabilityComponents, component.componentVersion);

        // return the component version bom
        return new ComponentVersionBom(component.componentName, component.componentVersionName, component.component, component.componentVersion,
                vulnerabilityWithRemediationViews.size(), vulnerabilityWithRemediationViews, matchedFiles.size(), matchedFiles,
                removeHubServerUrlFromLicense(component.licenses), origins, component.usages, component.releasedOn, component.licenseRiskProfile,
                component.securityRiskProfile, component.versionRiskProfile, component.activityRiskProfile, component.operationalRiskProfile,
                component.activityData, component.reviewStatus, component.reviewedDetails, component.approvalStatus);
    }

    /**
     * Replace the Hub Server Url in License with none
     *
     * @param licenses
     * @return
     */
    private List<VersionBomLicenseView> removeHubServerUrlFromLicense(List<VersionBomLicenseView> licenses) {
        // Replace the Hub Server Url in License with none
        for (int i = 0; licenses != null && i < licenses.size(); i++) {
            for (int j = 0; licenses.get(i).licenses != null && j < licenses.get(i).licenses.size(); j++) {
                if (licenses.get(i).licenses.get(j).license != null) {
                    licenses.get(i).licenses.get(j).license = licenses.get(i).licenses.get(j).license.replaceAll(propertyConstants.getHubServerUrl(), "");
                }
            }
        }

        return licenses;
    }
}
