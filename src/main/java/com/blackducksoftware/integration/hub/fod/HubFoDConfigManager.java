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
package com.blackducksoftware.integration.hub.fod;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.fod.common.VulnerabilityReportConstants;
import com.blackducksoftware.integration.hub.fod.utils.ConsoleUtils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@Component
public class HubFoDConfigManager {
	
	private final Logger logger = (Logger)LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    HubFoDConfigProperties props;
	
	private static final String PROMPT_ENTER_HUB_URL = "Enter Black Duck Hub URL=";
	private static final String PROMPT_ENTER_HUB_PROJECT = "Enter Black Duck Hub Project=";
	private static final String PROMPT_ENTER_HUB_PROJECT_VERSION = "Enter Black Duck Hub Project Version=";
	private static final String PROMPT_ENTER_HUB_PASSWORD = "Enter Black Duck Hub Password=";
	private static final String PROMPT_ENTER_HUB_USERNAME = "Enter Black Duck Hub Username=";
	private static final String PROMPT_ENTER_FOD_USERNAME = "Enter Fortify on Demand Username=";
	private static final String PROMPT_ENTER_FOD_PASSWORD = "Enter Fortify on Demand Password=";
	private static final String PROMPT_ENTER_FOD_TENANT = "Enter Fortify on Demand Tenant ID=";
	
	public void processUsage(String... args)
	{
		//Check for verbose mode
		List<String> argsList = Arrays.asList(args);
		if(argsList.contains("--v") | argsList.contains("--verbose"))
		{
			Logger duckLog = (Logger)LoggerFactory.getLogger("com.blackducksoftware");
			duckLog.setLevel(Level.DEBUG);
			logger.debug("ENTERING VERBOSE MODE");
			
		}
		
		//Check for help
		if(argsList.contains("--help"))
		{
			logger.info(VulnerabilityReportConstants.USAGE);
			System.exit(0);
			
		}
			
		// Start the prompts if optional props were not specified
		
		//If Hub URL is blank, prompt to get it
		if(StringUtils.isBlank(props.getHubURL())){props.setHubURL(String.valueOf(ConsoleUtils.readLine(PROMPT_ENTER_HUB_URL)));}	
	
		//If Hub Project is blank, prompt to get it
		if(StringUtils.isBlank(props.getHubProject())){props.setHubProject(String.valueOf(ConsoleUtils.readLine(PROMPT_ENTER_HUB_PROJECT)));}	
	
		//If Hub Project Version is blank, prompt to get it
		if(StringUtils.isBlank(props.getHubProjectVersion())){props.setHubProjectVersion(String.valueOf(ConsoleUtils.readLine(PROMPT_ENTER_HUB_PROJECT_VERSION)));}	
	
		//If Hub user is blank, prompt to get it
		if(StringUtils.isBlank(props.getHubUser())){props.setHubUser(String.valueOf(ConsoleUtils.readLine(PROMPT_ENTER_HUB_USERNAME)));}	
		
		//If Hub password is blank, prompt to get it
		if(StringUtils.isBlank(props.getHubPassword())){props.setHubPassword(String.valueOf(ConsoleUtils.readPassword(PROMPT_ENTER_HUB_PASSWORD)));}
		
		//If FoD user is blank, prompt to get it
		if(StringUtils.isBlank(props.getFodUsername())){props.setFodUsername(String.valueOf(ConsoleUtils.readLine(PROMPT_ENTER_FOD_USERNAME)));}

		//If FoD password is blank, prompt to get it
		if(StringUtils.isBlank(props.getFodPassword())){props.setFodPassword(String.valueOf(ConsoleUtils.readPassword(PROMPT_ENTER_FOD_PASSWORD)));}

		//If FoD tenant is blank, prompt to get it
		if(StringUtils.isBlank(props.getFodTenantId())){props.setFodTenantId(String.valueOf(ConsoleUtils.readLine(PROMPT_ENTER_FOD_TENANT)));}

	}
	
	

}
