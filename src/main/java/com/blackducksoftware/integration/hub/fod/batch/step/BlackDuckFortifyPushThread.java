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
import java.util.function.Predicate;

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
import com.blackducksoftware.integration.hub.model.view.MatchedFilesView;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.VersionBomComponentView;
import com.blackducksoftware.integration.hub.model.view.VulnerabilityView;
import com.blackducksoftware.integration.hub.model.view.VulnerabilityWithRemediationView;
import com.blackducksoftware.integration.hub.model.view.VulnerableComponentView;
import com.blackducksoftware.integration.hub.model.view.components.OriginView;
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

    public BlackDuckFortifyPushThread(BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup) {
        this.blackDuckFortifyMapperGroup = blackDuckFortifyMapperGroup;
    }

    @Override
    public Boolean call() throws DateTimeParseException, IntegrationException, IllegalArgumentException,
            FileNotFoundException, UnsupportedEncodingException, IOException {
        logger.info("blackDuckFortifyMapper::" + blackDuckFortifyMapperGroup.toString());
        final List<HubProjectVersion> hubProjectVersions = blackDuckFortifyMapperGroup.getHubProjectVersion();

        // Get the last successful runtime of the job
        final Date getLastSuccessfulJobRunTime = getLastSuccessfulJobRunTime(PropertyConstants.getBatchJobStatusFilePath());

        // Get the project version view from Hub and calculate the max BOM updated date
        final List<ProjectVersionView> projectVersionItems = getProjectVersionItemsAndMaxBomUpdatedDate(hubProjectVersions);
        logger.info("Compare Dates: "
                + ((getLastSuccessfulJobRunTime != null && maxBomUpdatedDate.after(getLastSuccessfulJobRunTime)) || (getLastSuccessfulJobRunTime == null)));
        logger.debug("getLastSuccessfulJobRunTime::" + getLastSuccessfulJobRunTime);
        logger.debug("maxBomUpdatedDate:: " + maxBomUpdatedDate);

        if ((getLastSuccessfulJobRunTime != null && maxBomUpdatedDate.after(getLastSuccessfulJobRunTime)) || (getLastSuccessfulJobRunTime == null)) {
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
            final ProjectVersionView projectVersionItem = HubServices.getProjectVersion(projectName, projectVersion);
            projectVersionItems.add(projectVersionItem);
            Date bomUpdatedValueAt = HubServices.getBomLastUpdatedAt(projectVersionItem);

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
     * @param hubProjectVersions
     * @param projectVersionItems
     * @return
     * @return
     * @throws IntegrationException
     * @throws IllegalArgumentException
     */
    private FortifyUploadRequest mergeVulnerabilities(final List<ProjectVersionView> projectVersionItems, final List<HubProjectVersion> hubProjectVersions)
            throws IllegalArgumentException, IntegrationException {
        int index = 0;
        List<ComponentVersionBom> componentVersionBoms = new ArrayList<>();
        for (ProjectVersionView projectVersionItem : projectVersionItems) {
            final List<VulnerableComponentView> vulnerabilities = HubServices.getVulnerabilityComponentViews(projectVersionItem);
            final Map<String, List<VulnerabilityWithRemediationView>> groupByVulnerabilityComponents = groupByVulnerabilityByComponent(vulnerabilities);

            final List<VersionBomComponentView> allComponents = HubServices.getAggregatedComponentLists(projectVersionItem);

            for (VersionBomComponentView component : allComponents) {
                componentVersionBoms.add(getComponentVersionBom(component, hubProjectVersions.get(index), groupByVulnerabilityComponents));
            }
            index++;
        }

        FortifyUploadRequest fortifyUploadRequest = new FortifyUploadRequest(componentVersionBoms.size(), maxBomUpdatedDate,
                componentVersionBoms);
        return fortifyUploadRequest;
    }

    /**
     * Get the Component version Bom for the given component
     *
     * @param component
     * @param groupByVulnerabilityComponents
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    private ComponentVersionBom getComponentVersionBom(final VersionBomComponentView component, HubProjectVersion hubProjectVersion,
            final Map<String, List<VulnerabilityWithRemediationView>> groupByVulnerabilityComponents)
            throws IllegalArgumentException, IntegrationException {
        List<MatchedFilesView> consolidatedMatchedFiles = new ArrayList<>();
        for (OriginView origin : component.origins) {
            consolidatedMatchedFiles.addAll(HubServices.getMatchedFiles(origin));
        }
        final List<TransformedMatchedFilesView> matchedFiles = transformMatchedFilesView(consolidatedMatchedFiles);

        final List<TransformedOriginView> origins = transformOriginView(component.origins);

        String componentVersionVulnerabilityUrl = HubServices.getComponentVersionVulnerabilityUrl(component.componentVersion);
        final List<VulnerabilityView> vulnerableComponentView = HubServices.getVulnerabilities(componentVersionVulnerabilityUrl);
        final List<TransformedVulnerabilityWithRemediationView> vulnerabilityWithRemediationViews = transformVulnerabilityRemediationView(
                vulnerableComponentView, groupByVulnerabilityComponents, component.componentVersion);

        String[] componentId = component.component.split("/");
        String[] componentVersionId = component.componentVersion.split("/");
        return new ComponentVersionBom(hubProjectVersion.getHubProject(), hubProjectVersion.getHubProjectVersion(), componentId[componentId.length - 1],
                componentVersionId[componentVersionId.length - 1], component.componentName, component.componentVersionName, component.component,
                component.componentVersion, vulnerabilityWithRemediationViews.size(), vulnerabilityWithRemediationViews, matchedFiles.size(), matchedFiles,
                component.licenses, origins, component.usages, component.releasedOn, component.licenseRiskProfile, component.securityRiskProfile,
                component.versionRiskProfile, component.activityRiskProfile, component.operationalRiskProfile, component.activityData, component.reviewStatus,
                component.reviewedDetails, component.approvalStatus);
    }

    /**
     * It will convert Matched Files view to Matched File View
     *
     * @param matchedFilesView
     * @return
     */
    private List<TransformedMatchedFilesView> transformMatchedFilesView(final List<MatchedFilesView> matchedFilesView) {
        List<TransformedMatchedFilesView> transformedMatchedFilesViews = new ArrayList<>();
        matchedFilesView.forEach(matchedFileview -> {
            TransformedMatchedFilesView transformedMatchedFileView = new TransformedMatchedFilesView(matchedFileview.filePath,
                    matchedFileview.usages);
            transformedMatchedFilesViews.add(transformedMatchedFileView);
        });
        return transformedMatchedFilesViews;
    }

    /**
     * It will convert Matched Files view to Matched File View
     *
     * @param matchedFilesView
     * @return
     */
    private static List<TransformedOriginView> transformOriginView(final List<OriginView> originViews) {
        List<TransformedOriginView> transformedOriginViews = new ArrayList<>();
        originViews.forEach(originView -> {
            TransformedOriginView transformedOriginView = new TransformedOriginView(originView.name, originView.externalNamespace,
                    originView.externalId, originView.externalNamespaceDistribution);
            transformedOriginViews.add(transformedOriginView);
        });
        return transformedOriginViews;
    }

    /**
     * It will convert Matched Files view to Matched File View
     *
     * @param matchedFilesView
     * @return
     */
    private List<TransformedVulnerabilityWithRemediationView> transformVulnerabilityRemediationView(
            final List<VulnerabilityView> vulnerabilityViews,
            Map<String, List<VulnerabilityWithRemediationView>> groupByVulnerabilityComponents, String componentVersionLink) {
        List<TransformedVulnerabilityWithRemediationView> transformedVulnerabilityWithRemediationViews = new ArrayList<>();
        List<VulnerabilityWithRemediationView> vulnerabilityWithRemediationViews = groupByVulnerabilityComponents.get(componentVersionLink);
        if (vulnerabilityViews != null) {
            vulnerabilityViews.forEach(vulnerabilityView -> {
                VulnerabilityWithRemediationView vulnerabilityWithRemediationView = getVulnerabilityRemediationView(vulnerabilityWithRemediationViews,
                        vulnerabilityView.vulnerabilityName);
                TransformedVulnerabilityWithRemediationView transformedVulnerabilityWithRemediationView = new TransformedVulnerabilityWithRemediationView(
                        vulnerabilityView.vulnerabilityName, vulnerabilityView.cweId, "", vulnerabilityView.description,
                        vulnerabilityWithRemediationView.vulnerabilityPublishedDate, vulnerabilityWithRemediationView.vulnerabilityUpdatedDate,
                        vulnerabilityView.baseScore, vulnerabilityView.exploitabilitySubscore, vulnerabilityView.impactSubscore,
                        vulnerabilityView.source, vulnerabilityView.severity, vulnerabilityView.accessVector,
                        vulnerabilityView.accessComplexity, vulnerabilityView.authentication, vulnerabilityView.confidentialityImpact,
                        vulnerabilityView.integrityImpact, vulnerabilityView.availabilityImpact, vulnerabilityWithRemediationView.remediationStatus,
                        vulnerabilityWithRemediationView.remediationTargetAt, vulnerabilityWithRemediationView.remediationActualAt,
                        vulnerabilityWithRemediationView.remediationCreatedAt, vulnerabilityWithRemediationView.remediationUpdatedAt,
                        vulnerabilityView.meta.href, "NVD".equalsIgnoreCase(vulnerabilityView.source)
                                ? "http://web.nvd.nist.gov/view/vuln/detail?vulnId=" + vulnerabilityView.vulnerabilityName : "");
                transformedVulnerabilityWithRemediationViews.add(transformedVulnerabilityWithRemediationView);
            });
        }
        return transformedVulnerabilityWithRemediationViews;
    }

    private Map<String, List<VulnerabilityWithRemediationView>> groupByVulnerabilityByComponent(List<VulnerableComponentView> vulnerabilities) {
        Map<String, List<VulnerabilityWithRemediationView>> groupByVulnerabilityComponents = new HashMap<>();
        for (VulnerableComponentView vulnerability : vulnerabilities) {
            String key = vulnerability.componentVersionLink;
            if (groupByVulnerabilityComponents.containsKey(key)) {
                List<VulnerabilityWithRemediationView> vulnerabilityWithRemediationViews = groupByVulnerabilityComponents.get(key);
                vulnerabilityWithRemediationViews.add(vulnerability.vulnerabilityWithRemediation);
            } else {
                List<VulnerabilityWithRemediationView> vulnerabilityWithRemediationViews = new ArrayList<>();
                vulnerabilityWithRemediationViews.add(vulnerability.vulnerabilityWithRemediation);
                groupByVulnerabilityComponents.put(key, vulnerabilityWithRemediationViews);
            }
        }
        return groupByVulnerabilityComponents;
    }

    private VulnerabilityWithRemediationView getVulnerabilityRemediationView(List<VulnerabilityWithRemediationView> vulnerabilities,
            String vulnerabiltyName) {
        Predicate<VulnerabilityWithRemediationView> predicate = c -> c.vulnerabilityName.equalsIgnoreCase(vulnerabiltyName);
        return vulnerabilities.stream().filter(predicate).findFirst().get();
    }
}