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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.fod.batch.TestApplication;
import com.blackducksoftware.integration.hub.fod.domain.FortifyImportSession;
import com.blackducksoftware.integration.hub.fod.utils.AttributeConstants;
import com.blackducksoftware.integration.hub.fod.utils.PropertyConstants;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { TestApplication.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FortifyOpenSourceScansApiTest extends TestCase {

    private static final String APPLICATION_NAME = "FortifyTest";

    private static final String RELEASE_NUMBER_1 = "1.0";

    private String accessToken;

    private FortifyAuthenticationApi fortifyAuthenticationApi;

    private FortifyAttributeApi fortifyAttributeApi;

    private FortifyApplicationApi fortifyApplicationApi;

    private FortifyOpenSourceScansApi fortifyOpenSourceScansApi;

    @Autowired
    private AttributeConstants attributeConstants;

    @Override
    @Before
    public void setUp() throws IOException, IntegrationException {
        fortifyAuthenticationApi = new FortifyAuthenticationApi();
        fortifyAttributeApi = new FortifyAttributeApi();
        fortifyApplicationApi = new FortifyApplicationApi(fortifyAttributeApi, attributeConstants);
        accessToken = fortifyAuthenticationApi.getAuthenticatedToken();
        fortifyOpenSourceScansApi = new FortifyOpenSourceScansApi();
    }

    @Test
    public void createFortifyApplication() throws IOException, IntegrationException, InterruptedException {
        Thread.sleep(30000);
        long fortifyApplicationId = fortifyApplicationApi.createFortifyApplicationRelease(accessToken, APPLICATION_NAME, RELEASE_NUMBER_1, 21501);
        System.out.println("fortifyApplicationId::" + fortifyApplicationId);
        assertNotNull(fortifyApplicationId);
    }

    @Test
    public void getImportSessionId() throws IOException, IntegrationException {
        long fortifyApplicationId = 0;
        try {
            fortifyApplicationId = fortifyApplicationApi.getFortifyApplication(accessToken, APPLICATION_NAME);
            System.out.println("fortifyApplicationId::" + fortifyApplicationId);
            assertTrue("Error while getting the Fortify Application", fortifyApplicationId != 0);

            long releaseId = fortifyApplicationApi.getFortifyApplicationReleases(accessToken, fortifyApplicationId, RELEASE_NUMBER_1);
            System.out.println("releaseId::" + releaseId);
            assertTrue("Error while getting the Fortify Release", releaseId != 0);

            assertNotNull(releaseId);
            final String fileDir = PropertyConstants.getReportDir();
            final String fileName = "hub-fod-v2-example-formatted.json";
            File jsonRequest = new File(fileDir + fileName);

            byte[] fortifyUploadBytes = FileUtils.readFileToByteArray(jsonRequest);
            long vulnJsonLength = fortifyUploadBytes.length;
            accessToken = fortifyAuthenticationApi.getAuthenticatedToken();

            String sessionId = fortifyOpenSourceScansApi.getImportSessionId(accessToken,
                    new FortifyImportSession(releaseId, vulnJsonLength, "BlackDuck"));
            fortifyOpenSourceScansApi.uploadVulnerabilities(accessToken, sessionId, fortifyUploadBytes, vulnJsonLength);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fortifyApplicationApi.deleteFortifyApplicationReleases(accessToken, fortifyApplicationId);
        }
    }
}
