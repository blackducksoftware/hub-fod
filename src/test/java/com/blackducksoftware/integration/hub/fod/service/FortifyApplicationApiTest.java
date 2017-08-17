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

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.fod.batch.TestApplication;
import com.blackducksoftware.integration.hub.fod.batch.job.BlackDuckFortifyJobConfig;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplication;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplication.Attribute;
import com.blackducksoftware.integration.hub.fod.domain.FortifyApplicationRelease;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { TestApplication.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FortifyApplicationApiTest extends TestCase {

    private static final String APPLICATION_NAME = "FortifyTest";

    private static final String RELEASE_NUMBER_1 = "1.0";

    private static final String RELEASE_NUMBER_2 = "2.0";

    private String accessToken;

    private BlackDuckFortifyJobConfig blackDuckFortifyJobConfig;

    @Override
    @Before
    public void setUp() throws IOException, IntegrationException {
        blackDuckFortifyJobConfig = new BlackDuckFortifyJobConfig();
        accessToken = blackDuckFortifyJobConfig.getFortifyAuthenticationApi().getAuthenticatedToken();
    }

    @Test
    public void createFortifyApplication() throws IOException, IntegrationException {
        FortifyApplication fortifyApplication = new FortifyApplication(null, APPLICATION_NAME, "", "Web_Thick_Client", RELEASE_NUMBER_1, "", "", 21501,
                new ArrayList<Attribute>(), "High", "Production");
        long fortifyApplicationId = blackDuckFortifyJobConfig.getFortifyApplicationApi().createFortifyApplicationRelease(accessToken, fortifyApplication);
        System.out.println("fortifyApplicationId::" + fortifyApplicationId);
        assertNotNull(fortifyApplicationId);

        FortifyApplicationRelease fortifyApplicationRelease = new FortifyApplicationRelease(null, RELEASE_NUMBER_2, "", fortifyApplicationId, false, null,
                "Production");

        long releaseId = blackDuckFortifyJobConfig.getFortifyApplicationApi().createFortifyApplicationRelease(accessToken, fortifyApplicationRelease);
        System.out.println("releaseId::" + releaseId);
        assertNotNull(releaseId);
    }

    @Test
    public void getFortifyApplication() throws IOException, IntegrationException {
        long fortifyApplicationId = blackDuckFortifyJobConfig.getFortifyApplicationApi().getFortifyApplication(accessToken, APPLICATION_NAME);
        System.out.println("fortifyApplicationId::" + fortifyApplicationId);
        assertTrue("Error while getting the Fortify Application", fortifyApplicationId != 0);

        long releaseId = blackDuckFortifyJobConfig.getFortifyApplicationApi().getFortifyApplicationReleases(accessToken, fortifyApplicationId,
                RELEASE_NUMBER_1);
        System.out.println("releaseId::" + releaseId);
        assertTrue("Error while getting the Fortify Release", releaseId != 0);

        releaseId = blackDuckFortifyJobConfig.getFortifyApplicationApi().getFortifyApplicationReleases(accessToken, fortifyApplicationId, RELEASE_NUMBER_2);
        System.out.println("releaseId::" + releaseId);
        assertTrue("Error while getting the Fortify Release", releaseId != 0);

        blackDuckFortifyJobConfig.getFortifyApplicationApi().deleteFortifyApplicationReleases(accessToken, fortifyApplicationId);
    }

    /*
       @Test
       public void deleteFortifyApplication() throws IOException {
           blackDuckFortifyJobConfig.getFortifyApplicationApi().deleteFortifyApplicationReleases(accessToken, 84072);
       }
     */
}
