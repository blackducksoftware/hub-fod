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
package com.blackducksoftware.integration.hub.fod.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.codelocation.CodeLocationRequestService;
import com.blackducksoftware.integration.hub.api.item.MetaService;
import com.blackducksoftware.integration.hub.api.project.ProjectRequestService;
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.fod.batch.model.CweVulnerabilityView;
import com.blackducksoftware.integration.hub.model.view.CodeLocationView;
import com.blackducksoftware.integration.hub.model.view.ComponentVersionView;
import com.blackducksoftware.integration.hub.model.view.MatchedFilesView;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectView;
import com.blackducksoftware.integration.hub.model.view.RiskProfileView;
import com.blackducksoftware.integration.hub.model.view.VersionBomComponentView;
import com.blackducksoftware.integration.hub.model.view.VulnerabilityView;
import com.blackducksoftware.integration.hub.model.view.VulnerableComponentView;
import com.blackducksoftware.integration.hub.model.view.components.LinkView;
import com.blackducksoftware.integration.hub.model.view.components.OriginView;
import com.blackducksoftware.integration.hub.request.HubPagedRequest;
import com.blackducksoftware.integration.hub.request.HubRequest;
import com.blackducksoftware.integration.hub.service.HubResponseService;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;

/**
 * This class will be used as REST client to access the Hub API's
 *
 * @author smanikantan
 *
 */
public final class HubServices {

    private final static Logger logger = Logger.getLogger(HubServices.class);

    private final HubServicesFactory hubServicesFactory;

    public HubServices(HubServicesFactory hubServicesFactory) {
        this.hubServicesFactory = hubServicesFactory;
    }

    /**
     * Get the Vulnerability component views
     *
     * @param projectVersionItem
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    public List<VulnerableComponentView> getVulnerabilityComponentViews(final ProjectVersionView projectVersionItem)
            throws IllegalArgumentException, IntegrationException {
        logger.info("Getting Vulnerability by components");
        if (projectVersionItem != null) {
            final String vulnerabililtyBomComponentUrl = getVulnerabililtyBomComponentUrl(projectVersionItem);
            return getVulnerabililtyComponentViews(vulnerabililtyBomComponentUrl);
        }
        return new ArrayList<>();
    }

    /**
     * Get the Hub project version information
     *
     * @param projectName
     * @param versionName
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    public ProjectVersionView getProjectVersion(final String projectName, final String versionName) throws IllegalArgumentException, IntegrationException {
        logger.info("Getting Hub project and project version info for::" + projectName + ", " + versionName);
        final ProjectView projectItem = getProjectByProjectName(projectName);
        return getProjectVersion(projectItem, versionName);
    }

    /**
     * Get the Hub Project version information based on project view
     *
     * @param project
     * @return
     * @throws IntegrationException
     */
    public List<ProjectVersionView> getProjectVersionsByProject(final ProjectView project) throws IntegrationException {
        final ProjectVersionRequestService projectVersionRequestService = hubServicesFactory.createProjectVersionRequestService();
        return projectVersionRequestService.getAllProjectVersions(project);
    }

    /**
     * Get the Hub Project information based on input project name
     *
     * @param projectName
     * @return
     * @throws IntegrationException
     */
    public ProjectView getProjectByProjectName(final String projectName) throws IntegrationException {
        logger.info("Getting Hub project info for::" + projectName);
        final ProjectRequestService projectRequestService = hubServicesFactory.createProjectRequestService();
        return projectRequestService.getProjectByName(projectName);
    }

    /**
     * Get the Hub project version view based on Project view and version name
     *
     * @param projectItem
     * @param versionName
     * @return
     * @throws IntegrationException
     */
    private ProjectVersionView getProjectVersion(final ProjectView projectItem, final String versionName) throws IntegrationException {
        logger.info("Getting Hub project version info for::" + versionName);
        final ProjectVersionRequestService projectVersionRequestService = hubServicesFactory.createProjectVersionRequestService();
        return projectVersionRequestService.getProjectVersion(projectItem, versionName);
    }

    /**
     * Get the Hub Vulnerability BOM component Url
     *
     * @param projectVersionItem
     * @return
     * @throws HubIntegrationException
     * @throws IllegalArgumentException
     * @throws EncryptionException
     */
    private String getVulnerabililtyBomComponentUrl(final ProjectVersionView projectVersionItem)
            throws HubIntegrationException, IllegalArgumentException, EncryptionException {
        final MetaService metaService = hubServicesFactory.createMetaService();
        return metaService.getFirstLink(projectVersionItem, MetaService.VULNERABLE_COMPONENTS_LINK);
    }

    /**
     * Get the Hub Vulnerability Component views based on Vulnerability BOM component Url
     *
     * @param vulnerabililtyBomComponentUrl
     * @return
     * @throws IntegrationException
     */
    public List<VulnerableComponentView> getVulnerabililtyComponentViews(final String vulnerabililtyBomComponentUrl) throws IntegrationException {
        logger.info("Getting Hub Vulnerability info");
        final HubResponseService hubResponseService = hubServicesFactory.createHubResponseService();
        HubPagedRequest hubPagedRequest = hubResponseService.getHubRequestFactory().createPagedRequest(500, vulnerabililtyBomComponentUrl);
        return hubResponseService.getAllItems(hubPagedRequest, VulnerableComponentView.class);
    }

    /**
     * Get the Hub Project version risk-profile url
     *
     * @param projectVersionItem
     * @return
     * @throws HubIntegrationException
     * @throws IllegalArgumentException
     * @throws EncryptionException
     */
    private String getProjectVersionRiskProfileUrl(final ProjectVersionView projectVersionItem)
            throws HubIntegrationException, IllegalArgumentException, EncryptionException {
        final MetaService metaService = hubServicesFactory.createMetaService();
        return metaService.getFirstLink(projectVersionItem, MetaService.RISK_PROFILE_LINK);
    }

    /**
     * Get the Hub Project version components url
     *
     * @param projectVersionItem
     * @return
     * @throws HubIntegrationException
     * @throws IllegalArgumentException
     * @throws EncryptionException
     */
    private String getProjectVersionComponentsUrl(final ProjectVersionView projectVersionItem)
            throws HubIntegrationException, IllegalArgumentException, EncryptionException {
        final MetaService metaService = hubServicesFactory.createMetaService();
        return metaService.getFirstLink(projectVersionItem, MetaService.COMPONENTS_LINK);
    }

    /**
     * Get the Hub project version last BOM updated date based on project version risk-profile url
     *
     * @param projectVersionRiskProfileLink
     * @return
     * @throws IntegrationException
     */
    private RiskProfileView getBomLastUpdatedAt(final String projectVersionRiskProfileLink) throws IntegrationException {
        final HubResponseService hubResponseService = hubServicesFactory.createHubResponseService();
        HubRequest hubRequest = hubResponseService.getHubRequestFactory().createRequest(projectVersionRiskProfileLink);
        return hubResponseService.getItem(hubRequest, RiskProfileView.class);
    }

    /**
     * Get the Hub project version last BOM updated date based on Hub project version view
     *
     * @param projectVersionItem
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    public Date getBomLastUpdatedAt(final ProjectVersionView projectVersionItem) throws IllegalArgumentException, IntegrationException {
        logger.info("Getting Hub last BOM updated at");
        if (projectVersionItem != null) {
            final String projectVersionRiskProfileUrl = getProjectVersionRiskProfileUrl(projectVersionItem);
            RiskProfileView riskProfile = getBomLastUpdatedAt(projectVersionRiskProfileUrl);
            return riskProfile.bomLastUpdatedAt;
        }
        return null;
    }

    /**
     * Get the Aggregated Bom Components info corresponding to a project version
     *
     * @param projectVersionItem
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    public List<VersionBomComponentView> getAggregatedComponentLists(final ProjectVersionView projectVersionItem)
            throws IllegalArgumentException, IntegrationException {
        logger.info("Getting Hub Components Info");
        if (projectVersionItem != null) {
            final String componentsUrl = getProjectVersionComponentsUrl(projectVersionItem);
            final HubResponseService hubResponseService = hubServicesFactory.createHubResponseService();
            final HubPagedRequest hubPagedRequest = hubResponseService.getHubRequestFactory().createPagedRequest(componentsUrl);
            return hubResponseService.getAllItems(hubPagedRequest, VersionBomComponentView.class);
        }
        return null;
    }

    /**
     * Get the Matched files component version url
     *
     * @param versionBomComponentView
     * @return
     * @throws HubIntegrationException
     * @throws IllegalArgumentException
     * @throws EncryptionException
     */
    private String getMatchedFilesComponentVersionUrl(final OriginView originView, String linkKey) throws HubIntegrationException {
        List<LinkView> links = originView.meta.links;
        for (LinkView link : links) {
            if (linkKey.equalsIgnoreCase(link.rel)) {
                return link.href;
            }
        }
        return null;
    }

    /**
     * Get Matched files info corresponding to the component version
     *
     * @param projectVersionItem
     * @return
     * @throws IllegalArgumentException
     * @throws IntegrationException
     */
    public List<MatchedFilesView> getMatchedFiles(final OriginView originView) throws IllegalArgumentException, IntegrationException {
        logger.debug("Getting getMatchedFiles");
        if (originView != null) {
            final String matchedFilesComponentVersionUrl = getMatchedFilesComponentVersionUrl(originView, "matched-files");
            final HubResponseService hubResponseService = hubServicesFactory.createHubResponseService();
            return hubResponseService.getAllItems(matchedFilesComponentVersionUrl, MatchedFilesView.class);
        }
        return null;
    }

    /**
     * Based on Component version origin view, get the Component version origin Vulnerability Url
     *
     * @param componentVersionView
     * @return
     * @throws IntegrationException
     */
    public String getComponentVersionOriginVulnerabilityUrl(final ComponentVersionView componentVersionView) throws IntegrationException {
        logger.debug("Getting Component version origin Vulnerability Url");
        final MetaService metaService = hubServicesFactory.createMetaService();
        return metaService.getFirstLink(componentVersionView, MetaService.VULNERABILITIES_LINK);
    }

    /**
     * Based on component version url, get the component version vulnerability url
     *
     * @param componentUrl
     * @return
     * @throws IntegrationException
     */
    public String getComponentVersionVulnerabilityUrl(final String componentUrl) throws IntegrationException {
        logger.debug("Getting Component version Vulnerability Url");
        final HubResponseService hubResponseService = hubServicesFactory.createHubResponseService();
        final HubRequest hubRequest = hubResponseService.getHubRequestFactory().createRequest(componentUrl);
        ComponentVersionView componentVersionView = hubResponseService.getItem(hubRequest, ComponentVersionView.class);
        final MetaService metaService = hubServicesFactory.createMetaService();
        return metaService.getFirstLink(componentVersionView, MetaService.VULNERABILITIES_LINK);
    }

    /**
     * Get the Hub Vulnerability Component views based on Vulnerability BOM component Url
     *
     * @param vulnerabililtyBomComponentUrl
     * @return
     * @throws IntegrationException
     */
    public List<VulnerabilityView> getVulnerabilities(final String vulnerabililtyComponentVersionUrl) throws IntegrationException {
        logger.debug("Getting Hub Vulnerability Bom info");
        final HubResponseService hubResponseService = hubServicesFactory.createHubResponseService();
        HubPagedRequest hubPagedRequest = hubResponseService.getHubRequestFactory().createPagedRequest(500, vulnerabililtyComponentVersionUrl);
        return hubResponseService.getAllItems(hubPagedRequest, VulnerabilityView.class);
    }

    /**
     * Get the Scan Created and Updated date
     *
     * @param projectVersionCodeLocationLink
     * @return
     * @throws IntegrationException
     */
    public List<CodeLocationView> getScanDates(final ProjectVersionView projectVersionItem) throws IntegrationException {
        logger.info("Getting Hub project version Scan dates");
        final CodeLocationRequestService codeLocationRequestService = hubServicesFactory.createCodeLocationRequestService();
        return codeLocationRequestService.getAllCodeLocationsForProjectVersion(projectVersionItem);
    }

    /**
     * Get the Scan Created and Updated date
     *
     * @param projectVersionCodeLocationLink
     * @return
     * @throws IntegrationException
     */
    public String getComponentVersionOriginUrl(final OriginView originView) throws IntegrationException {
        logger.info("Getting Component Version Origin View");
        final MetaService metaService = hubServicesFactory.createMetaService();
        return metaService.getFirstLink(originView, "origin");
    }

    /**
     * Get the Component Version Origin view based on component version origin Url
     *
     * @param componentVersionOriginUrl
     * @return
     * @throws IntegrationException
     */
    public ComponentVersionView getComponentVersionOriginView(final String componentVersionOriginUrl) throws IntegrationException {
        logger.debug("Getting Hub Vulnerability Bom info");
        final HubResponseService hubResponseService = hubServicesFactory.createHubResponseService();
        HubRequest hubRequest = hubResponseService.getHubRequestFactory().createRequest(componentVersionOriginUrl);
        return hubResponseService.getItem(hubRequest, ComponentVersionView.class);
    }

    /**
     * Get the Cwe vulnerability info based on Cwe vulnerability url
     *
     * @param cweVulnerabilityUrl
     * @return
     * @throws IntegrationException
     */
    public CweVulnerabilityView getCweVulnerabilityView(final String cweVulnerabilityUrl) throws IntegrationException {
        logger.debug("Getting Hub Cwe Vulnerability info");
        final HubResponseService hubResponseService = hubServicesFactory.createHubResponseService();
        HubRequest hubRequest = hubResponseService.getHubRequestFactory().createRequest(cweVulnerabilityUrl);
        return hubResponseService.getItem(hubRequest, CweVulnerabilityView.class);
    }
}