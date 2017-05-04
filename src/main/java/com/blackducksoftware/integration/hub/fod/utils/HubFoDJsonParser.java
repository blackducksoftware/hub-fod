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
package com.blackducksoftware.integration.hub.fod.utils;

import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HubFoDJsonParser {
	
	private final static String JSON_META_LABEL = "_meta";
	private final static String JSON_META_LINKS = "links";
	private final static String JSON_META_REL = "rel";
	private final static String JSON_META_VULNERABLE_COMPONENTS = "vulnerable-components";
	private final static String JSON_HREF = "href";
	private final static String JSON_LICENSE = "license";
	private final static String JSON_LICENSES = "licenses";
	private final static String JSON_NAME = "name";
	private final static String JSON_CODE_SHARING = "codeSharing";
	//private final static String JSON_COMPONENTVERSION = "componentVersion";
	//private final static String JSON_VULNERABILITIES = "vulnerabilities";

	private final static String HUB_API_PROJECTS = "api/projects/";
	private final static String HUB_UI_PROJECTS = "#projects/id:";
	private final static String HUB_API_VERSIONS = "versions/";
	private final static String HUB_UI_VERSIONS = "#versions/id:";
	//private final static String HUB_UI_VULN = "/#vulnerabilities/id:";
	//private final static String HUB_API_COMPONENTS = "api/components/";
	
	public static String getVulnerableComponentsURL(String projectVersionJson)
	{
		
		final JsonObject jsonObject = new JsonParser().parse(projectVersionJson).getAsJsonObject();
        final JsonArray linksArray = jsonObject.get(JSON_META_LABEL).getAsJsonObject()
        										.get(JSON_META_LINKS).getAsJsonArray();	
        for (JsonElement link : linksArray) {
		    JsonObject linkObj = link.getAsJsonObject();
		    if(linkObj.get(JSON_META_REL).getAsString().equalsIgnoreCase(JSON_META_VULNERABLE_COMPONENTS))
		    {
		    	return linkObj.get(JSON_HREF).getAsString();
		    }
		}
        
        return null;
		
	}
	
	public static String getProjectHubUIURL(String projectJson)
	{
		final JsonObject jsonObject = new JsonParser().parse(projectJson).getAsJsonObject();
		String projectApiLink = jsonObject.get(JSON_META_LABEL).getAsJsonObject()
											.get(JSON_HREF).getAsString();
		
		return projectApiLink.replace(HUB_API_PROJECTS, HUB_UI_PROJECTS);						
		
	}
	
	public static String getProjectVersionHubUIURL(String projectVersionJson)
	{
		final JsonObject jsonObject = new JsonParser().parse(projectVersionJson).getAsJsonObject();
		String projectVersionApiLink = jsonObject.get(JSON_META_LABEL).getAsJsonObject()
											.get(JSON_HREF).getAsString();
		
		
		
		return projectVersionApiLink.substring(0,projectVersionApiLink.indexOf(HUB_API_PROJECTS)).concat(
						projectVersionApiLink.substring(projectVersionApiLink.indexOf(HUB_API_VERSIONS)))
				.replace(HUB_API_VERSIONS, HUB_UI_VERSIONS);						
		
	}
	
	public static String getProjectVersionRestURL(String projectVersionJson)
	{
		final JsonObject jsonObject = new JsonParser().parse(projectVersionJson).getAsJsonObject();
		return jsonObject.get(JSON_META_LABEL).getAsJsonObject()
											.get(JSON_HREF).getAsString();			
		
	}
	
	public static HashMap<String, String> getVulnerableComponentLicense(String vulnComponentJson)
	{
		HashMap<String, String> licenseMap = new HashMap<String, String>();
		
		final JsonObject jsonObject = new JsonParser().parse(vulnComponentJson).getAsJsonObject(); 
		final JsonArray licenseArray = jsonObject.get(JSON_LICENSE).getAsJsonObject()
				.get(JSON_LICENSES).getAsJsonArray();
				
		for (JsonElement license : licenseArray) {
			JsonObject licenseObj = license.getAsJsonObject();
			licenseMap.put(licenseObj.get(JSON_NAME).getAsString(), licenseObj.get(JSON_CODE_SHARING).getAsString());
		}
			
		return licenseMap;
	}
	

}
