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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.TypeToken;

import com.blackducksoftware.integration.hub.fod.batch.model.TransformedMatchedFilesView;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedOriginView;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedVulnerabilityWithRemediationView;
import com.blackducksoftware.integration.hub.model.view.MatchedFilesView;
import com.blackducksoftware.integration.hub.model.view.VulnerabilityView;
import com.blackducksoftware.integration.hub.model.view.components.LinkView;
import com.blackducksoftware.integration.hub.model.view.components.OriginView;

/**
 * This class is used to transform the view from one to another
 *
 * @author smanikantan
 *
 */
public final class TransformViewsUtil {

    /**
     * It will convert Matched Files view to Transformed Matched File View
     *
     * @param matchedFilesView
     * @return
     */
    public static List<TransformedMatchedFilesView> transformMatchedFilesView(final List<MatchedFilesView> matchedFilesView) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(MatchedFilesView.class, TransformedMatchedFilesView.class).setProvider(
                new Provider<TransformedMatchedFilesView>() {
                    @Override
                    public TransformedMatchedFilesView get(ProvisionRequest<TransformedMatchedFilesView> request) {
                        MatchedFilesView matchedFilesView = MatchedFilesView.class.cast(request.getSource());
                        return new TransformedMatchedFilesView(matchedFilesView.filePath, matchedFilesView.usages);
                    }
                });

        Type transformedMatchedFilesViewType = new TypeToken<List<TransformedMatchedFilesView>>() {
        }.getType();

        return modelMapper.map(matchedFilesView, transformedMatchedFilesViewType);
    }

    /**
     * It will convert Origin view to Transformed Origin View
     *
     * @param originViews
     * @return
     */
    public static List<TransformedOriginView> transformOriginView(final List<OriginView> originViews, final PropertyConstants propertyConstants) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(OriginView.class, TransformedOriginView.class).setProvider(
                new Provider<TransformedOriginView>() {
                    @Override
                    public TransformedOriginView get(ProvisionRequest<TransformedOriginView> request) {
                        OriginView originView = OriginView.class.cast(request.getSource());
                        String componentVersionOriginUrl = null;
                        for (LinkView link : originView.meta.links) {
                            if ("origin".equalsIgnoreCase(link.rel)) {
                                componentVersionOriginUrl = link.href.replaceAll(propertyConstants.getHubServerUrl(), "");
                                break;
                            }
                        }
                        return new TransformedOriginView(originView.name, originView.externalNamespace,
                                originView.externalId, originView.externalNamespaceDistribution, componentVersionOriginUrl);
                    }
                });

        Type transformedOriginViewType = new TypeToken<List<TransformedOriginView>>() {
        }.getType();

        return modelMapper.map(originViews, transformedOriginViewType);
    }

    /**
     * It will convert Vulnerability view to Vulnerability Remediation View
     *
     * @param vulnerabilityViews
     * @param groupByVulnerabilityComponents
     * @param componentVersionLink
     * @return
     */
    public static List<TransformedVulnerabilityWithRemediationView> transformVulnerabilityRemediationView(final List<VulnerabilityView> vulnerabilityViews,
            final PropertyConstants propertyConstants) {
        List<TransformedVulnerabilityWithRemediationView> transformedVulnerabilityWithRemediationViews = new ArrayList<>();
        /*
         * List<VulnerabilityWithRemediationView> vulnerabilityWithRemediationViews =
         * groupByVulnerabilityComponents.get(componentVersionLink);
         */
        if (vulnerabilityViews != null) {
            vulnerabilityViews.forEach(vulnerabilityView -> {
                /*
                 * VulnerabilityWithRemediationView vulnerabilityWithRemediationView =
                 * VulnerabilityUtil.getVulnerabilityRemediationView(
                 * vulnerabilityWithRemediationViews, vulnerabilityView.vulnerabilityName);
                 */
                if ("NVD".equalsIgnoreCase(vulnerabilityView.source)) {
                    TransformedVulnerabilityWithRemediationView transformedVulnerabilityWithRemediationView = new TransformedVulnerabilityWithRemediationView(
                            vulnerabilityView.vulnerabilityName, vulnerabilityView.cweId, "", vulnerabilityView.description,
                            vulnerabilityView.baseScore, vulnerabilityView.exploitabilitySubscore, vulnerabilityView.impactSubscore,
                            vulnerabilityView.source, vulnerabilityView.severity, vulnerabilityView.accessVector,
                            vulnerabilityView.accessComplexity, vulnerabilityView.authentication, vulnerabilityView.confidentialityImpact,
                            vulnerabilityView.integrityImpact, vulnerabilityView.availabilityImpact,
                            vulnerabilityView.meta.href.replaceAll(propertyConstants.getHubServerUrl(), ""),
                            "http://web.nvd.nist.gov/view/vuln/detail?vulnId=" + vulnerabilityView.vulnerabilityName);
                    transformedVulnerabilityWithRemediationViews.add(transformedVulnerabilityWithRemediationView);
                }
            });
        }
        return transformedVulnerabilityWithRemediationViews;
    }
}
