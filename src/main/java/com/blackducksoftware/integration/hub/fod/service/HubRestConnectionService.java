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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.nonpublic.HubVersionRequestService;
import com.blackducksoftware.integration.hub.api.project.ProjectRequestService;
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService;
import com.blackducksoftware.integration.hub.api.vulnerablebomcomponent.VulnerableBomComponentRequestService;
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.fod.HubFoDConfigProperties;
import com.blackducksoftware.integration.hub.fod.domain.HubProjectVersion;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.request.HubRequest;
import com.blackducksoftware.integration.hub.request.HubRequestFactory;
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnection;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntBufferedLogger;
import com.blackducksoftware.integration.log.IntLogger;
import com.google.gson.Gson;

import okhttp3.Response;


public class HubRestConnectionService {
	
	@Autowired
    HubFoDConfigProperties appProps;
	
	private final HubServicesFactory hubServicesFactory;
	private final HubRequestFactory hubRequestFactory;

    private final IntLogger logger;
    
    private final Logger appLog = LoggerFactory.getLogger(HubRestConnectionService.class);

    private final RestConnection restConnection;

    private PhoneHomeDataService phoneHomeDataService;

    private HubVersionRequestService hubVersionRequestService;
    
    private ProjectRequestService projectRequestService;

    private ProjectVersionRequestService projectVersionRequestService;
    
    private VulnerableBomComponentRequestService vulnerableBomComponentRequestService;


    
    public HubRestConnectionService() {
        restConnection = null;
        this.logger = new IntBufferedLogger();
        hubServicesFactory = null;
        hubRequestFactory = null;
    }

    public HubRestConnectionService(RestConnection restConnection) {
        this.restConnection = restConnection;
        this.logger = new IntBufferedLogger();
        this.hubServicesFactory = new HubServicesFactory(restConnection);
        this.hubRequestFactory = new HubRequestFactory(restConnection);
    }

    public CredentialsRestConnection getCredentialsRestConnection(final HubServerConfig config)
            throws IllegalArgumentException, EncryptionException, HubIntegrationException {
        return new CredentialsRestConnection(this.logger, 
        				config.getHubUrl(), 
        				config.getGlobalCredentials().getUsername(), 
        				config.getGlobalCredentials().getDecryptedPassword(), 
        				config.getTimeout());
    }
    
 
    public void updateProjectVersion(String url, HubProjectVersion hubProjectVersion)
    {
    	Gson gson = new Gson();
        String json = gson.toJson(hubProjectVersion); 

    	HubRequest putRequest = hubRequestFactory.createRequest(url);
    	
    	try(Response response = putRequest.executePut("application/json", json)) {
    		
    		appLog.debug("UPDATE PROJECT VERSION RESPONSE CODE: " +response.code());

    	} catch (IntegrationException e) {
    		appLog.error("IntegrationException in updateProjectVersion");
			e.printStackTrace();
		}
    	
    }

    //TODO: Implement phone Home
    public PhoneHomeDataService getPhoneHomeDataService() {
        if (phoneHomeDataService == null) {
            phoneHomeDataService = hubServicesFactory.createPhoneHomeDataService(logger);
        }
        return phoneHomeDataService;
    }

    public HubVersionRequestService getHubVersionRequestService() {
        if (hubVersionRequestService == null) {
            hubVersionRequestService = hubServicesFactory.createHubVersionRequestService();
        }
        return hubVersionRequestService;
    }
    
    public ProjectRequestService getProjectRequestService()
    {
    	if(projectRequestService == null)
    	{
    		projectRequestService = hubServicesFactory.createProjectRequestService(logger);
    	}
    	return projectRequestService;
    }
    
    public ProjectVersionRequestService getProjectVersionRequestService()
    {
    	if(projectVersionRequestService == null)
    	{
    		projectVersionRequestService = hubServicesFactory.createProjectVersionRequestService(logger);
    	}
    	return projectVersionRequestService;
    }
    
    public VulnerableBomComponentRequestService getVulnerableBomComponentRequestService()
    {
    	if(vulnerableBomComponentRequestService == null)
    	{
    		vulnerableBomComponentRequestService = hubServicesFactory.createVulnerableBomComponentRequestService();
    	}
    	return vulnerableBomComponentRequestService;
    }

    public RestConnection getRestConnection() {
        return restConnection;
    }

    public boolean hasActiveHubConnection() {
        return restConnection != null;
    }

}
