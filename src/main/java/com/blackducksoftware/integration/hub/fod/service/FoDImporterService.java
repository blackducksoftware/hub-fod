/**
 * hub-fod
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackducksoftware.integration.hub.fod.HubFoDConfigProperties;
import com.blackducksoftware.integration.hub.fod.domain.FoDApplication;
import com.blackducksoftware.integration.hub.fod.domain.FoDApplicationRelease;

@Service
public class FoDImporterService {

    private static final String REPORT_NAME = "Black Duck Open Source Vulnerability Report";

    @Autowired
    FoDRestConnectionService fodRestClient;

    @Autowired
    HubFoDConfigProperties appProps;

    private final Logger appLog = LoggerFactory.getLogger(VulnerabilityReportService.class);

    public void foDAuthenticate() throws Exception {
        // Authenticate to FoD
        fodRestClient.authenticate();
    }

    public String importVulnerabilityPDF() throws IOException {

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:s");

        final Date reportDate = new Date(System.currentTimeMillis());

        // InputStream is = new FileInputStream(outputPDF);
        final long vulnPdfLength = new File(appProps.getOutputFolder().concat("/").concat(appProps.getOutputPDFFilename())).length();
        appLog.debug(appProps.getOutputFolder().concat("/").concat(appProps.getOutputPDFFilename()) + "PDF size: " + vulnPdfLength);

        // Get the import report session id
        final String importSessionId = fodRestClient.getFoDImportSessionId(appProps.getFodReleaseId(), vulnPdfLength,
                REPORT_NAME + " - " + appProps.getHubProject() + " " + appProps.getHubProjectVersion() + " " + sdf.format(reportDate),
                appProps.getReportNotes());
        appLog.debug("FoD Import Session Id: " + importSessionId);

        // upload the PDF
        return fodRestClient.uploadFoDPDF(appProps.getFodReleaseId(), importSessionId,
                appProps.getOutputFolder().concat("/").concat(appProps.getOutputPDFFilename()), vulnPdfLength);

    }

    public List<FoDApplication> getFodApplications() {
        return fodRestClient.getFoDApplicationList();
    }

    public List<FoDApplicationRelease> getFodApplicationReleases(final String applicationId) {
        return fodRestClient.getFoDApplicationReleases(applicationId);
    }
}
