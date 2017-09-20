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
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.fod.batch.BatchSchedulerConfig;
import com.blackducksoftware.integration.hub.fod.batch.TestApplication;
import com.blackducksoftware.integration.hub.fod.batch.job.BlackDuckFortifyJobConfig;
import com.blackducksoftware.integration.hub.fod.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.hub.fod.batch.model.ComponentVersionBom;
import com.blackducksoftware.integration.hub.fod.batch.model.FortifyUploadRequest;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedMatchedFilesView;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedOriginView;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedVulnerabilityWithRemediationView;
import com.blackducksoftware.integration.hub.fod.service.HubServices;
import com.blackducksoftware.integration.hub.model.view.MatchedFilesView;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectView;
import com.blackducksoftware.integration.hub.model.view.VersionBomComponentView;
import com.blackducksoftware.integration.hub.model.view.VulnerabilityView;
import com.blackducksoftware.integration.hub.model.view.VulnerabilityWithRemediationView;
import com.blackducksoftware.integration.hub.model.view.VulnerableComponentView;
import com.blackducksoftware.integration.hub.model.view.components.OriginView;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
@ContextConfiguration(classes = { BlackDuckFortifyJobConfig.class, BatchSchedulerConfig.class, PropertyConstants.class })
public class HubServicesTest extends TestCase {
    private String PROJECT_NAME;

    private String VERSION_NAME;

    @Autowired
    private HubServices hubServices;

    @Autowired
    private MappingParser mappingParser;

    @Autowired
    private PropertyConstants propertyConstants;

    @Override
    @Before
    public void setUp() throws JsonIOException, IOException, IntegrationException {
        final List<BlackDuckFortifyMapperGroup> blackDuckFortifyMappers = mappingParser
                .createMapping(propertyConstants.getMappingJsonPath());
        PROJECT_NAME = blackDuckFortifyMappers.get(0).getHubProjectVersion().get(0).getHubProject();
        VERSION_NAME = blackDuckFortifyMappers.get(0).getHubProjectVersion().get(0).getHubProjectVersion();
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
        System.out.println("vulnerableComponentViews size::" + vulnerableComponentViews.size() + ", vulnerableComponentViews::" + vulnerableComponentViews);
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
        ProjectVersionView projectVersionItem = null;
        projectVersionItem = hubServices.getProjectVersion(PROJECT_NAME, VERSION_NAME);

        System.out.println("projectReleaseUrl::" + projectVersionItem.meta.href);

        Date bomUpdatedValueAt = hubServices.getBomLastUpdatedAt(projectVersionItem);

        List<VulnerableComponentView> vulnerabilities = hubServices.getVulnerabilityComponentViews(projectVersionItem);
        Map<String, List<VulnerabilityWithRemediationView>> groupByVulnerabilityComponents = VulnerabilityUtil.groupByVulnerabilityByComponent(vulnerabilities);

        List<VersionBomComponentView> allComponents = hubServices.getAggregatedComponentLists(projectVersionItem);
        System.out.println("allComponents::" + allComponents.get(0));
        assertNotNull(allComponents);

        List<ComponentVersionBom> componentVersionBoms = new ArrayList<>();
        for (VersionBomComponentView component : allComponents) {
            List<MatchedFilesView> consolidatedMatchedFiles = new ArrayList<>();
            for (OriginView origin : component.origins) {
                // System.out.println("origin::" + origin);
                consolidatedMatchedFiles.addAll(hubServices.getMatchedFiles(origin));
            }
            // System.out.println("consolidatedMatchedFiles::" + consolidatedMatchedFiles);
            assertNotNull(consolidatedMatchedFiles);

            List<TransformedMatchedFilesView> matchedFiles = TransformViewsUtil.transformMatchedFilesView(consolidatedMatchedFiles);
            List<TransformedOriginView> origins = TransformViewsUtil.transformOriginView(component.origins, propertyConstants);

            // System.out.println("componentUrl::" + component.getComponentVersion());
            String componentVersionVulnerabilityUrl = hubServices
                    .getComponentVersionVulnerabilityUrl(component.componentVersion);
            // System.out.println("componentVersionVulnerabilityUrl::" + componentVersionVulnerabilityUrl);

            List<VulnerabilityView> vulnerableComponentView = hubServices.getVulnerabilities(componentVersionVulnerabilityUrl);
            List<TransformedVulnerabilityWithRemediationView> vulnerabilityWithRemediationViews = transformVulnerabilityRemediationView(
                    vulnerableComponentView, groupByVulnerabilityComponents, component.componentVersion, propertyConstants);

            for (int i = 0; component.licenses != null && i < component.licenses.size(); i++) {
                for (int j = 0; component.licenses.get(i).licenses != null && j < component.licenses.get(i).licenses.size(); j++) {
                    if (component.licenses.get(i).licenses.get(j).license != null) {
                        component.licenses.get(i).licenses.get(j).license = component.licenses.get(i).licenses.get(j).license
                                .replaceAll(propertyConstants.getHubServerUrl(), "");
                    }
                }
            }

            componentVersionBoms.add(
                    new ComponentVersionBom(component.componentName, component.componentVersionName,
                            component.component.replaceAll(propertyConstants.getHubServerUrl(), ""),
                            component.componentVersion.replaceAll(propertyConstants.getHubServerUrl(), ""), vulnerabilityWithRemediationViews.size(),
                            vulnerabilityWithRemediationViews, matchedFiles.size(), matchedFiles, component.licenses, origins, component.usages,
                            component.releasedOn, component.licenseRiskProfile, component.securityRiskProfile, component.versionRiskProfile,
                            component.activityRiskProfile, component.operationalRiskProfile, component.activityData, component.reviewStatus,
                            component.reviewedDetails, component.approvalStatus));
            // System.out.println("componentVersionBom::" + componentVersionBom));
        }

        FortifyUploadRequest fortifyUploadRequest = new FortifyUploadRequest(componentVersionBoms.size(), propertyConstants.getHubServerUrl(), PROJECT_NAME,
                VERSION_NAME, projectVersionItem.meta.href.replaceAll(propertyConstants.getHubServerUrl(), ""), bomUpdatedValueAt, componentVersionBoms);

        // Java object to JSON, and assign to a String
        Gson gson = new Gson();
        String jsonInString = gson.toJson(fortifyUploadRequest);
        System.out.println("fortifyUploadRequest::" + jsonInString);
        assertNotNull(fortifyUploadRequest);
    }

    /**
     * It will convert Matched Files view to Matched File View
     *
     * @param matchedFilesView
     * @return
     */
    private static List<TransformedVulnerabilityWithRemediationView> transformVulnerabilityRemediationView(
            final List<VulnerabilityView> vulnerabilityViews,
            Map<String, List<VulnerabilityWithRemediationView>> groupByVulnerabilityComponents, String componentVersionLink,
            final PropertyConstants propertyConstants) {
        List<TransformedVulnerabilityWithRemediationView> transformedVulnerabilityWithRemediationViews = new ArrayList<>();
        List<VulnerabilityWithRemediationView> vulnerabilityWithRemediationViews = groupByVulnerabilityComponents.get(componentVersionLink);
        vulnerabilityViews.forEach(vulnerabilityView -> {
            VulnerabilityWithRemediationView vulnerabilityWithRemediationView = VulnerabilityUtil.getVulnerabilityRemediationView(
                    vulnerabilityWithRemediationViews,
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
                    vulnerabilityView.meta.href.replaceAll(propertyConstants.getHubServerUrl(), ""), "NVD".equalsIgnoreCase(vulnerabilityView.source)
                            ? "http://web.nvd.nist.gov/view/vuln/detail?vulnId=" + vulnerabilityView.vulnerabilityName : "");
            transformedVulnerabilityWithRemediationViews.add(transformedVulnerabilityWithRemediationView);
        });
        return transformedVulnerabilityWithRemediationViews;
    }
}
