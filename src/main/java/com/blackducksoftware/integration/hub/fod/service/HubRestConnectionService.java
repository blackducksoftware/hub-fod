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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.fod.HubFoDConfigProperties;
import com.blackducksoftware.integration.hub.fod.domain.HubProjectVersion;
import com.blackducksoftware.integration.hub.request.BodyContent;
import com.blackducksoftware.integration.hub.request.Request;
import com.blackducksoftware.integration.hub.request.Response;
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnection;
import com.blackducksoftware.integration.hub.rest.HttpMethod;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubService;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.hub.service.PhoneHomeService;
import com.blackducksoftware.integration.hub.service.ProjectService;
import com.blackducksoftware.integration.log.IntBufferedLogger;
import com.blackducksoftware.integration.log.IntLogger;
import com.google.gson.Gson;

public class HubRestConnectionService {

    @Autowired
    HubFoDConfigProperties appProps;

    private final HubServicesFactory hubServicesFactory;

    private final IntLogger logger;

    private final Logger appLog = LoggerFactory.getLogger(HubRestConnectionService.class);

    private final RestConnection restConnection;

    private PhoneHomeService phoneHomeDataService;

    private ProjectService projectRequestService;

    private HubService hubService;

    public HubRestConnectionService() {
        restConnection = null;
        this.logger = new IntBufferedLogger();
        hubServicesFactory = null;
    }

    public HubRestConnectionService(final RestConnection restConnection) {
        this.restConnection = restConnection;
        this.logger = new IntBufferedLogger();
        this.hubServicesFactory = new HubServicesFactory(restConnection);
    }

    public CredentialsRestConnection getCredentialsRestConnection(final HubServerConfig config)
            throws IllegalArgumentException, EncryptionException, HubIntegrationException {
        new CredentialsRestConnection(this.logger, config.getHubUrl(), config.getGlobalCredentials().getUsername(),
                config.getGlobalCredentials().getDecryptedPassword(), config.getTimeout(), config.getProxyInfo());
        return new CredentialsRestConnection(this.logger,
                config.getHubUrl(),
                config.getGlobalCredentials().getUsername(),
                config.getGlobalCredentials().getDecryptedPassword(),
                config.getTimeout(),
                config.getProxyInfo());
    }

    public void updateProjectVersion(final String url, final HubProjectVersion hubProjectVersion) {
        final Gson gson = new Gson();
        final String json = gson.toJson(hubProjectVersion);

        final Request request = new Request.Builder(url).method(HttpMethod.PUT).bodyContent(new BodyContent(json)).build();
        try (Response response = hubService.executeRequest(request)) {
            appLog.debug("UPDATE PROJECT VERSION RESPONSE CODE: " + response.getStatusCode());
        } catch (final IOException e) {
            appLog.error("IntegrationException in updateProjectVersion");
            e.printStackTrace();
        } catch (final IntegrationException e) {
            appLog.error("IntegrationException in updateProjectVersion");
            e.printStackTrace();
        }
    }

    public PhoneHomeService getPhoneHomeDataService() {
        if (phoneHomeDataService == null) {
            phoneHomeDataService = hubServicesFactory.createPhoneHomeService();
        }
        return phoneHomeDataService;
    }

    public ProjectService getProjectRequestService() {
        if (projectRequestService == null) {
            projectRequestService = hubServicesFactory.createProjectService();
        }
        return projectRequestService;
    }

    public HubService getHubService() {
        if (hubService == null) {
            hubService = hubServicesFactory.createHubService();
        }
        return hubService;
    }

    public RestConnection getRestConnection() {
        return restConnection;
    }

    public boolean hasActiveHubConnection() {
        return restConnection != null;
    }

}
