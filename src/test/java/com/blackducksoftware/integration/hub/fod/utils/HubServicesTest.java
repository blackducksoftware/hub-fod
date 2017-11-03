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
package com.blackducksoftware.integration.hub.fod.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.fod.batch.TestApplication;
import com.blackducksoftware.integration.hub.fod.batch.job.BlackDuckFortifyJobConfig;
import com.blackducksoftware.integration.hub.fod.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.hub.fod.batch.model.ComponentVersionOriginBom;
import com.blackducksoftware.integration.hub.fod.batch.model.FortifyUploadRequest;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedMatchedFilesView;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedVulnerabilityWithRemediationView;
import com.blackducksoftware.integration.hub.fod.batch.model.VulnerabilityView;
import com.blackducksoftware.integration.hub.fod.service.HubServices;
import com.blackducksoftware.integration.hub.model.view.ComponentVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectView;
import com.blackducksoftware.integration.hub.model.view.VersionBomComponentView;
import com.blackducksoftware.integration.hub.model.view.VulnerableComponentView;
import com.blackducksoftware.integration.hub.model.view.components.OriginView;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class HubServicesTest extends TestCase {
    private String PROJECT_NAME;

    private String VERSION_NAME;

    private BlackDuckFortifyJobConfig blackDuckFortifyJobConfig;

    private HubServices hubServices;

    @Override
    @Before
    public void setUp() throws JsonIOException, IOException, IntegrationException {
        blackDuckFortifyJobConfig = new BlackDuckFortifyJobConfig();
        final List<BlackDuckFortifyMapperGroup> blackDuckFortifyMappers = blackDuckFortifyJobConfig.getMappingParser()
                .createMapping(PropertyConstants.getMappingJsonPath());
        PROJECT_NAME = blackDuckFortifyMappers.get(0).getHubProjectVersion().get(0).getHubProject();
        VERSION_NAME = blackDuckFortifyMappers.get(0).getHubProjectVersion().get(0).getHubProjectVersion();
        // PROJECT_NAME = "struts2-bom";
        // VERSION_NAME = "2.5.3";
        hubServices = blackDuckFortifyJobConfig.getHubServices();
    }

    @Test
    public void getProjectVersionsByProject() {
        System.out.println("Executing getProjectVersionsByProject");
        ProjectView project = null;
        List<ProjectVersionView> projectVersionViews = new ArrayList<>();
        try {
            project = hubServices.getProjectByProjectName(PROJECT_NAME);
            projectVersionViews = hubServices.getProjectVersionsByProject(project);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
        }
        assertTrue(!projectVersionViews.isEmpty());
    }

    @Test
    public void getProjectVersion() {
        System.out.println("Executing getProjectVersion");
        ProjectVersionView projectVersionItem = null;
        try {
            projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, VERSION_NAME);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
        }
        assertNotNull(projectVersionItem);
    }

    @Test
    public void getProjectVersionWithInvalidProjectName() {
        System.out.println("Executing getProjectVersionWithInvalidProjectName");
        ProjectVersionView projectVersionItem = null;
        try {
            projectVersionItem = hubServices.getProjectVersion("Solr1", VERSION_NAME);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            // e.printStackTrace();
            System.out.println("Error message::" + e.getMessage());
            assertTrue(e.getMessage().contains("This Project does not exist"));
        }
        assertNull(projectVersionItem);
    }

    @Test
    public void getProjectVersionWithInvalidVersionName() {
        System.out.println("Executing getProjectVersionWithInvalidVersionName");
        ProjectVersionView projectVersionItem = null;
        try {
            projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, "3.10");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            // e.printStackTrace();
            System.out.println("Error message::" + e.getMessage());
            assertTrue(e.getMessage().contains("Could not find the version"));
        }
        assertNull(projectVersionItem);
    }

    @Test
    public void getVulnerability() throws Exception {
        System.out.println("Executing getVulnerability");
        ProjectVersionView projectVersionItem = null;
        try {
            projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, VERSION_NAME);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
        }
        List<VulnerableComponentView> vulnerableComponentViews = hubServices.getVulnerabilityComponentViews(projectVersionItem);
        assertNotNull(vulnerableComponentViews);
    }

    @Test
    public void getBomLastUpdatedAt() throws IllegalArgumentException, IntegrationException {
        System.out.println("Executing getBomLastUpdatedAt");
        ProjectVersionView projectVersionItem = null;
        try {
            projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, VERSION_NAME);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntegrationException e) {
            e.printStackTrace();
        }
        Date bomLastUpdatedAt = hubServices.getBomLastUpdatedAt(projectVersionItem);
        System.out.println("bomLastUpdatedAt::" + bomLastUpdatedAt);
        assertNotNull(bomLastUpdatedAt);
    }

    @Test
    public void getAggregatedComponentInfo() throws IllegalArgumentException, IntegrationException {
        System.out.println("Executing getAggregatedComponentInfo");
        final ProjectVersionView projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, VERSION_NAME);

        System.out.println("projectReleaseUrl::" + projectVersionItem.meta.href);

        // Get the last bom updated value
        final Date bomUpdatedValueAt = hubServices.getBomLastUpdatedAt(projectVersionItem);

        // Get the component version bom information for the given project version
        final List<VersionBomComponentView> allComponents = hubServices.getAggregatedComponentLists(projectVersionItem);
        System.out.println("allComponents::" + allComponents.get(0));
        assertNotNull(allComponents);

        List<ComponentVersionOriginBom> componentVersionOriginBoms = new ArrayList<>();
        for (VersionBomComponentView component : allComponents) {
            for (OriginView origin : component.origins) {
                // Get the matched file for the component version origin
                final List<TransformedMatchedFilesView> matchedFiles = TransformViewsUtil
                        .transformMatchedFilesView(hubServices.getMatchedFiles(origin));

                // Get the component version origin information
                final String componentVersionOriginUrl = hubServices.getComponentVersionOriginUrl(origin);
                ComponentVersionView componentVersionOriginView = hubServices
                        .getComponentVersionOriginView(componentVersionOriginUrl);

                // Get the component version origin vulnerability information
                final List<VulnerabilityView> vulnerableComponentView = hubServices
                        .getVulnerabilities(hubServices.getComponentVersionOriginVulnerabilityUrl(componentVersionOriginView));

                List<TransformedVulnerabilityWithRemediationView> vulnerabilityWithRemediationViews = TransformViewsUtil.transformVulnerabilityRemediationView(
                        vulnerableComponentView, hubServices);

                if (componentVersionOriginView.license != null) {
                    for (int i = 0; componentVersionOriginView.license.licenses != null && i < componentVersionOriginView.license.licenses.size(); i++) {
                        if (componentVersionOriginView.license.licenses.get(i).license != null)
                            componentVersionOriginView.license.licenses.get(i).license = componentVersionOriginView.license.licenses.get(i).license
                                    .replaceAll(PropertyConstants.getHubServerUrl(), "");
                    }
                }

                componentVersionOriginBoms.add(
                        new ComponentVersionOriginBom(component.componentName, component.componentVersionName,
                                component.component.replaceAll(PropertyConstants.getHubServerUrl(), ""),
                                component.componentVersion.replaceAll(PropertyConstants.getHubServerUrl(), ""), origin.name, origin.externalNamespace,
                                origin.externalId, componentVersionOriginUrl.replaceAll(PropertyConstants.getHubServerUrl(), ""),
                                vulnerabilityWithRemediationViews.size(), vulnerabilityWithRemediationViews, matchedFiles.size(), matchedFiles,
                                componentVersionOriginView.license, component.usages, component.releasedOn, component.licenseRiskProfile,
                                component.securityRiskProfile, component.versionRiskProfile, component.activityRiskProfile, component.operationalRiskProfile,
                                component.activityData, component.reviewStatus, component.reviewedDetails, component.approvalStatus));
            }
        }

        FortifyUploadRequest fortifyUploadRequest = new FortifyUploadRequest(componentVersionOriginBoms.size(), PropertyConstants.getHubServerUrl(),
                PROJECT_NAME, VERSION_NAME, projectVersionItem.meta.href.replaceAll(PropertyConstants.getHubServerUrl(), ""), bomUpdatedValueAt,
                componentVersionOriginBoms);

        // Java object to JSON, and assign to a String
        Gson gson = new Gson();
        String jsonInString = gson.toJson(fortifyUploadRequest);
        System.out.println("fortifyUploadRequest::" + jsonInString);
        assertNotNull(jsonInString);
    }
}
