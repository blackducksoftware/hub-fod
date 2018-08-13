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

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.configuration.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.fod.HubFoDConfigProperties;
import com.blackducksoftware.integration.hub.rest.BlackduckRestConnection;
import com.blackducksoftware.integration.hub.service.HubRegistrationService;
import com.blackducksoftware.integration.hub.service.HubService;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.hub.service.PhoneHomeService;
import com.blackducksoftware.integration.hub.service.ProjectService;
import com.blackducksoftware.integration.log.Slf4jIntLogger;
import com.blackducksoftware.integration.phonehome.PhoneHomeClient;
import com.blackducksoftware.integration.rest.connection.RestConnection;
import com.blackducksoftware.integration.util.IntEnvironmentVariables;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

@Service
public class HubRestConnectionService {

	@Autowired
	private final HubFoDConfigProperties configurationProperties;

    private HubServicesFactory hubServicesFactory;
    
    private final Logger logger = LoggerFactory.getLogger(HubRestConnectionService.class);

    private RestConnection restConnection;
    
    public static final String ALLIANCES_TRACKING_ID = "UA-116682967-3";

    
     public HubRestConnectionService(final HubFoDConfigProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    
    public RestConnection getBlackDuckConnection() throws IntegrationException, EncryptionException, HubIntegrationException
    {
    	final HubServerConfig hubServerConfig = createBuilder().build();
       restConnection = null;

        try {
            if (StringUtils.isNotBlank(configurationProperties.getBlackDuckApiToken())) {
            	restConnection = hubServerConfig.createApiTokenRestConnection(new Slf4jIntLogger(logger));
            } else {
            	restConnection = hubServerConfig.createCredentialsRestConnection(new Slf4jIntLogger(this.logger));
            }
            
            restConnection.connect();

        } catch (Exception e){
        	
        	logger.error("Unable to connect to Black Duck with error:" + e.getMessage());
        	throw (e);
            
        }

        logger.info("Successful connection to Black Duck!");
        return restConnection;
    	
    }
    
    private HubServerConfigBuilder createBuilder() {
        final HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder();
        hubServerConfigBuilder.setUrl(configurationProperties.getHubURL());
        hubServerConfigBuilder.setApiToken(configurationProperties.getBlackDuckApiToken());
        hubServerConfigBuilder.setUsername(configurationProperties.getHubUser());
        hubServerConfigBuilder.setPassword(configurationProperties.getHubPassword());
        hubServerConfigBuilder.setTimeout(configurationProperties.getHubTimeout());
        hubServerConfigBuilder.setProxyHost(configurationProperties.getProxyHost());
        hubServerConfigBuilder.setProxyPort(configurationProperties.getProxyPort());
        hubServerConfigBuilder.setProxyUsername(configurationProperties.getProxyUsername());
        hubServerConfigBuilder.setProxyPassword(configurationProperties.getProxyPassword());
        hubServerConfigBuilder.setTrustCert(configurationProperties.getTrustCerts());

        return hubServerConfigBuilder;
    }

 
    public PhoneHomeService getPhoneHomeDataService() {
       
    	logger.debug("Creating Phone home data service");
        return new PhoneHomeService(createHubService(), new Slf4jIntLogger(logger), createPhoneHomeClient(), createHubRegistrationService(), new IntEnvironmentVariables(true));
    	//return createHubServicesFactory(restConnection).createPhoneHomeService(); 

    }
    
    public PhoneHomeClient createPhoneHomeClient() {
        logger.debug("Creating Phone home client");
        final HttpClientBuilder httpClientBuilder = restConnection.getClientBuilder();
        final Gson gson = HubServicesFactory.createDefaultGson();
        return new PhoneHomeClient(ALLIANCES_TRACKING_ID, httpClientBuilder, gson);
    }
    
    public HubServicesFactory createHubServicesFactory(RestConnection restconnection)
    {
    	if(hubServicesFactory==null)
    	{
	    	final Gson gson = HubServicesFactory.createDefaultGson();
	        final JsonParser jsonParser = HubServicesFactory.createDefaultJsonParser();
	        hubServicesFactory = new HubServicesFactory(gson, jsonParser, (BlackduckRestConnection) restConnection, new Slf4jIntLogger(logger));
	    }
        return hubServicesFactory;

    }
    
    public HubService createHubService() {
        logger.debug("Creating Hub service");
        return createHubServicesFactory(restConnection).createHubService();
    }
    
    public HubRegistrationService createHubRegistrationService() {
        logger.debug("Creating Hub registration service");
        return createHubServicesFactory(restConnection).createHubRegistrationService();
    }

    public ProjectService getProjectService() {
    	
    	return createHubServicesFactory(restConnection).createProjectService();
    }
    

    public HubService getHubService() {
    	
    	return createHubServicesFactory(restConnection).createHubService();
       
    }

    public RestConnection getRestConnection() {
        return restConnection;
    }

    public boolean hasActiveHubConnection() {
        return restConnection != null;
    }

}
