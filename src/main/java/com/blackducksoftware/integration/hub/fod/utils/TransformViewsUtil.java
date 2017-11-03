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

import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.TypeToken;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.fod.batch.model.CweVulnerabilityView;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedMatchedFilesView;
import com.blackducksoftware.integration.hub.fod.batch.model.TransformedVulnerabilityWithRemediationView;
import com.blackducksoftware.integration.hub.fod.batch.model.VulnerabilityView;
import com.blackducksoftware.integration.hub.fod.service.HubServices;
import com.blackducksoftware.integration.hub.model.view.MatchedFilesView;

/**
 * This class is used to transform the view from one to another
 *
 * @author smanikantan
 *
 */
public final class TransformViewsUtil {

    private static Map<String, CweVulnerabilityView> cweNamesMap = new HashMap<>();

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
        for (VulnerabilityView vulnerabilityView : vulnerabilityViews) {
            StringBuffer cweIds = new StringBuffer();
            StringBuffer cweNames = new StringBuffer();
            boolean isDelimiterRequired = false;
            if ("NVD".equalsIgnoreCase(vulnerabilityView.getSource())) {
                List<String> cweVulnerabilityUrls = hubServices.getcweVulnerabilityUrls(vulnerabilityView);

                for (String cweVulnerabilityUrl : cweVulnerabilityUrls) {
                    CweVulnerabilityView cweVulnerabilityView = getCWEVulnerabilityView(cweVulnerabilityUrl, hubServices);
                    if (!isDelimiterRequired) {
                        cweIds.append(cweVulnerabilityView.getId());
                        cweNames.append(cweVulnerabilityView.getName());
                        isDelimiterRequired = true;
                    } else {
                        cweIds.append(", " + cweVulnerabilityView.getId());
                        cweNames.append(", " + cweVulnerabilityView.getName());
                    }
                }

                transformedVulnerabilityWithRemediationViews
                        .add(getTransformedVulnerabilityWithRemediationView(vulnerabilityView, cweIds.toString(), cweNames.toString()));
            }
        }
        return transformedVulnerabilityWithRemediationViews;
    }

    /**
     * Transform the Vulnerability View
     *
     * @param vulnerabilityView
     * @param cweId
     * @param cweName
     * @return
     */
    private static TransformedVulnerabilityWithRemediationView getTransformedVulnerabilityWithRemediationView(VulnerabilityView vulnerabilityView, String cweId,
            String cweName) {
        return new TransformedVulnerabilityWithRemediationView(
                vulnerabilityView.getVulnerabilityName(), cweId, cweName, vulnerabilityView.getDescription(),
                DateUtil.getDateTime(vulnerabilityView.getVulnerabilityPublishedDate()),
                DateUtil.getDateTime(vulnerabilityView.getVulnerabilityUpdatedDate()),
                vulnerabilityView.getCvssView().getBaseScore(), vulnerabilityView.getCvssView().getExploitabilitySubscore(),
                vulnerabilityView.getCvssView().getImpactSubscore(), vulnerabilityView.getSource(), vulnerabilityView.getSeverity(),
                vulnerabilityView.getCvssView().getAccessVector(), vulnerabilityView.getCvssView().getAccessComplexity(),
                vulnerabilityView.getCvssView().getAuthentication(), vulnerabilityView.getCvssView().getConfidentialityImpact(),
                vulnerabilityView.getCvssView().getIntegrityImpact(), vulnerabilityView.getCvssView().getAvailabilityImpact(),
                vulnerabilityView.meta.href.replaceAll(PropertyConstants.getHubServerUrl(), ""),
                "http://web.nvd.nist.gov/view/vuln/detail?vulnId=" + vulnerabilityView.getVulnerabilityName());
    }

    /**
     * Get CWE Vulnerability view for the given CWE Vulnerability Url.
     *
     * @param cweVulnerabilityUrl
     * @param hubServices
     * @return
     * @throws IntegrationException
     */
    private static CweVulnerabilityView getCWEVulnerabilityView(String cweVulnerabilityUrl, final HubServices hubServices) throws IntegrationException {
        synchronized (cweNamesMap) {
            if (cweNamesMap.containsKey(cweVulnerabilityUrl)) {
                return cweNamesMap.get(cweVulnerabilityUrl);
            } else {
                CweVulnerabilityView cweVulnerabilityView = hubServices.getCweVulnerabilityView(cweVulnerabilityUrl);
                cweNamesMap.put(cweVulnerabilityUrl, cweVulnerabilityView);
                return cweVulnerabilityView;
            }
        }
    }
}
