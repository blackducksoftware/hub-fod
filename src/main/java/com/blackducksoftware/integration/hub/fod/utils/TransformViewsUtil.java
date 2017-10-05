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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.TypeToken;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedMatchedFilesView;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedOriginView;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedVulnerabilityWithRemediationView;
import com.blackducksoftware.integration.hub.fod.service.HubServices;
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

    private final static String CWE_API_URL = "api/cwes/";

    private static Map<String, String> cweNamesMap = new HashMap<>();

    /**
     * Reset the Cwe names map for each job call
     */
    public static void resetCweNamesMap() {
        cweNamesMap = new HashMap<>();
    }

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
    public static List<TransformedOriginView> transformOriginView(final List<OriginView> originViews) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(OriginView.class, TransformedOriginView.class).setProvider(
                new Provider<TransformedOriginView>() {
                    @Override
                    public TransformedOriginView get(ProvisionRequest<TransformedOriginView> request) {
                        OriginView originView = OriginView.class.cast(request.getSource());
                        String componentVersionOriginUrl = null;
                        for (LinkView link : originView.meta.links) {
                            if ("origin".equalsIgnoreCase(link.rel)) {
                                componentVersionOriginUrl = link.href.replaceAll(PropertyConstants.getHubServerUrl(), "");
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
            final HubServices hubServices) throws IntegrationException, DateTimeParseException {
        List<TransformedVulnerabilityWithRemediationView> transformedVulnerabilityWithRemediationViews = new ArrayList<>();
        String cweId = null;
        for (VulnerabilityView vulnerabilityView : vulnerabilityViews) {

            if ("NVD".equalsIgnoreCase(vulnerabilityView.source)) {
                cweId = vulnerabilityView.cweId;

                TransformedVulnerabilityWithRemediationView transformedVulnerabilityWithRemediationView = new TransformedVulnerabilityWithRemediationView(
                        vulnerabilityView.vulnerabilityName, cweId, getCWENames(cweId, hubServices), vulnerabilityView.description,
                        DateUtil.getDateTime(vulnerabilityView.vulnerabilityPublishedDate), DateUtil.getDateTime(vulnerabilityView.vulnerabilityUpdatedDate),
                        vulnerabilityView.baseScore, vulnerabilityView.exploitabilitySubscore, vulnerabilityView.impactSubscore, vulnerabilityView.source,
                        vulnerabilityView.severity, vulnerabilityView.accessVector, vulnerabilityView.accessComplexity, vulnerabilityView.authentication,
                        vulnerabilityView.confidentialityImpact, vulnerabilityView.integrityImpact, vulnerabilityView.availabilityImpact,
                        vulnerabilityView.meta.href.replaceAll(PropertyConstants.getHubServerUrl(), ""),
                        "http://web.nvd.nist.gov/view/vuln/detail?vulnId=" + vulnerabilityView.vulnerabilityName);
                transformedVulnerabilityWithRemediationViews.add(transformedVulnerabilityWithRemediationView);
            }
        }
        return transformedVulnerabilityWithRemediationViews;
    }

    /**
     * Get CWE names for the given CWE id. The CWE id can be comma separated.
     *
     * @param cweId
     * @param hubServices
     * @return
     * @throws IntegrationException
     */
    private static String getCWENames(String cweId, final HubServices hubServices) throws IntegrationException {
        String cweName = null;
        String trimmedCweId = null;
        StringBuffer cweNames = new StringBuffer();
        if (!StringUtils.isEmpty(cweId)) {
            String[] cweIds = cweId.split(",");
            for (int i = 0; i < cweIds.length; i++) {
                trimmedCweId = cweIds[i].trim();

                if (cweNamesMap.containsKey(trimmedCweId)) {
                    synchronized (cweNamesMap) {
                        cweName = cweNamesMap.get(trimmedCweId);
                    }
                } else {
                    cweName = getCWEName(trimmedCweId, hubServices);
                    synchronized (cweNamesMap) {
                        cweNamesMap.put(trimmedCweId, cweName);
                    }
                }

                if (i == 0)
                    cweNames.append(cweName);
                else
                    cweNames.append(", " + cweName);
            }
        }
        return cweNames.toString();
    }

    /**
     * Get the CWE name for the given CWE id
     *
     * @param cweId
     * @param hubServices
     * @return
     * @throws IntegrationException
     */
    private static String getCWEName(String cweId, final HubServices hubServices) throws IntegrationException {
        if (!StringUtils.isEmpty(cweId))
            return hubServices.getCweVulnerabilityView(PropertyConstants.getHubServerUrl() + CWE_API_URL + cweId).getName();
        else
            return "";
    }
}
