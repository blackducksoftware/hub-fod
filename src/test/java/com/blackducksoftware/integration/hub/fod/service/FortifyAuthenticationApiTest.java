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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.hub.fod.batch.TestApplication;
import com.blackducksoftware.integration.hub.fod.utils.PropertyConstants;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { TestApplication.class })
public class FortifyAuthenticationApiTest extends TestCase {

    @Test
    public void getAuthenticationToken() throws Exception {
        String accessToken = FortifyAuthenticationApi.getAuthenticatedToken("https://hpfod.com/tenant", "password",
                PropertyConstants.getFortifyTenantId() + "\\" + PropertyConstants.getFortifyUserName(), PropertyConstants.getFortifyPassword());
        System.out.println("accessToken::" + accessToken);
        assertNotNull(accessToken);
    }

}
