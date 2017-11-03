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

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { TestApplication.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FortifyUserApiTest extends TestCase {
    private String accessToken;

    private BlackDuckFortifyJobConfig blackDuckFortifyJobConfig;

    @Override
    @Before
    public void setUp() throws IOException, IntegrationException {
        blackDuckFortifyJobConfig = new BlackDuckFortifyJobConfig();
        accessToken = blackDuckFortifyJobConfig.getFortifyAuthenticationApi().getAuthenticatedToken();
    }

    @Test
    public void getFortifyUser() throws IOException, IntegrationException {
        long userId = blackDuckFortifyJobConfig.getFortifyUserApi().getFortifyUsers(accessToken);
        System.out.println("userId::" + userId);
        assertTrue("Error while getting the Fortify User", userId != 0);
    }
}
